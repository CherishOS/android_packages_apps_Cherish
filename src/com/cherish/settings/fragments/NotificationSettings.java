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
import com.cherish.settings.preferences.Utils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NotificationSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener{

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String PREF_FLASH_ON_CALL = "flashlight_on_call";
    private static final String PREF_FLASH_ON_CALL_DND = "flashlight_on_call_ignore_dnd";
    private static final String PREF_FLASH_ON_CALL_RATE = "flashlight_on_call_rate";
	private static final String KEY_EDGE_LIGHTNING = "pulse_ambient_light";

    private SystemSettingMasterSwitchPreference mEdgeLightning;
    private SystemSettingListPreference mFlashOnCall;
    private SystemSettingSwitchPreference mFlashOnCallIgnoreDND;
    private CustomSeekBarPreference mFlashOnCallRate;
	private Preference mChargingLeds;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
		
		PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
                prefScreen.removePreference(incallVibCategory);
        }
		
		mFlashOnCallRate = (CustomSeekBarPreference)
                findPreference(PREF_FLASH_ON_CALL_RATE);
        int value = Settings.System.getInt(resolver,
                Settings.System.FLASHLIGHT_ON_CALL_RATE, 1);
        mFlashOnCallRate.setValue(value);
        mFlashOnCallRate.setOnPreferenceChangeListener(this);

        mFlashOnCallIgnoreDND = (SystemSettingSwitchPreference)
                findPreference(PREF_FLASH_ON_CALL_DND);
        value = Settings.System.getInt(resolver,
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashOnCallIgnoreDND.setVisible(value > 1);
        mFlashOnCallRate.setVisible(value != 0);

        mFlashOnCall = (SystemSettingListPreference)
                findPreference(PREF_FLASH_ON_CALL);
        mFlashOnCall.setSummary(mFlashOnCall.getEntries()[value]);
        mFlashOnCall.setOnPreferenceChangeListener(this);
		
		mEdgeLightning = (SystemSettingMasterSwitchPreference)
                findPreference(KEY_EDGE_LIGHTNING);
        boolean enabled = Settings.System.getIntForUser(resolver,
                KEY_EDGE_LIGHTNING, 0, UserHandle.USER_CURRENT) == 1;
        mEdgeLightning.setChecked(enabled);
        mEdgeLightning.setOnPreferenceChangeListener(this);

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		final ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mFlashOnCall) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL, value);
            mFlashOnCall.setSummary(mFlashOnCall.getEntries()[value]);
            mFlashOnCallIgnoreDND.setVisible(value > 1);
            mFlashOnCallRate.setVisible(value != 0);
            return true;
        } else if (preference == mFlashOnCallRate) {
            int value = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL_RATE, value);
            return true;
        } else if (preference == mEdgeLightning) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, KEY_EDGE_LIGHTNING,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
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
                    return keys;
                }
    };
}
