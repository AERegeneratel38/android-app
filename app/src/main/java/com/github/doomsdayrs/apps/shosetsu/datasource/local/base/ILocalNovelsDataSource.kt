package com.github.doomsdayrs.apps.shosetsu.datasource.local.base

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.BookmarkedNovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity

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
interface ILocalNovelsDataSource {
	/** load list of novels that are to be bookmarked */
	suspend fun loadLiveBookmarkedNovels(): LiveData<HResult<List<NovelEntity>>>

	/** Load list of bookmarked [NovelEntity] */
	suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>>

	/** Loads a [List] of [BookmarkedNovelEntity] that are in the library */
	suspend fun loadLiveBookmarkedNovelsAndCount(): LiveData<HResult<List<BookmarkedNovelEntity>>>

	/** Loads a [NovelEntity] by its [novelID] */
	suspend fun loadNovel(novelID: Int): HResult<NovelEntity>

	/** Loads a [LiveData] of a [NovelEntity] by its [novelID] */
	suspend fun loadNovelLive(novelID: Int): LiveData<HResult<NovelEntity>>

	/** Updates a [NovelEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateNovel(novelEntity: NovelEntity)

	/** Updates a list of [BookmarkedNovelEntity] */
	suspend fun updateBookmarkedNovels(list: List<BookmarkedNovelEntity>)

	/** Inserts a [NovelEntity] then returns its [IDTitleImageBook] */
	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): HResult<IDTitleImageBook>

	/** Inserts a [NovelEntity] */
	@Throws(SQLiteException::class)
	suspend fun insertNovel(novelEntity: NovelEntity)
}