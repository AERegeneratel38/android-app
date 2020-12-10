package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.ext.convertTo
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.usecases.ConvertNCToCNUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handleReturn
import app.shosetsu.common.dto.handledReturnAny
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel

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
 * 15 / 05 / 2020
 */
class LoadCatalogueQueryDataUseCase(
		private val extensionRepository: IExtensionsRepository,
		private val novelsRepository: INovelsRepository,
		private val convertNCToCNUIUseCase: ConvertNCToCNUIUseCase,
		private val iSettingsRepository: ISettingsRepository
) {
	suspend operator fun invoke(
			extID: Int,
			query: String,
			filters: Map<Int, Any>
	) = extensionRepository.loadIExtension(extID).handleReturn {
		invoke(it, query, filters)
	}

	suspend operator fun invoke(
			ext: IExtension,
			query: String,
			filters: Map<Int, Any>
	): HResult<List<ACatalogNovelUI>> = extensionRepository.loadCatalogueSearch(
			ext,
			query,
			filters
	).handleReturn {
		val data: List<Novel.Listing> = it
		iSettingsRepository.getInt(SettingKey.NovelCardType).handleReturn { cardType ->
			successResult(data.map { novelListing ->
				novelListing.convertTo(ext)
			}.mapNotNull { ne ->
				novelsRepository.insertNovelReturnCard(ne).handledReturnAny { card ->
					convertNCToCNUIUseCase(card, cardType)
				}
			})
		}
	}

}