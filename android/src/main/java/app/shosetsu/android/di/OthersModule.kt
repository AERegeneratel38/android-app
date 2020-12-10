package app.shosetsu.android.di

import app.shosetsu.android.backend.workers.onetime.AppUpdateWorker
import app.shosetsu.android.backend.workers.onetime.DownloadWorker
import app.shosetsu.android.backend.workers.onetime.UpdateWorker
import app.shosetsu.android.backend.workers.perodic.AppUpdateCycleWorker
import app.shosetsu.android.backend.workers.perodic.UpdateCycleWorker
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
 * 30 / 07 / 2020
 */
@Suppress("PublicApiImplicitType")
val othersModule = Kodein.Module("others") {

	// Workers

	// - onetime
	bind<DownloadWorker.Manager>() with singleton { DownloadWorker.Manager(instance()) }
	bind<AppUpdateWorker.Manager>() with singleton { AppUpdateWorker.Manager(instance()) }
	bind<UpdateWorker.Manager>() with singleton { UpdateWorker.Manager(instance()) }


	// - perodic
	bind<AppUpdateCycleWorker.Manager>() with singleton { AppUpdateCycleWorker.Manager(instance()) }
	bind<UpdateCycleWorker.Manager>() with singleton { UpdateCycleWorker.Manager(instance()) }

}