package com.github.doomsdayrs.apps.shosetsu.ui.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibCNovelViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import com.squareup.picasso.Picasso
import java.util.*

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
 * ====================================================================
 */

/**
 * shosetsu
 * 23 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryCNovelAdapter(private val novelCards: ArrayList<Int>, private val libraryController: LibraryController, @LayoutRes val layout: Int) : RecyclerView.Adapter<LibCNovelViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LibCNovelViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(layout, viewGroup, false)
        return LibCNovelViewHolder(view)
    }

    override fun onBindViewHolder(libNovelViewHolder: LibCNovelViewHolder, i: Int) {
        val novelCard = Database.DatabaseNovels.getNovel(novelCards[i])
        //Sets values
        run {
            if (novelCard.imageURL.isNotEmpty()) Picasso.get().load(novelCard.imageURL).into(libNovelViewHolder.imageView)
            libNovelViewHolder.libraryFragment = libraryController
            libNovelViewHolder.novelCard = novelCard
            libNovelViewHolder.formatter = DefaultScrapers.getByID(novelCard.formatterID)
            libNovelViewHolder.title.text = novelCard.title
        }
        val count = Database.DatabaseChapter.getCountOfChaptersUnread(novelCard.novelID)
        if (count != 0) {
            libNovelViewHolder.chip.visibility = View.VISIBLE
            libNovelViewHolder.chip.text = count.toString()
        } else libNovelViewHolder.chip.visibility = View.INVISIBLE
        if (libraryController.selectedNovels.contains(novelCard.novelID)) {
            libNovelViewHolder.materialCardView.strokeWidth = Utilities.selectedStrokeWidth
        } else {
            libNovelViewHolder.materialCardView.strokeWidth = 0
        }
        if (libraryController.selectedNovels.size > 0) {
            libNovelViewHolder.itemView.setOnClickListener { libNovelViewHolder.addToSelect() }
        } else {
            libNovelViewHolder.itemView.setOnClickListener(libNovelViewHolder)
        }
    }

    override fun getItemCount(): Int {
        return novelCards.size
    }
}
