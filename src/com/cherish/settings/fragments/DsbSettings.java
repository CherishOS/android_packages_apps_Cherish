/*
 * Copyright (C) 2022 The Nusantara Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cherish.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;

import androidx.preference.SeekBarPreference;

@SearchIndexable
public class DsbSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_DYNAMIC_STATUS_BAR = "DYNAMIC_STATUS_BAR_STATE";
    private static final String KEY_DYNAMIC_NAVIGATION_BAR = "DYNAMIC_NAVIGATION_BAR_STATE";
    private static final String KEY_DYNAMIC_STATUS_BAR_FILTER = "DYNAMIC_STATUS_BAR_FILTER_STATE";
    private static final String KEY_DYNAMIC_LINKED_COLOR = "LINKED_COLOR";
    private static final String KEY_DYNAMIC_ABU_ABU = "ABU_ABU";
    private static final String KEY_DYNAMIC_UI_COLOR = "UI_COLOR";
    private static final String KEY_DYNAMIC_ACCENT_COLOR = "ACCENT_COLOR";

    private SwitchPreference mDynamicStatusBar;
    private SwitchPreference mDynamicNavigationBar;
    private SwitchPreference mDynamicStatusBarFilter;
    private SwitchPreference mDynamicLinkedColor;
    private SwitchPreference mDynamicAbuAbu;
    private SwitchPreference mDynamicUiColor;
    private SwitchPreference mDynamicAccentColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getContext();
        String name = ctx.getPackageName();

        addPreferencesFromResource(getResources().getIdentifier("dsb_settings", "xml", name));

        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = ctx.getContentResolver();

        SwitchPreference statusBar = mDynamicStatusBar = prefSet.findPreference(KEY_DYNAMIC_STATUS_BAR);
        statusBar.setOnPreferenceChangeListener(this);
        boolean status = Settings.System.getIntForUser(resolver, "DYNAMIC_STATUS_BAR_STATE", 0, UserHandle.USER_CURRENT) != 0;
        statusBar.setChecked(status);

        SwitchPreference navi = mDynamicNavigationBar = prefSet.findPreference(KEY_DYNAMIC_NAVIGATION_BAR);
        navi.setOnPreferenceChangeListener(this);
        boolean naviEnable = Settings.System.getIntForUser(resolver, "DYNAMIC_NAVIGATION_BAR_STATE", 0, UserHandle.USER_CURRENT) != 0;
        navi.setChecked(naviEnable);
        Resources res = getResources();
        final boolean hasNavigationBar = res.getDimensionPixelSize(res.getIdentifier(
                "navigation_bar_height", "dimen", "android")) > 0;
        navi.setEnabled(hasNavigationBar);

        final boolean isAnyBarDynamic = status || naviEnable;

        SwitchPreference linkedColor = mDynamicLinkedColor = prefSet.findPreference(KEY_DYNAMIC_LINKED_COLOR);
        linkedColor.setOnPreferenceChangeListener(this);
        boolean linkedColorEnabled = Settings.System.getIntForUser(resolver, "LINKED_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        boolean uiEnabled = Settings.System.getIntForUser(resolver, "UI_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        linkedColor.setChecked(linkedColorEnabled);
        linkedColor.setEnabled(status && naviEnable && !uiEnabled);

        SwitchPreference ui = mDynamicUiColor = prefSet.findPreference(KEY_DYNAMIC_UI_COLOR);
        ui.setOnPreferenceChangeListener(this);
        ui.setChecked(uiEnabled);
        ui.setEnabled(status && naviEnable && !linkedColorEnabled);

        SwitchPreference filter = mDynamicStatusBarFilter = prefSet.findPreference(KEY_DYNAMIC_STATUS_BAR_FILTER);
        filter.setOnPreferenceChangeListener(this);
        boolean filterEnabled = Settings.System.getIntForUser(resolver, "DYNAMIC_STATUS_BAR_FILTER_STATE", 0, UserHandle.USER_CURRENT) != 0;
        filter.setChecked(filterEnabled);
        filter.setEnabled(status);

        SwitchPreference abu = mDynamicAbuAbu = prefSet.findPreference(KEY_DYNAMIC_ABU_ABU);
        abu.setOnPreferenceChangeListener(this);
        boolean abuEnabled = Settings.System.getIntForUser(resolver, "ABU_ABU", 0, UserHandle.USER_CURRENT) != 0;
        boolean accentEnabled = Settings.System.getIntForUser(resolver, "ACCENT_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        abu.setChecked(abuEnabled);
        abu.setEnabled(isAnyBarDynamic && !accentEnabled);

        SwitchPreference accent = mDynamicAccentColor = prefSet.findPreference(KEY_DYNAMIC_ACCENT_COLOR);
        accent.setOnPreferenceChangeListener(this);
        accent.setChecked(accentEnabled);
        accent.setEnabled(isAnyBarDynamic && !abuEnabled);


        updatePrefAll();
    }

    protected void updatePrefAll() {
        Context ctx = getContext();
        ContentResolver resolver = ctx.getContentResolver();

        boolean status = Settings.System.getIntForUser(resolver, "DYNAMIC_STATUS_BAR_STATE", 0, UserHandle.USER_CURRENT) != 0;

        SwitchPreference navi = mDynamicNavigationBar;
        boolean naviEnable = Settings.System.getIntForUser(resolver, "DYNAMIC_NAVIGATION_BAR_STATE", 0, UserHandle.USER_CURRENT) != 0;
        Resources res = getResources();
        final boolean hasNavigationBar = res.getDimensionPixelSize(res.getIdentifier(
                "navigation_bar_height", "dimen", "android")) > 0;
        navi.setEnabled(hasNavigationBar);

        final boolean isAnyBarDynamic = status || naviEnable;

        SwitchPreference linkedColor = mDynamicLinkedColor;
        boolean linkedColorEnabled = Settings.System.getIntForUser(resolver, "LINKED_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        boolean uiEnabled = Settings.System.getIntForUser(resolver, "UI_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        linkedColor.setEnabled(status && naviEnable && !uiEnabled);

        SwitchPreference ui = mDynamicUiColor;
        ui.setEnabled(status && naviEnable && !linkedColorEnabled);

        SwitchPreference filter = mDynamicStatusBarFilter;
        boolean filterEnabled = Settings.System.getIntForUser(resolver, "DYNAMIC_STATUS_BAR_FILTER_STATE", 0, UserHandle.USER_CURRENT) != 0;
        filter.setEnabled(status);

        SwitchPreference abu = mDynamicAbuAbu;
        boolean abuEnabled = Settings.System.getIntForUser(resolver, "ABU_ABU", 0, UserHandle.USER_CURRENT) != 0;
        boolean accentEnabled = Settings.System.getIntForUser(resolver, "ACCENT_COLOR", 0, UserHandle.USER_CURRENT) != 0;
        abu.setEnabled(isAnyBarDynamic && !accentEnabled);

        SwitchPreference accent = mDynamicAccentColor;
        accent.setEnabled(isAnyBarDynamic && !abuEnabled);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getContext().getContentResolver();
        if (preference == mDynamicStatusBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    "DYNAMIC_STATUS_BAR_STATE",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            updatePrefAll();
            return true;
        } else if (preference == mDynamicNavigationBar) {
            boolean value = (Boolean) newValue;
            final Resources res = getResources();
            Settings.System.putIntForUser(resolver,
                    "DYNAMIC_NAVIGATION_BAR_STATE",
                    value && res.getDimensionPixelSize(
                            res.getIdentifier("navigation_bar_height", "dimen", "android")) > 0 ?
                            1 : 0, UserHandle.USER_CURRENT);
            updatePrefAll();
            return true;
        } else if (preference == mDynamicLinkedColor) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, "LINKED_COLOR",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            if (value) {
                Settings.System.putIntForUser(resolver,
                        "UI_COLOR", 0, UserHandle.USER_CURRENT);
            }
            updatePrefAll();
            return true;
        } else if (preference == mDynamicUiColor) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, "UI_COLOR",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            if (value) {
                Settings.System.putIntForUser(resolver,
                        "LINKED_COLOR", 0, UserHandle.USER_CURRENT);
            }
            updatePrefAll();
            return true;
        } else if (preference == mDynamicStatusBarFilter) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    "DYNAMIC_STATUS_BAR_FILTER_STATE",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            updatePrefAll();
            return true;
        } else if (preference == mDynamicAbuAbu) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    "ABU_ABU",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            if (value) {
                Settings.System.putIntForUser(resolver,
                        "ACCENT_COLOR", 0, UserHandle.USER_CURRENT);
            }
            updatePrefAll();
            return true;
        } else if (preference == mDynamicAccentColor) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    "ACCENT_COLOR",
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            if (value) {
                Settings.System.putIntForUser(resolver,
                        "ABU_ABU", 0, UserHandle.USER_CURRENT);
            }
            updatePrefAll();
            return true;
        }
        return false;
    }

    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.dsb_settings;
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


