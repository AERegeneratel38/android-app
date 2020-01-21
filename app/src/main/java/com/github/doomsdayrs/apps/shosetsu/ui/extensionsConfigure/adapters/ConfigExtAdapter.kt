package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.viewHolders.ConfigExtView

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
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ConfigExtAdapter(val configureExtensions: ConfigureExtensions) : RecyclerView.Adapter<ConfigExtView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigExtView {
        return ConfigExtView(LayoutInflater.from(parent.context).inflate(R.layout.alert_extensions_handle_card, parent, false))
    }

    override fun getItemCount(): Int {
        return configureExtensions.jsonArray.length()
    }

    override fun onBindViewHolder(holder: ConfigExtView, position: Int) {
    }

}