package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IDownloadsViewModel

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsViewModel(private val downloadsRepository: IDownloadsRepository)
	: ViewModel(), IDownloadsViewModel {

	override val liveData: LiveData<List<DownloadUI>>
		get() = TODO("Not yet implemented")

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<DownloadUI>>
	) = downloadsRepository.subscribeRepository(owner, observer)

	override suspend fun getLiveData(): List<DownloadUI> = downloadsRepository.loadData()

}