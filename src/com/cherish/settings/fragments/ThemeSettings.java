package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import static android.os.UserHandle.USER_SYSTEM;
import android.app.UiModeManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.om.IOverlayManager;
import android.os.RemoteException;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.android.internal.util.cherish.ThemesUtils;
import com.android.internal.util.cherish.CherishUtils;

public class ThemeSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
			
			private static final String PREF_THEME_SWITCH = "theme_switch";

    private UiModeManager mUiModeManager;
	private IOverlayManager mOverlayService;
    private ListPreference mThemeSwitch;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.cherish_settings_theme);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
		mUiModeManager = getContext().getSystemService(UiModeManager.class);
		setupThemeSwitchPref();
        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
           if (preference == mThemeSwitch) {
            String theme_switch = (String) objValue;
            final Context context = getContext();
            switch (theme_switch) {
                case "1":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.CHOCO_X);
					handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.PITCH_BLACK);
                    break;
                case "2":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    break;
                case "3":
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    break;
                case "4":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    break;
                case "5":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    break;
                case "6":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    break;
            }
            try {
                 mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
        }
        return true;
    }
	
	private void setupThemeSwitchPref() {
        mThemeSwitch = (ListPreference) findPreference(PREF_THEME_SWITCH);
        mThemeSwitch.setOnPreferenceChangeListener(this);
        if (CherishUtils.isThemeEnabled("com.android.theme.pitchblack.system")) {
            mThemeSwitch.setValue("6");
        } else if (CherishUtils.isThemeEnabled("com.android.theme.chocox.system")) {
            mThemeSwitch.setValue("5");
        } else if (CherishUtils.isThemeEnabled("com.android.theme.bakedgreen.system")) {
            mThemeSwitch.setValue("4");
        } else if (CherishUtils.isThemeEnabled("com.android.theme.solarizeddark.system")) {
            mThemeSwitch.setValue("3");
        } else if (mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            mThemeSwitch.setValue("2");
        } else {
            mThemeSwitch.setValue("1");
        }
    }

    private void handleBackgrounds(Boolean state, Context context, int mode, String[] overlays) {
        if (context != null) {
            Objects.requireNonNull(context.getSystemService(UiModeManager.class))
                    .setNightMode(mode);
        }
        for (int i = 0; i < overlays.length; i++) {
            String background = overlays[i];
            try {
                mOverlayService.setEnabled(background, state, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }

}