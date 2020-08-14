package com.github.doomsdayrs.apps.shosetsu.ui.extensions

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

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsViewModel

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsController : RecyclerController<ExtensionsAdapter, ExtensionUI>() {
	init {
		setHasOptionsMenu(true)
	}

	/***/
	val extensionViewModel: IExtensionsViewModel by viewModel()

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_extensions, menu)
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.extensions)
	}

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		extensionViewModel.liveData.observe(this, Observer { handleRecyclerUpdate(it) })
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.refresh -> {
			extensionViewModel.refreshRepository()
			true
		}
		R.id.reload -> {
			extensionViewModel.reloadFormatters()
			true
		}
		else -> false
	}

	override fun difAreItemsTheSame(oldItem: ExtensionUI, newItem: ExtensionUI): Boolean =
			oldItem.id == newItem.id

	override fun createRecyclerAdapter(): ExtensionsAdapter =
			ExtensionsAdapter(this)
}