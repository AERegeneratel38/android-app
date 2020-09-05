package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_LUA_BROKEN
import com.github.doomsdayrs.apps.shosetsu.common.dto.*
import com.github.doomsdayrs.apps.shosetsu.common.ext.logError
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtensionDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteCatalogueDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteExtensionDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsRepository(
		private val memorySource: ICacheExtensionsDataSource,
		private val databaseSource: ILocalExtensionsDataSource,
		private val fileSource: IFileExtensionDataSource,
		private val remoteSource: IRemoteExtensionDataSource,
		private val repositorySource: ILocalExtRepoDataSource,
		private val remoteCatalogueDataSource: IRemoteCatalogueDataSource,
) : IExtensionsRepository {
	override suspend fun getExtensions(): LiveData<HResult<List<ExtensionEntity>>> =
			databaseSource.loadExtensions()

	override suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<*> {
		val repo = repositorySource.loadRepository(extensionEntity.repoID)
		if (repo is HResult.Success)
			when (val result = remoteSource.downloadExtension(
					repo.data,
					extensionEntity
			)) {
				is HResult.Success -> {
					try {
						val formatter = LuaFormatter(result.data)

						// Write to storage/cache
						memorySource.putFormatterInMemory(formatter)
						fileSource.writeFormatter(extensionEntity.fileName, result.data)

						// Update database info
						formatter.getMetaData()!!.let { meta ->
							val version = meta.getString("version")
							extensionEntity.installedVersion = version
							extensionEntity.repositoryVersion = version
						}
						extensionEntity.name = formatter.name
						extensionEntity.imageURL = formatter.imageURL
						extensionEntity.installed = true
						extensionEntity.enabled = true
						databaseSource.updateExtension(extensionEntity)
						return successResult("")
					} catch (e: IllegalArgumentException) {
						return errorResult(ERROR_LUA_BROKEN, e).also { logError { it } }
					} catch (e: Exception) {
						return errorResult(ERROR_GENERAL, e).also { logError { it } }
					}
				}
				is HResult.Error -> {
					logError { result }
					return result
				}
			}
		return emptyResult()
	}

	@Throws(SQLiteException::class)
	override suspend fun uninstallExtension(extensionEntity: ExtensionEntity) {
		memorySource.removeFormatterFromMemory(extensionEntity.id)
		fileSource.deleteFormatter(extensionEntity.fileName)
		databaseSource.updateExtension(
				extensionEntity.copy(enabled = false, installed = false, installedVersion = null)
		)
	}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): Unit =
			databaseSource.insertOrUpdate(extensionEntity)

	override suspend fun updateExtension(extensionEntity: ExtensionEntity): Unit =
			databaseSource.updateExtension(extensionEntity)

	override suspend fun loadFormatter(extensionEntity: ExtensionEntity): HResult<Formatter> = try {
		memorySource.loadFormatterFromMemory(extensionEntity.id).takeIf { it is HResult.Success }
				?: fileSource.loadFormatter(extensionEntity.fileName).takeIf { it is HResult.Success }
						?.also { if (it is HResult.Success) memorySource.putFormatterInMemory(it.data) }
				?: errorResult(ErrorKeys.ERROR_NOT_FOUND, "Formatter not found")
	} catch (e: NullPointerException) {
		errorResult(ErrorKeys.ERROR_IMPOSSIBLE, "Impossible NPE")
	}

	override suspend fun loadFormatter(formatterID: Int): HResult<Formatter> =
			databaseSource.loadExtension(formatterID).withSuccess { loadFormatter(it) }

	override suspend fun getCards(): LiveData<HResult<List<IDTitleImage>>> =
			databaseSource.loadPoweredExtensionsCards()

	override suspend fun loadCatalogueData(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> =
			remoteCatalogueDataSource.loadListing(formatter, listing, page, data)
}