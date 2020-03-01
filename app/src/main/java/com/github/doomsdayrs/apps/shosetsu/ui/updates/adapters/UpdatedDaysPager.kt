package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

import android.util.Log
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateFragment
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesFragment
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import org.joda.time.DateTime

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
 */ /**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedDaysPager(private val updateFragment: UpdatesFragment, val fragments: Array<UpdateFragment>) : RouterPagerAdapter(updateFragment) {
    override fun getPageTitle(position: Int): CharSequence? {
        val dateTime = DateTime(fragments[position].date)
        if (dateTime == DatabaseUpdates.trimDate(DateTime(System.currentTimeMillis()))) {
            return updateFragment.getString(R.string.today, "Today")
        } else if (dateTime == DatabaseUpdates.trimDate(DateTime(System.currentTimeMillis())).minusDays(1)) {
            return updateFragment.getString(R.string.yesterday, "Yesterday")
        }
        return dateTime.dayOfMonth.toString() + "/" + dateTime.monthOfYear + "/" + dateTime.year
    }

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            Log.d("SwapScreen", fragments[position].toString())
            val controller = fragments[position]
            router.setRoot(RouterTransaction.with(controller))
        }
    }

    override fun getCount(): Int {
        return fragments.size
    }
}