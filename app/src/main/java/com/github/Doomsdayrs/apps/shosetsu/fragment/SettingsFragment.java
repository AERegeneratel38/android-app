package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.Types;
import com.github.Doomsdayrs.apps.shosetsu.adapters.SettingsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.SettingsCard;
import com.github.Doomsdayrs.apps.shosetsu.settings.Settings;

import java.util.ArrayList;

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
public class SettingsFragment extends Fragment {
    static ArrayList<SettingsCard> cards = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public SettingsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_settings, menu);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = view.findViewById(R.id.fragment_settings_recycler);

        if (savedInstanceState == null) {
            cards.add(new SettingsCard(Types.DOWNLOAD));
            cards.add(new SettingsCard(Types.VIEW));
            cards.add(new SettingsCard(Types.ADVANCED));
            cards.add(new SettingsCard(Types.CREDITS));
        }
        System.out.println(cards);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(container.getContext());
            adapter = new SettingsAdapter(cards,getFragmentManager());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}


