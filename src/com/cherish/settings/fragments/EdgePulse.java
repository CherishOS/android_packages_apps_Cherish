/*
 * Copyright (C) 2020 CherishOS
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
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class EdgePulse extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PULSE_AMBIENT_LIGHT_COLOR_LEFT = "pulse_ambient_light_color_left";
    private static final String PULSE_AMBIENT_LIGHT_COLOR_RIGHT = "pulse_ambient_light_color_right";

    private ColorPickerPreference mLeftEdgeLightColorPreference;
    private ColorPickerPreference mRightEdgeLightColorPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.edgepulse_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mLeftEdgeLightColorPreference = (ColorPickerPreference) findPreference(PULSE_AMBIENT_LIGHT_COLOR_LEFT);
        mLeftEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        int leftEdgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR_LEFT, 0xFF3980FF);
        String leftEdgeLightColorHex = String.format("#%08x", (0xFF3980FF & leftEdgeLightColor));
        if (leftEdgeLightColorHex.equals("#ff3980ff")) {
            mLeftEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mLeftEdgeLightColorPreference.setSummary(leftEdgeLightColorHex);
        }
        mLeftEdgeLightColorPreference.setNewPreviewColor(leftEdgeLightColor);

        mRightEdgeLightColorPreference = (ColorPickerPreference) findPreference(PULSE_AMBIENT_LIGHT_COLOR_RIGHT);
        mRightEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        int rightEdgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR_RIGHT, 0xFF3980FF);
        String rightEdgeLightColorHex = String.format("#%08x", (0xFF3980FF & rightEdgeLightColor));
        if (rightEdgeLightColorHex.equals("#ff3980ff")) {
            mRightEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mRightEdgeLightColorPreference.setSummary(rightEdgeLightColorHex);
        }
        mRightEdgeLightColorPreference.setNewPreviewColor(rightEdgeLightColor);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLeftEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_AMBIENT_LIGHT_COLOR_LEFT, intHex);
            return true;
        } else if (preference == mRightEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_AMBIENT_LIGHT_COLOR_RIGHT, intHex);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
}
