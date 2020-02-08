package com.github.doomsdayrs.apps.shosetsu.ui.library

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.UpdateManager.init
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.ui.library.adapter.LibraryNovelAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.library.listener.LibrarySearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.migration.NewMigrationView
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.fragment_library.*
import java.io.IOException
import java.util.*

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibraryFragment : Fragment(R.layout.fragment_library) {
    var libraryNovelCards = ArrayList<Int>()
    var selectedNovels: ArrayList<Int> = ArrayList()
    var libraryNovelCardsAdapter: LibraryNovelAdapter? = null
    val inflater: MenuInflater?
        get() = MenuInflater(context)

    init {
        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        Log.d("Library", "Paused")
        selectedNovels = ArrayList()
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedNovels = ArrayList()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Library", "Resumed")
        if (libraryNovelCards.isEmpty()) {
            readFromDB()
            setLibraryCards(libraryNovelCards)
        } else
            libraryNovelCardsAdapter!!.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("selected", selectedNovels)
        outState.putIntegerArrayList("lib", libraryNovelCards)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, getString(R.string.my_library))
        if (savedInstanceState == null) readFromDB() else {
            libraryNovelCards = savedInstanceState.getIntegerArrayList("lib")!!
            selectedNovels = savedInstanceState.getIntegerArrayList("selected")!!
        }
        setLibraryCards(libraryNovelCards)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (selectedNovels.size <= 0) {
            Log.d("LibraryFragment", "Creating default menu")
            inflater.inflate(R.menu.toolbar_library, menu)
            val searchView = menu.findItem(R.id.library_search).actionView as SearchView?
            searchView?.setOnQueryTextListener(LibrarySearchQuery(this))
            searchView?.setOnCloseListener {
                setLibraryCards(libraryNovelCards)
                false
            }
        } else {
            Log.d("LibraryFragment", "Creating selected menu")
            inflater.inflate(R.menu.toolbar_library_selected, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.updater_now -> {
                init(context!!, libraryNovelCards)
                return true
            }
            R.id.chapter_select_all -> {
                for (i in libraryNovelCards) if (!selectedNovels.contains(i)) selectedNovels.add(i)
                recyclerView!!.post { libraryNovelCardsAdapter!!.notifyDataSetChanged() }
                return true
            }
            R.id.chapter_deselect_all -> {
                selectedNovels = ArrayList()
                recyclerView!!.post { libraryNovelCardsAdapter!!.notifyDataSetChanged() }
                if (inflater != null) activity?.invalidateOptionsMenu()
                return true
            }
            R.id.remove_from_library -> {
                for (i in selectedNovels) {
                    DatabaseNovels.unBookmark(i)
                    var x = 0
                    while (x < libraryNovelCards.size) {
                        if (libraryNovelCards[x] == i) libraryNovelCards.removeAt(x)
                        x++
                    }
                }
                selectedNovels = ArrayList()
                recyclerView!!.post { libraryNovelCardsAdapter!!.notifyDataSetChanged() }
                return true
            }
            R.id.source_migrate -> {
                val intent = Intent(activity, NewMigrationView::class.java)
                try {
                    intent.putIntegerArrayListExtra("selection", selectedNovels)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                intent.putExtra("target", 1)
                startActivity(intent)
                //Utilities.regret(context!!)
                return true
            }
        }
        return false
    }

    private fun readFromDB() {
        libraryNovelCards = DatabaseNovels.getIntLibrary()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            libraryNovelCards.sortWith(Comparator { novel: Int?, t1: Int? -> DatabaseNovels.getNovelTitle(novel!!).compareTo(DatabaseNovels.getNovelTitle(t1!!)) })
        } else {
            for (i in libraryNovelCards.size - 1 downTo 2) {
                for (j in 0 until i) {
                    if (DatabaseNovels.getNovelTitle(libraryNovelCards[j]) > DatabaseNovels.getNovelTitle(libraryNovelCards[j + 1])) {
                        val indexOne = libraryNovelCards[j]
                        libraryNovelCards[j] = libraryNovelCards[j + 1]
                        libraryNovelCards[j + 1] = indexOne
                    }
                }
            }
        }

    }

    /**
     * Sets the cards to display
     */
    fun setLibraryCards(novelCards: ArrayList<Int>?) {
        recyclerView!!.setHasFixedSize(false)

        if (Settings.novelCardType == 0) {
            libraryNovelCardsAdapter = LibraryNovelAdapter(novelCards!!, this, R.layout.recycler_novel_card)
            recyclerView!!.layoutManager = GridLayoutManager(context, Utilities.calculateNoOfColumns(context!!, 200f), RecyclerView.VERTICAL, false)
        } else {
            libraryNovelCardsAdapter = LibraryNovelAdapter(novelCards!!, this, R.layout.recycler_novel_card_compressed)
            recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView!!.adapter = libraryNovelCardsAdapter

    }
}