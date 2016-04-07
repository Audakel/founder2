package edu.byu.cet.founderdirectory;

import android.app.VoiceInteractor;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLDisplay;

import edu.byu.cet.founderdirectory.provider.FounderProvider;

public class EditMe extends AppCompatActivity {

    private static final String TAG = "Debugging";
    EditText PREFERRED_FULL_NAME ;
    EditText EMAIL ;
    EditText CELL ;
    EditText LINKED_IN ;
    EditText JOB_TITLE ;
    EditText WEB_SITE ;
    EditText EXPERTISE;
    EditText SPOUSE_PREFERRED_FULL_NAME ;
    EditText SPOUSE_EMAIL ;
    EditText SPOUSE_CELL ;
    EditText HOME_ADDRESS1 ;
    EditText WORK_ADDRESS1 ;

    HashMap<EditText,String> editFields = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Your profile has been updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Call Save
                updateFounder();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), FounderListActivity.class));
                    }
                }, 2000);

            }
        });

//        ((name.getText().toString().equals("") ? "missing" : name));

        PREFERRED_FULL_NAME = (EditText) findViewById(R.id.edit_founder_detail);
        EMAIL = (EditText) findViewById(R.id.edit_contact_email);
        CELL = (EditText) findViewById(R.id.edit_contact_cell);
        LINKED_IN = (EditText) findViewById(R.id.edit_contact_linkedIn);
        JOB_TITLE = (EditText) findViewById(R.id.edit_contact_title);
        WEB_SITE = (EditText) findViewById(R.id.edit_contact_website);
        EXPERTISE = (EditText) findViewById(R.id.edit_contact_expertise);
        SPOUSE_PREFERRED_FULL_NAME = (EditText) findViewById(R.id.edit_contact_spouse_name);
        SPOUSE_EMAIL = (EditText) findViewById(R.id.edit_contact_spouse_email);
        SPOUSE_CELL = (EditText) findViewById(R.id.edit_contact_spouse_cell);
        HOME_ADDRESS1 = (EditText) findViewById(R.id.edit_contact_home_address);
        WORK_ADDRESS1 = (EditText) findViewById(R.id.edit_contact_work_address);

        editFields.put(PREFERRED_FULL_NAME, FounderProvider.Contract.PREFERRED_FIRST_NAME);
        editFields.put(EMAIL, FounderProvider.Contract.EMAIL);
        editFields.put(LINKED_IN, FounderProvider.Contract.LINKED_IN);
        editFields.put(JOB_TITLE, FounderProvider.Contract.JOB_TITLE);
        editFields.put(WEB_SITE, FounderProvider.Contract.WEB_SITE);
        editFields.put(EXPERTISE, FounderProvider.Contract.EXPERTISE);
        editFields.put(SPOUSE_PREFERRED_FULL_NAME, FounderProvider.Contract.SPOUSE_PREFERRED_FULL_NAME);
        editFields.put(SPOUSE_EMAIL, FounderProvider.Contract.SPOUSE_EMAIL);
        editFields.put(SPOUSE_CELL, FounderProvider.Contract.SPOUSE_CELL);
        editFields.put(HOME_ADDRESS1, FounderProvider.Contract.HOME_ADDRESS1);
        editFields.put(WORK_ADDRESS1, FounderProvider.Contract.WORK_ADDRESS1);
        editFields.put(WORK_ADDRESS1, FounderProvider.Contract.WORK_ADDRESS1);
    }



    private int updateFounder(){
        String whateverIdIam = "6";

        ContentValues contentValues  = new ContentValues();
        for (Map.Entry<EditText, String> i : editFields.entrySet()){
            if (!(i.getKey().getText().toString().equals(""))){
                contentValues.put(i.getValue(), i.getKey().getText().toString());
            }
        }

        contentValues.put(FounderProvider.Contract.DIRTY, FounderProvider.Contract.FLAG_DIRTY);
//        contentValues.put(FounderProvider.Contract.PREFERRED_FULL_NAME, PREFERRED_FULL_NAME);

        int updateResponse = getApplicationContext().getContentResolver().update(FounderProvider.Contract.CONTENT_URI, contentValues,
                "_id = " + whateverIdIam , null);



        Log.d(TAG, "updateFounder updateResponse: " + updateResponse);

        return updateResponse;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, FounderProvider.Contract.FLURRY_API_KEY);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

}
