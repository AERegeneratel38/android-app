package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.dto.HResult
import app.shosetsu.common.datasource.database.base.IDBExtRepoDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtRepoDataSource
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtRepoRepository
import app.shosetsu.lib.json.RepoIndex
import kotlinx.coroutines.flow.Flow

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
class ExtRepoRepository(
	private val databaseSource: IDBExtRepoDataSource,
	private val remoteSource: IRemoteExtRepoDataSource
) : IExtRepoRepository {
    override suspend fun getRepoData(repositoryEntity: RepositoryEntity): HResult<RepoIndex> =
	    remoteSource.downloadRepoData(repositoryEntity)

    override suspend fun loadRepositories(): HResult<List<RepositoryEntity>> =
            databaseSource.loadRepositories()

    override suspend fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>> =
            databaseSource.loadRepositoriesLive()

}