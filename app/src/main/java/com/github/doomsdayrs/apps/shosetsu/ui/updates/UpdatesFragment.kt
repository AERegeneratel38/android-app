package com.github.doomsdayrs.apps.shosetsu.ui.updates

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.UpdateManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedDaysPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import org.joda.time.DateTime
import java.util.*

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */
/**
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatesFragment : Fragment() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.updater_now) {
            if (context != null) {
                UpdateManager.init(Database.DatabaseNovels.getIntLibrary(), context!!)
                true
            } else false
        } else false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_updater, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        Utilities.setActivityTitle(activity, "Updates")
        viewPager = view.findViewById(R.id.viewpager)
        tabLayout = view.findViewById(R.id.tabLayout)
        setViewPager()
        return view
    }

    private fun setViewPager() {
        val updatesFragments = ArrayList<UpdateFragment>()
        val days = DatabaseUpdates.getTotalDays()
        Log.d("TotalDays", days.toString())
        var startTime = DatabaseUpdates.getStartingDay()
        Log.d("StartingDay", DateTime(startTime).toString())
        for (x in 0 until days) {
            val updateFragment = UpdateFragment()
            updateFragment.setDate(startTime)
            startTime += 86400000
            updatesFragments.add(updateFragment)
        }
        // Removing empty days
        for (x in updatesFragments.size - 1 downTo 1) {
            val updateFragment = updatesFragments[x]
            try {
                val c = DatabaseUpdates.getCountBetween(updateFragment.date, updateFragment.date + 86399999)
                if (c <= 0) {
                    updatesFragments.removeAt(x)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // TODAY
        val updateFragment = UpdateFragment()
        updateFragment.setDate(DatabaseUpdates.trimDate(DateTime(System.currentTimeMillis())).millis)
        updatesFragments.add(updateFragment)
        updatesFragments.reverse()
        val pagerAdapter = UpdatedDaysPager(childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, updatesFragments)
        viewPager!!.adapter = pagerAdapter
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        tabLayout!!.post { tabLayout!!.setupWithViewPager(viewPager) }
    }

    init {
        setHasOptionsMenu(true)
    }
}