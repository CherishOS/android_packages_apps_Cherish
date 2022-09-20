package com.cherish.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;
import static android.os.UserHandle.USER_SYSTEM;
import static android.os.UserHandle.USER_CURRENT;
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
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.android.internal.util.cherish.CherishUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.cherish.settings.preferences.SystemSettingListPreference;
import com.cherish.settings.preferences.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class ThemeSettings extends DashboardFragment implements OnPreferenceChangeListener {

    private static final String ALT_SETTINGS_LAYOUT = "alt_settings_layout";
    private static final String SETTINGS_DASHBOARD_STYLE = "settings_dashboard_style";
    private static final String USE_STOCK_LAYOUT = "use_stock_layout";
    private static final String DISABLE_USERCARD = "disable_usercard";
	private static final String QS_PANEL_STYLE  = "qs_panel_style";
			
	public static final String TAG = "ThemeSettings";
    static final int DEFAULT_QS_PANEL_COLOR = 0xffffffff;
	static final int DEFAULT = 0xff1a73e8;
	private Context mContext;

    private Handler mHandler;
    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;
    private SystemSettingListPreference mQsStyle;
    private UiModeManager mUiModeManager;
    private SystemSettingListPreference mSettingsDashBoardStyle;
    private SystemSettingSwitchPreference mAltSettingsLayout;
    private SystemSettingSwitchPreference mUseStockLayout;
    private SystemSettingSwitchPreference mDisableUserCard; 
	
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

        mSettingsDashBoardStyle = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_STYLE);
        mSettingsDashBoardStyle.setOnPreferenceChangeListener(this);
        mAltSettingsLayout = (SystemSettingSwitchPreference) findPreference(ALT_SETTINGS_LAYOUT);
        mAltSettingsLayout.setOnPreferenceChangeListener(this);
        mUseStockLayout = (SystemSettingSwitchPreference) findPreference(USE_STOCK_LAYOUT);
        mUseStockLayout.setOnPreferenceChangeListener(this);
        mDisableUserCard = (SystemSettingSwitchPreference) findPreference(DISABLE_USERCARD);
        mDisableUserCard.setOnPreferenceChangeListener(this);
		
		mOverlayService = IOverlayManager.Stub
        .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        mQsStyle = (SystemSettingListPreference) findPreference(QS_PANEL_STYLE);
        mCustomSettingsObserver.observe();
        }

    public boolean isAvailable() {
        return true;
    }
	
	private CustomSettingsObserver mCustomSettingsObserver = new CustomSettingsObserver(mHandler);
    private class CustomSettingsObserver extends ContentObserver {

        CustomSettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            Context mContext = getContext();
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.QS_PANEL_STYLE),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.QS_PANEL_STYLE))) {
                updateQsStyle();
            }
        }
    }
	
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSettingsDashBoardStyle) {
            CherishUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mAltSettingsLayout) {
            CherishUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mUseStockLayout) {
            CherishUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mDisableUserCard) {
            CherishUtils.showSettingsRestartDialog(getContext());
            return true;
		} else if (preference == mQsStyle) {
            mCustomSettingsObserver.observe();
            return true;
        }
        return false;
    }
	
	private void updateQsStyle() {
        ContentResolver resolver = getActivity().getContentResolver();

        int qsPanelStyle = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.QS_PANEL_STYLE , 0, UserHandle.USER_CURRENT);

        if (qsPanelStyle == 0) {
            setDefaultStyle(mOverlayService);
        } else if (qsPanelStyle == 1) {
            setQsStyle(mOverlayService, "com.android.system.qs.outline");
        } else if (qsPanelStyle == 2 || qsPanelStyle == 3) {
            setQsStyle(mOverlayService, "com.android.system.qs.twotoneaccent");
        }
    }

    public static void setDefaultStyle(IOverlayManager overlayManager) {
        for (int i = 0; i < QS_STYLES.length; i++) {
            String qsStyles = QS_STYLES[i];
            try {
                overlayManager.setEnabled(qsStyles, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setQsStyle(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < QS_STYLES.length; i++) {
                String qsStyles = QS_STYLES[i];
                try {
                    overlayManager.setEnabled(qsStyles, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static final String[] QS_STYLES = {
        "com.android.system.qs.outline",
        "com.android.system.qs.twotoneaccent"
    };

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