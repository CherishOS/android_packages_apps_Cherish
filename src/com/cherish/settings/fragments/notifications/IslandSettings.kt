package com.cherish.settings.fragments.notifications

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.SettingsPreferenceFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable
import com.android.internal.util.android.VibrationUtils

@SearchIndexable
class IslandSettings : SettingsPreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.island_settings)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != null) {
            VibrationUtils.triggerVibration(context, 3)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.CHERISH_SETTINGS
    }

    companion object {
        const val TAG = "IslandSettings"

        /** For search */
        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = object : BaseSearchIndexProvider(R.xml.island_settings) {
            override fun getNonIndexableKeys(context: Context): List<String> {
                return super.getNonIndexableKeys(context)
            }
        }
    }
}
