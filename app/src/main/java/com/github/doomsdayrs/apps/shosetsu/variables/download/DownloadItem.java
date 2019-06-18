package com.github.doomsdayrs.apps.shosetsu.variables.download;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;

/**
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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DownloadItem extends DeleteItem {
    public final NovelFragmentChapters novelFragmentChapters;

    public DownloadItem(Formatter formatter, String novelName, String chapterName, String novelURL, String chapterURL, NovelFragmentChapters novelFragmentChapters) {
        super(formatter, novelName, chapterName, novelURL, chapterURL);
        this.novelFragmentChapters = novelFragmentChapters;
    }
}
