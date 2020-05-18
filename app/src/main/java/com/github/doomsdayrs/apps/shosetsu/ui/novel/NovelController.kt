package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelController(val bundle: Bundle) : ViewedController(bundle) {
	override val layoutRes: Int = R.layout.novel

	@Attach(R.id.fragment_novel_tabLayout)
	var novelTabLayout: TabLayout? = null

	@Attach(R.id.fragment_novel_viewpager)
	var novelViewpager: ViewPager? = null

	override fun onViewCreated(view: View) {
		novelViewpager?.adapter = NovelPagerAdapter(this)
		novelViewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(novelTabLayout))
		novelTabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				novelViewpager?.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})
		novelTabLayout?.post { novelTabLayout?.setupWithViewPager(novelViewpager) }
	}
}