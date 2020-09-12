package app.shosetsu.android.view.uimodels.model

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.shosetsu.android.common.consts.selectedStrokeWidth
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.FastAdapter

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class ChapterUI(
		val id: Int,
		val novelID: Int,
		val link: String,
		val formatterID: Int,
		var title: String,
		var releaseDate: String,
		var order: Double,
		var readingPosition: Int,
		var readingStatus: ReadingStatus,
		var bookmarked: Boolean,
		var isSaved: Boolean,
) : BaseRecyclerItem<ChapterUI.ViewHolder>(), Convertible<ChapterEntity> {
	override fun convertTo(): ChapterEntity =
			ChapterEntity(
					id,
					link,
					novelID,
					formatterID,
					title,
					releaseDate,
					order,
					readingPosition,
					readingStatus,
					bookmarked,
					isSaved
			)

	override val layoutRes: Int
		get() = R.layout.recycler_novel_chapter

	override val type: Int
		get() = -1

	override var identifier: Long
		get() = id.toLong()
		set(@Suppress("UNUSED_PARAMETER") value) {}

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<ChapterUI>(itemView) {
		private var cardView: MaterialCardView = itemView.findViewById(R.id.recycler_novel_chapter_card)
		var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint)
		var title: TextView = itemView.findViewById(R.id.title)
		private var readProgressValue: TextView = itemView.findViewById(R.id.read_progress_value)
		private var readTag: TextView = itemView.findViewById(R.id.read_progress_label)
		private var downloadTag: TextView = itemView.findViewById(R.id.download_status)
		private var moreOptions: ImageView = itemView.findViewById(R.id.more_options)

		var popupMenu: PopupMenu? = null

		private var oldColors: ColorStateList? = null

		init {
			if (popupMenu == null) {
				popupMenu = PopupMenu(moreOptions.context, moreOptions)
				popupMenu!!.inflate(R.menu.popup_chapter_menu)
			}
		}

		override fun bindView(item: ChapterUI, payloads: List<Any>) {
			//Log.d(logID(), "Binding ${chapterUI.id}")

			cardView.strokeWidth = if (item.isSelected) selectedStrokeWidth else 0
			if (item.isSelected) {
				cardView.isSelected
			}


			title.text = item.title
			oldColors = title.textColors
			if (item.bookmarked) {
				title.setTextColor(ContextCompat.getColor(
						itemView.context,
						R.color.bookmarked
				))
			}

			if (item.isSaved) {
				//Log.d(logID(), "Chapter is downloaded")
				popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
						.title = "Delete"

			} else {
				//Log.d(logID(), "Chapter is!downloaded")
				popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
						.title = "Download"
			}

			downloadTag.visibility = if (item.isSaved) View.VISIBLE else View.INVISIBLE

			when (item.readingStatus) {
				ReadingStatus.READING -> {
					setAlpha(1f)
					readTag.visibility = View.VISIBLE
					readProgressValue.visibility = View.VISIBLE
					readProgressValue.text = item.readingPosition.toString()
				}
				ReadingStatus.UNREAD -> setAlpha(1f)
				ReadingStatus.READ -> {
					setAlpha(0.5F)
					readTag.visibility = View.GONE
					readProgressValue.visibility = View.GONE
				}
				else -> {
				}
			}

			moreOptions.setOnClickListener { popupMenu?.show() }
		}

		private fun setAlpha(float: Float) {
			title.alpha = float
			readProgressValue.alpha = float
			readTag.alpha = float
			downloadTag.alpha = float
			moreOptions.imageAlpha = (255 * float).toInt()
		}

		override fun unbindView(item: ChapterUI) {
			title.text = null
			oldColors?.let { title.setTextColor(it) }
			readProgressValue.text = null
			readTag.visibility = View.GONE
			downloadTag.visibility = View.GONE
			moreOptions.setOnClickListener(null)
		}
	}
}
