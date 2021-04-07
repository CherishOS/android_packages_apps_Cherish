/*
 * Copyright (C) 2021 TenX-OS
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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.cherish.settings.preferences.SystemSettingListPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class KeygaurdBatteryBar extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String BATTERY_BAR_COLOR_MODE = "keygaurd_battery_bar_color_mode";
    private static final String BATTERY_BAR_COLOR_CUSTOM = "keygaurd_battery_bar_color_custom";

    private SystemSettingListPreference mColor;
    private ColorPickerPreference mColorCustom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.cherish_keyguard_battery_bar);
        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mColor = (SystemSettingListPreference) findPreference(BATTERY_BAR_COLOR_MODE);
        mColor.setOnPreferenceChangeListener(this);
        int color = Settings.System.getIntForUser(resolver,
                Settings.System.KEYGAURD_BATTERY_BAR_COLOR_MODE, 0, UserHandle.USER_CURRENT);
        mColor.setValue(String.valueOf(color));
        mColor.setSummary(mColor.getEntry());

        mColorCustom = (ColorPickerPreference) findPreference(BATTERY_BAR_COLOR_CUSTOM);
        mColorCustom.setOnPreferenceChangeListener(this);
        int custom = Settings.System.getInt(getContentResolver(),
                Settings.System.KEYGAURD_BATTERY_BAR_COLOR_CUSTOM, 0xFFFFFFFF);
        String hex = String.format("#%08x", (0xFFFFFFFF & custom));
        if (hex.equals("#ffffffff")) {
            mColorCustom.setSummary(R.string.default_string);
        } else {
            mColorCustom.setSummary(hex);
        }
        mColorCustom.setNewPreviewColor(custom);

        updateColorPrefs(color);
   }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mColor) {
            int color = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(resolver,
                    Settings.System.KEYGAURD_BATTERY_BAR_COLOR_MODE, color, UserHandle.USER_CURRENT);
            int index = mColor.findIndexOfValue((String) newValue);
            mColor.setSummary(
                    mColor.getEntries()[index]);
            updateColorPrefs(color);
            return true;
        } else if (preference == mColorCustom) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.KEYGAURD_BATTERY_BAR_COLOR_CUSTOM, intHex);
            return true;
        }
        return false;
    }

    private void updateColorPrefs(int color) {
        if (color == 3) {
            mColorCustom.setEnabled(true);
        } else {
            mColorCustom.setEnabled(false);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
}
