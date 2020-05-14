package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import android.util.Log
import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtensionDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtensionsDataSource
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

		private val repositorySource: ILocalExtRepoDataSource
) : IExtensionsRepository {
	override suspend fun getExtensions(): LiveData<HResult<List<ExtensionEntity>>> =
			databaseSource.loadExtensions()

	override suspend fun installExtension(extensionEntity: ExtensionEntity) {
		val repo = repositorySource.loadRepository(extensionEntity.repoID)
		if (repo is HResult.Success)
			when (val result = remoteSource.downloadFormatter(
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
					} catch (e: Exception) {
						Log.e(logID(), "Failed to parse formatter", e)
					}
				}
				is HResult.Error -> {
					Log.e(logID(), "${result.code}\t${result.message}")
				}
			}
	}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity) =
			databaseSource.insertOrUpdate(extensionEntity)

	override suspend fun loadFormatter(extensionEntity: ExtensionEntity): HResult<Formatter> {
		val f = fileSource.loadFormatter(extensionEntity.fileName)
		if (f is HResult.Success) memorySource.putFormatterInMemory(f.data)
		return f
	}

	override suspend fun loadFormatter(formatterID: Int): HResult<Formatter> =
			loadFormatter(databaseSource.loadExtension(formatterID))

	override suspend fun getCards(): LiveData<HResult<List<IDTitleImage>>> =
			databaseSource.loadPoweredExtensionsCards()
}