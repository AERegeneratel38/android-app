package app.shosetsu.android.datasource.local.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.lib.Novel

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
interface ILocalChaptersDataSource {
	/** Get the chapters of a novel */
	suspend fun loadChapters(novelID: Int): LiveData<HResult<List<ChapterEntity>>>

	/** Loads a chapter by its ID */
	suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity>

	/** Loads chapters by novelID */
	suspend fun loadReaderChapters(novelID: Int): LiveData<HResult<List<ReaderChapterEntity>>>

	/** Handles chapters from a remote source */
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>): HResult<*>

	/** Handles chapters from a remote source, then returns the new chapters */
	suspend fun handleChapterReturn(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>>

	/** Updates a [chapterEntity] */
	suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*>

	/** Updates a [readerChapterEntity], a cut down version [updateChapter] */
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*>
}