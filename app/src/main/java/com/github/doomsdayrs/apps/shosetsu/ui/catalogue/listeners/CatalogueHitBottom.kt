package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController

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
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueHitBottom(private val catalogueFragment: CatalogueController) : RecyclerView.OnScrollListener() {
    private var running = false
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!catalogueFragment.isQuery && !catalogueFragment.isInSearch) if (!running) if (!catalogueFragment.recyclerView!!.canScrollVertically(1)) {
            Log.d("CatalogueFragmentLoad", "Getting next page")
            running = true
            catalogueFragment.currentMaxPage++
            catalogueFragment.executePageLoader()
        }
    }

}