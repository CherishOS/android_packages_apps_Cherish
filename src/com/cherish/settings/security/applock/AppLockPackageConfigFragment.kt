/*
 * Copyright (C) 2022 FlamingoOS Project
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

package com.cherish.settings.security.applock

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle

import androidx.lifecycle.lifecycleScope

import com.android.settings.R
import com.android.settings.widget.EntityHeaderController
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference
import com.cherish.settings.fragments.CherishDashboardFragment

private val TAG = AppLockPackageConfigFragment::class.simpleName
private const val KEY_HEADER = "header_view"

class AppLockPackageConfigFragment : CherishDashboardFragment() {

    private lateinit var pm: PackageManager
    private lateinit var packageInfo: PackageInfo

    override fun onAttach(context: Context) {
        pm = context.packageManager
        packageInfo = arguments?.getParcelable(PACKAGE_INFO, PackageInfo::class.java)!!
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        val header = preferenceScreen.findPreference<LayoutPreference>(KEY_HEADER)
        EntityHeaderController.newInstance(
            requireActivity(),
            this,
            header?.findViewById(R.id.entity_header)
        ).setRecyclerView(listView, settingsLifecycle)
            .setPackageName(packageInfo.packageName)
            .setButtonActions(
                EntityHeaderController.ActionType.ACTION_NONE,
                EntityHeaderController.ActionType.ACTION_NONE
            )
            .bindHeaderButtons()
            .setLabel(getLabel(packageInfo))
            .setIcon(getIcon(packageInfo))
            .done(requireActivity(), false /* rebindActions */)
    }

    private fun getLabel(packageInfo: PackageInfo) =
        packageInfo.applicationInfo.loadLabel(pm).toString()

    private fun getIcon(packageInfo: PackageInfo) =
        packageInfo.applicationInfo.loadIcon(pm)

    override protected fun createPreferenceControllers(
        context: Context
    ) : List<AbstractPreferenceController> = listOf(
        AppLockPackageProtectionPC(context, packageInfo.packageName, lifecycleScope),
        AppLockNotificationRedactionPC(context, packageInfo.packageName, lifecycleScope),
    )

    override protected fun getPreferenceScreenResId() = R.xml.app_lock_package_config_settings

    override protected fun getLogTag() = TAG
}