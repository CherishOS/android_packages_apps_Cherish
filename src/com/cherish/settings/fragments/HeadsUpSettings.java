/*
 * Copyright (C) 2017 AospExtended ROM
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.cherish.settings.preferences.PackageListAdapter;
import com.cherish.settings.preferences.PackageListAdapter.PackageItem;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadsUpSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final int DIALOG_STOPLIST_APPS = 0;

    private PackageListAdapter mPackageAdapter;
    private PackageManager mPackageManager;
    private PreferenceGroup mStoplistPrefList;
    private Preference mAddStoplistPref;

    private String mStoplistPackageList;
    private Map<String, Package> mStoplistPackages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get launch-able applications
        addPreferencesFromResource(R.xml.heads_up_settings);
        mPackageManager = getPackageManager();
        mPackageAdapter = new PackageListAdapter(getActivity());

        mStoplistPrefList = (PreferenceGroup) findPreference("stoplist_applications");
        mStoplistPrefList.setOrderingAsAdded(false);

        mStoplistPackages = new HashMap<String, Package>();

        mAddStoplistPref = findPreference("add_stoplist_packages");

        mAddStoplistPref.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCustomApplicationPrefs();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public int getDialogMetricsCategory(int dialogId) {
        if (dialogId == DIALOG_STOPLIST_APPS) {
            return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
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
        switch (id) {
            case DIALOG_STOPLIST_APPS:
                final ListView list = new ListView(getActivity());
                list.setAdapter(mPackageAdapter);

                builder.setTitle(R.string.profile_choose_app);
                builder.setView(list);
                dialog = builder.create();

                list.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Add empty application definition, the user will be able to edit it later
                        PackageItem info = (PackageItem) parent.getItemAtPosition(position);
                        addCustomApplicationPref(info.packageName);
                        dialog.cancel();
                    }
                });
                break;
            default:
                dialog = null;
        }
        return dialog;
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
        if (mStoplistPrefList != null) {
            mStoplistPrefList.removeAll();

            for (Package pkg : mStoplistPackages.values()) {
                try {
                    Preference pref = createPreferenceFromInfo(pkg);
                    mStoplistPrefList.addPreference(pref);
                } catch (PackageManager.NameNotFoundException e) {
                    // Do nothing
                }
            }
        }

        // Keep these at the top
        mAddStoplistPref.setOrder(0);
        // Add 'add' options
        mStoplistPrefList.addPreference(mAddStoplistPref);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mAddStoplistPref) {
            showDialog(DIALOG_STOPLIST_APPS);
        } else {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_message)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeCustomApplicationPref(preference.getKey());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);

        builder.show();
        }
        return true;
    }

     private void addCustomApplicationPref(String packageName) {
        Package pkg = mStoplistPackages.get(packageName);
        if (pkg == null) {
            pkg = new Package(packageName);
            mStoplistPackages.put(packageName, pkg);
            savePackageList(false);
            refreshCustomApplicationPrefs();
        }
    }

    private Preference createPreferenceFromInfo(Package pkg)
            throws PackageManager.NameNotFoundException {
        PackageInfo info = mPackageManager.getPackageInfo(pkg.name,
                PackageManager.GET_META_DATA);
        Preference pref =
                new Preference(getActivity());

        pref.setKey(pkg.name);
        pref.setTitle(info.applicationInfo.loadLabel(mPackageManager));
        pref.setIcon(info.applicationInfo.loadIcon(mPackageManager));
        pref.setPersistent(false);
        pref.setOnPreferenceClickListener(this);
        return pref;
    }

    private void removeCustomApplicationPref(String packageName) {
        if (mStoplistPackages.remove(packageName) != null) {
            savePackageList(false);
            refreshCustomApplicationPrefs();
        }
    }

    private boolean parsePackageList() {

        final String stoplistString = Settings.System.getString(getContentResolver(),
                Settings.System.HEADS_UP_STOPLIST_VALUES);

        if (TextUtils.equals(mStoplistPackageList, stoplistString)) {
            return false;
        }
            mStoplistPackageList = stoplistString;
            mStoplistPackages.clear();

        if (stoplistString != null) {
            final String[] array = TextUtils.split(stoplistString, "\\|");
            for (String item : array) {
                if (TextUtils.isEmpty(item)) {
                    continue;
                }
                Package pkg = Package.fromString(item);
                if (pkg != null) {
                    mStoplistPackages.put(pkg.name, pkg);
                }
            }
        }

        return true;
    }

     private void savePackageList(boolean preferencesUpdated) {
        List<String> settings = new ArrayList<String>();
        for (Package app : mStoplistPackages.values()) {
            settings.add(app.toString());
        }
        final String value = TextUtils.join("|", settings);
        if (preferencesUpdated) {
                mStoplistPackageList = value;
            }
        Settings.System.putString(getContentResolver(),
                Settings.System.HEADS_UP_STOPLIST_VALUES, value);
    }
}
