package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
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
import com.android.settings.Utils;
import com.android.internal.util.cherish.CherishUtils;
import android.util.Log;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
			
	private static final String STATUS_BAR_LOGO = "status_bar_logo";
	
	private SystemSettingMasterSwitchPreference mStatusBarLogo;

    private static final String PREF_KEY_CUTOUT = "cutout_settings";
    private static final String VOLTE_ICON_STYLE = "volte_icon_style";
    private static final String VOWIFI_ICON_STYLE = "vowifi_icon_style";

    private SystemSettingListPreference mVolteIconStyle;
    private SystemSettingListPreference mVowifiIconStyle;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_statusbar);
		
		ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();
		
		mStatusBarLogo = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_LOGO);
        mStatusBarLogo.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mStatusBarLogo.setOnPreferenceChangeListener(this);
		
		Preference mCutoutPref = (Preference) findPreference(PREF_KEY_CUTOUT);
		
		String hasDisplayCutout = getResources().getString(com.android.internal.R.string.config_mainBuiltInDisplayCutout);

        mVolteIconStyle = (SystemSettingListPreference) findPreference(VOLTE_ICON_STYLE);
        int volteIconStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOLTE_ICON_STYLE, 0);
        mVolteIconStyle.setValue(String.valueOf(volteIconStyle));
        mVolteIconStyle.setOnPreferenceChangeListener(this);

        mVowifiIconStyle = (SystemSettingListPreference) findPreference(VOWIFI_ICON_STYLE);
        int vowifiIconStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOWIFI_ICON_STYLE, 0);
        mVowifiIconStyle.setValue(String.valueOf(vowifiIconStyle));
        mVowifiIconStyle.setOnPreferenceChangeListener(this);

        if (TextUtils.isEmpty(hasDisplayCutout)) {
            getPreferenceScreen().removePreference(mCutoutPref);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mStatusBarLogo) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
		}else if (preference == mVolteIconStyle) {
            int volteIconStyle = Integer.parseInt(((String) objValue).toString());
            Settings.System.putInt(resolver,
                  Settings.System.VOLTE_ICON_STYLE, volteIconStyle);
            mVolteIconStyle.setValue(String.valueOf(volteIconStyle));
            CherishUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mVowifiIconStyle) {
            int vowifiIconStyle = Integer.parseInt(((String) objValue).toString());
            Settings.System.putInt(resolver,
                  Settings.System.VOWIFI_ICON_STYLE, vowifiIconStyle);
            mVowifiIconStyle.setValue(String.valueOf(vowifiIconStyle));
            CherishUtils.showSystemUiRestartDialog(getContext());
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