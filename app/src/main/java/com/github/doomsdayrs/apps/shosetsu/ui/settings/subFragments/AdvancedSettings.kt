package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsItemData
import kotlinx.android.synthetic.main.settings_advanced.*


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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 * <p>
 *     TODO add text size options
 * </p>
 */
class AdvancedSettings : Fragment() {
    val settings: ArrayList<SettingsItemData> = arrayListOf(
            SettingsItemData(SettingsItemData.SettingsType.SPINNER)
                    .setTitle(R.string.theme)
                    .setOnItemSelectedListener(
                            object : OnItemSelectedListener {
                                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                    if (i in 0..2) {
                                        Utilities.changeMode(activity!!, i)
                                        adapterView.setSelection(i)
                                    }
                                }

                                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                            })
                    .setArrayAdapter(ArrayAdapter(context!!, android.R.layout.simple_spinner_item, arrayListOf("Light", "Dark")))
                    .setSpinnerSelection(Settings.themeMode),
            SettingsItemData(SettingsItemData.SettingsType.BUTTON)
                    .setTitle(R.string.remove_novel_cache)
                    .setOnClickListenerButton { view1: View? -> Database.DatabaseIdentification.purgeUnSavedNovels(view1) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings_advanced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SettingItemsAdapter(settings)
    }


}