package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheSecondaryChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileChapterDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ReaderChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository

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
 * 02 / 05 / 2020
 * @param memorySource Source from memory
 * @param dbSource Source from db
 * @param remoteSource Source from online
 * @param fileSource Source from storage
 */
class ChaptersRepository(
		private val memorySource: ICacheChaptersDataSource,
		private val cacheSource: ICacheSecondaryChaptersDataSource,
		private val dbSource: ILocalChaptersDataSource,
		private val fileSource: IFileChapterDataSource,
		private val remoteSource: IRemoteChaptersDataSource,
) : IChaptersRepository {

	private suspend fun handleReturn(chapterEntity: ChapterEntity, value: HResult<String>) {
		if (value is HResult.Success)
			saveChapterPassageToMemory(chapterEntity, value.data)
	}

	override suspend fun loadChapterPassage(
			formatter: Formatter,
			chapterEntity: ChapterEntity,
	): HResult<String> = memorySource.loadChapterFromCache(chapterEntity.id!!)
			.takeIf { it is HResult.Success }
			?: cacheSource.loadChapterPassage(chapterEntity.id!!)
					.takeIf { it is HResult.Success }
			?: fileSource.loadChapterPassageFromStorage(chapterEntity)
					.takeIf { it is HResult.Success }?.also { handleReturn(chapterEntity, it) }
			?: remoteSource.loadChapterPassage(
					formatter,
					chapterEntity.url
			).also { handleReturn(chapterEntity, it) }

	override suspend fun saveChapterPassageToMemory(
			chapterEntity: ChapterEntity,
			passage: String,
	) {
		memorySource.saveChapterInCache(chapterEntity.id!!, passage)
		cacheSource.saveChapterInCache(chapterEntity.id!!, passage)
	}

	@Throws(SQLiteException::class)
	override suspend fun saveChapterPassageToStorage(
			chapterEntity: ChapterEntity,
			passage: String,
	): Unit = saveChapterPassageToMemory(chapterEntity, passage).also {
		fileSource.saveChapterPassageToStorage(chapterEntity, passage)
		dbSource.updateChapter(chapterEntity.copy(isSaved = true))
	}

	@Throws(SQLiteException::class)
	override suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>): Unit =
			dbSource.handleChapters(novelEntity, list)

	override suspend fun handleChaptersReturn(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> =
			dbSource.handleChapterReturn(novelEntity, list)

	override suspend fun loadChapters(novelID: Int): LiveData<HResult<List<ChapterEntity>>> =
			dbSource.loadChapters(novelID)

	@Throws(SQLiteException::class)
	override suspend fun updateChapter(chapterEntity: ChapterEntity) =
			dbSource.updateChapter(chapterEntity)

	override suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity> =
			dbSource.loadChapter(chapterID)

	override suspend fun loadReaderChapters(
			novelID: Int,
	): LiveData<HResult<List<ReaderChapterEntity>>> = dbSource.loadReaderChapters(novelID)

	@Throws(SQLiteException::class)
	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): Unit =
			dbSource.updateReaderChapter(readerChapterEntity)

	@Throws(SQLiteException::class)
	override suspend fun deleteChapterPassage(chapterEntity: ChapterEntity) {
		dbSource.updateChapter(chapterEntity.copy(
				isSaved = false
		))
		fileSource.deleteChapter(chapterEntity)
	}

}