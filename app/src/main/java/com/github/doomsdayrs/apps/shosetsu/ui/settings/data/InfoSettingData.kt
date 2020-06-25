package com.github.doomsdayrs.apps.shosetsu.ui.settings.data

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 25 / 06 / 2020
 */
class InfoSettingData(id: Int) : SettingsItemData(id) {
	var itemViewOnClick: (View) -> Unit = {}
	override fun setupView(settingsItem: SettingsItem) {
		super.setupView(settingsItem)
		with(settingsItem) {
			itemView.setOnClickListener(itemViewOnClick)
		}
	}

}