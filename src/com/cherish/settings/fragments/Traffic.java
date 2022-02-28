/*
 * Copyright (C) 2021-2022 The PixelDust Project
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
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.cherish.settings.preferences.CustomSeekBarPreference;

public class Traffic extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private CustomSeekBarPreference mNetTrafficSize;
    private CustomSeekBarPreference mNetTrafficAutohideThreshold;
    private CustomSeekBarPreference mNetTrafficRefreshInterval;
    private ListPreference mNetTrafficLocation;
    private ListPreference mNetTrafficMode;
    private ListPreference mNetTrafficUnits;
    private SwitchPreference mNetTrafficAutohide;
    private SwitchPreference mNetTrafficHideArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.cherish_settings_traffic);
        final ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficSize = (CustomSeekBarPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_FONT_SIZE);
        mNetTrafficAutohideThreshold = (CustomSeekBarPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        mNetTrafficRefreshInterval = (CustomSeekBarPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL);
        mNetTrafficLocation = (ListPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_LOCATION);
        mNetTrafficLocation.setOnPreferenceChangeListener(this);
        mNetTrafficMode = (ListPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_MODE);
        mNetTrafficAutohide = (SwitchPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_AUTOHIDE);
        mNetTrafficUnits = (ListPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_UNITS);
        mNetTrafficHideArrow = (SwitchPreference)
                findPreference(Settings.System.NETWORK_TRAFFIC_HIDEARROW);

        int location = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_LOCATION, 0, UserHandle.USER_CURRENT);
        updateEnabledStates(location);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficLocation) {
            int location = Integer.valueOf((String) newValue);
            updateEnabledStates(location);
            return true;
        }
        return false;
    }

    private void updateEnabledStates(int location) {
        final boolean enabled = location != 0;
        mNetTrafficSize.setEnabled(enabled);
        mNetTrafficMode.setEnabled(enabled);
        mNetTrafficAutohide.setEnabled(enabled);
        mNetTrafficAutohideThreshold.setEnabled(enabled);
        mNetTrafficHideArrow.setEnabled(enabled);
        mNetTrafficRefreshInterval.setEnabled(enabled);
        mNetTrafficUnits.setEnabled(enabled);
    }
}