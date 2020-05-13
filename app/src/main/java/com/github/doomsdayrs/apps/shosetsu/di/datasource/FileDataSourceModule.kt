package com.github.doomsdayrs.apps.shosetsu.di.datasource

import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileChapterDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtensionDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.model.FileChapterDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.model.FileExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.model.FileExtensionDataSource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

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
 * 12 / 05 / 2020
 */
val fileDataSourceModule = Kodein.Module("file_data_source") {
	bind<IFileExtensionDataSource>() with singleton {
		FileExtensionDataSource(instance())
	}
	bind<IFileChapterDataSource>() with singleton {
		FileChapterDataSource(instance())
	}
	bind<IFileExtLibDataSource>() with singleton {
		FileExtLibDataSource(instance())
	}
}