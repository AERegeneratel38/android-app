package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard

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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CataloguePageLoader : AsyncTask<Int, Void, Boolean> {
    // References to objects
    private val catalogueFragment: CatalogueFragment
    private val catalogueHitBottom: CatalogueHitBottom?

    /**
     * Constructor
     *
     * @param catalogueFragment the fragment this is assigned to (reference to parent)
     */
    constructor(catalogueFragment: CatalogueFragment) {
        this.catalogueFragment = catalogueFragment
        catalogueHitBottom = null
    }

    /**
     * @param catalogueFragment  the fragment this is assigned to (reference to parent)
     * @param catalogueHitBottom The listener to update once new page is loaded
     */
    constructor(catalogueFragment: CatalogueFragment, catalogueHitBottom: CatalogueHitBottom?) {
        this.catalogueFragment = catalogueFragment
        this.catalogueHitBottom = catalogueHitBottom
    }

    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    override fun doInBackground(vararg integers: Int?): Boolean {
        Log.d("Loading", "Catalogue")
        catalogueFragment.library_view.post { catalogueFragment.errorView.visibility = View.GONE }
        if (catalogueFragment.formatter!!.hasCloudFlare()) {
            if (catalogueFragment.activity != null) catalogueFragment.activity!!.runOnUiThread { Toast.makeText(catalogueFragment.context, "CLOUDFLARE", Toast.LENGTH_SHORT).show() }
        }
        val novels: List<Novel> = CatalogueLoader(catalogueFragment.formatter!!).execute(integers[0]).get()

        for (novel in novels) catalogueFragment.catalogueNovelCards.add(CatalogueNovelCard(novel.imageURL, novel.title, Database.DatabaseIdentification.getNovelIDFromNovelURL(novel.link), novel.link))
        catalogueFragment.library_view.post { catalogueFragment.catalogueAdapter!!.notifyDataSetChanged() }
        if (catalogueHitBottom != null) {
            catalogueFragment.library_view.post {
                catalogueFragment.catalogueAdapter!!.notifyDataSetChanged()
                catalogueFragment.library_view.addOnScrollListener(catalogueHitBottom)
            }
            catalogueHitBottom.running = false
            Log.d("CatalogueFragmentLoad", "Completed")
        }
        Log.d("FragmentRefresh", "Complete")
        if (catalogueFragment.activity != null) catalogueFragment.activity!!.runOnUiThread {
            catalogueFragment.catalogueAdapter!!.notifyDataSetChanged()
            catalogueFragment.swipeRefreshLayout.isRefreshing = false
        }
        return true
    }

    /**
     * Ends progress bar
     */
    override fun onCancelled() {
        if (catalogueHitBottom != null) catalogueFragment.bottomProgressBar.visibility = View.INVISIBLE else catalogueFragment.swipeRefreshLayout.isRefreshing = false
    }

    /**
     * Starts the loading action
     */
    override fun onPreExecute() {
        if (catalogueHitBottom != null) catalogueFragment.bottomProgressBar.visibility = View.VISIBLE else catalogueFragment.swipeRefreshLayout.isRefreshing = true
    }

    /**
     * Once done remove progress bar
     *
     * @param aBoolean result of doInBackground
     */
    override fun onPostExecute(aBoolean: Boolean) {
        if (catalogueHitBottom != null) {
            catalogueFragment.bottomProgressBar.visibility = View.GONE
            if (catalogueFragment.catalogueNovelCards.size > 0) catalogueFragment.empty.visibility = View.GONE
        } else catalogueFragment.swipeRefreshLayout.isRefreshing = false
    }


}