package com.github.doomsdayrs.apps.shosetsu.variables

import org.luaj.vm2.LuaError

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
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */

/**
 * Cleans a string
 * @return string without specials
 */
fun String.clean(): String {
    return replace("[^A-Za-z0-9]".toRegex(), "_")
}

fun LuaError.smallMessage(): String {
    return this.message?.let { return it.substring(it.lastIndexOf("}:")) } ?: "UNKNOWN ERROR"
}