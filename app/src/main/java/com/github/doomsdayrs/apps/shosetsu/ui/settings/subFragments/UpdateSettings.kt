package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Build.VERSION_CODES
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl.*

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
 * 20 / 06 / 2020
 */
class UpdateSettings : SettingsSubController() {
	override val settings: List<SettingsItemData> by settingsList {
		// Update frequency
		seekBarSettingData(6) {
			title { "Update frequency" }
			range { 0F to 6F }
			progressValue = when (Settings.updateCycle) {
				1 -> 0F
				2 -> 1F
				4 -> 2F
				6 -> 3F
				12 -> 4F
				24 -> 5F
				168 -> 6F
				else -> 0F
			}
			array.apply {
				put(0, "1 Hour")
				put(1, "2 Hours")
				put(2, "4 Hours")
				put(3, "6 Hours")
				put(4, "12 Hours")
				put(5, "Daily")
				put(6, "Weekly")
			}
			onProgressChanged { _, progress, _, fromUser ->
				if (fromUser)
					Settings.updateCycle = when (progress) {
						0 -> 1
						1 -> 2
						2 -> 4
						3 -> 6
						4 -> 12
						5 -> 24
						6 -> 168
						else -> 1
					}
			}
			showSectionMark = true
			showSectionText = true

			seekBySection = true
			seekByStepSection = true
			autoAdjustSectionMark = true
			touchToSeek = true
			hideBubble = true
			sectionC = 6
		}
		// Download on update
		switchSettingData(0) {
			title { "Download on update" }
			checker { Settings::downloadOnUpdate }
		}
		// Update only ongoing
		switchSettingData(1) {
			title { "Only update ongoing" }
			checker { Settings::onlyUpdateOngoing }
		}
		switchSettingData(2) {
			title { "Allow updating on metered connection" }
			checker { Settings::updateOnMetered }
		}
		switchSettingData(3) {
			title { "Update on low battery" }
			checker { Settings::updateOnLowBattery }
		}
		switchSettingData(4) {
			title { "Update on low storage" }
			checker { Settings::updateOnLowStorage }
		}
		switchSettingData(5) {
			title { "Update only when idle" }
			requiredVersion { VERSION_CODES.M }
			checker { Settings::updateOnlyIdle }
		}
	}
}