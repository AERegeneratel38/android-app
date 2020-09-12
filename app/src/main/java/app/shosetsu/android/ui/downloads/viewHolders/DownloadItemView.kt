package app.shosetsu.android.ui.downloads.viewHolders

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
 */

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R

/**
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val novelTitle: TextView = itemView.findViewById(R.id.novel_title)
	val chapterTitle: TextView = itemView.findViewById(R.id.chapter_title)
	val status: TextView = itemView.findViewById(R.id.status)
	private var moreOptions: ImageView = itemView.findViewById(R.id.more_options)
	private var popupMenu: PopupMenu? = null

	init {
		if (popupMenu == null) {
			popupMenu = PopupMenu(moreOptions.context, moreOptions)
			popupMenu!!.inflate(R.menu.popup_download_menu)
		}
	}
}