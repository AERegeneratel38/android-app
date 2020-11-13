package app.shosetsu.android.view.uimodels.model.reader

import android.view.View
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.ui.reader.types.base.TypedReaderViewHolder
import app.shosetsu.android.ui.reader.types.model.StringReader
import app.shosetsu.lib.Novel.ChapterType
import com.github.doomsdayrs.apps.shosetsu.R

/**
 * Data class that holds each chapter and its data
 */

data class ReaderChapterUI(
		val id: Int,
		val link: String,
		val title: String,
		var readingPosition: Int,
		var readingStatus: ReadingStatus,
		var bookmarked: Boolean,
		private val chapterType: ChapterType
) : Convertible<ReaderChapterEntity>, ReaderUIItem<ReaderChapterUI, TypedReaderViewHolder>() {

	var reader: TypedReaderViewHolder? = null

	override val layoutRes: Int by lazy {
		when (chapterType) {
			ChapterType.STRING -> R.layout.chapter_reader_text_view
			ChapterType.HTML -> R.layout.chapter_reader_html
			ChapterType.MARKDOWN -> R.layout.chapter_reader_mark_down

			ChapterType.EPUB -> R.layout.chapter_reader_text_view
			ChapterType.PDF -> R.layout.chapter_reader_text_view
		}
	}

	override val type: Int by lazy {
		when (chapterType) {
			ChapterType.STRING -> R.layout.chapter_reader_text_view
			ChapterType.HTML -> R.layout.chapter_reader_html
			ChapterType.MARKDOWN -> R.layout.chapter_reader_mark_down

			ChapterType.EPUB -> R.layout.chapter_reader_text_view
			ChapterType.PDF -> R.layout.chapter_reader_text_view
		}
	}

	override fun getViewHolder(v: View): TypedReaderViewHolder {
		return when (chapterType) {
			ChapterType.STRING -> StringReader(v)
			else -> TODO()
		}.also { reader = it }
	}

	override fun convertTo(): ReaderChapterEntity = ReaderChapterEntity(
			id,
			link,
			title,
			readingPosition,
			readingStatus,
			bookmarked
	)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ReaderChapterUI

		if (id != other.id) return false
		if (link != other.link) return false
		if (title != other.title) return false
		if (bookmarked != other.bookmarked) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id
		result = 31 * result + link.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + bookmarked.hashCode()
		return result
	}
}