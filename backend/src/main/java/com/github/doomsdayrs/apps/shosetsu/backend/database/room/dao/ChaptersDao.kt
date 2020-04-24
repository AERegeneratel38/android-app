package com.github.doomsdayrs.apps.shosetsu.backend.database.room.dao

import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.BooleanChapterIDTuple
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.CountIDTuple

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
interface ChaptersDao {
	@Insert(entity = ChapterEntity::class, onConflict = OnConflictStrategy.IGNORE)
	fun insertChapterEntity(chapterEntity: ChapterEntity): Long


	@Transaction
	fun insertAndReturnChapterEntity(chapterEntity: ChapterEntity): ChapterEntity =
			loadChapter(insertChapterEntity(chapterEntity))

	@Update
	fun updateChapter(chapterEntity: ChapterEntity)

	@Query("SELECT * FROM chapters")
	fun loadChapters(): Array<ChapterEntity>

	@Query("SELECT * FROM chapters WHERE id = :chapterID LIMIT 1")
	fun loadChapter(chapterID: Int): ChapterEntity

	@Query("SELECT * FROM chapters WHERE _rowid_ = :rowID LIMIT 1")
	fun loadChapter(rowID: Long): ChapterEntity

	@Query("SELECT COUNT(*),id FROM chapters WHERE link = :chapterURL")
	fun loadChapterCount(chapterURL: String): CountIDTuple

	fun hasChapter(chapterURL: String): BooleanChapterIDTuple {
		val c = loadChapterCount(chapterURL)
		return BooleanChapterIDTuple(
				c.count > 0,
				c.id
		)
	}

	@Query("SELECT COUNT(*) FROM chapters WHERE readingReadingStatus != 2")
	fun loadChapterUnreadCount(): Int
}