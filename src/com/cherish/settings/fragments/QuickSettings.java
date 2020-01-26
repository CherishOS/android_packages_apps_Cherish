package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;

public class QuickSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
   private static final String QS_BLUR_ALPHA = "qs_blur_alpha";
   private static final String QS_BLUR_INTENSITY = "qs_blur_intensity";
  
   private CustomSeekBarPreference mQSBlurAlpha;
   private CustomSeekBarPreference mQSBlurIntensity;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_quicksettings);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        mQSBlurAlpha = (CustomSeekBarPreference) findPreference(QS_BLUR_ALPHA);
        int qsBlurAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BLUR_ALPHA, 100);
        mQSBlurAlpha.setValue(qsBlurAlpha);
        mQSBlurAlpha.setOnPreferenceChangeListener(this);

        mQSBlurIntensity = (CustomSeekBarPreference) findPreference(QS_BLUR_INTENSITY);
        int qsBlurIntensity = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BLUR_INTENSITY, 30);
        mQSBlurIntensity.setValue(qsBlurIntensity);
        mQSBlurIntensity.setOnPreferenceChangeListener(this);

        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
         if (preference == mQSBlurAlpha) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BLUR_ALPHA, value);
            return true;
        } else if (preference == mQSBlurIntensity) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BLUR_INTENSITY, value);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

}
