package com.aventuracctv.ordermangement.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.aventuracctv.ordermangement.R;

import java.util.List;

public class AppSettings extends PreferenceActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DatabaseLoginFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences, target);
    }

    public static class DatabaseLoginFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_database_login);
        }
    }

}