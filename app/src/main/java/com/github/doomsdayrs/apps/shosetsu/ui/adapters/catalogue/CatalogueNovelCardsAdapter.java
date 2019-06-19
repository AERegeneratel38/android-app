package com.github.doomsdayrs.apps.shosetsu.ui.adapters.catalogue;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.List;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueNovelCardsAdapter extends RecyclerView.Adapter<CatalogueNovelCardsAdapter.NovelCardsViewHolder> {
    private static List<CatalogueNovelCard> recycleCards;
    private final FragmentManager fragmentManager;
    private final Formatter formatter;

    public CatalogueNovelCardsAdapter(List<CatalogueNovelCard> recycleCards, FragmentManager fragmentManager, Formatter formatter) {
        if (CatalogueNovelCardsAdapter.recycleCards != null && !CatalogueNovelCardsAdapter.recycleCards.containsAll(recycleCards)) {
            CatalogueNovelCardsAdapter.recycleCards = null;
            CatalogueNovelCardsAdapter.recycleCards = recycleCards;
        } else if (CatalogueNovelCardsAdapter.recycleCards == null)
            CatalogueNovelCardsAdapter.recycleCards = recycleCards;

        this.fragmentManager = fragmentManager;
        this.formatter = formatter;
    }

    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        NovelCardsViewHolder novelCardsViewHolder = new NovelCardsViewHolder(view);
        novelCardsViewHolder.fragmentManager = fragmentManager;
        novelCardsViewHolder.formatter = formatter;
        return novelCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {
        CatalogueNovelCard recycleCard = recycleCards.get(i);
        if (recycleCard != null) {
            novelCardsViewHolder.url = recycleCard.novelURL;
            novelCardsViewHolder.library_card_title.setText(recycleCard.title);
            Picasso.get().load(recycleCard.imageURL).into(novelCardsViewHolder.library_card_image);
        }

    }

    @Override
    public int getItemCount() {
        return recycleCards.size();
    }

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FragmentManager fragmentManager;
        Formatter formatter;
        final ImageView library_card_image;
        final TextView library_card_title;
        String url;

        NovelCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.novel_item_image);
            library_card_title = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NovelFragment novelFragment = new NovelFragment();
            novelFragment.formatter = formatter;
            novelFragment.url = url;
            novelFragment.fragmentManager = fragmentManager;
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, novelFragment)
                    .commit();
        }
    }
}
