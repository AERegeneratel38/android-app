package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.*
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadCatalogueData
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.NovelBackgroundAddUseCase
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogViewModel
import kotlinx.coroutines.Dispatchers

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
 * 01 / 05 / 2020
 */
class CatalogViewModel(
		private val getFormatterUseCase: GetFormatterUseCase,
		private val backgroundAddUseCase: NovelBackgroundAddUseCase,
		private val loadCatalogueData: LoadCatalogueData
) : ICatalogViewModel() {
	private val currentList: ArrayList<String> = arrayListOf()
	private val items: MutableLiveData<HResult<List<String>>> = MutableLiveData()

	override val formatterID: MutableLiveData<Int> = MutableLiveData()

	override var displayItems: LiveData<HResult<List<String>>> =
			liveData { emitSource(items) }

	override val formatterData: LiveData<HResult<Formatter>> =
			liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
				formatterID.switchMap {
					getFormatterUseCase(it)
				}
			}

	override fun setFormatterID(fID: Int) {
		launchAsync {
			if (formatterData.value !is HResult.Success<Formatter>)
				formatterID.postValue(fID)
		}
	}

	override fun loadData(formatter: Formatter) {
		launchIO {
			val i = loadCatalogueData(formatter, currentMaxPage)

			when (i) {
				is HResult.Success -> {
					items.postValue()
				}
				is HResult.Empty -> {
				}
				is HResult.Error -> {
				}
				is HResult.Empty -> {
				}
			}
		}
	}

	override fun loadQuery(query: String) {
	}

	override fun loadMore() {
		launchIO {

		}
	}

	override fun searchPage(query: String) {
		launchIO {

		}
	}

	override fun resetView(formatter: Formatter) {
		items.postValue(successResult(arrayListOf()))
		items.postValue(loading())
		loadData(formatter)
	}

	override fun backgroundNovelAdd(novelID: Int) = backgroundAddUseCase(novelID)
}