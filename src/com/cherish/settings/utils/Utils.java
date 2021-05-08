/*
 * Copyright (C) 2016 The Pure Nexus Project
 * used for Cherish OS
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

package com.cherish.settings.utils;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.om.IOverlayManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.widget.Toast;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import com.android.settings.R;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;

public class Utils {

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename, state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
	
	public static boolean isBlurSupported() {
        boolean blurSupportedSysProp = SystemProperties
            .getBoolean("ro.surface_flinger.supports_background_blur", false);
        boolean blurDisabledSysProp = SystemProperties
            .getBoolean("persist.sys.sf.disable_blurs", false);
        return blurSupportedSysProp && !blurDisabledSysProp && ActivityManager.isHighEndGfx();
    }

}