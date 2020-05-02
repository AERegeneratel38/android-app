package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ChaptersDao

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
 */
class ChaptersRepository(
		val chaptersDao: ChaptersDao
) : IChaptersRepository {
	override fun loadChapterUnreadCount(novelID: Int) = chaptersDao.loadChapterUnreadCount()

	override fun addSavePath(chapterID: Int, savePath: String) =
			chaptersDao.setChapterSavePath(chapterID, savePath)
}