package com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters

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

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsController
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.viewHolders.DownloadItemView

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadAdapter(private val downloadsController: DownloadsController)
	: RecyclerView.Adapter<DownloadItemView>() {

	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DownloadItemView {
		val view = LayoutInflater.from(viewGroup.context).inflate(
				R.layout.recycler_download_card,
				viewGroup,
				false
		)
		return DownloadItemView(view)
	}

	override fun onBindViewHolder(downloadItemView: DownloadItemView, i: Int) {
		downloadsController.recyclerArray[i].let { downloadUI ->
			downloadItemView.apply {
				Log.d(logID(), "DownloadUI NName ${downloadUI.novelName}")
				Log.d(logID(), "DownloadUI CName ${downloadUI.chapterName}")

				novelTitle.text = downloadUI.novelName
				chapterTitle.text = downloadUI.chapterName
				status.text = downloadUI.status.toString()
				popupMenu?.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.delete -> {
							downloadsController.viewModel.delete(downloadUI)
							true
						}
						R.id.pause -> {
							downloadsController.viewModel.pause(downloadUI)
							true
						}
						R.id.start -> {
							downloadsController.viewModel.start(downloadUI)
							true
						}
						else -> {
							false
						}
					}
				}
				moreOptions.setOnClickListener { popupMenu?.show() }

			}
		}
	}

	override fun getItemCount(): Int = downloadsController.recyclerArray.size

	override fun getItemId(position: Int): Long =
			downloadsController.recyclerArray[position].chapterID.toLong()
}