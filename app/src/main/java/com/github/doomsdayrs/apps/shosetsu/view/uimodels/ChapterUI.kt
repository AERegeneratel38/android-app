package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import android.view.View
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.variables.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerUI
import com.github.doomsdayrs.apps.shosetsu.view.viewholders.ChapterViewHolder

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
		val link: String,
		val novelID: Int,
		val formatter: Formatter,
		var title: String,
		var releaseDate: String,
		var order: Double,
		var readingPosition: Int,
		var readingReadingStatus: ReadingStatus,
		var bookmarked: Boolean,
		var isSaved: Boolean,
		var savePath: String
) : BaseRecyclerUI<ChapterViewHolder>() {
	override fun getViewHolder(v: View): ChapterViewHolder {
		TODO("Not yet implemented")
	}

}