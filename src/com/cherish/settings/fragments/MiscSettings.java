package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemProperties;
import android.os.UserHandle;
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
import android.widget.Toast;
import android.content.pm.PackageManager.NameNotFoundException;
import com.android.settings.SettingsPreferenceFragment;
import com.cherish.settings.preferences.SystemSettingMasterSwitchPreference;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SecureSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
			
	private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String KEY_STREAM_SPOOF = "use_stream_spoof";
    private static final String SYS_PHOTOS_SPOOF = "persist.sys.photo";
    private static final String SYS_STREAM_SPOOF = "persist.sys.stream";

    private SwitchPreference mPhotosSpoof;
    private SwitchPreference mStreamSpoof;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.cherish_settings_misc);
		
		final PreferenceScreen prefSet = getPreferenceScreen();

        final String usePhotosSpoof = SystemProperties.get(SYS_PHOTOS_SPOOF, "1");
        mPhotosSpoof = (SwitchPreference) findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked("1".equals(usePhotosSpoof));
        mPhotosSpoof.setOnPreferenceChangeListener(this);

        final String useStreamSpoof = SystemProperties.get(SYS_STREAM_SPOOF, "1");
        mStreamSpoof = (SwitchPreference) findPreference(KEY_STREAM_SPOOF);
        mStreamSpoof.setChecked("1".equals(useStreamSpoof));
        mStreamSpoof.setOnPreferenceChangeListener(this);
		
		Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mPhotosSpoof) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USE_PHOTOS_SPOOF, value ? 1 : 0);
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "1" : "0");
            Toast.makeText(getActivity(),
                    (R.string.photos_spoof_toast),
                    Toast.LENGTH_LONG).show();
            return true;
		} else if (preference == mStreamSpoof) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USE_STREAM_SPOOF, value ? 1 : 0);
            SystemProperties.set(SYS_STREAM_SPOOF, value ? "1" : "0");
            Toast.makeText(getActivity(),
                    (R.string.stream_spoof_toast),
                    Toast.LENGTH_LONG).show();
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

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.cherish_settings_misc;
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
