package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders;
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


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

public class LibNovelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final MaterialCardView materialCardView;
    public final ImageView library_card_image;
    public final TextView library_card_title;
    public final Chip chip;

    public LibraryFragment libraryFragment;
    @Nullable
    public Formatter formatter;
    public NovelCard novelCard;

    public LibNovelViewHolder(@NonNull View itemView) {
        super(itemView);
        materialCardView = itemView.findViewById(R.id.novel_item_card);
        library_card_image = itemView.findViewById(R.id.image);
        library_card_title = itemView.findViewById(R.id.title);

        chip = itemView.findViewById(R.id.novel_item_left_to_read);
        chip.setOnClickListener(view -> Toast.makeText(view.getContext(), libraryFragment.getResources().getString(R.string.chapters_unread_label) + chip.getText(), Toast.LENGTH_SHORT).show());
        itemView.setOnLongClickListener(view -> {
            addToSelect();
            return true;
        });
    }

    public void addToSelect() {
        if (!libraryFragment.contains(novelCard.novelID))
            libraryFragment.selectedNovels.add(novelCard.novelID);
        else
            removeFromSelect();

        if (libraryFragment.selectedNovels.size() == 1 || libraryFragment.selectedNovels.size() <= 0)
            libraryFragment.onCreateOptionsMenu(libraryFragment.menu, libraryFragment.getInflater());
        libraryFragment.recyclerView.post(() -> libraryFragment.libraryNovelCardsAdapter.notifyDataSetChanged());
    }

    private void removeFromSelect() {
        if (libraryFragment.contains(novelCard.novelID))
            for (int x = 0; x < libraryFragment.selectedNovels.size(); x++)
                if (libraryFragment.selectedNovels.get(x) == novelCard.novelID) {
                    libraryFragment.selectedNovels.remove(x);
                    return;
                }
    }

    @Override
    public void onClick(View v) {
        NovelFragment novelFragment = new NovelFragment();
        novelFragment.formatter = formatter;
        novelFragment.novelURL = novelCard.novelURL;
        novelFragment.novelID = novelCard.novelID;
        assert libraryFragment.getFragmentManager() != null;
        libraryFragment.getFragmentManager().beginTransaction()
                .addToBackStack("tag")
                .replace(R.id.fragment_container, novelFragment)
                .commit();
    }
}
