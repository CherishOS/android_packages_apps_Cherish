package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;
import android.provider.Settings;
import com.android.settings.R;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import com.cherish.settings.preferences.SystemSettingSeekBarPreference;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import com.cherish.settings.preferences.SystemSettingMasterSwitchPreference;
import com.cherish.settings.utils.DeviceUtils;
import com.android.settings.Utils;
import com.android.internal.util.cherish.CherishUtils;
import android.util.Log;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";

    private SystemSettingListPreference mStatusBarClock;

    private static final String PREF_NEW_STATUS_BAR_ICONS = "new_status_bar_icons";

    private SwitchPreferenceCompat mNewStatusBarIconsPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_statusbar);

		ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();

        mNewStatusBarIconsPref = findPreference(PREF_NEW_STATUS_BAR_ICONS);
        mNewStatusBarIconsPref.setOnPreferenceChangeListener(this);

        boolean newIconsEnabled = Settings.System.getIntForUser(
            resolver, "new_status_bar_icons_enabled", 0, UserHandle.USER_CURRENT) == 1;
        mNewStatusBarIconsPref.setChecked(newIconsEnabled);
        updatePreferenceStates(newIconsEnabled);
    }

    /**
     * This is the helper method that enables or disables all incompatible preferences.
     * @param newIconsEnabled true if the new UI is on, which means old options should be disabled.
     */
    private void updatePreferenceStates(boolean newIconsEnabled) {
        // We use !newIconsEnabled because if the new UI is ON (true),
        // the old preferences should be DISABLED (false).
        final boolean oldPrefsEnabled = !newIconsEnabled;

        findPreference("systemui_tuner_statusbar").setEnabled(oldPrefsEnabled);
        findPreference("clock").setEnabled(oldPrefsEnabled);
        findPreference("battery_bar_category").setEnabled(oldPrefsEnabled);
        //findPreference("network_traffic_settings").setEnabled(oldPrefsEnabled);
        //findPreference("ongoing_progress_settings").setEnabled(oldPrefsEnabled);
        findPreference("show_fourg_icon").setEnabled(oldPrefsEnabled);
        findPreference("data_disabled_icon").setEnabled(oldPrefsEnabled);
        findPreference("enable_camera_privacy_indicator").setEnabled(oldPrefsEnabled);
        findPreference("enable_location_privacy_indicator").setEnabled(oldPrefsEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        final Context context = getContext();

        if (preference == mNewStatusBarIconsPref) {
            boolean value = (Boolean) objValue;

            updatePreferenceStates(value);

            Settings.System.putIntForUser(resolver, "status_bar_root_modernization_enabled",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            Settings.System.putIntForUser(resolver, "new_status_bar_icons_enabled",
                    value ? 1 : 0, UserHandle.USER_CURRENT);

            CherishUtils.showSystemUiRestartDialog(context);
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
                    sir.xmlResId = R.xml.cherish_settings_statusbar;
                    result.add(sir);
                    return result;
                }
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
