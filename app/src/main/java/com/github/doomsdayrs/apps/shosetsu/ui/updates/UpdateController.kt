package com.github.doomsdayrs.apps.shosetsu.ui.updates
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

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.UpdateEntity
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedNovelsAdapter
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import java.util.*

/**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdateController(bundle: Bundle)
	: RecyclerController<UpdatedNovelsAdapter, UpdateEntity>() {
	val updatesViewModel: IUpdatesViewModel by viewModel()

	var date: Long = bundle.getLong("date")

	val novelIDs = ArrayList<Int>()

	override fun onViewCreated(view: View) {
		launchAsync {
			updatesViewModel.getTimeBetweenDates(
					date,
					date + 86399999
			).observe(
					this@UpdateController,
					Observer {
						with(recyclerArray) {
							clear()
							addAll(it)
							filter { !novelIDs.contains(it.novelID) }
									.forEach { novelIDs.add(it.novelID) }
						}
						recyclerView?.post { adapter?.notifyDataSetChanged() }
					}
			)
		}

		adapter = UpdatedNovelsAdapter(this, activity!!)
		recyclerView?.post { adapter?.notifyDataSetChanged() }
				?: Log.e(logID(), "Recyclerview is null")

		Log.d(logID(), "Updates on this day: " + recyclerArray.size.toString())
	}
}