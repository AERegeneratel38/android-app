package com.github.doomsdayrs.apps.shosetsu.ui.migration.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.io.IOException;
import java.util.ArrayList;

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
 * shosetsu
 * 22 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class MigrationViewLoad extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final MigrationView migrationView;
    private final Formatter targetFormat;

    public MigrationViewLoad(MigrationView migrationView) {
        this.migrationView = migrationView;
        this.targetFormat = DefaultScrapers.getByID(migrationView.target);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("Searching with", targetFormat.getName());
        for (int x = 0; x < migrationView.novels.size(); x++) {
            try {
                // Retrieves search results
                ArrayList<Novel> N = (ArrayList<Novel>) targetFormat.search(migrationView.novels.get(x).title);

                // Sets the results
                migrationView.novelResults.set(x, N);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        migrationView.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        migrationView.swipeRefreshLayout.setRefreshing(false);
        migrationView.mappingNovels.post(migrationView.mappingNovelsAdapter::notifyDataSetChanged);
    }
}
