package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.convertTo
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.usecases.ConvertNCToCNUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.lib.Formatter
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
) {
	suspend operator fun invoke(
			formatterID: Int,
			query: String,
			page: Int,
			filters: Map<Int, Any>
	) = extensionRepository.loadFormatter(formatterID).let {
		when (it) {
			is HResult.Success -> invoke(it.data, query, page, filters)
			is HResult.Error -> it
			is HResult.Empty -> it
			is HResult.Loading -> it
		}
	}

	suspend operator fun invoke(
			formatter: Formatter,
			query: String,
			page: Int,
			filters: Map<Int, Any>
	): HResult<List<ACatalogNovelUI>> {
		return when (val it = extensionRepository.loadCatalogueSearch(
				formatter,
				query,
				page,
				filters
		)) {
			is HResult.Success -> {
				val data: List<Novel.Listing> = it.data
				successResult(data.map { novelListing ->
					novelListing.convertTo(formatter)
				}.mapNotNull { ne ->
					novelsRepository.insertNovelReturnCard(ne).let { result ->
						if (result is HResult.Success)
							convertNCToCNUIUseCase(result.data)
						else null
					}
				})
			}
			is HResult.Loading -> it
			is HResult.Error -> it
			is HResult.Empty -> it
		}
	}

}