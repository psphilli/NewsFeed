package com.example.android.newsfeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsfeedPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        final int minNumberResults = 1;
        final int maxNumberResults = 100;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference apiKey = findPreference(getString(R.string.settings_api_key_key));
            bindPreferenceSummaryToValue(apiKey);

            Preference numberResults = findPreference(getString(R.string.settings_number_results_key));
            bindPreferenceSummaryToValue(numberResults);

            Preference sport = findPreference(getString(R.string.settings_sport_key));
            bindPreferenceSummaryToValue(sport);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                // Assure number of results is between 1 and 1000
                if (preference.getKey().equals(getString(R.string.settings_number_results_key))) {
                    if (value.toString().equals(""))
                        value = 0;
                    int val = Integer.parseInt(value.toString());
                    if ((val <= minNumberResults) || (val >= maxNumberResults)) {
                        // invalid you can show invalid message
                        Toast.makeText(getActivity(), getString(R.string.settings_number_results_range_error_message, minNumberResults, maxNumberResults), Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
