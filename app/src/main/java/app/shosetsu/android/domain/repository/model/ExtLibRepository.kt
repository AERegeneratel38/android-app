package app.shosetsu.android.domain.repository.model

import android.database.sqlite.SQLiteException
import android.util.Log
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.datasource.database.base.ILocalExtLibDataSource
import app.shosetsu.android.datasource.file.base.IFileExtLibDataSource
import app.shosetsu.android.datasource.memory.base.IMemExtLibDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteExtLibDataSource
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.domain.repository.base.IExtLibRepository
import app.shosetsu.lib.Version
import app.shosetsu.lib.json.J_VERSION
import org.json.JSONException
import org.json.JSONObject

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
 * 12 / 05 / 2020
 */
class ExtLibRepository(
		private val fileSource: IFileExtLibDataSource,
		private val databaseSource: ILocalExtLibDataSource,
		private val remoteSource: IRemoteExtLibDataSource,
		private val memSource: IMemExtLibDataSource,
) : IExtLibRepository {
	override suspend fun loadExtLibByRepo(
			repositoryEntity: RepositoryEntity,
	): HResult<List<ExtLibEntity>> =
			databaseSource.loadExtLibByRepo(repositoryEntity)

	@Throws(JSONException::class, SQLiteException::class)
	override suspend fun installExtLibrary(
			repositoryEntity: RepositoryEntity,
			extLibEntity: ExtLibEntity,
	): HResult<*> {
		remoteSource.downloadLibrary(repositoryEntity, extLibEntity).handle {
			val data = it
			val json = JSONObject(data.substring(0, data.indexOf("\n")).replace("--", "").trim())
			try {
				extLibEntity.version = Version(json.getString(J_VERSION))
				databaseSource.updateOrInsert(extLibEntity)
				memSource.setLibrary(extLibEntity.scriptName, data)
				fileSource.writeExtLib(extLibEntity.scriptName, data)
			} catch (e: JSONException) {
				Log.e(logID(), "Unhandled", e)
				return errorResult(e)
			}
		}
		return successResult("")
	}

	override fun blockingLoadExtLibrary(name: String): HResult<String> =
			memSource.blockingLoadLibrary(name).takeIf { it is HResult.Success }
					?: fileSource.blockingLoadLib(name).also {
						if (it is HResult.Success)
							memSource.blockingSetLibrary(name, it.data)
					}
}