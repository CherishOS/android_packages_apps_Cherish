package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.ContentResolver;
import com.android.settings.R;
import android.content.res.Resources;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NotificationSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener{

    private static final String PULSE_AMBIENT_LIGHT_COLOR = "pulse_ambient_light_color";

    private Preference mChargingLeds;
    private ColorPickerPreference mEdgeLightColorPreference;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_notifications);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(PULSE_AMBIENT_LIGHT_COLOR);
		mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR, 0xFF3980FF);
        String edgeLightColorHex = String.format("#%08x", (0xFF3980FF & edgeLightColor));
        if (edgeLightColorHex.equals("#ff3980ff")) {
            mEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
           ContentResolver resolver = getActivity().getContentResolver();
           if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_AMBIENT_LIGHT_COLOR, intHex);
            return true;
		   }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
}
