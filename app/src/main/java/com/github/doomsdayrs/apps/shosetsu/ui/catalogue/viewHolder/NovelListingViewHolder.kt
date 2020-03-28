package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder

import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.NovelBackgroundAdd
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction

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
 * 06 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
    var url: String? = null
    var novelID = 0
    val imageView: ImageView = itemView.findViewById(R.id.image)
    val title: TextView = itemView.findViewById(R.id.title)

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    lateinit var catalogueFragment: CatalogueController
    lateinit var formatter: Formatter

    override fun onClick(v: View) {
        val novelFragment = NovelController()
        novelFragment.novelURL = url!!
        novelFragment.formatter = formatter
        novelFragment.novelID = Database.DatabaseIdentification.getNovelIDFromNovelURL(url!!)
        catalogueFragment.router.pushController(novelFragment.withFadeTransaction())
    }

    override fun onLongClick(view: View): Boolean {
        NovelBackgroundAdd(this).execute(view)
        return true
    }

}