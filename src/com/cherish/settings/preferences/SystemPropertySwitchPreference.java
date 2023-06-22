/*
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2017 AICP
 * Copyright (C) 2022 Project Kaleidoscope
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

 package com.cherish.settings.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.SwitchPreference;

public class SystemPropertySwitchPreference extends SwitchPreference {

    public SystemPropertySwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPreferenceDataStore(new SystemPropertiesStore());
    }

    public SystemPropertySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPreferenceDataStore(new SystemPropertiesStore());
    }

    public SystemPropertySwitchPreference(Context context) {
        super(context);
        setPreferenceDataStore(new SystemPropertiesStore());
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        // This is what default TwoStatePreference implementation is doing without respecting
        // real default value:
        //setChecked(restoreValue ? getPersistedBoolean(mChecked)
        //        : (Boolean) defaultValue);
        // Instead, we better do
        setChecked(restoreValue ? getPersistedBoolean((Boolean) defaultValue)
                : (Boolean) defaultValue);
    }
}
