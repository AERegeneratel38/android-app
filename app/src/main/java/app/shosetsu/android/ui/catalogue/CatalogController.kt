package app.shosetsu.android.ui.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.listeners.CatalogueHitBottom
import app.shosetsu.android.ui.catalogue.listeners.CatalogueSearchQuery
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerCatalogueBinding
import com.google.android.material.navigation.NavigationView
import org.kodein.di.generic.instance

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
 * ====================================================================
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogController(
		/** data bundle uwu */
		val bundle: Bundle,
) : FastAdapterRecyclerController<ControllerCatalogueBinding, ACatalogNovelUI>(bundle),
		PushCapableController {
	private var searchView: SearchView? = null

	private var navigationView: NavigationView? = null
	private var drawerLayout: DrawerLayout? = null

	lateinit var pushController: (Controller) -> Unit

	/***/
	val viewModel: ICatalogViewModel by viewModel()
	private val settings by instance<ShosetsuSettings>()


	init {
		setHasOptionsMenu(true)
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
		searchView = null
	}

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return if (settings.novelCardType == 0) GridLayoutManager(
				context,
				settings.calculateColumnCount(context!!, 200f),
				VERTICAL,
				false
		)
		else LinearLayoutManager(
				context,
				VERTICAL,
				false
		)
	}

	override fun setupFastAdapter() {
		super.setupFastAdapter()
		fastAdapter.apply {
			setOnClickListener { _, _, item, _ ->
				pushController(NovelController(
						bundleOf(
								BUNDLE_NOVEL_ID to item.id,
								BUNDLE_EXTENSION to bundle.getInt(BUNDLE_EXTENSION)
						)
				))
				true
			}
			onLongClickListener = { _, _, i, _ ->
				viewModel.backgroundNovelAdd(i.id)
				true
			}
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.setFormatterID(bundle.getInt(BUNDLE_EXTENSION))
		binding.swipeRefreshLayout.setOnRefreshListener { viewModel.resetView() }
		setupObservers()
		setupRecyclerView()
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		recyclerView.addOnScrollListener(CatalogueHitBottom(viewModel))
		super.setupRecyclerView()
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_library, menu)
		searchView = menu.findItem(R.id.library_search).actionView as SearchView
		searchView?.setOnQueryTextListener(CatalogueSearchQuery(this))
		searchView?.setOnCloseListener {
			viewModel.setQuery("")
			viewModel.resetView()
			true
		}
	}

	override fun updateUI(newList: List<ACatalogNovelUI>) {
		super.updateUI(newList)
		binding.swipeRefreshLayout.isRefreshing = false
	}

	override fun showError(e: HResult.Error) {
		logE("Error $e")
		super.showError(e)
	}

	override fun showLoading() {
		super.showLoading()
		if (recyclerArray.isEmpty() && !binding.swipeRefreshLayout.isRefreshing)
			binding.swipeRefreshLayout.isRefreshing = true
		else {
			//TODO show bottom loader
		}
	}

	private fun setupObservers() {
		viewModel.listingItemsLive.observe(this) {
			handleRecyclerUpdate(it)
		}
		viewModel.extensionName.observe(this) { result ->
			result.handle(onLoading = {
				setViewTitle(getString(R.string.loading))
			}) {
				setViewTitle(it)
				if (recyclerArray.isEmpty()) viewModel.resetView()
			}
		}
		viewModel.hasSearchLive.observe(this) { result ->
			result.handle {
				searchView?.isEnabled = it
			}
		}
		viewModel.filterItemsLive.observe(this) {
		}
	}

	override fun bindView(inflater: LayoutInflater): ControllerCatalogueBinding =
			ControllerCatalogueBinding.inflate(inflater).also { recyclerView = it.recyclerView }
}
