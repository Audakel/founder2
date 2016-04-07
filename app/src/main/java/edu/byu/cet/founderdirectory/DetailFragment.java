package edu.byu.cet.founderdirectory;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import edu.byu.cet.founderdirectory.provider.FounderProvider;
import edu.byu.cet.founderdirectory.utilities.BitmapWorkerTask;
import edu.byu.cet.founderdirectory.utilities.PhotoManager;

/**
 * A fragment representing a single Founder detail screen.
 * This fragment is either contained in a {@link }
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
//    private  final String TAG = getClass().getSimpleName();
    private  final String TAG = "Debugging";

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private Cursor mCursor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public DetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            mCursor = getActivity().getContentResolver().query(FounderProvider.Contract.CONTENT_URI, null,
                    "_id = " + getArguments().getString(ARG_ITEM_ID), null, null);

            if (mCursor.moveToFirst()) {
                String name = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FULL_NAME));
                String id = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract._ID));
                Log.d(TAG, "Sucecss! " + name + " (" + id + ")");

                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FIRST_NAME)));
//                    BitmapWorkerTask.loadBitmapForFragment(getContext(), imageUrl, mPhoto);

                    Bitmap founderImage = (PhotoManager.getSharedPhotoManager(getContext())).getPhotoForFounderId(Integer.parseInt(id));
                    BitmapDrawable ob = new BitmapDrawable(getResources(), founderImage);
                    appBarLayout.setBackground(ob);
                }
//                mCursor.close();
            }
            else{
                Log.d(TAG, "onCreate: failed on cursor");
            }


//            TEst full text search
//            Cursor cursor = db.rawQuery("SELECT * FROM fts_table WHERE fts_table MATCH ?", selectionArgs);
            String[] selectionArgs = { "zed" };
//            mCursor = getActivity().getContentResolver().query(FounderProvider.Contract.CONTENT_FTS_URI, null,
//                    "fts_table MATCH ?", selectionArgs, null);

//            if (mCursor.moveToFirst()) {
//                String name = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FULL_NAME));
//                String id = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract._ID));
//                Log.d(TAG, "Sucecss! " + name + " (" + id + ")");
//
//                Activity activity = this.getActivity();
//                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//                if (appBarLayout != null) {
//                    appBarLayout.setTitle(mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FIRST_NAME)));
////                    BitmapWorkerTask.loadBitmapForFragment(getContext(), imageUrl, mPhoto);
//                    Bitmap founderImage = (PhotoManager.getSharedPhotoManager(getContext())).getPhotoForFounderId(Integer.parseInt(id));
//                    BitmapDrawable ob = new BitmapDrawable(getResources(), founderImage);
//                    appBarLayout.setBackground(ob);
//                }
//
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.founder_detail, container, false);

        // Show the dummy content as text in a TextView.
//        if (mCursor != null) {
        if (mCursor.moveToFirst()) {
            String name = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.PREFERRED_FULL_NAME));
            String email = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.EMAIL));
            String cell = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.CELL));
            String linkedIn = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.LINKED_IN));
            String status = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.STATUS));
            String years = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.YEAR_JOINED));
            String title = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.JOB_TITLE));
            String website = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.WEB_SITE));
            String expertise = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.EXPERTISE));
            String bio = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.BIOGRAPHY));
            String spouseName = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.SPOUSE_PREFERRED_FULL_NAME));
            String spouseEmail = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.SPOUSE_EMAIL));
            String spouseCell = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.SPOUSE_CELL));
            String homeAddress = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.HOME_ADDRESS1));
            String workAddress = mCursor.getString(mCursor.getColumnIndexOrThrow(FounderProvider.Contract.WORK_ADDRESS1));


            ((TextView) rootView.findViewById(R.id.founder_detail)).setText((name.equals("") ? "missing" : name));
            ((TextView) rootView.findViewById(R.id.contact_email)).setText((email.equals("")? "missing" : email));
            ((TextView) rootView.findViewById(R.id.contact_cell)).setText((cell.equals("")? "missing" : cell));
            ((TextView) rootView.findViewById(R.id.contact_linkedIn)).setText((linkedIn.equals("")? "missing" : linkedIn));
            ((TextView) rootView.findViewById(R.id.contact_status)).setText((status.equals("")? "missing" : status));
            ((TextView) rootView.findViewById(R.id.contact_years)).setText((years.equals("")? "missing" : years));
            ((TextView) rootView.findViewById(R.id.contact_title)).setText((title.equals("")? "missing" : title));
            ((TextView) rootView.findViewById(R.id.contact_website)).setText((website.equals("")? "missing" : website));
            ((TextView) rootView.findViewById(R.id.contact_expertise)).setText((expertise.equals("")? "missing" : expertise));
            ((TextView) rootView.findViewById(R.id.contact_bio)).setText((bio.equals("")? "missing" : bio));
            ((TextView) rootView.findViewById(R.id.contact_spouse_name)).setText((spouseName.equals("")? "missing" : spouseName));
            ((TextView) rootView.findViewById(R.id.contact_spouse_email)).setText((spouseEmail.equals("")? "missing" : spouseEmail));
            ((TextView) rootView.findViewById(R.id.contact_spouse_cell)).setText((spouseCell.equals("")? "missing" : spouseCell));
            ((TextView) rootView.findViewById(R.id.contact_home_address)).setText((homeAddress.equals("")? "missing" : homeAddress));
            ((TextView) rootView.findViewById(R.id.contact_work_address)).setText((workAddress.equals("")? "missing" : workAddress));

        }

        return rootView;
    }



}
