package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.utils.ColumnCalculator
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.base.*
import app.shosetsu.common.enums.InclusionState
import app.shosetsu.common.enums.NovelSortType
import app.shosetsu.common.enums.NovelUIType

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class ILibraryViewModel :
	SubscribeHandleViewModel<List<ABookmarkedNovelUI>>,
	ShosetsuViewModel(),
	IsOnlineCheckViewModel,
	StartUpdateManagerViewModel,
	ErrorReportingViewModel,
	ColumnCalculator {

	/** All genres from all [ABookmarkedNovelUI] combined*/
	abstract val genresFlow: LiveData<List<String>>

	/** All tags from all [ABookmarkedNovelUI] combined*/
	abstract val tagsFlow: LiveData<List<String>>

	/** All authors from all [ABookmarkedNovelUI] combined*/
	abstract val authorsFlow: LiveData<List<String>>

	/** All artists from all [ABookmarkedNovelUI] combined*/
	abstract val artistsFlow: LiveData<List<String>>

	abstract fun getNovelUIType(): NovelUIType

	/**
	 * Remove the following from the library
	 */
	abstract fun removeFromLibrary(list: List<ABookmarkedNovelUI>)

	abstract fun getSortType(): NovelSortType
	abstract fun setSortType(novelSortType: NovelSortType)

	abstract fun isSortReversed(): Boolean
	abstract fun setIsSortReversed(reversed: Boolean)

	abstract fun addGenreToFilter(genre: String, inclusionState: InclusionState): Boolean
	abstract fun removeGenreFromFilter(genre: String): Boolean
	abstract fun getFilterGenres(): List<Pair<String, InclusionState>>

	abstract fun addAuthorToFilter(author: String, inclusionState: InclusionState): Boolean
	abstract fun removeAuthorFromFilter(author: String): Boolean
	abstract fun getFilterAuthors(): List<Pair<String, InclusionState>>

	abstract fun addArtistToFilter(artist: String, inclusionState: InclusionState): Boolean
	abstract fun removeArtistFromFilter(artist: String): Boolean
	abstract fun getFilterArtists(): List<Pair<String, InclusionState>>

	abstract fun addTagToFilter(tag: String, inclusionState: InclusionState): Boolean
	abstract fun removeTagFromFilter(tag: String): Boolean
	abstract fun getFilterTags(): List<Pair<String, InclusionState>>

	abstract fun resetSortAndFilters()

}