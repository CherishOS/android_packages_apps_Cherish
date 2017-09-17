package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
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

    private ColorPickerPreference mEdgeLightColorPreference;

    private Preference mChargingLeds;

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
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
}
