package app.shosetsu.android.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.SearchBookMarkedNovelsUseCase
import app.shosetsu.android.domain.usecases.load.LoadCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.load.LoadSearchRowUIUseCase
import app.shosetsu.android.view.uimodels.model.IDTitleImageUI
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.FullCatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ISearchViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handleReturn
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.collections.set

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
class SearchViewModel(
		private val searchBookMarkedNovelsUseCase: SearchBookMarkedNovelsUseCase,
		private val loadSearchRowUIUseCase: LoadSearchRowUIUseCase,
		private val loadCatalogueQueryDataUseCase: LoadCatalogueQueryDataUseCase,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : ISearchViewModel() {
	private val hashMap = HashMap<Int, MutableLiveData<HResult<List<ACatalogNovelUI>>>>()
	private val jobMap = HashMap<Int, Job>()

	override val listings: LiveData<HResult<List<SearchRowUI>>> by lazy {
		liveData {
			emit(loading())
			emitSource(loadSearchRowUIUseCase().asIOLiveData())
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	private var query: String = ""

	override fun setQuery(query: String) {
		this.query = query
	}

	override fun searchLibrary(): LiveData<HResult<List<ACatalogNovelUI>>> {
		if (!hashMap.containsKey(-1)) {
			hashMap[-1] = MutableLiveData(loading())
			loadLibrary()
		}
		return hashMap[-1]!!
	}

	private fun loadLibrary(): Job {
		jobMap[-1] = launchIO {
			hashMap[-1]?.postValue(searchBookMarkedNovelsUseCase(query).let { result: HResult<List<IDTitleImageUI>> ->
				result.handleReturn {
					successResult(it.map { (id, title, imageURL) ->
						FullCatalogNovelUI(id, title, imageURL, false)
					})
				}
			})
		}
		return jobMap[-1]!!
	}

	private fun loadFormatter(formatterID: Int): Job {
		jobMap[formatterID] = launchIO {
			hashMap[formatterID]?.postValue(
					loadCatalogueQueryDataUseCase(
							formatterID,
							query,
							mapOf()
					)
			)
		}
		return jobMap[formatterID]!!
	}

	override fun loadQuery() {
		jobMap.forEach { it.value.cancel("New query") }

		val keys = hashMap.keys
		keys.forEach { key ->
			hashMap[key]?.postValue(successResult(arrayListOf()))
			hashMap[key]?.postValue(loading())
			jobMap[key] = if (key == -1) loadLibrary()
			else loadFormatter(key)
		}
	}

	override fun searchFormatter(formatterID: Int): LiveData<HResult<List<ACatalogNovelUI>>> {
		if (!hashMap.containsKey(formatterID)) {
			hashMap[formatterID] = MutableLiveData(loading())
			loadFormatter(formatterID)
		}
		return hashMap[formatterID]!!
	}
}