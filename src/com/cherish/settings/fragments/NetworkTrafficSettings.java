/*
 * Copyright (C) 2017-2019 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cherish.settings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NetworkTrafficSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener  {

    private static final String TAG = "NetworkTrafficSettings";

    private SwitchPreference mNetTrafficAutohide;
    private DropDownPreference mNetTrafficUnitType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.network_traffic_settings);
        final ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficAutohide = findPreference(Settings.System.NETWORK_TRAFFIC_AUTOHIDE);
        mNetTrafficAutohide.setOnPreferenceChangeListener(this);

        mNetTrafficUnitType = findPreference(Settings.System.NETWORK_TRAFFIC_UNIT_TYPE);
        mNetTrafficUnitType.setOnPreferenceChangeListener(this);
        int units = Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_UNIT_TYPE, /* Bytes */ 0);
        mNetTrafficUnitType.setValue(String.valueOf(units));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficUnitType) {
            int unitType = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_UNIT_TYPE, unitType);
        }
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CHERISH_SETTINGS;
    }
}