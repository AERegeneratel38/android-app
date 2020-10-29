package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.entity
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.CountIDTuple
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import app.shosetsu.lib.Novel
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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface ChaptersDao : BaseDao<ChapterEntity> {

	//# Queries

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters")
	fun loadAllChapters(): Array<ChapterEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	fun loadLiveChapters(novelID: Int): Flow<List<ChapterEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	suspend fun loadChapters(novelID: Int): List<ChapterEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT id, url, title, readingPosition, readingStatus, bookmarked FROM chapters WHERE novelID = :novelID")
	fun loadLiveReaderChapters(novelID: Int): Flow<List<ReaderChapterEntity>>


	//## Single result queries

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE id = :chapterID LIMIT 1")
	suspend fun loadChapter(chapterID: Int): ChapterEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE _rowid_ = :rowID LIMIT 1")
	suspend fun loadChapter(rowID: Long): ChapterEntity

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*),id FROM chapters WHERE url = :chapterURL")
	suspend fun loadChapterCount(chapterURL: String): CountIDTuple

	@Query("SELECT COUNT(*) FROM chapters WHERE readingStatus != 2")
	@Throws(SQLiteException::class)
	suspend fun loadChapterUnreadCount(): Int

	//# Transactions

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): Unit =
			loadChapter(readerChapterEntity.id).copy(
					readingPosition = readerChapterEntity.readingPosition,
					readingStatus = readerChapterEntity.readingStatus,
					bookmarked = readerChapterEntity.bookmarked
			).let { suspendedUpdate(it) }

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>) {
		val databaseChapters: List<ChapterEntity> = loadChapters(novelEntity.id!!)
		list.forEach { novelChapter: Novel.Chapter ->
			databaseChapters.find { it.url == novelChapter.link }?.let {
				handleUpdate(it, novelChapter)
			} ?: handleAbortInsert(novelChapter, novelEntity)
		}
	}

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun handleChaptersReturnNew(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> {
		val newChapters = ArrayList<ChapterEntity>()
		val databaseChapters: List<ChapterEntity> = loadChapters(novelEntity.id!!)
		list.forEach { novelChapter: Novel.Chapter ->
			databaseChapters.find { it.url == novelChapter.link }?.let {
				handleUpdate(it, novelChapter)
			} ?: insertReturn(novelEntity, novelChapter).let {
				if (it is HResult.Success)
					newChapters.add(it.data)
			}
		}
		return successResult(newChapters)
	}

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertAndReturnChapterEntity(chapterEntity: ChapterEntity): ChapterEntity =
			loadChapter(insertReplace(chapterEntity))


	private suspend fun insertReturn(
			novelEntity: NovelEntity,
			novelChapter: Novel.Chapter,
	): HResult<ChapterEntity> {
		try {
			val row = handleAbortInsert(novelChapter, novelEntity)
			if (row < 0) return errorResult(ERROR_GENERAL, "Aborted")
			val c = loadChapter(row)
			return successResult(c)
		} catch (e: Exception) {
			return errorResult(ERROR_GENERAL, "Unprecedented error")
		}
	}

	@Throws(SQLiteException::class)
	private suspend fun handleAbortInsert(novelChapter: Novel.Chapter, novelEntity: NovelEntity) =
			insertAbort(novelChapter.entity(novelEntity))

	@Throws(SQLiteException::class)
	private suspend fun handleUpdate(chapterEntity: ChapterEntity, novelChapter: Novel.Chapter) {
		suspendedUpdate(chapterEntity.copy(
				title = novelChapter.title,
				releaseDate = novelChapter.release,
				order = novelChapter.order
		))
	}
}