package com.jonathanrlemos.passphrasegen;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    // cannot initialize up here because getActivity() has not been initialized yet
    private Prefs prefs;

    private void setupPrefPwstrCrackStrength(){
        SeekBarPreference sbpCrackStrength = (SeekBarPreference)findPreference(getResources().getString(R.string.pref_pwstr_crackstrength_key));

        String[] crackStrengthValues = getResources().getStringArray(R.array.pref_pwstr_crackstrength_options);
        sbpCrackStrength.setMax(crackStrengthValues.length - 1);
        sbpCrackStrength.setMin(0);

        sbpCrackStrength.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SeekBarPreference sbp = (SeekBarPreference)preference;
                String[] crackStrengthValues = getResources().getStringArray(R.array.pref_pwstr_crackstrength_options);
                int index = (Integer)o;

                sbp.setSummary(crackStrengthValues[index]);
                return true;
            }
        });
        try {
            sbpCrackStrength.setValue(prefs.getInt(R.string.pref_pwstr_crackstrength_key));
        }
        catch (Prefs.PrefNotFoundException e){
            sbpCrackStrength.setValue(0);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        prefs = new Prefs(getActivity());
        setupPrefPwstrCrackStrength();
    }


}
