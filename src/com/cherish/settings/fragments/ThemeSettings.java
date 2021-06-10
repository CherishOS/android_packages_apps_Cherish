package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;
import static android.os.UserHandle.USER_SYSTEM;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.UiModeManager;
import android.graphics.Color;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.ServiceManager;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.cherish.settings.preferences.CustomSeekBarPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.provider.Settings;
import com.android.settings.R;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settings.development.OverlayCategoryPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import android.provider.SearchIndexableResource;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import static android.os.UserHandle.USER_CURRENT;
import com.android.internal.util.cherish.CherishUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class ThemeSettings extends DashboardFragment implements OnPreferenceChangeListener {
			
	public static final String TAG = "ThemeSettings";
	
	private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";
    static final int DEFAULT_QS_PANEL_COLOR = 0xffffffff;
	static final int DEFAULT = 0xff1a73e8;
	private Context mContext;

    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;
	private ColorPickerPreference mMonetColor;
	
	@Override
    protected String getLogTag() {
        return TAG;
    }
	
	@Override
    protected int getPreferenceScreenResId() {
        return R.xml.cherish_settings_theme;
    }
	
	@Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {

        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        return controllers;
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
		final Resources res = getResources();
		mContext =  getActivity();
		
		mMonetColor = (ColorPickerPreference)findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);
        }

    public boolean isAvailable() {
        return true;
    }
	
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
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
                    sir.xmlResId = R.xml.cherish_settings_theme;
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