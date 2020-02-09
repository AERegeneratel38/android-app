package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.variables.toast

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
 */ /**
 * shosetsu
 * 18 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueHolder(itemView: View, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val title: TextView = itemView.findViewById(R.id.textView)

    lateinit var formatter: Formatter

    init {
        itemView.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        Log.d("FormatterSelection", formatter.name)
        if (Utilities.isOnline) {
            val catalogueFragment = CatalogueFragment()
            catalogueFragment.formatter = (formatter)
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, catalogueFragment)
                    .commit()
        } else v.context.toast(R.string.you_not_online)
    }

}