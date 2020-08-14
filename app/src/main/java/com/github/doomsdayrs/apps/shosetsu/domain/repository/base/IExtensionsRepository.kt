package com.github.doomsdayrs.apps.shosetsu.domain.repository.base

import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage

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
 * 25 / 04 / 2020
 */
interface IExtensionsRepository {
	/**
	 * Live data of all extensions
	 */
	suspend fun getExtensions(): LiveData<HResult<List<ExtensionEntity>>>

	suspend fun installExtension(extensionEntity: ExtensionEntity)

	suspend fun uninstallExtension(extensionEntity: ExtensionEntity)

	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity)

	suspend fun updateExtension(extensionEntity: ExtensionEntity)

	/**
	 * Loads the formatter via its extension
	 */
	suspend fun loadFormatter(extensionEntity: ExtensionEntity): HResult<Formatter>

	/** Loads the formatter via its ID */
	suspend fun loadFormatter(formatterID: Int): HResult<Formatter>

	suspend fun getCards(): LiveData<HResult<List<IDTitleImage>>>

	fun loadPoweredExtensionsFileNames(): HResult<List<String>>

	fun loadExtensionMD5(extensionID: Int): HResult<String>

	suspend fun loadCatalogueData(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>
	): HResult<List<Novel.Listing>>
}