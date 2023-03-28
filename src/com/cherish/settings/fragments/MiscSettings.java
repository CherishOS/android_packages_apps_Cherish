package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import android.provider.Settings;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import android.content.pm.PackageManager.NameNotFoundException;
import com.android.settings.SettingsPreferenceFragment;
import com.cherish.settings.preferences.SystemSettingMasterSwitchPreference;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SecureSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import com.cherish.settings.fragments.SmartPixels;
import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";

    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";

	private static final String SMART_PIXELS = "smart_pixels";

    private SwitchPreference mPhotosSpoof;
	private Preference mSmartPixels;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.cherish_settings_misc);
		
		final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        mPhotosSpoof = (SwitchPreference) findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
        mPhotosSpoof.setOnPreferenceChangeListener(this);
		
		mSmartPixels = (Preference) prefScreen.findPreference(SMART_PIXELS);
           boolean mSmartPixelsSupported = getResources().getBoolean(
                 com.android.internal.R.bool.config_supportSmartPixels);
           if (!mSmartPixelsSupported)
                 prefScreen.removePreference(mSmartPixels);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    if (preference == mPhotosSpoof) {
        boolean value = (Boolean) objValue;
        SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
        return true;
    }
        return false;
    }
	

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
	
	/**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
     new BaseSearchIndexProvider(R.xml.cherish_settings_misc) {
         @Override
         public List<String> getNonIndexableKeys(Context context) {
             List<String> keys = super.getNonIndexableKeys(context);

             boolean mSmartPixelsSupported = context.getResources().getBoolean(
                     com.android.internal.R.bool.config_supportSmartPixels);
             if (!mSmartPixelsSupported)
                 keys.add(SMART_PIXELS);

             return keys;
         }
     };
}
