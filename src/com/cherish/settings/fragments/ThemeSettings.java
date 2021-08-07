package com.cherish.settings.fragments;

import static com.cherish.settings.utils.Utils.handleOverlays;

import com.android.internal.logging.nano.MetricsProto;
import static android.os.UserHandle.USER_SYSTEM;
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
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settings.display.FontPickerPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.provider.Settings;
import com.android.settings.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import android.provider.SearchIndexableResource;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.cherish.settings.display.QsTileStylePreferenceController;

import com.android.internal.util.cherish.ThemesUtils;
import com.android.internal.util.cherish.CherishUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class ThemeSettings extends DashboardFragment implements OnPreferenceChangeListener {
			
	public static final String TAG = "ThemeSettings";
    private static final String BRIGHTNESS_SLIDER_STYLE = "brightness_slider_style";
    private static final String SYSTEM_SLIDER_STYLE = "system_slider_style";
    private static final String UI_STYLE = "ui_style";
    private static final String PREF_PANEL_BG = "panel_bg";
    private static final String PREF_THEME_SWITCH = "theme_switch";
    private static final String QS_HEADER_STYLE = "qs_header_style";
    private static final String QS_TILE_STYLE = "qs_tile_style";
    private static final String ACCENT_COLOR = "accent_color";
    private static final String ACCENT_COLOR_PROP = "persist.sys.theme.accentcolor";
    private static final String GRADIENT_COLOR = "gradient_color";
    private static final String GRADIENT_COLOR_PROP = "persist.sys.theme.gradientcolor";
	private static final String PREF_NB_COLOR = "navbar_color";
    static final int DEFAULT_QS_PANEL_COLOR = 0xffffffff;
	static final int DEFAULT = 0xff1a73e8;
	private static final String QS_PANEL_COLOR = "qs_panel_color";
	private static final String SWITCH_STYLE = "switch_style";
	private static final String HIDE_NOTCH = "display_hide_notch";
	
    private SystemSettingListPreference mSwitchStyle;
    private ColorPickerPreference mQsPanelColor;

    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;
    private ColorPickerPreference mThemeColor;
    private ColorPickerPreference mGradientColor;
    private ListPreference mThemeSwitch;
    private ListPreference mBrightnessSliderStyle;
    private ListPreference mSystemSliderStyle;
    private ListPreference mUIStyle;
    private ListPreference mPanelBg;
    private ListPreference mQsHeaderStyle;
    private ListPreference mQsTileStyle;
	private ListPreference mGesbar;
	private SystemSettingSwitchPreference mHideNotch;
	
	private IntentFilter mIntentFilter;
    private static FontPickerPreferenceController mFontPickerPreference;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.android.server.ACTION_FONT_CHANGED")) {
                mFontPickerPreference.stopProgress();
            }
        }
    };
	
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
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack.android"));
		controllers.add(new OverlayCategoryPreferenceController(context,
				"android.theme.customization.statusbar_height"));
		controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.signal_icon"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.wifi_icon"));
        controllers.add(new QsTileStylePreferenceController(context));
		controllers.add(mFontPickerPreference = new FontPickerPreferenceController(context, lifecycle));
        return controllers;
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
		final Resources res = getResources();
		
		mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.android.server.ACTION_FONT_CHANGED");
		
		mHideNotch = (SystemSettingSwitchPreference) prefScreen.findPreference(HIDE_NOTCH);
        boolean mHideNotchSupported = res.getBoolean(
                com.android.internal.R.bool.config_showHideNotchSettings);
        if (!mHideNotchSupported) {
            prefScreen.removePreference(mHideNotch);
        }
		
	mSwitchStyle = (SystemSettingListPreference)findPreference(SWITCH_STYLE);
        int switchStyle = Settings.System.getInt(resolver,Settings.System.SWITCH_STYLE, 2);
        int switchIndex = mSwitchStyle.findIndexOfValue(String.valueOf(switchStyle));
        mSwitchStyle.setValueIndex(switchIndex >= 0 ? switchIndex : 0);
        mSwitchStyle.setSummary(mSwitchStyle.getEntry());
        mSwitchStyle.setOnPreferenceChangeListener(this);

        mUIStyle = (ListPreference) findPreference(UI_STYLE);
        int UIStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.UI_STYLE, 0);
        int UIStyleValue = getOverlayPosition(ThemesUtils.UI_THEMES);
        if (UIStyleValue != 0) {
            mUIStyle.setValue(String.valueOf(UIStyle));
        }
        mUIStyle.setSummary(mUIStyle.getEntry());
        mUIStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference == mUIStyle) {
                    String value = (String) newValue;
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.UI_STYLE, Integer.valueOf(value));
                    int valueIndex = mUIStyle.findIndexOfValue(value);
                    mUIStyle.setSummary(mUIStyle.getEntries()[valueIndex]);
                    String overlayName = getOverlayName(ThemesUtils.UI_THEMES);
                    if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayService);
                    }
                    if (valueIndex > 0) {
                        handleOverlays(ThemesUtils.UI_THEMES[valueIndex],
                                true, mOverlayService);
                    }
                    return true;
                }
                return false;
            }
       });

        mBrightnessSliderStyle = (ListPreference) findPreference(BRIGHTNESS_SLIDER_STYLE);
        int BrightnessSliderStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.BRIGHTNESS_SLIDER_STYLE, 0);
        int BrightnessSliderStyleValue = getOverlayPosition(ThemesUtils.BRIGHTNESS_SLIDER_THEMES);
        if (BrightnessSliderStyleValue != 0) {
            mBrightnessSliderStyle.setValue(String.valueOf(BrightnessSliderStyle));
        }
        mBrightnessSliderStyle.setSummary(mBrightnessSliderStyle.getEntry());
        mBrightnessSliderStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference == mBrightnessSliderStyle) {
                    String value = (String) newValue;
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.BRIGHTNESS_SLIDER_STYLE, Integer.valueOf(value));
                    int valueIndex = mBrightnessSliderStyle.findIndexOfValue(value);
                    mBrightnessSliderStyle.setSummary(mBrightnessSliderStyle.getEntries()[valueIndex]);
                    String overlayName = getOverlayName(ThemesUtils.BRIGHTNESS_SLIDER_THEMES);
                    if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayService);
                    }
                    if (valueIndex > 0) {
                        handleOverlays(ThemesUtils.BRIGHTNESS_SLIDER_THEMES[valueIndex],
                                true, mOverlayService);
                    }
                    return true;
                }
                return false;
            }
       });

       mPanelBg = (ListPreference) findPreference(PREF_PANEL_BG);
        int mPanelValue = getOverlayPosition(ThemesUtils.PANEL_BG_STYLE);
        if (mPanelValue != -1) {
                mPanelBg.setValue(String.valueOf(mPanelValue + 2));
        } else {
                mPanelBg.setValue("1");
              }
        mPanelBg.setSummary(mPanelBg.getEntry());
        mPanelBg.setOnPreferenceChangeListener(this);

        mQsHeaderStyle = (ListPreference)findPreference(QS_HEADER_STYLE);
        int qsHeaderStyle = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_STYLE, 0);
        int qsvalueIndex = mQsHeaderStyle.findIndexOfValue(String.valueOf(qsHeaderStyle));
        mQsHeaderStyle.setValueIndex(qsvalueIndex >= 0 ? qsvalueIndex : 0);
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);

        mQsTileStyle = (ListPreference)findPreference(QS_TILE_STYLE);
        int qsTileStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_STYLE, 0, UserHandle.USER_CURRENT);
        int valueIndex = mQsTileStyle.findIndexOfValue(String.valueOf(qsTileStyle));
        mQsTileStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mQsTileStyle.setSummary(mQsTileStyle.getEntry());
        mQsTileStyle.setOnPreferenceChangeListener(this);
		
		mQsPanelColor = (ColorPickerPreference)findPreference(QS_PANEL_COLOR);
        mQsPanelColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_BG_COLOR, DEFAULT_QS_PANEL_COLOR, UserHandle.USER_CURRENT);
        String hexColor = String.format("#%08x", (0xFFFFFFFF & intColor));
        mQsPanelColor.setSummary(hexColor);
        mQsPanelColor.setNewPreviewColor(intColor);

        mUiModeManager = getContext().getSystemService(UiModeManager.class);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        setupAccentPref();
        setSystemSliderPref();
        setupGradientPref();
		setupNavbarSwitchPref();
        }
        
    public String getPreferenceKey() {
        return QS_PANEL_COLOR;
    }

    public boolean isAvailable() {
        return true;
    }
	
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
         if (preference == mThemeColor) {
            int color = (Integer) objValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & color));
            SystemProperties.set(ACCENT_COLOR_PROP, hexColor);
            try {
                 mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
        } else if (preference == mGradientColor) {
            int color = (Integer) objValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & color));
            SystemProperties.set(GRADIENT_COLOR_PROP, hexColor);
            try {
                 mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
         } else if (preference == mPanelBg) {
                String panelbg = (String) objValue;
                int panelBgValue = Integer.parseInt(panelbg);
                mPanelBg.setValue(String.valueOf(panelBgValue));
                String overlayName = getOverlayName(ThemesUtils.PANEL_BG_STYLE);
                    if (overlayName != null) {
                        handleOverlays(overlayName, false, mOverlayService);
                    }
                    if (panelBgValue > 1) {
                        CherishUtils.showSystemUiRestartDialog(getContext());
                        handleOverlays(ThemesUtils.PANEL_BG_STYLE[panelBgValue -2],
                                true, mOverlayService);
    
                }
                mPanelBg.setSummary(mPanelBg.getEntry());
            } else if (preference == mQsHeaderStyle) {
                String value = (String) objValue;
                Settings.System.putInt(resolver,
                    Settings.System.QS_HEADER_STYLE, Integer.valueOf(value));
                int newIndex = mQsHeaderStyle.findIndexOfValue(value);
                mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntries()[newIndex]);
			}else if (preference == mSwitchStyle) {
            String value = (String) objValue;
            Settings.System.putInt(resolver, Settings.System.SWITCH_STYLE, Integer.valueOf(value));
            int valueIndex = mSwitchStyle.findIndexOfValue(value);
            mSwitchStyle.setSummary(mSwitchStyle.getEntries()[valueIndex]);
        } else if (preference == mSystemSliderStyle) {
            String slider_style = (String) objValue;
            final Context context = getContext();
            switch (slider_style) {
                case "1":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, false, mOverlayService);
                   break;
                case "2":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, true, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, false, mOverlayService);
                   break;
                case "3":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, true, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, false, mOverlayService);
                   break;
                case "4":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, true, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, false, mOverlayService);
                   break;
                case "5":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, true, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, false, mOverlayService);
                   break;
                case "6":
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_DANIEL, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEMINII, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUND, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMEROUNDSTROKE, false, mOverlayService);
                    handleOverlays(ThemesUtils.SYSTEM_SLIDER_MEMESTROKE, true, mOverlayService);
                   break;
            }
        } else if (preference == mQsTileStyle) {
            int qsTileStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_STYLE, qsTileStyleValue, UserHandle.USER_CURRENT);
            mQsTileStyle.setSummary(mQsTileStyle.getEntries()[qsTileStyleValue]);
		} else if (preference == mQsPanelColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_PANEL_BG_COLOR, intHex, UserHandle.USER_CURRENT);
		} else if (preference == mGesbar){
            String nbSwitch = (String) objValue;
            final Context context = getContext();
            switch (nbSwitch) {
                case "1":
                handleOverlays(ThemesUtils.NAVBAR_COLOR_ORCD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_OPRD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_PURP, false, mOverlayService);
                break;
                case "2":
                handleOverlays(ThemesUtils.NAVBAR_COLOR_ORCD, true, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_OPRD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_PURP, false, mOverlayService);
                break;
                case "3":
                handleOverlays(ThemesUtils.NAVBAR_COLOR_ORCD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_OPRD, true, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_PURP, false, mOverlayService);
                break;
                case "4":
                handleOverlays(ThemesUtils.NAVBAR_COLOR_ORCD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_OPRD, false, mOverlayService);
                handleOverlays(ThemesUtils.NAVBAR_COLOR_PURP, true, mOverlayService);
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

    private void setSystemSliderPref() {
        mSystemSliderStyle = (ListPreference) findPreference(SYSTEM_SLIDER_STYLE);
        mSystemSliderStyle.setOnPreferenceChangeListener(this);
        if (CherishUtils.isThemeEnabled("com.android.system.slider.memestroke")) {
            mSystemSliderStyle.setValue("6");
        } else if (CherishUtils.isThemeEnabled("com.android.system.slider.memeroundstroke")) {
            mSystemSliderStyle.setValue("5");
        } else if (CherishUtils.isThemeEnabled("com.android.system.slider.memeround")) {
            mSystemSliderStyle.setValue("4");
        } else if (CherishUtils.isThemeEnabled("com.android.system.slider.mememini")) {
            mSystemSliderStyle.setValue("3");
        } else if (CherishUtils.isThemeEnabled("com.android.system.slider.daniel")) {
            mSystemSliderStyle.setValue("2");
        } else {
            mSystemSliderStyle.setValue("1");
        }
    }


    private void setupAccentPref() {
        mThemeColor = (ColorPickerPreference) findPreference(ACCENT_COLOR);
        String colorVal = SystemProperties.get(ACCENT_COLOR_PROP, "-1");
        int color = "-1".equals(colorVal)
                ? DEFAULT
                : Color.parseColor("#" + colorVal);
        mThemeColor.setNewPreviewColor(color);
        mThemeColor.setOnPreferenceChangeListener(this);
    }

    private void setupGradientPref() {
        mGradientColor = (ColorPickerPreference) findPreference(GRADIENT_COLOR);
        String colorVal = SystemProperties.get(GRADIENT_COLOR_PROP, "-1");
        int color = "-1".equals(colorVal)
                ? DEFAULT
                : Color.parseColor("#" + colorVal);
        mGradientColor.setNewPreviewColor(color);
        mGradientColor.setOnPreferenceChangeListener(this);
    }
	
	private void setupNavbarSwitchPref() {
        mGesbar = (ListPreference) findPreference(PREF_NB_COLOR);
        mGesbar.setOnPreferenceChangeListener(this);
        if (CherishUtils.isNavbarColor("com.gnonymous.gvisualmod.pgm_purp")){
            mGesbar.setValue("4");
        } else if (CherishUtils.isNavbarColor("com.gnonymous.gvisualmod.pgm_oprd")){
            mGesbar.setValue("3");
        } else if (CherishUtils.isNavbarColor("com.gnonymous.gvisualmod.pgm_orcd")){
            mGesbar.setValue("2");
        }
        else{
            mGesbar.setValue("1");
        }
    }

    private String getOverlayName(String[] overlays) {
        String overlayName = null;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (CherishUtils.isThemeEnabled(overlay)) {
                overlayName = overlay;
            }
        }
        return overlayName;
    }

private int getOverlayPosition(String[] overlays) {
        int position = -1;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (CherishUtils.isThemeEnabled(overlay)) {
                position = i;
            }
        }
        return position;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS;
    }
	
	@Override
    public void onResume() {
        super.onResume();
        final Context context = getActivity();
        context.registerReceiver(mIntentReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        final Context context = getActivity();
        context.unregisterReceiver(mIntentReceiver);
        mFontPickerPreference.stopProgress();
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