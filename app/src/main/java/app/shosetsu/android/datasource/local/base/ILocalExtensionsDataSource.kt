package app.shosetsu.android.datasource.local.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.IDTitleImage

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
 * 04 / 05 / 2020
 */
interface ILocalExtensionsDataSource {
	/** Loads LiveData of extensions */
	fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>>

	/** Loads LiveData of extension cards that are enabled */
	fun loadPoweredExtensionsCards(): LiveData<HResult<List<IDTitleImage>>>

	/** Updates [extensionEntity] */
	suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*>

	/** Delete [extensionEntity] */
	suspend fun deleteExtension(extensionEntity: ExtensionEntity): HResult<*>

	/** Load an [ExtensionEntity] via its [formatterID]*/
	suspend fun loadExtension(formatterID: Int): HResult<ExtensionEntity>


	/** Load an [ExtensionEntity] via its [formatterID]*/
	fun loadExtensionLive(formatterID: Int): LiveData<HResult<ExtensionEntity>>


	/** Inserts an [extensionEntity] otherwise updates it */
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*>
}