/*
 * Copyright (C) 2015 The Dirty Unicorns Project
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

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class LockColors extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "LockscreenColors";

    private static final String LOCKSCREEN_PHONE_ICON_COLOR = "lockscreen_phone_icon_color";
    private static final String LOCKSCREEN_CAMERA_ICON_COLOR = "lockscreen_camera_icon_color";
    private static final String LOCKSCREEN_INDICATION_TEXT_COLOR = "lockscreen_indication_text_color";
    private static final String LOCKSCREEN_CLOCK_COLOR = "lockscreen_clock_color";
    private static final String LOCKSCREEN_CLOCK_DATE_COLOR = "lockscreen_clock_date_color";
    private static final String LOCKSCREEN_OWNER_INFO_COLOR = "lockscreen_owner_info_color";
    private static final String LOCKSCREEN_WEATHER_TEMP_COLOR = "lockscreen_weather_temp_color";
    private static final String LOCKSCREEN_WEATHER_CITY_COLOR = "lockscreen_weather_city_color";
    private static final String LOCKSCREEN_WEATHER_ICON_COLOR = "lockscreen_weather_icon_color";

    static final int DEFAULT = 0xffffffff;
    static final int TRANSPARENT = 0x99FFFFFF;

    private static final int MENU_RESET = Menu.FIRST;

    private ColorPickerPreference mLockscreenPhoneColorPicker;
    private ColorPickerPreference mLockscreenCameraColorPicker;
    private ColorPickerPreference mLockscreenIndicationTextColorPicker;
    private ColorPickerPreference mLockscreenClockColorPicker;
    private ColorPickerPreference mLockscreenClockDateColorPicker;
    private ColorPickerPreference mLockscreenOwnerInfoColorPicker;
    private ColorPickerPreference mWeatherRightTextColorPicker;
    private ColorPickerPreference mWeatherLeftTextColorPicker;
    private ColorPickerPreference mWeatherIconColorPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_colors);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mLockscreenPhoneColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_PHONE_ICON_COLOR);
        mLockscreenPhoneColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_PHONE_ICON_COLOR, TRANSPARENT);
        hexColor = String.format("#%08x", (0x99FFFFFF & intColor));
        mLockscreenPhoneColorPicker.setSummary(hexColor);
        mLockscreenPhoneColorPicker.setNewPreviewColor(intColor);

        mLockscreenCameraColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CAMERA_ICON_COLOR);
        mLockscreenCameraColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CAMERA_ICON_COLOR, TRANSPARENT);
        hexColor = String.format("#%08x", (0x99FFFFFF & intColor));
        mLockscreenCameraColorPicker.setSummary(hexColor);
        mLockscreenCameraColorPicker.setNewPreviewColor(intColor);

        mLockscreenIndicationTextColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_INDICATION_TEXT_COLOR);
        mLockscreenIndicationTextColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenIndicationTextColorPicker.setSummary(hexColor);
        mLockscreenIndicationTextColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_COLOR);
        mLockscreenClockColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockColorPicker.setSummary(hexColor);
        mLockscreenClockColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockDateColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_DATE_COLOR);
        mLockscreenClockDateColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockDateColorPicker.setSummary(hexColor);
        mLockscreenClockDateColorPicker.setNewPreviewColor(intColor);

        mLockscreenOwnerInfoColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_OWNER_INFO_COLOR);
        mLockscreenOwnerInfoColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
        		   Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenOwnerInfoColorPicker.setSummary(hexColor);
        mLockscreenOwnerInfoColorPicker.setNewPreviewColor(intColor);

       mWeatherRightTextColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_WEATHER_TEMP_COLOR);
       mWeatherRightTextColorPicker.setOnPreferenceChangeListener(this);
       intColor = Settings.System.getInt(getContentResolver(),
                   Settings.System.LOCK_SCREEN_WEATHER_TEMP_COLOR, DEFAULT);
       hexColor = String.format("#%08x", (0xffffffff & intColor));
       mWeatherRightTextColorPicker.setSummary(hexColor);
       mWeatherRightTextColorPicker.setNewPreviewColor(intColor);

       mWeatherLeftTextColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_WEATHER_CITY_COLOR);
       mWeatherLeftTextColorPicker.setOnPreferenceChangeListener(this);
       intColor = Settings.System.getInt(getContentResolver(),
                   Settings.System.LOCK_SCREEN_WEATHER_CITY_COLOR, DEFAULT);
       hexColor = String.format("#%08x", (0xffffffff & intColor));
       mWeatherLeftTextColorPicker.setSummary(hexColor);
       mWeatherLeftTextColorPicker.setNewPreviewColor(intColor);

       mWeatherIconColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_WEATHER_ICON_COLOR);
       mWeatherIconColorPicker.setOnPreferenceChangeListener(this);
       intColor = Settings.System.getInt(getContentResolver(),
                   Settings.System.LOCK_SCREEN_WEATHER_ICON_COLOR, TRANSPARENT);
       hexColor = String.format("#%08x", (0x99FFFFFF & intColor));
       mWeatherIconColorPicker.setSummary(hexColor);
       mWeatherIconColorPicker.setNewPreviewColor(intColor);

       setHasOptionsMenu(true);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
                ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenCameraColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CAMERA_ICON_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenPhoneColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_PHONE_ICON_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenIndicationTextColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockDateColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, intHex);
            return true;
         } else if (preference == mLockscreenOwnerInfoColorPicker) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, intHex);
                return true;
        } else if (preference == mWeatherRightTextColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCK_SCREEN_WEATHER_TEMP_COLOR, intHex);
            return true;
        } else if (preference == mWeatherLeftTextColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCK_SCREEN_WEATHER_CITY_COLOR, intHex);
            return true;
        } else if (preference == mWeatherIconColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCK_SCREEN_WEATHER_ICON_COLOR, intHex);
            return true;
        }
         return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset_alpha)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.lockscreen_colors_reset_title);
        alertDialog.setMessage(R.string.lockscreen_colors_reset_message);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
	ContentResolver resolver = getActivity().getContentResolver();
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_PHONE_ICON_COLOR, TRANSPARENT);
        mLockscreenPhoneColorPicker.setNewPreviewColor(TRANSPARENT);
        mLockscreenPhoneColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_CAMERA_ICON_COLOR, TRANSPARENT);
        mLockscreenCameraColorPicker.setNewPreviewColor(TRANSPARENT);
        mLockscreenCameraColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR, DEFAULT);
        mLockscreenIndicationTextColorPicker.setNewPreviewColor(DEFAULT);
        mLockscreenIndicationTextColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT);
        mLockscreenClockColorPicker.setNewPreviewColor(DEFAULT);
        mLockscreenClockColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT);
        mLockscreenClockDateColorPicker.setNewPreviewColor(DEFAULT);
        mLockscreenClockDateColorPicker.setSummary(R.string.default_string);
	Settings.System.putInt(resolver,
                 Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT);
        mLockscreenOwnerInfoColorPicker.setNewPreviewColor(DEFAULT);
        mLockscreenOwnerInfoColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCK_SCREEN_WEATHER_TEMP_COLOR, DEFAULT);
        mWeatherRightTextColorPicker.setNewPreviewColor(DEFAULT);
        mWeatherRightTextColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCK_SCREEN_WEATHER_CITY_COLOR, DEFAULT);
        mWeatherLeftTextColorPicker.setNewPreviewColor(DEFAULT);
        mWeatherLeftTextColorPicker.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.LOCK_SCREEN_WEATHER_ICON_COLOR, DEFAULT);
        mWeatherIconColorPicker.setNewPreviewColor(DEFAULT);
        mWeatherIconColorPicker.setSummary(R.string.default_string);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    sir.xmlResId = R.xml.lock_colors;
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
