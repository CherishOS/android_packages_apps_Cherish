/*
 * Copyright (C) 2020 CherishOS
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
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.ListPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class CustomCarrierLabel extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "CarrierLabel";
    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String STATUS_BAR_CARRIER_COLOR = "status_bar_carrier_color";
    private static final String STATUS_BAR_CARRIER_FONT_SIZE  = "status_bar_carrier_font_size";
    private static final String CARRIER_FONT_STYLE  = "status_bar_carrier_font_style";

    static final int DEFAULT_STATUS_CARRIER_COLOR = 0xffffffff;

    private PreferenceScreen mCustomCarrierLabel;
    private String mCustomCarrierLabelText;
    private ColorPickerPreference mCarrierColorPicker;
    private CustomSeekBarPreference mStatusBarCarrierSize;
    private ListPreference mCarrierFontStyle;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.custom_carrier_label);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        // custom carrier label
        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mCarrierColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_CARRIER_COLOR);
            mCarrierColorPicker.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_CARRIER_COLOR, DEFAULT_STATUS_CARRIER_COLOR);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCarrierColorPicker.setSummary(hexColor);
            mCarrierColorPicker.setNewPreviewColor(intColor);

        mStatusBarCarrierSize = (CustomSeekBarPreference) findPreference(STATUS_BAR_CARRIER_FONT_SIZE);
        int StatusBarCarrierSize = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, 14);
        mStatusBarCarrierSize.setValue(StatusBarCarrierSize / 1);
        mStatusBarCarrierSize.setOnPreferenceChangeListener(this);

        mCarrierFontStyle = (ListPreference) findPreference(CARRIER_FONT_STYLE);
        int showCarrierFont = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, 0);
        mCarrierFontStyle.setValue(String.valueOf(showCarrierFont));
        mCarrierFontStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

     public boolean onPreferenceChange(Preference preference, Object newValue) {
 		ContentResolver resolver = getActivity().getContentResolver();

         if (preference == mCarrierColorPicker) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
                return true;
        }  else if (preference == mStatusBarCarrierSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, width);
            return true;
        }  else if (preference == mCarrierFontStyle) {
            int showCarrierFont = Integer.valueOf((String) newValue);
            int index = mCarrierFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.
                STATUS_BAR_CARRIER_FONT_STYLE, showCarrierFont);
            mCarrierFontStyle.setSummary(mCarrierFontStyle.getEntries()[index]);
            return true;
        }
         return false;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
        if (preference.getKey().equals(CUSTOM_CARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);
            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
            return true;
        }
        return false;
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }
}
