package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.CompactBookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.FullBookmarkedNovelUI
import app.shosetsu.common.com.consts.settings.SettingKey
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.handleReturn
import app.shosetsu.common.com.dto.successResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * 08 / 05 / 2020
 */
class LoadLibraryUseCase(
		private val iNovelsRepository: INovelsRepository,
		private val settings: ISettingsRepository,
) {
	operator fun invoke(): Flow<HResult<List<ABookmarkedNovelUI>>> = flow {
		emitAll(iNovelsRepository.getLiveBookmarked().combine(settings.observeInt(SettingKey.NovelCardType)) { origin, cardType ->
			origin.handleReturn {
				val list = it
				val newList = list.map { (id, title, imageURL, bookmarked, unread) ->
					if (cardType == 0)
						FullBookmarkedNovelUI(
								id,
								title,
								imageURL,
								bookmarked,
								unread
						)
					else CompactBookmarkedNovelUI(
							id,
							title,
							imageURL,
							bookmarked,
							unread
					)
				}
				successResult(newList)
			}

		})
	}


}