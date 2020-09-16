package app.shosetsu.android.ui.browse

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
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.setOnClickListener
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.catalogue.CatalogController
import app.shosetsu.android.ui.extensionsConfigure.ConfigureExtension
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.IExtensionsViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class BrowseController : BasicFastAdapterRecyclerController<ExtensionUI>(),
		PushCapableController {
	override val viewTitleRes: Int = R.string.browse

	init {
		setHasOptionsMenu(true)
	}

	lateinit var pushController: (Controller) -> Unit

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}

	/***/
	private val viewModel: IExtensionsViewModel by viewModel()

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_extensions, menu)
		(menu.findItem(R.id.catalogues_search).actionView as SearchView)
				.setOnQueryTextListener(BrowseSearchQuery(pushController))
	}

	override fun setupFastAdapter() {
		fastAdapter.setOnClickListener { _, _, item, _ ->
			if (item.installed)
				if (viewModel.isOnline()) {
					pushController(CatalogController(bundleOf(
							BUNDLE_EXTENSION to item.id
					)))
				} else context?.toast(R.string.you_not_online)
			else toast(R.string.ext_not_installed)
			true
		}

		fastAdapter.addEventHook(object : ClickEventHook<ExtensionUI>() {
			override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = if (viewHolder is ExtensionUI.ViewHolder) viewHolder.binding.button else null
			override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ExtensionUI>, item: ExtensionUI) {
				var installed = false
				var update = false
				if (item.installed && item.isExtEnabled) {
					installed = true
					if (item.updateState() == ExtensionUI.State.UPDATE) update = true
				}

				if (!installed || update) viewModel.installExtension(item)
			}
		})

		fastAdapter.addEventHook(object : ClickEventHook<ExtensionUI>() {
			override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = if (viewHolder is ExtensionUI.ViewHolder) viewHolder.binding.settings else null

			override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ExtensionUI>, item: ExtensionUI) {
				pushController(ConfigureExtension(bundleOf(BUNDLE_EXTENSION to item.id)))
			}

		})
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun updateUI(newList: List<ExtensionUI>) {
		super.updateUI(newList.sortedBy { it.name }.sortedBy { it.lang }.sortedBy { !it.installed }.sortedBy { !(it.updateState() == ExtensionUI.State.UPDATE) })
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.refresh -> {
			if (viewModel.isOnline())
				viewModel.refreshRepository()
			else toast(R.string.you_not_online)
			true
		}
		R.id.catalogues_search -> true
		else -> false
	}
}