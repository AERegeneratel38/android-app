package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handleReturn
import app.shosetsu.android.common.dto.mapTo
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IUpdatesRepository
import app.shosetsu.android.view.uimodels.model.UpdateUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

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
 * 13 / 05 / 2020
 */
class LoadUpdatesUseCase(
		private val updatesRepository: IUpdatesRepository,
)  {
	operator fun invoke(): Flow<HResult<List<UpdateUI>>> = flow {
		emitAll(updatesRepository.getCompleteUpdates().mapLatest { result ->
			result.handleReturn { successResult(it.mapTo()) }
		})
	}
}