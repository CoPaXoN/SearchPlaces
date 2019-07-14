package com.example.jbt.searchplaces.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.jbt.searchplaces.R;




// create a new Java class and extends PreferenceFragment
public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SharedPreferences sp;

    // Override onCreate
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        // load my xml settings file as the preferences in the fragment
        addPreferencesFromResource(R.xml.settings);

        ListPreference distanceUnitsPrefs = (ListPreference) findPreference("units_key");

        // add listener for changed in the preference
        distanceUnitsPrefs.setOnPreferenceChangeListener(this);

        distanceUnitsPrefs.setSummary(sp.getString("units_key", "km"));
        distanceUnitsPrefs.setValue(sp.getString("units_key" , "km"));

        EditTextPreference radiusPrefs = (EditTextPreference) findPreference("radius_key");

        // add listener for changed in the preference
        radiusPrefs.setOnPreferenceChangeListener(this);
        // read the value of the radius_key from SharedPreference and show it when first loading the fragment
        String radius = sp.getString("radius_key", "3");
        radiusPrefs.setSummary(radius);

    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // check which preference changed
        switch (preference.getKey()){
            case "units_key" :
                preference.setSummary((String)newValue);
            break;

            case "radius_key" :
                preference.setSummary(getString(R.string.radius_current) + newValue);
                break;
        }
        return true;
    }
}




