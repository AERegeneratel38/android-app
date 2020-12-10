package app.shosetsu.android.domain.usecases.settings

import app.shosetsu.common.com.consts.settings.SettingKey
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.domain.repositories.base.ISettingsRepository

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
 * shosetsu
 * 25 / 09 / 2020
 */
class LoadChaptersResumeFirstUnreadUseCase(
		private val settings: ISettingsRepository
) {
	suspend operator fun invoke(): HResult<Boolean> =
			settings.getBoolean(SettingKey.ChaptersResumeFirstUnread)
}