package app.shosetsu.android.datasource.database.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.domain.model.local.UpdateEntity
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
 */




/**
 * shosetsu
 * 04 / 05 / 2020
 */
interface ILocalUpdatesDataSource {

	/** Loads [LiveData] of a [List] of [UpdateEntity] */
	suspend fun getUpdates(): Flow<HResult<List<UpdateEntity>>>

	/** Insert a [List] of [UpdateEntity] and returns an [HResult] of [Array] of [Long] */
	suspend fun insertUpdates(list: List<UpdateEntity>): HResult<Array<Long>>

	/** Loads [LiveData] of a [List] of [UpdateCompleteEntity] */
	suspend fun getCompleteUpdates(): Flow<HResult<List<UpdateCompleteEntity>>>
}