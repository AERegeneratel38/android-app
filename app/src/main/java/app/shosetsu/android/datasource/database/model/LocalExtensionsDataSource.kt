package app.shosetsu.android.datasource.database.model

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.mapLatestToSuccess
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.database.base.ILocalExtensionsDataSource
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.IDTitleImage
import app.shosetsu.android.providers.database.dao.ExtensionsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
 * 12 / May / 2020
 */
class LocalExtensionsDataSource(
		private val extensionsDao: ExtensionsDao,
) : ILocalExtensionsDataSource {
	override fun loadExtensions(): Flow<HResult<List<ExtensionEntity>>> = flow {
		try {
			emitAll(extensionsDao.loadExtensions().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override fun loadExtensionLive(formatterID: Int): Flow<HResult<ExtensionEntity>> = flow {
		try {
			emitAll(extensionsDao.getExtensionLive(formatterID).mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override fun loadPoweredExtensionsCards(): Flow<HResult<List<IDTitleImage>>> = flow {
		try {
			emitAll(extensionsDao.loadPoweredExtensionsBasic().mapLatest { list ->
				successResult(list.map { IDTitleImage(it.id, it.name, it.imageURL) })
			})
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedUpdate(extensionEntity))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun deleteExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedDelete(extensionEntity))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadExtension(formatterID: Int): HResult<ExtensionEntity> = try {
		successResult(extensionsDao.getExtension(formatterID))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.insertOrUpdate(extensionEntity))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun getExtensions(repoID: Int): HResult<List<ExtensionEntity>> = try {
		successResult(extensionsDao.getExtensions(repoID))
	} catch (e: Exception) {
		e.toHError()
	}

}