package app.shosetsu.android.domain.usecases.get

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.lib.Filter
import kotlinx.coroutines.Dispatchers.IO

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
 * 14 / 09 / 2020
 */
class GetExtensionSettingsUseCase(
		private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(id: Int): LiveData<HResult<List<Filter<*>>>> =
			liveData<HResult<List<Filter<*>>>>(context = IO) {
				emit(loading())
				if (id != -1)
					emit(successResult(arrayListOf()))
				//TODO Create livedata for settings for extensions via [ShosetsuSettings] as source
			}
}