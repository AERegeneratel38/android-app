package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueRefresh implements SwipeRefreshLayout.OnRefreshListener {
    final CatalogueFragment catalogueFragment;

    public CatalogueRefresh(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public void onRefresh() {
        catalogueFragment.swipeRefreshLayout.setRefreshing(true);

        CatalogueFragment.catalogueNovelCards = new ArrayList<>();
        catalogueFragment.currentMaxPage = 1;
        try {
            Log.d("FragmentRefresh", "Refreshing catalogue data");
            if (new CataloguePageLoader(catalogueFragment, CatalogueFragment.formatter, CatalogueFragment.catalogueNovelCards).execute().get()) {
                Log.d("FragmentRefresh", "Complete");
                catalogueFragment.library_Adapter.notifyDataSetChanged();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }
}
