package com.mcssoft.racemeeting.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mcssoft.racemeeting.dialogs.DeleteDialog;
import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.interfaces.IShowMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingDisplay;
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingTime;

import java.util.Map;

import mcssoft.com.racemeeting3.R;

public class MainActivity extends AppCompatActivity
    implements IEditMeeting, IShowMeeting, IDeleteMeeting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialise();

        Bundle prefsState = setStateFromPreferences();
        mainFragment.setArguments(prefsState);

        if(savedInstanceState == null) {
            fragmentManager.beginTransaction()
                           .replace(R.id.listing_container, mainFragment, null)
                           .addToBackStack(null)
                           .commit();
        } else {
            // TBA
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.menu_insert:
                onEditMeeting(0);
                break;
            case R.id.preference_settings:
                Intent paIntent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(paIntent, MeetingConstants.PREFERENCES_ACTIVITY_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    @Override
    public void onEditMeeting(long id) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(MeetingConstants.EDIT_NEW, id);
        intent.setAction(MeetingConstants.EDIT_ACTION_NEW);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IShowMeeting">
    @Override
    public void onShowMeeting(long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MeetingConstants.SHOW_SUMMARY, id);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IDeleteMeeting">
    public void onDeleteMeeting(long rowId) {
        DialogFragment df = new DeleteDialog();

        Bundle args = new Bundle();
        args.putLong(MeetingConstants.DELETE_DIALOG_ROWID, rowId);
        df.setArguments(args);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        df.show(ft, MeetingConstants.DEFAULT_DELETE_DIALOG_FRAGMENT_TAG);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - Utility">
    private void initialise() {
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        mainFragment = new MainFragment();

        if(!MeetingPreferences.instanceExists()) {
            MeetingPreferences.getInstance(getApplicationContext());
        }
        if(!MeetingTime.instanceExists()) {
            MeetingTime.getInstance(getApplicationContext());
        }
        if(!MeetingDisplay.instanceExists()) {
            MeetingDisplay.getInstance(getApplicationContext());
        }
    }

    /**
     * Get state values based on what's currently in the SharedPreferences.
     * @return The state bundle containg the preferences (as strings).
     */
    private Bundle setStateFromPreferences() {

        Map<String,?> prefsMap = MeetingPreferences.getInstance().getAllPreferences();

        // No SharedPreferences set yet. App has probably been uninstalled then re-installed and/or
        // cache and data cleared. Set the app preferences defaults.
        if(prefsMap.isEmpty()) {
            MeetingPreferences.getInstance().setDefaultValues();
            prefsMap = MeetingPreferences.getInstance().getAllPreferences();
        }

        Bundle prefsState = new Bundle();

        for (String key : prefsMap.keySet()) {
            Object obj = prefsMap.get(key);
            prefsState.putString(key, obj.toString());
        }

        return prefsState;
    }
    //</editor-fold>

    private MainFragment mainFragment;
    private FragmentManager fragmentManager;
    private String LOG_TAG = this.getClass().getCanonicalName();
}
