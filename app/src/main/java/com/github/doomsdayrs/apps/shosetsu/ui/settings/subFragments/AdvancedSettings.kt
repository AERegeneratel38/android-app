package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.res.Resources
import android.database.SQLException
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.ui.settings.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.SpinnerSettingData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData


/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 * 13 / 07 / 2019
 */
class AdvancedSettings : SettingsSubController() {
	override val settings: ArrayList<SettingsItemData> by lazy {
		arrayListOf(
				spinnerSettingData(1) {
					title { R.string.theme }
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.application_themes)
					)
					onSpinnerItemSelected { adapterView, _, position, _ ->
						if (position in 0..1) {
							val delegate = (activity as AppCompatActivity).delegate
							when (position) {
								0 -> delegate.localNightMode = MODE_NIGHT_NO
								1 -> delegate.localNightMode = MODE_NIGHT_YES
							}
							val theme = delegate.localNightMode
							adapterView?.setSelection(if (
									theme == MODE_NIGHT_YES ||
									theme == MODE_NIGHT_FOLLOW_SYSTEM ||
									theme == MODE_NIGHT_AUTO_BATTERY
							) 1 else 0)
						}
					}
				},
				buttonSettingData(2) {
					title { R.string.remove_novel_cache }
					onButtonClicked {
						try {
							// TODO purge
						} catch (e: SQLException) {
							context!!.toast("SQLITE Error")
							Log.e("AdvancedSettings", "DatabaseError", e)
						}
					}
				}

		)
	}

	@Throws(Resources.NotFoundException::class)
	override fun onViewCreated(view: View) {
		val theme = (activity as AppCompatActivity).delegate.localNightMode
		(settings[0] as SpinnerSettingData).spinnerSelection = (if (
				theme == MODE_NIGHT_YES ||
				theme == MODE_NIGHT_FOLLOW_SYSTEM ||
				theme == MODE_NIGHT_AUTO_BATTERY)
			1 else 0)

		if (BuildConfig.DEBUG && findDataByID(9) == -1)
			settings.add(switchSettingData(9) {
				title { "Show Intro" }
				checker { Settings::showIntro }
			})
		super.onViewCreated(view)
	}
}