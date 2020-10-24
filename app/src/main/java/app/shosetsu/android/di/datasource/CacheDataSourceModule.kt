package app.shosetsu.android.di.datasource

import app.shosetsu.android.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.android.datasource.memory.base.IMemExtLibDataSource
import app.shosetsu.android.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.android.datasource.memory.model.GuavaMemChaptersDataSource
import app.shosetsu.android.datasource.memory.model.GuavaMemExtLibDataSource
import app.shosetsu.android.datasource.memory.model.GuavaMemExtensionDataSource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
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
 * 04 / 05 / 2020
 * These modules handle cached data that is in memory
 */
val cacheDataSouceModule: Kodein.Module = Kodein.Module("cache_data_source_module") {
	bind<IMemChaptersDataSource>() with singleton { GuavaMemChaptersDataSource() }

	bind<IMemExtensionsDataSource>() with singleton { GuavaMemExtensionDataSource() }

	bind<IMemExtLibDataSource>() with singleton { GuavaMemExtLibDataSource() }

}