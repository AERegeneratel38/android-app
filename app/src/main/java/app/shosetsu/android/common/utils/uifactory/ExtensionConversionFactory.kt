package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.handleReturn
import app.shosetsu.common.com.dto.successResult
import app.shosetsu.common.domain.model.local.ExtensionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

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
 * 05 / 12 / 2020
 */
class ExtensionConversionFactory(data: ExtensionEntity) : UIConversionFactory<ExtensionEntity, ExtensionUI>(data) {
	override fun ExtensionEntity.convertTo(): ExtensionUI = ExtensionUI(
			id,
			repoID,
			name,
			fileName,
			imageURL,
			lang,
			enabled,
			installed,
			installedVersion,
			repositoryVersion,
			md5
	)
}

fun List<ExtensionEntity>.mapToFactory() =
		map { ExtensionConversionFactory(it) }

fun HResult<List<ExtensionEntity>>.mapResultWithFactory() =
		handleReturn { successResult(it.mapToFactory()) }

fun Flow<HResult<List<ExtensionEntity>>>.mapLatestToResultFlowWithFactory() =
		mapLatest { it.mapResultWithFactory() }
