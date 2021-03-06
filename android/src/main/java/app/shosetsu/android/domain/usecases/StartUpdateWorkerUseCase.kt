package app.shosetsu.android.domain.usecases

import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker.Manager

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
 * 23 / 06 / 2020
 */
class StartUpdateWorkerUseCase(
	private val manager: Manager
) {
	/**
	 * Starts the update worker
	 * @param override if true then will override the current update loop
	 */
	operator fun invoke(override: Boolean = false) {
		if (!manager.isRunning() || override)
			manager.start()
	}
}