package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer.SDBuilder
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CatalogueAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CataloguePageLoader
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueRefresh
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.ext.*
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
import com.google.android.material.navigation.NavigationView

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
//TODO fix issue with not loading
class CatalogueController(bundle: Bundle) : ViewedController(bundle), SecondDrawerController {
    companion object {
        private const val logID = "CatalogueController"
        private const val LISTING_SEL_ID = -2
    }


    override val layoutRes: Int = R.layout.catalogue

    @Attach(R.id.swipeRefreshLayout)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @Attach(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    lateinit var catalogueAdapter: CatalogueAdapter

    private var cataloguePageLoader: CataloguePageLoader? = null
    var catalogueNovelCards = ArrayList<NovelListingCard>()

    var selectedListing: Int
    var formatter: Formatter


    var currentMaxPage = 1
    var isInSearch = false
    private var dontRefresh = false
    var isQuery = false

    init {
        setHasOptionsMenu(true)
        formatter = DefaultScrapers.getByID(bundle.getInt("formatter"))
        selectedListing = formatter.defaultListing
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("list", catalogueNovelCards)
        outState.putInt("formatter", formatter.formatterID)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        catalogueNovelCards = (savedInstanceState.getSerializable("list") as ArrayList<NovelListingCard>)
        formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))

    }

    override fun onViewCreated(view: View) {
        Utilities.setActivityTitle(activity, formatter.name)
        swipeRefreshLayout?.setOnRefreshListener(CatalogueRefresh(this))
        if (!dontRefresh) {
            Log.d("Process", "Loading up latest")
            setLibraryCards(catalogueNovelCards)
            if (catalogueNovelCards.size > 0) {
                catalogueNovelCards = ArrayList()
                catalogueAdapter.notifyDataSetChanged()
            }
            if (!formatter.hasCloudFlare) {
                executePageLoader()
            } else {
                val intent = Intent(activity, WebViewApp::class.java)
                // TODO Formatter require of base URL
                intent.putExtra("url", formatter.imageURL)
                intent.putExtra("action", 1)
                startActivityForResult(intent, 42)
            }
        } else setLibraryCards(catalogueNovelCards)
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        Log.d("Pause", "HERE")
        dontRefresh = true
        cataloguePageLoader?.cancel(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        dontRefresh = false
        cataloguePageLoader?.cancel(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42) {
            executePageLoader()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.toolbar_library, menu)
        val searchView = menu.findItem(R.id.library_search).actionView as SearchView
        searchView.setOnQueryTextListener(CatalogueSearchQuery(this))
        searchView.setOnCloseListener {
            isQuery = false
            isInSearch = false
            setLibraryCards(catalogueNovelCards)
            true
        }
    }

    fun setLibraryCards(recycleListingCards: ArrayList<NovelListingCard>) {
        recyclerView?.setHasFixedSize(false)

        if (Settings.novelCardType == 0) {
            catalogueAdapter = CatalogueAdapter(recycleListingCards, this, formatter, R.layout.recycler_novel_card)
            recyclerView?.layoutManager = GridLayoutManager(context, Utilities.calculateNoOfColumns(context!!, 200f), RecyclerView.VERTICAL, false)
        } else {
            catalogueAdapter = CatalogueAdapter(recycleListingCards, this, formatter, R.layout.recycler_novel_card_compressed)
            recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView?.adapter = catalogueAdapter
        recyclerView?.addOnScrollListener(CatalogueHitBottom(this))

    }

    fun executePageLoader() {
        when {
            cataloguePageLoader?.isCancelled == false -> cataloguePageLoader = CataloguePageLoader(this)
            cataloguePageLoader == null -> cataloguePageLoader = CataloguePageLoader(this)
        }
        cataloguePageLoader?.execute(currentMaxPage)
    }


    var listingMap: MutableMap<Int, Any> = mutableMapOf()

    override fun createTabs(navigationView: NavigationView, drawerLayout: DrawerLayout) {
        val builder = SDBuilder(navigationView, drawerLayout, this)

        // Listing selection
        val a = ArrayList<String>()
        formatter.listings.forEach { a.add(it.name) }

        builder
                .addSpinner("Listing", a.toTypedArray(), this.selectedListing, LISTING_SEL_ID)
                .createInner(R.string.listings) { menu ->
                    val method = {
                        formatter.listings[this.selectedListing].let { listing ->
                            listing.filters.forEach { filter -> filter.build(menu) }
                        }
                    }
                    method()
                    (builder.layout[0].findViewById<Spinner>(R.id.spinner)).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            selectedListing = position
                            menu.removeAll()
                            method()
                        }
                    }
                    menu
                }
                .createInner(R.string.search_filters) {
                    formatter.filters.forEach { filter -> filter.build(it) }
                    it
                }

        navigationView.addView(builder.build())
        listingMap = formatter.getListing().filters.defaultMap()
    }

    override fun handleConfirm(linearLayout: LinearLayout) {
        val a = linearLayout.findFilters()
        selectedListing = a[LISTING_SEL_ID] as Int
        listingMap.putAll(a)
        listingMap.forEach{
            Log.d(logID,"${it.key}\t=\t${it.value}")
        }
        catalogueNovelCards = arrayListOf()
        setLibraryCards(catalogueNovelCards)
        catalogueAdapter.notifyDataSetChanged()
        executePageLoader()
    }

}