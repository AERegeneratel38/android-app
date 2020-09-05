package com.github.doomsdayrs.apps.shosetsu.datasource.cache.base

import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult

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
 * 05 / 05 / 2020
 */
interface ICacheExtensionsDataSource {
	/** Load formatter from memory */
	suspend fun loadFormatterFromMemory(formatterID: Int): HResult<Formatter>

	/** Put formatter in memory */
	suspend fun putFormatterInMemory(formatter: Formatter): HResult<*>

	/** Remove formatter by ID from cache*/
	suspend fun removeFormatterFromMemory(formatterID: Int): HResult<*>
}