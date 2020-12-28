package app.shosetsu.common.domain.model.local

import app.shosetsu.common.enums.ChapterSortType
import app.shosetsu.common.enums.ReaderType
import app.shosetsu.common.enums.ReadingStatus

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
 */

/**
 * 25 / 12 / 2020
 */
data class NovelSettingEntity(
	val novelID: Int,

	// how chapters are sorted
	var sortType: ChapterSortType,
	var showOnlyReadingStatusOf: ReadingStatus?,
	var showOnlyBookmarked: Boolean,
	var showOnlyDownloaded: Boolean,
	var reverseOrder: Boolean,

	// how the reader is set-up

	var readerType: ReaderType
)