/*
 * Copyright (C) 2019-2021 The Evolution X Project
 * Copyright (C) 2019-2021 crDroid Android Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.cherish.settings.preferences.AppListPreference;
import com.cherish.settings.preferences.PackageListAdapter;
import com.cherish.settings.preferences.PackageListAdapter.PackageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class SensorBlockSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final int DIALOG_BLOCKED_APPS = 1;
    private static final String SENSOR_BLOCK = "sensor_block";
    private static final String SENSOR_BLOCK_FOOTER = "sensor_block_footer";

    private PackageListAdapter mPackageAdapter;
    private PackageManager mPackageManager;
    private PreferenceGroup mSensorBlockPrefList;
    private Preference mAddSensorBlockPref;
    private Preference mSleepMode;

    private String mBlockedPackageList;
    private Map<String, Package> mBlockedPackages;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get launch-able applications
        addPreferencesFromResource(R.xml.settings_sensor_block);

        findPreference(SENSOR_BLOCK_FOOTER).setTitle(R.string.add_sensor_block_package_summary);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        mPackageManager = getPackageManager();
        mPackageAdapter = new PackageListAdapter(getActivity());

        mSensorBlockPrefList = (PreferenceGroup) findPreference("sensor_block_applications");
        mSensorBlockPrefList.setOrderingAsAdded(false);

        mBlockedPackages = new HashMap<String, Package>();

        mAddSensorBlockPref = findPreference("add_sensor_block_packages");

        mAddSensorBlockPref.setOnPreferenceClickListener(this);

        mContext = getActivity().getApplicationContext();

        mSleepMode = findPreference("sleep_mode");
        updateSleepModeSummary();
    }

    private void updateSleepModeSummary() {
        if (mSleepMode == null) return;
        boolean enabled = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        String timeValue = Settings.Secure.getStringForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_TIME, UserHandle.USER_CURRENT);
        if (timeValue == null || timeValue.equals("")) timeValue = "20:00,07:00";
        String[] time = timeValue.split(",", 0);
        String outputFormat = DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "h:mm a";
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime sinceValue = LocalTime.parse(time[0], formatter);
        LocalTime tillValue = LocalTime.parse(time[1], formatter);
        String detail;
        switch (mode) {
            default:
            case 0:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_never
                        : R.string.night_display_summary_off_auto_mode_never);
                break;
            case 1:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_twilight
                        : R.string.night_display_summary_off_auto_mode_twilight);
                break;
            case 2:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                   detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
            case 3:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_twilight);
                }
                break;
            case 4:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_twilight);
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
        }
        String summary = getActivity().getString(enabled
                ? R.string.night_display_summary_on
                : R.string.night_display_summary_off, detail);
        mSleepMode.setSummary(summary);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCustomApplicationPrefs();
        updateSleepModeSummary();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateSleepModeSummary();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public int getDialogMetricsCategory(int dialogId) {
        if (dialogId == DIALOG_BLOCKED_APPS) {
            return MetricsEvent.CHERISH_SETTINGS;
        }
        return 0;
    }

    /**
     * Utility classes and supporting methods
     */
    @Override
    public Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Dialog dialog;
        final ListView list = new ListView(getActivity());
        list.setAdapter(mPackageAdapter);

        builder.setTitle(R.string.profile_choose_app);
        builder.setView(list);
        dialog = builder.create();

        switch (id) {
            case DIALOG_BLOCKED_APPS:
                list.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Add empty application definition, the user will be able to edit it later
                        PackageItem info = (PackageItem) parent.getItemAtPosition(position);
                        addCustomApplicationPref(info.packageName, mBlockedPackages);
                        dialog.cancel();
                    }
                });
        }
        return dialog;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.SENSOR_BLOCK, 0, UserHandle.USER_CURRENT);
        Settings.System.putString(resolver,
                Settings.System.SENSOR_BLOCKED_APP, null);
    }

    /**
     * Application class
     */
    private static class Package {
        public String name;
        /**
         * Stores all the application values in one call
         * @param name
         */
        public Package(String name) {
            this.name = name;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(name);
            return builder.toString();
        }

        public static Package fromString(String value) {
            if (TextUtils.isEmpty(value)) {
                return null;
            }

            try {
                Package item = new Package(value);
                return item;
            } catch (NumberFormatException e) {
                return null;
            }
        }

    };

    private void refreshCustomApplicationPrefs() {
        if (!parsePackageList()) {
            return;
        }

        // Add the Application Preferences
        if (mSensorBlockPrefList != null) {
            mSensorBlockPrefList.removeAll();

            for (Package pkg : mBlockedPackages.values()) {
                try {
                    Preference pref = createPreferenceFromInfo(pkg);
                    mSensorBlockPrefList.addPreference(pref);
                } catch (PackageManager.NameNotFoundException e) {
                    // Do nothing
                }
            }

            // Keep these at the top
            mAddSensorBlockPref.setOrder(0);
            // Add 'add' options
            mSensorBlockPrefList.addPreference(mAddSensorBlockPref);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mAddSensorBlockPref) {
            showDialog(DIALOG_BLOCKED_APPS);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_message)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (preference == mSensorBlockPrefList.findPreference(preference.getKey())) {
                                removeApplicationPref(preference.getKey(), mBlockedPackages);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);

            builder.show();
        }
        return true;
    }

    private void addCustomApplicationPref(String packageName, Map<String,Package> map) {
        Package pkg = map.get(packageName);
        if (pkg == null) {
            pkg = new Package(packageName);
            map.put(packageName, pkg);
            savePackageList(false, map);
            refreshCustomApplicationPrefs();
        }
    }

    private Preference createPreferenceFromInfo(Package pkg)
            throws PackageManager.NameNotFoundException {
        PackageInfo info = mPackageManager.getPackageInfo(pkg.name,
                PackageManager.GET_META_DATA);
        Preference pref =
                new AppListPreference(getActivity());

        pref.setKey(pkg.name);
        pref.setTitle(info.applicationInfo.loadLabel(mPackageManager));
        pref.setIcon(info.applicationInfo.loadIcon(mPackageManager));
        pref.setPersistent(false);
        pref.setOnPreferenceClickListener(this);
        return pref;
    }

    private void removeApplicationPref(String packageName, Map<String,Package> map) {
        if (map.remove(packageName) != null) {
            savePackageList(false, map);
            refreshCustomApplicationPrefs();
        }
    }

    private boolean parsePackageList() {
        boolean parsed = false;

        String sensorBlockString = Settings.System.getString(getContentResolver(),
                Settings.System.SENSOR_BLOCKED_APP);

        if (sensorBlockString != null &&
                !TextUtils.equals(mBlockedPackageList, sensorBlockString)) {
            mBlockedPackageList = sensorBlockString;
            mBlockedPackages.clear();
            parseAndAddToMap(sensorBlockString, mBlockedPackages);
            parsed = true;
        }

        return parsed;
    }

    private void parseAndAddToMap(String baseString, Map<String,Package> map) {
        if (baseString == null) {
            return;
        }

        final String[] array = TextUtils.split(baseString, "\\|");
        for (String item : array) {
            if (TextUtils.isEmpty(item)) {
                continue;
            }
            Package pkg = Package.fromString(item);
            map.put(pkg.name, pkg);
        }
    }

    private void savePackageList(boolean preferencesUpdated, Map<String,Package> map) {
        String setting = map == mBlockedPackages
                ? Settings.System.SENSOR_BLOCKED_APP
                : Settings.System.SENSOR_BLOCKED_APP_DUMMY;

        List<String> settings = new ArrayList<String>();
        for (Package app : map.values()) {
            settings.add(app.toString());
        }
        final String value = TextUtils.join("|", settings);
        if (preferencesUpdated) {
            if (TextUtils.equals(setting, Settings.System.SENSOR_BLOCKED_APP)) {
                mBlockedPackageList = value;
            }
        }
        Settings.System.putString(getContentResolver(),
                setting, value);
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.settings_sensor_block);
}
