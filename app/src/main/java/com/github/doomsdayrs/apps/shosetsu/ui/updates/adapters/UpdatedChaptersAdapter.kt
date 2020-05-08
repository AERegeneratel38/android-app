package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

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

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.common.utils.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedChapterHolder
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedNovelHolder
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedChaptersAdapter(
		private val updatedNovelHolder: UpdatedNovelHolder,
		val viewModel: IUpdatesViewModel
) : RecyclerView.Adapter<UpdatedChapterHolder>() {
	var size = if (updatedNovelHolder.updates.size > 20) 5 else updatedNovelHolder.updates.size
	val downloadManager: DownloadManager = TODO("IMPLEMENT")

	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UpdatedChapterHolder {
		val view = LayoutInflater.from(viewGroup.context).inflate(
				R.layout.update_card,
				viewGroup,
				false
		)
		val updatedChapterHolder = UpdatedChapterHolder(view)
		if (!set) {
			DefaultTextColor = updatedChapterHolder.title.currentTextColor
			Log.i("TextDefaultColor", DefaultTextColor.toString())
			set = !set
		}
		return updatedChapterHolder
	}

	override fun onBindViewHolder(updatedChapterHolder: UpdatedChapterHolder, i: Int) {
		val updateUI = updatedNovelHolder.updates[i]
		updatedNovelHolder
		Log.d("Binding", updateUI.toString())
		val chapterUI: UpdateChapterUI = viewModel.getChapter(updateUI.chapterID)
		// chapterName chapterURL chapterID novelID

		with(updatedChapterHolder) {
			val activity = itemView.context as Activity
			title.text = chapterUI.title
			//TODO fix this disgust

			itemView.setOnClickListener {
				(itemView.context as Activity).openChapter(chapterUI)
			}

			popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
				val novelName: String = updatedNovelHolder.novelName

				when (menuItem.itemId) {
					R.id.popup_chapter_menu_bookmark -> {
						chapterUI.bookmarked = !chapterUI.bookmarked
						if (chapterUI.bookmarked)
							title.setTextColor(itemView.resources.getColor(R.color.bookmarked))
						else {
							Log.i("SetDefault", DefaultTextColor.toString())
							title.setTextColor(DefaultTextColor)
						}
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_download -> {
						run {
							if (!chapterUI.isSaved) {
								val downloadItem: DownloadUI = with(chapterUI) {
									DownloadUI(id, novelID, link, title, "TODO", formatterID)
								}
								downloadManager.addToDownload(updatedNovelHolder.activity, downloadItem)
							} else {
								if (downloadManager.delete(
												itemView.context,
												with(chapterUI) {
													DownloadUI(id, novelID, link, title, "TODO", formatterID)
												}
										)) {
									downloadTag.visibility = View.INVISIBLE
								}
							}
						}
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_read -> {
						viewModel
						viewModel.updateChapter(updateUI, ReadingStatus.READ)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_unread -> {
						viewModel.updateChapter(updateUI, ReadingStatus.UNREAD)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_reading -> {
						viewModel.updateChapter(updateUI, ReadingStatus.READING)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.browser -> {
						activity.openInBrowser(chapterUI.link)
						return@setOnMenuItemClickListener true
					}
					R.id.webview -> {
						activity.openInWebView(chapterUI.link)
						return@setOnMenuItemClickListener true
					}
					else -> return@setOnMenuItemClickListener false
				}
			}
			moreOptions.setOnClickListener { updatedChapterHolder.popupMenu.show() }
		}
	}

	override fun getItemCount(): Int = size

	companion object {
		var DefaultTextColor = 0
		private var set = false
	}

}