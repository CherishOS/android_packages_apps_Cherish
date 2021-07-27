package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.content.ContentResolver;
import com.android.settings.R;
import android.content.res.Resources;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import android.provider.Settings;
import androidx.preference.ListPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;
import com.cherish.settings.preferences.SystemSettingMasterSwitchPreference;
import com.cherish.settings.preferences.SystemSettingSeekBarPreference;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.internal.util.cherish.CherishUtils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NotificationSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener{

    private static final String NOTIFICATION_PULSE_COLOR = "ambient_notification_light_color";
    private static final String NOTIFICATION_PULSE_DURATION = "notification_pulse_duration";
    private static final String NOTIFICATION_PULSE_REPEATS = "notification_pulse_repeats";
    private static final String PULSE_COLOR_MODE_PREF = "ambient_notification_light_color_mode";
	private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String KEY_AMBIENT = "ambient_notification_light_enabled";
	private static final String ALERT_SLIDER_PREF = "alert_slider_notifications";
	private static final String KEY_CHARGING_LIGHT = "charging_light";
    private static final String LED_CATEGORY = "led";

    private Preference mChargingLeds;
    private PreferenceCategory mLedCategory;
    private ColorPickerPreference mEdgeLightColorPreference;
    private CustomSeekBarPreference mEdgeLightDurationPreference;
    private CustomSeekBarPreference mEdgeLightRepeatCountPreference;
    private ListPreference mColorMode;
    private SystemSettingSwitchPreference mAmbientPref;
	private Preference mAlertSlider;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
		
		mAlertSlider = (Preference) prefScreen.findPreference(ALERT_SLIDER_PREF);
        boolean mAlertSliderAvailable = res.getBoolean(
                com.android.internal.R.bool.config_hasAlertSlider);
        if (!mAlertSliderAvailable)
            prefScreen.removePreference(mAlertSlider);

        boolean hasLED = res.getBoolean(
                com.android.internal.R.bool.config_hasNotificationLed);
        if (hasLED) {
            mChargingLeds = findPreference(KEY_CHARGING_LIGHT);
            if (mChargingLeds != null
                    && !res.getBoolean(
                            com.android.internal.R.bool.config_intrusiveBatteryLed)) {
                prefScreen.removePreference(mChargingLeds);
            }
        } else {
            mLedCategory = findPreference(LED_CATEGORY);
            mLedCategory.setVisible(false);
        }
		
		PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!CherishUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        mAmbientPref = (SystemSettingSwitchPreference) findPreference(KEY_AMBIENT);
        boolean aodEnabled = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT) == 1;
        if (!aodEnabled) {
            mAmbientPref.setChecked(false);
            mAmbientPref.setEnabled(false);
            mAmbientPref.setSummary(R.string.aod_disabled);
        }

        mEdgeLightRepeatCountPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_REPEATS);
        mEdgeLightRepeatCountPreference.setOnPreferenceChangeListener(this);
        int repeats = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_REPEATS, 0);
        mEdgeLightRepeatCountPreference.setValue(repeats);

        mEdgeLightDurationPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_DURATION);
        mEdgeLightDurationPreference.setOnPreferenceChangeListener(this);
        int duration = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_DURATION, 2);
        mEdgeLightDurationPreference.setValue(duration);

        mColorMode = (ListPreference) findPreference(PULSE_COLOR_MODE_PREF);
        int value;
        boolean colorModeAutomatic = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0) != 0;
        boolean colorModeAccent = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_ACCENT, 0) != 0;
        if (colorModeAutomatic) {
            value = 0;
        } else if (colorModeAccent) {
            value = 1;
        } else {
            value = 2;
        }

        mColorMode.setValue(Integer.toString(value));
        mColorMode.setSummary(mColorMode.getEntry());
        mColorMode.setOnPreferenceChangeListener(this);

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(NOTIFICATION_PULSE_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR, 0xFF3980FF);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        mEdgeLightColorPreference.setAlphaSliderEnabled(false);
        String edgeLightColorHex = String.format("#%08x", (0xFF3980FF & edgeLightColor));
        if (edgeLightColorHex.equals("#ff1a73e8")) {
            mEdgeLightColorPreference.setSummary(R.string.color_default);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff1a73e8")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_COLOR, intHex);
            return true;
        } else if (preference == mEdgeLightRepeatCountPreference) {
                int value = (Integer) newValue;
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_REPEATS, value);
                return true;
        } else if (preference == mEdgeLightDurationPreference) {
            int value = (Integer) newValue;
                Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_DURATION, value);
            return true;
        } else if (preference == mColorMode) {
             int value = Integer.valueOf((String) newValue);
            int index = mColorMode.findIndexOfValue((String) newValue);
            mColorMode.setSummary(mColorMode.getEntries()[index]);
            if (value == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 1);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            } else if (value == 1) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 1);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
	
	/**
     * For Search.
     */

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.cherish_settings_notifications;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();
                    boolean mAlertSliderAvailable = res.getBoolean(
                            com.android.internal.R.bool.config_hasAlertSlider);
                    if (!mAlertSliderAvailable)
                        keys.add(ALERT_SLIDER_PREF);
                    return keys;
                }
    };
}
