package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.ChaptersDao
import app.shosetsu.common.datasource.database.base.ILocalChaptersDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * ====================================================================
 */


/**
 * Shosetsu
 * 10 / May / 2020
 *
 * @author github.com/doomsdayrs
 */
class LocalChaptersDataSource(
	private val chaptersDao: ChaptersDao,
) : ILocalChaptersDataSource {

	override suspend fun loadChapters(
		novelID: Int,
	): Flow<HResult<List<ChapterEntity>>> = flow {
		try {
			emitAll(chaptersDao.loadLiveChapters(novelID).mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity> = try {
		successResult(chaptersDao.loadChapter(chapterID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadReaderChapters(
		novelID: Int,
	): Flow<HResult<List<ReaderChapterEntity>>> = flow {
		try {
			emitAll(chaptersDao.loadLiveReaderChapters(novelID).mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun handleChapters(
		novelEntity: NovelEntity,
		list: List<Novel.Chapter>,
	): HResult<*> =
		try {
			successResult(chaptersDao.handleChapters(novelEntity, list))
		} catch (e: Exception) {
			e.toHError()
		}


	override suspend fun handleChapterReturn(
		novelEntity: NovelEntity,
		list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> = try {
		chaptersDao.handleChaptersReturnNew(novelEntity, list).convertList()
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> = try {
		successResult(chaptersDao.suspendedUpdate(chapterEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}


	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
		try {
			successResult(chaptersDao.updateReaderChapter(readerChapterEntity))
		} catch (e: Exception) {
			e.toHError()
		}

}