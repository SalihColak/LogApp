package com.thk.viewmylog.activties;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.thk.viewmylog.R;
import com.thk.viewmylog.helper.FilterPreferenceDialogFragmentCompat;
import com.thk.viewmylog.helper.LogFilterPreference;
import com.thk.viewmylog.views.LogToast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SharedPreferences preferences;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            final SwitchPreference toast = findPreference("logToast");
            assert toast != null;
            toast.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    LogToast logToast = LogToast.getInstance(getContext());
                    if((boolean) newValue){
                        logToast.registerToast(preferences.getString("logToastTag","tag"));
                    }else {
                        logToast.unregisterToast();
                    }
                    return true;
                }
            });
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            // Try if the preference is one of our custom Preferences
            DialogFragment dialogFragment = null;
            if (preference instanceof LogFilterPreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = FilterPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }
            if(preference instanceof EditTextPreference){
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String newString = (String)newValue;
                        if(newValue == null || newString.equals("")){
                            Toast.makeText(getContext(),"Der eingegebene Tag muss mindestens ein Zeichen enthalten.",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(preference.getKey().equals("logToastTag")){
                            LogToast logToast = LogToast.getInstance(getContext());
                            logToast.unregisterToast();
                            logToast.registerToast((String)newValue);
                        }
                        return true;
                    }
                });
            }

            // If it was one of our cutom Preferences, show its dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getParentFragmentManager(),"test");
            }
            // Could not be handled here. Try with the super method.
            else {
                super.onDisplayPreferenceDialog(preference);
            }
        }


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}