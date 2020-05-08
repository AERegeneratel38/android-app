package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.*
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IFormatterRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.SearchBookMarkedNovelsUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.URLTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISearchViewModel
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
class SearchViewModel(
		val searchBookMarkedNovelsUseCase: SearchBookMarkedNovelsUseCase,
		val iFormatterRepository: IFormatterRepository
) : ISearchViewModel, ViewModel() {
	override var query: MutableLiveData<String> = MutableLiveData()

	override fun setQuery(query: String) = this.query.postValue(query)

	override fun searchLibrary(): LiveData<HResult<List<IDTitleImageUI>>> {
		return query.switchMap {
			liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
				emit(loading())
				emitSource(searchBookMarkedNovelsUseCase(it))
			}
		}
	}

	override fun searchFormatter(formatter: Formatter): LiveData<HResult<List<URLTitleImageUI>>> {
		TODO("Not yet implemented")
	}
}