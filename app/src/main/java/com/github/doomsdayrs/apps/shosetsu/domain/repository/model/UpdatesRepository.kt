package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IUpdatesRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.UpdatesDao
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.UpdateEntity

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
class UpdatesRepository(val updatesDao: UpdatesDao) : IUpdatesRepository {
	override fun addUpdate(updateEntity: UpdateEntity) {
		TODO("Not yet implemented")
	}

	override fun getUpdates(): LiveData<List<UpdateEntity>> {
		TODO("Not yet implemented")
	}

}