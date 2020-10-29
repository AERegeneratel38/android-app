package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.local.CountIDTuple
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
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
 * ====================================================================
 */

/**
 * shosetsu
 * 18 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface RepositoryDao : BaseDao<RepositoryEntity> {
	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertRepositoryAndReturn(repositoryEntity: RepositoryEntity): RepositoryEntity =
			loadRepositoryFromROWID(insertReplace(repositoryEntity))

	/**
	 * Run only if you know for sure the data exists
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE url = :url LIMIT 1")
	fun loadRepositoryFromURL(url: String): RepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE id = :rowID LIMIT 1")
	fun loadRepositoryFromROWID(rowID: Long): RepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE id = :repositoryID LIMIT 1")
	fun loadRepositoryFromID(repositoryID: Int): RepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories ORDER BY id ASC")
	fun loadRepositoriesLive(): Flow<List<RepositoryEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories ORDER BY id ASC")
	fun loadRepositories(): List<RepositoryEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM repositories WHERE url = :url")
	fun repositoryCountFromURL(url: String): Int

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*), id FROM repositories WHERE url = :url LIMIT 1")
	fun repositoryCountAndROWIDFromURL(url: String): CountIDTuple

	@Ignore
	@Throws(SQLiteException::class)
	fun doesRepositoryExist(url: String): Boolean = repositoryCountFromURL(url) > 0

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun initializeData() {
		val branch = if (BuildConfig.DEBUG) "dev" else "master"
		val name = if (BuildConfig.DEBUG) "Development" else "Stable"
		val repo = RepositoryEntity(
				url = "https://raw.githubusercontent.com/shosetsuorg/extensions/$branch",
				name = name
		)
		createIfNotExist(repo)
	}

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun createIfNotExist(repositoryEntity: RepositoryEntity): Int {
		val tuple = repositoryCountAndROWIDFromURL(repositoryEntity.url)
		if (tuple.count == 0)
			return insertRepositoryAndReturn(repositoryEntity).id
		return tuple.id
	}
}