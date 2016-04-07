package edu.byu.cet.founderdirectory;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import edu.byu.cet.founderdirectory.provider.FounderProvider;
import edu.byu.cet.founderdirectory.utilities.BitmapWorkerTask;
import edu.byu.cet.founderdirectory.utilities.PhotoManager;

/**
 * Created by audakel on 3/14/16.
 */

public class FounderAdapter extends RecyclerView.Adapter<FounderAdapter.ViewHolder> {

    private static final String TAG = "Debugging";
    protected boolean mDataValid;
    protected boolean mAutoRequery;
    protected Cursor mCursor;
    protected Context mContext;
    protected int mRowIDColumn;
    protected ChangeObserver mChangeObserver;
    protected DataSetObserver mDataSetObserver;
    protected FilterQueryProvider mFilterQueryProvider;
    public static final int FLAG_AUTO_REQUERY = 0x01;
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;


    public Cursor getCursor() {
        return mCursor;
    }

    public FounderAdapter(Context context, Cursor c) {
        int flags = FLAG_AUTO_REQUERY;

        if ((flags & FLAG_AUTO_REQUERY) == FLAG_AUTO_REQUERY) {
            flags |= FLAG_REGISTER_CONTENT_OBSERVER;
            mAutoRequery = true;
        } else {
            mAutoRequery = false;
        }
        boolean cursorPresent = c != null;

        mCursor = c;
        mDataValid = cursorPresent;
        mContext = context;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        if ((flags & FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = new ChangeObserver();
            mDataSetObserver = new MyDataSetObserver();
        } else {
            mChangeObserver = null;
            mDataSetObserver = null;
        }

        if (cursorPresent) {
            Log.d(TAG, "init: cursor size: " + c.getCount());

            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.founder_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position = " + position);
        final Cursor founder = getItem(position);
        if (founder == null) return;


        Context context = mContext;
        String imageFileName = founder.getString(founder.getColumnIndexOrThrow(FounderProvider.Contract.IMAGE_URL));
        String imageUrl = PhotoManager.getSharedPhotoManager(context).urlForFileName(imageFileName);

        Log.d(TAG, "bindModel url: " + imageUrl);

        if (imageUrl != null) {
            BitmapWorkerTask.loadBitmap(context, imageUrl, holder.mPhoto);
        } else {
            holder.mPhoto.setImageResource(R.drawable.rollins_logo_e_40);
        }

        holder.mName.setText(founder.getString(founder.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FULL_NAME)));


//        Long id = cursor.getLong(cursor.getColumnIndex(Contract.ID));
//        holder.mIdView.setText(cursor.getLong(cursor.getColumnIndexOrThrow(FounderProvider.Contract._ID))+"");
//        holder.mContentView.setText(cursor.getString(cursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FULL_NAME)));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick getAdapterPosition: " + holder.getAdapterPosition());

                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                founder.moveToPosition(holder.getAdapterPosition());
                intent.putExtra(DetailFragment.ARG_ITEM_ID, founder.getLong(founder.getColumnIndex(FounderProvider.Contract._ID)) + "");
                Log.d(TAG, "onClick founder.getLong: " + founder.getLong(founder.getColumnIndex(FounderProvider.Contract._ID)));

                context.startActivity(intent);
            }

        });
    }

    //@Override
    // public View getView(int position, View view, ViewGroup viewGroup) {
    //     return view;
    // }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return getCount();
    }

    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public Cursor getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor setFounders(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = setFounders(cursor);
        if (old != null) {
            old.close();
        }
    }

    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }
        return mCursor;
    }


    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    protected void onContentChanged() {
        if (mAutoRequery && mCursor != null && !mCursor.isClosed()) {
            if (false) Log.v("Cursor", "Auto requerying " + mCursor + " due to update");
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }


    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public final TextView mIdView;
//        public final TextView mContentView;
//        public DummyContent.DummyItem mItem;
        private ImageView mPhoto = null;
        private TextView mName = null;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPhoto = (ImageView) view.findViewById(R.id.photo);
            mName = (TextView) view.findViewById(R.id.name);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}