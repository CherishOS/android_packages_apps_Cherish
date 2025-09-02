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

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String HEADS_UP_TIMEOUT_PREF = "heads_up_timeout";

    private CustomSeekBarPreference mHeadsUpTimeOut;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!CherishUtils.isVoiceCapable(getActivity())) {
                prefScreen.removePreference(incallVibCategory);
        }

        mHeadsUpTimeOut = (CustomSeekBarPreference) findPreference(HEADS_UP_TIMEOUT_PREF);
        if (mHeadsUpTimeOut != null) {
            mHeadsUpTimeOut.setOnPreferenceChangeListener(this);
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

    private static int getDefaultDecay(Context context) {
        int defaultHeadsUpTimeOut = 5;
        Resources systemUiResources;
        try {
            systemUiResources = context.getPackageManager().getResourcesForApplication("com.android.systemui");
            defaultHeadsUpTimeOut = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_notification_decay", null, null)) / 1000;
        } catch (Exception e) {
        }
        return defaultHeadsUpTimeOut;
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
