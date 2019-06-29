package com.github.doomsdayrs.apps.shosetsu.variables;

import android.graphics.Color;
import android.net.ConnectivityManager;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Settings {
    /**
     * Reader text size
     */
    @SuppressWarnings("unused")
    public static float ReaderTextSize = 1;

    /**
     * Reader text color
     */
    public static int ReaderTextColor = Color.BLACK;

    /**
     * Reader background color
     */
    public static int ReaderTextBackgroundColor = Color.WHITE;

    /**
     * global connectivity manager variable
     */
    public static ConnectivityManager connectivityManager;

    /**
     * If download is paused
     */
    public static boolean downloadPaused;
}
