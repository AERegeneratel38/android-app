package com.github.doomsdayrs.apps.shosetsu.viewmodel.model.catalog

import androidx.lifecycle.MutableLiveData
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.NovelBackgroundAddUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.load.LoadCatalogueListingDataUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.load.LoadCatalogueQueryDataUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.load.LoadFormatterUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast.ToastErrorUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ICatalogViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

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
		private val getFormatterUseCase: LoadFormatterUseCase,
		private val backgroundAddUseCase: NovelBackgroundAddUseCase,
		private val loadCatalogueListingData: LoadCatalogueListingDataUseCase,
		private val loadCatalogueQueryDataUseCase: LoadCatalogueQueryDataUseCase,
		private var toastErrorUseCase: ToastErrorUseCase,
) : ICatalogViewModel() {
	private var formatter: Formatter? = null

	private var listingItems: ArrayList<ACatalogNovelUI> = arrayListOf()
	private var filterData = hashMapOf<Int, Any>()
	private var query: String = ""

	override val listingItemsLive: MutableLiveData<HResult<List<ACatalogNovelUI>>> by lazy {
		MutableLiveData<HResult<List<ACatalogNovelUI>>>(loading())
	}

	override val filterItemsLive: MutableLiveData<HResult<List<Filter<*>>>> by lazy {
		MutableLiveData(loading())
	}

	override val hasSearchLive: MutableLiveData<HResult<Boolean>> by lazy {
		MutableLiveData(loading())
	}

	override val extensionName: MutableLiveData<HResult<String>> by lazy {
		MutableLiveData<HResult<String>>(loading())
	}

	override fun setFormatterID(fID: Int) {
		launchIO {
			if (formatter == null) {
				when (val v = getFormatterUseCase(fID)) {
					is HResult.Success -> {
						formatter = v.data

						extensionName.postValue(successResult(v.data.name))
						hasSearchLive.postValue(successResult(v.data.hasSearch))
						filterItemsLive.postValue(successResult(v.data.searchFiltersModel.toList()))
					}
					is HResult.Loading -> extensionName.postValue(v)
					is HResult.Error -> extensionName.postValue(v)
					is HResult.Empty -> extensionName.postValue(v)
				}
			}
		}
	}

	/**
	 * Current loading job
	 */
	private var loadingJob: Job = launchIO { }

	override fun setQuery(string: String) {
		this.query = string
	}

	@Synchronized
	override fun loadData(): Job = launchIO {
		if (formatter == null) this.cancel("Extension not loaded")
		checkNotNull(formatter)
		currentMaxPage++
		val values = listingItems

		listingItemsLive.postValue(loading())

		when (val i: HResult<List<ACatalogNovelUI>> = if (query.isEmpty()) loadCatalogueListingData(
				formatter!!,
				currentMaxPage
		) else loadCatalogueQueryDataUseCase(
				formatter!!,
				query,
				currentMaxPage,
				filterData
		)) {
			is HResult.Success -> {
				listingItems = values.also { it.addAll(i.data) }
				listingItemsLive.postValue(successResult(values + i.data))
			}
			is HResult.Empty -> {
			}
			is HResult.Error -> toastErrorUseCase<CatalogViewModel>(i)
		}
	}

	override fun loadQuery(): Job = launchIO {
		currentMaxPage = 0
		loadingJob.cancel("Loading a query")
		listingItems.clear()
		listingItemsLive.postValue(successResult(arrayListOf()))
		listingItemsLive.postValue(loading())
		loadingJob = loadData()
	}

	@Synchronized
	override fun loadMore() {
		if (loadingJob.isCompleted)
			loadingJob = loadData()
	}

	override fun resetView() {
		listingItemsLive.postValue(successResult(arrayListOf()))
		listingItems.clear()
		currentMaxPage = 0
		loadData()
	}

	override fun backgroundNovelAdd(novelID: Int) {
		launchIO { backgroundAddUseCase(novelID) }
	}

	override fun setFilters(map: Map<Int, Any>) {
		launchIO {
			filterData.putAll(map)
			listingItems.clear()
			listingItemsLive.postValue(successResult(arrayListOf()))
			listingItemsLive.postValue(loading())
			loadData()
		}
	}
}

