package com.github.doomsdayrs.apps.shosetsu.ui.library.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.consts.selectedStrokeWidth
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.observe
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibraryItemViewHolder
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel
import com.squareup.picasso.Picasso

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
 */

/**
 * shosetsu
 * 23 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryNovelAdapter(
		private val libraryController: LibraryController,
		@LayoutRes private val layout: Int,
		private val viewModel: ILibraryViewModel = libraryController.viewModel,
		var novels: List<IDTitleImageUI> = arrayListOf()
) : RecyclerView.Adapter<LibraryItemViewHolder>() {
	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LibraryItemViewHolder {
		Log.d(logID(), "Creating view holder for ${novels[i].id}")

		return LibraryItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(
				layout,
				viewGroup,
				false
		))
	}

	override fun onBindViewHolder(viewHolder: LibraryItemViewHolder, i: Int) {
		novels[i].let { (id, title, imageURL) ->
			Log.d(logID(), "Binding view for $id")
			//Sets values
			run {
				if (imageURL.isNotEmpty())
					Picasso.get().load(imageURL).into(viewHolder.imageView)
				viewHolder.title.text = title
			}

			setUnreadCount(viewHolder)

			// Loads Chapters Unread for a specific novel
			viewModel.loadChaptersUnread(id).observe(libraryController) {
				when (it) {
					is HResult.Loading -> Log.d(logID(), "Novel $id is loading unread")
					is HResult.Empty -> Log.d(logID(), "Novel $id has no data")
					is HResult.Error -> {
						TODO("Logging")
					}
					is HResult.Success -> {
						viewHolder.unreadCount = it.data
						Log.d(logID(), "Novel $id has received #${viewHolder.unreadCount} unread")
						setUnreadCount(viewHolder)
					}
				}
			}

			run {
				val selectedList = libraryController.selectedNovels

				viewHolder.materialCardView.strokeWidth = if (selectedList.contains(id))
					selectedStrokeWidth else 0

				viewHolder.itemView.setOnLongClickListener {
					libraryController.viewModel.handleSelect(id)
					true
				}

				if (selectedList.isNotEmpty()) {
					viewHolder.itemView.setOnClickListener {
						libraryController.viewModel.handleSelect(id)
					}
				} else {
					viewHolder.itemView.setOnClickListener {
						libraryController.router.pushController(NovelController(
								bundleOf(BundleKeys.BUNDLE_NOVEL_ID to id)
						).withFadeTransaction())
					}
				}
			}

			viewHolder.chip.setOnClickListener {
				it.context.toast(it.context.getString(R.string.chapters_unread_label) +
						viewHolder.chip.text)
			}
		}
	}

	private fun setUnreadCount(viewHolder: LibraryItemViewHolder) {
		Log.d(logID(), "Setting unread count of ${viewHolder.unreadCount}")
		if (viewHolder.unreadCount != 0) {
			viewHolder.chip.visibility = View.VISIBLE
			viewHolder.chip.text = viewHolder.unreadCount.toString()
		} else viewHolder.chip.visibility = View.INVISIBLE
	}

	override fun getItemCount(): Int {
		Log.d(logID(), "Size i have is ${novels.size}")
		return novels.size
	}
}
