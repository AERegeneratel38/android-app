package com.github.doomsdayrs.apps.shosetsu.datasource.cache.base

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.remote.DebugAppUpdate

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
 * 07 / 09 / 2020
 */
interface ICacheAppUpdateDataSource {
	/**
	 * Live data of the current update
	 */
	val updateAvaLive: LiveData<HResult<DebugAppUpdate>>

	/**
	 * Accessor method to read the current cached update
	 */
	suspend fun loadCachedAppUpdate(): HResult<DebugAppUpdate>

	/** Puts an update into cache */
	suspend fun putAppUpdateInCache(debugAppUpdate: DebugAppUpdate, isUpdate: Boolean): HResult<*>

}