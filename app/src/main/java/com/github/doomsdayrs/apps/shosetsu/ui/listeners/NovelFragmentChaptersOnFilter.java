package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.view.MenuItem;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

import java.util.Collections;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChaptersOnFilter implements MenuItem.OnMenuItemClickListener {
    private final NovelFragmentChapters novelFragmentChapters;

    public NovelFragmentChaptersOnFilter(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Collections.reverse(StaticNovel.novelChapters);
        NovelFragmentChapters.reversed = !NovelFragmentChapters.reversed;
        return NovelFragmentChapters.recyclerView.post(() -> {
            NovelFragmentChapters.adapter.notifyDataSetChanged();
        });
    }
}
