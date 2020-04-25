package com.github.doomsdayrs.apps.shosetsu.variables.ext

import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.variables.obj.FormattersRepository
import java.util.*

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

@get:Throws(MissingResourceException::class)
@Deprecated("ROOM IN FUTURE", level = DeprecationLevel.WARNING)
val DownloadEntity.chapterURL: String
	get() = Database.DatabaseIdentification.getChapterURLFromChapterID(this.chapterID)


@get:Throws(MissingResourceException::class)
@Deprecated("ROOM IN FUTURE", level = DeprecationLevel.WARNING)
val DownloadEntity.formatter: Formatter
	get() = FormattersRepository.getByID(
			Database.DatabaseIdentification.getFormatterIDFromChapterID(this.chapterID)
	)

