package com.github.doomsdayrs.apps.shosetsu.datasource.remote.model

import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.consts.repoFolderStruct
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.quickie
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import okhttp3.OkHttpClient
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
 * 13 / 05 / 2020
 */
class RemoteExtRepoDataSource(
		private val client: OkHttpClient,
) : IRemoteExtRepoDataSource {
	override suspend fun downloadRepoData(
			repo: RepositoryEntity,
	): HResult<JSONObject> = try {
		successResult(
				JSONObject(
						client.quickie(
								"${repo.url}${repoFolderStruct}index.json"
						).body!!.string()
				)
		)
	} catch (e: Exception) {
		errorResult(ErrorKeys.ERROR_GENERAL, e.message ?: "Unknown general error")
	}

}