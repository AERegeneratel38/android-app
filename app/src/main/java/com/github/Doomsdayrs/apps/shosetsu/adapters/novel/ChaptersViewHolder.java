package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.download.DeleteItem;
import com.github.Doomsdayrs.apps.shosetsu.download.DownloadItem;
import com.github.Doomsdayrs.apps.shosetsu.download.Downloadmanager;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapterView;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapters;
import com.github.Doomsdayrs.apps.shosetsu.settings.SettingsController;

import org.json.JSONException;
import org.json.JSONObject;

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
public class ChaptersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    NovelChapter novelChapter;
    FragmentManager fragmentManager;
    TextView library_card_title;
    ImageView bookmarked;
    public ImageView download;
    public boolean downloaded;

    NovelFragmentChapters novelFragmentChapters;

    ChaptersViewHolder(@NonNull View itemView) {
        super(itemView);
        library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
        bookmarked = itemView.findViewById(R.id.recycler_novel_chapter_bookmarked);
        download = itemView.findViewById(R.id.recycler_novel_chapter_download);
        itemView.setOnClickListener(this);
        download.setOnClickListener(v -> {
            if (!downloaded) {
                DownloadItem downloadItem = new DownloadItem(NovelChaptersAdapter.formatter, novelFragmentChapters.novelTitle, novelChapter.chapterNum, novelFragmentChapters.novelURL, novelChapter.link, this);
                Downloadmanager.addToDownload(downloadItem);
                downloaded = true;
            } else {
                if (Downloadmanager.delete(new DeleteItem(NovelChaptersAdapter.formatter, novelFragmentChapters.novelTitle, novelChapter.chapterNum, novelFragmentChapters.novelURL, novelChapter.link)))
                    download.setImageResource(R.drawable.ic_outline_arrow_drop_down_circle_24px);
                downloaded = false;
            }

        });
        bookmarked.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("y", 0);
                if (SettingsController.toggleBookmarkChapter(novelChapter.link, jsonObject))
                    bookmarked.setImageResource(R.drawable.ic_bookmark_black_24dp);
                else
                    bookmarked.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(novelFragmentChapters.getActivity(), NovelFragmentChapterView.class);
        intent.putExtra("url", novelChapter.link);
        intent.putExtra("novelURL", novelFragmentChapters.novelURL);
        intent.putExtra("formatter", NovelChaptersAdapter.formatter.getID());
        intent.putExtra("downloaded", downloaded);
        novelFragmentChapters.startActivity(intent);
    }
}
