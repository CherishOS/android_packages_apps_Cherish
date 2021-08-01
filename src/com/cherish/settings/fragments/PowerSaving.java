/*
* Copyright (C) 2021 NezukoOS
*
* Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.ArrayList;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Context;
import android.content.ContentResolver;
import android.os.UserHandle;
import com.cherish.settings.preferences.SystemSettingListPreference;
import androidx.preference.*;
import android.provider.Settings;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.logging.MetricsLogger;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.os.Bundle;

public class PowerSaving extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String POWER_PROFILE = "power_profile_type";

    private SystemSettingListPreference mPowerProfile;

    public static final String[] POWER_MODE_PROFILES = {
        "advertise_is_enabled=false,datasaver_disabled=true,enable_night_mode=false,launch_boost_disabled=false,vibration_disabled=false,animation_disabled=false,soundtrigger_disabled=false,fullbackup_deferred=true,keyvaluebackup_deferred=true,firewall_disabled=false,gps_mode=4,adjust_brightness_disabled=true,adjust_brightness_factor=0.5,force_all_apps_standby=false,force_background_check=false,optional_sensors_disabled=true,aod_disabled=false,quick_doze_enabled=true",
        "null",
        "advertise_is_enabled=true,datasaver_disabled=true,enable_night_mode=true,launch_boost_disabled=false,vibration_disabled=false,animation_disabled=false,soundtrigger_disabled=false,fullbackup_deferred=true,keyvaluebackup_deferred=true,firewall_disabled=false,gps_mode=4,adjust_brightness_disabled=true,adjust_brightness_factor=0.75,force_all_apps_standby=false,force_background_check=false,optional_sensors_disabled=true,aod_disabled=true,quick_doze_enabled=true",
        "advertise_is_enabled=true,datasaver_disabled=false,enable_night_mode=true,launch_boost_disabled=true,vibration_disabled=true,animation_disabled=false,soundtrigger_disabled=true,fullbackup_deferred=true,keyvaluebackup_deferred=true,firewall_disabled=true,gps_mode=2,adjust_brightness_disabled=false,adjust_brightness_factor=0.6,force_all_apps_standby=true,force_background_check=true,optional_sensors_disabled=true,aod_disabled=true,quick_doze_enabled=true",
        "advertise_is_enabled=true,datasaver_disabled=false,enable_night_mode=true,launch_boost_disabled=true,vibration_disabled=true,animation_disabled=true,soundtrigger_disabled=true,fullbackup_deferred=true,keyvaluebackup_deferred=true,firewall_disabled=true,gps_mode=2,adjust_brightness_disabled=false,adjust_brightness_factor=0.5,force_all_apps_standby=true,force_background_check=true,optional_sensors_disabled=true,aod_disabled=true,quick_doze_enabled=true",
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.cherish_settings_powersave);
        ContentResolver resolver = getActivity().getContentResolver();
        mPowerProfile = (SystemSettingListPreference) findPreference(POWER_PROFILE);
        mPowerProfile.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPowerProfile){
            int profile = Integer.valueOf((String) newValue);
            for (int i = 0; i < POWER_MODE_PROFILES.length; i++) {
                if(profile == i){
                    if (profile != 1){
                 Settings.Global.putString(getActivity().getContentResolver(), Settings.Global.BATTERY_SAVER_CONSTANTS, POWER_MODE_PROFILES[i]);
                    } else{
                Settings.Global.putString(getActivity().getContentResolver(), Settings.Global.BATTERY_SAVER_CONSTANTS, null);
                    }
                }
            }
            return true;
        }
            return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
}
