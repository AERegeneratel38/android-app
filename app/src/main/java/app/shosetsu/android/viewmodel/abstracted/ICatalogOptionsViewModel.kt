package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.ViewModel
import app.shosetsu.android.view.uimodels.model.catlog.CatalogOptionUI
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel

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
 * 30 / 04 / 2020
 * ViewModel for [com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogsController]
 * [liveData] is a [com.github.doomsdayrs.apps.shosetsu.common.dto.HResult] of [FormatterCard]
 * [FormatterCard] are representation of the different extensions one can browse
 */
abstract class ICatalogOptionsViewModel :
		SubscribeHandleViewModel<List<CatalogOptionUI>>, IsOnlineCheckViewModel, ViewModel()