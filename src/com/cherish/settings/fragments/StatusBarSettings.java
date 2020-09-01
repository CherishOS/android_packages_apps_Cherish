package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import com.cherish.settings.preferences.SystemSettingMasterSwitchPreference;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_BATTERY_TEXT_CHARGING = "status_bar_battery_text_charging";
    private static final String STATUS_BAR_BATTERY_CHARGING_BOLT = "status_bar_battery_charging_bolt";
    private static final String BATTERY_PERCENTAGE_HIDDEN = "0";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";

    private static final int BATTERY_STYLE_Q = 0;
    private static final int BATTERY_STYLE_DOTTED_CIRCLE = 1;
    private static final int BATTERY_STYLE_CIRCLE = 2;
    private static final int BATTERY_STYLE_TEXT = 3;
    private static final int BATTERY_STYLE_HIDDEN = 4;

    private SystemSettingMasterSwitchPreference mStatusBarClockShow;
    private ListPreference mBatteryPercent;
    private ListPreference mBatteryStyle;
    private SwitchPreference mBatteryCharging;
	private CustomSeekBarPreference mThreshold;
    private SystemSettingSwitchPreference mNetMonitor;
    private SwitchPreference mBatteryBolt;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_statusbar);
		
		ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarClockShow = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CLOCK);
        mStatusBarClockShow.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarClockShow.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);
        mBatteryCharging = (SwitchPreference) findPreference(STATUS_BAR_BATTERY_TEXT_CHARGING);
        mBatteryCharging.setOnPreferenceChangeListener(this);

        mBatteryBolt = (SwitchPreference) findPreference(STATUS_BAR_BATTERY_CHARGING_BOLT);
        mBatteryBolt.setOnPreferenceChangeListener(this);

        mBatteryStyle = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        int batterystyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_Q);
        mBatteryStyle.setOnPreferenceChangeListener(this);

        updateBatteryOptions(batterystyle);
		
		boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingSwitchPreference) findPreference("network_traffic_state");
        mNetMonitor.setChecked(isNetMonitorEnabled);
        mNetMonitor.setOnPreferenceChangeListener(this);

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference("network_traffic_autohide_threshold");
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);
        mThreshold.setEnabled(isNetMonitorEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
         if (preference == mStatusBarClockShow) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
        }   else if (preference == mBatteryStyle) {
            int value = Integer.parseInt((String) objValue);
            updateBatteryOptions(value);
            return true;
		} else if (preference == mNetMonitor) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitor.setChecked(value);
            mThreshold.setEnabled(value);
            return true;
        } else if (preference == mThreshold) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBatteryCharging) {
            boolean enabled = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING,
                    enabled ? 1 : 0);
            updateBoltEnablement();
            return true;
        } else if (preference == mBatteryBolt) {
            boolean enabled = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_CHARGING_BOLT,
                    enabled ? 1 : 0);
            return true;
        }
		
        return false;
    }
	
	private void updateBatteryOptions(int batterystyle) {
        boolean enabled = batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN;
        if (batterystyle == BATTERY_STYLE_HIDDEN) {
            mBatteryPercent.setValue(BATTERY_PERCENTAGE_HIDDEN);
            mBatteryPercent.setSummary(mBatteryPercent.getEntry());
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        }
        mBatteryCharging.setEnabled(enabled);
        mBatteryPercent.setEnabled(enabled);
        updateBoltEnablement();
    }

    private void updateBoltEnablement() {
        int batteryStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_Q);
        boolean precentOnCharging = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING, 1) == 1;
        boolean textEnabled = batteryStyle == BATTERY_STYLE_TEXT ||
                (batteryStyle == BATTERY_STYLE_HIDDEN && precentOnCharging);
        mBatteryBolt.setEnabled(textEnabled);
		
	}

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

}

