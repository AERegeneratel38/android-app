package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.common.ext.convertTo
import app.shosetsu.android.domain.usecases.ConvertNCToCNUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.dto.transmogrify
import app.shosetsu.lib.IExtension

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
class GetCatalogueListingDataUseCase(
	private val extensionRepository: IExtensionsRepository,
	private val novelsRepository: INovelsRepository,
	private val convertNCToCNUIUseCase: ConvertNCToCNUIUseCase,
	private val iSettingsRepository: ISettingsRepository
) {
	suspend operator fun invoke(
		iExtension: IExtension,
		data: Map<Int, Any>
	): HResult<List<ACatalogNovelUI>> {
		val cardType = iSettingsRepository.getInt(SettingKey.SelectedNovelCardType).transmogrify(
			onError = {
				return it
			},
			onEmpty = {
				return HResult.Empty
			}
		) {
			it
		}!!

		return extensionRepository.getCatalogueData(
			iExtension,
			0,
			data
		).transform { list ->
			successResult(list.map { novelListing ->
				novelListing.convertTo(iExtension)
			}.mapNotNull { ne ->
				novelsRepository.insertReturnStripped(ne).transmogrify { result ->
					convertNCToCNUIUseCase(result, cardType)
				}
			})
		}
	}
}