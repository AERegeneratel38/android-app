package app.shosetsu.android.ui.reader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.ui.reader.types.base.ReaderType
import app.shosetsu.android.ui.reader.types.model.StringReader
import com.github.doomsdayrs.apps.shosetsu.R

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
		private val chapterReader: ChapterReader,
) : RecyclerView.Adapter<ReaderType>() {
	var textReaders: ArrayList<ReaderType> = ArrayList()

	init {
		setHasStableIds(true)
	}

	private fun chapters() = chapterReader.chapters

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderType {
		Log.d(logID(), "Creating new view holder")
		val r = StringReader(LayoutInflater.from(parent.context).inflate(
				R.layout.chapter_reader_text_view,
				parent,
				false
		))
		textReaders.add(r)
		return r
	}

	override fun getItemCount(): Int = chapters().size

	override fun onBindViewHolder(holder: ReaderType, position: Int) {
		val chapter = chapters()[position]
		holder.attachData(chapter, chapterReader)

		chapterReader.viewModel.getChapterPassage(chapter).observe(chapterReader) {
			when (it) {
				is HResult.Loading -> {
					Log.d(logID(), "Showing loading")
					holder.showProgress()
				}
				is HResult.Empty -> {
					Log.d(logID(), "Empty result")
				}
				is HResult.Error -> {
					Log.d(logID(), "Showing error")
					//	holder.setError(it.message, "Retry") {
					//		TODO("Figure out how to restart the liveData")
					//		}
				}
				is HResult.Success -> {
					Log.d(logID(), "Successfully loaded :D")
					holder.hideProgress()
					holder.setData(it.data)
					holder.itemView.post {
						holder.setProgress(chapter.readingPosition)
					}
				}
			}
		}


		holder.setTextSize(chapterReader.shosetsuSettings.readerTextSize)

		holder.setOnFocusListener {
			chapterReader.animateBottom()
			chapterReader.animateToolbar()
		}

	}

	override fun getItemId(position: Int): Long = chapterReader.chapters[position].id.toLong()
}