package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.viewHolders.ConfigExtView
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File

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
        return ConfigExtView(LayoutInflater.from(parent.context).inflate(R.layout.alert_extensions_configure_card, parent, false))
    }

    override fun getItemCount(): Int {
        return configureExtensions.jsonArray.length() + DefaultScrapers.formatters.size
    }

    override fun onBindViewHolder(holder: ConfigExtView, position: Int) {
        var name = ""
        var id = -1
        var image = ""

        var enabled = false

        if (position < configureExtensions.jsonArray.length()) {
            val jsonObject: JSONObject = configureExtensions.jsonArray[position] as JSONObject
            name = jsonObject.getString("name")
            id = jsonObject.getInt("id")
            image = jsonObject.getString("imageUrl")
        } else {
            val fom = DefaultScrapers.formatters[position - configureExtensions.jsonArray.length()]
            name = fom.name
            id = fom.formatterID
            image = fom.imageURL
            enabled = true
        }

        if (image.isNotEmpty())
            Picasso.get().load(image).into(holder.imageView)

        holder.title.text = name

        holder.switch.isChecked = enabled
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.switch.setText(R.string.enabled)
                val file = File(Utilities.shoDir + FormatterController.directory + FormatterController.scriptFolder + name + ".lua")
                FormatterController.confirm(file, object : FormatterController.CheckSumAction {
                    override fun fail() {
                        holder.switch.isChecked = !isChecked
                    }

                    override fun pass() {
                        configureExtensions.jsonArray.remove(findPostion(id))
                        DefaultScrapers.formatters.add(LuaFormatter(file))
                        Settings.disabledFormatters = configureExtensions.jsonArray
                    }

                    override fun noMeta() {
                        holder.switch.isChecked = !isChecked
                    }

                })

            } else {
                holder.switch.setText(R.string.disabled)
                if (findPostion(id) == -1) {
                    val js = JSONObject()
                    js.put("name", name)
                    js.put("id", id)
                    js.put("imageUrl", image)
                    configureExtensions.jsonArray.put(js)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DefaultScrapers.formatters.removeIf { it.formatterID == id }
                    } else {
                        var point = -1
                        for (i in 0 until DefaultScrapers.formatters.size)
                            if (DefaultScrapers.formatters[i].formatterID == id)
                                point = i
                        if (point != -1)
                            DefaultScrapers.formatters.removeAt(point)
                    }
                    Settings.disabledFormatters = configureExtensions.jsonArray
                }
            }
        }
    }

    fun findPostion(id: Int): Int {
        for (i in 0 until configureExtensions.jsonArray.length())
            if ((configureExtensions.jsonArray[i] as JSONObject).getInt("id") == id)
                return i
        return -1
    }

}