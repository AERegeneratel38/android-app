package app.shosetsu.android.datasource.file.model

import app.shosetsu.android.common.consts.APP_UPDATE_CACHE_FILE
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.DebugAppUpdate
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.common.com.consts.ErrorKeys
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.emptyResult
import app.shosetsu.common.com.dto.successResult
import app.shosetsu.common.com.dto.withSuccess
import app.shosetsu.common.com.enums.InternalFileDir.CACHE
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

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
 * 07 / 09 / 2020
 */
@ExperimentalCoroutinesApi
class FileAppUpdateDataSource(
		private val iFileSystemProvider: IFileSystemProvider
) : IFileCachedAppUpdateDataSource {

	override val updateAvaLive: MutableStateFlow<HResult<DebugAppUpdate>> by lazy {
		MutableStateFlow(emptyResult())
	}

	private fun write(debugAppUpdate: DebugAppUpdate): HResult<*> = try {
		iFileSystemProvider.writeInternalFile(
				CACHE,
				APP_UPDATE_CACHE_FILE,
				ObjectMapper().registerKotlinModule().writeValueAsString(debugAppUpdate)
		)
	} catch (e: JsonProcessingException) {
		app.shosetsu.common.com.dto.errorResult(ErrorKeys.ERROR_IO, e)
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadCachedAppUpdate(): HResult<DebugAppUpdate> =
			iFileSystemProvider.readInternalFile(
					CACHE,
					APP_UPDATE_CACHE_FILE
			).withSuccess { ObjectMapper().registerKotlinModule().readValue(it) }

	override suspend fun putAppUpdateInCache(
			debugAppUpdate: DebugAppUpdate,
			isUpdate: Boolean
	): HResult<*> {
		updateAvaLive.value = if (isUpdate) successResult(debugAppUpdate) else emptyResult()
		return write(debugAppUpdate)
	}
}