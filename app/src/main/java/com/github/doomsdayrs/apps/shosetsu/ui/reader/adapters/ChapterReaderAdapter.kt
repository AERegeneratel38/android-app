package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewTextReader

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
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 * @param chapterReader ChapterReader
 */
class ChapterReaderAdapter(
		fm: FragmentManager,
		l: Lifecycle,
		val chapterReader: ChapterReader
) : RecyclerView.Adapter<NewTextReader>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTextReader {
		TODO("Not yet implemented")
	}

	override fun getItemCount(): Int = chapterReader.chapterIDs.size

	override fun onBindViewHolder(holder: NewTextReader, position: Int) {
		TODO("Not yet implemented")
	}

}