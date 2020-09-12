package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.mapListTo
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import app.shosetsu.android.view.uimodels.model.DownloadUI

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
 * shosetsu
 * 12 / May / 2020
 */
class LoadDownloadsUseCase(
		private val iDownloadsRepository: IDownloadsRepository,
) : (() -> LiveData<HResult<List<DownloadUI>>>) {
	override fun invoke(): LiveData<HResult<List<DownloadUI>>> {
		return liveData { emitSource(iDownloadsRepository.loadLiveDownloads().map { it.mapListTo() }) }
	}
}