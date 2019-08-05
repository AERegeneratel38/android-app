package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.LibraryNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.LibrarySearchQuery;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.calculateNoOfColumns;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class LibraryFragment extends Fragment {
    public ArrayList<NovelCard> libraryNovelCards = new ArrayList<>();
    public ArrayList<NovelCard> selectedNovels = new ArrayList<>();
    public static boolean changedData = false;


    public boolean contains(NovelCard novelCard) {
        for (NovelCard n : selectedNovels)
            if (n.novelURL.equalsIgnoreCase(novelCard.novelURL))
                return true;
        return false;
    }


    private Context context;
    public RecyclerView recyclerView;
    public LibraryNovelAdapter libraryNovelCardsAdapter;
    public Menu menu;


    /**
     * Constructor
     */
    public LibraryFragment() {
        setHasOptionsMenu(true);
    }

    private void readFromDB() {
        libraryNovelCards = Database.DatabaseLibrary.getLibrary();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            libraryNovelCards.sort((novelCard, t1) -> novelCard.title.compareTo(t1.title));
        } else {
            bubbleSortA_Z();
        }
    }

    private void bubbleSortA_Z() {
        for (int i = libraryNovelCards.size() - 1; i > 1; i--) {
            for (int j = 0; j < i; j++) {
                if (libraryNovelCards.get(j).title.compareTo(libraryNovelCards.get(j + 1).title) > 0)
                    swapValues(j, j + 1);
            }
        }
    }

    private void swapValues(int indexOne, int indexTwo) {
        NovelCard novelCard = libraryNovelCards.get(indexOne);
        libraryNovelCards.set(indexOne, libraryNovelCards.get(indexTwo));
        libraryNovelCards.set(indexTwo, novelCard);
    }

    /**
     * Sets the cards to display
     */
    public void setLibraryCards(ArrayList<NovelCard> novelCards) {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            library_layoutManager = new GridLayoutManager(context, calculateNoOfColumns(getContext(), 200), RecyclerView.VERTICAL, false);

            libraryNovelCardsAdapter = new LibraryNovelAdapter(novelCards, this);
            recyclerView.setLayoutManager(library_layoutManager);
            recyclerView.setAdapter(libraryNovelCardsAdapter);
        }
    }

    public MenuInflater getInflater() {
        return new MenuInflater(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Library", "Paused");
        selectedNovels = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedNovels = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Library", "Resumed");
        if (LibraryFragment.changedData) {
            Log.d("Library", "Updating data");
            libraryNovelCards = Database.DatabaseLibrary.getLibrary();
            changedData = !changedData;
        }
        libraryNovelCardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selected", selectedNovels);
        outState.putSerializable("lib", libraryNovelCards);
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Statics.mainActionBar.setTitle("Library");
        Log.d("Library", "creating");
        if (savedInstanceState == null)
            readFromDB();
        else {
            this.selectedNovels = (ArrayList<NovelCard>) savedInstanceState.getSerializable("selected");
            this.libraryNovelCards = (ArrayList<NovelCard>) savedInstanceState.getSerializable("lib");
        }
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = view.findViewById(R.id.fragment_library_recycler);
        this.context = Objects.requireNonNull(container).getContext();
        setLibraryCards(libraryNovelCards);
        return view;
    }


    /**
     * Creates the option menu
     *
     * @param menu     menu to fill
     * @param inflater inflater of layouts and shiz
     */
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        if (selectedNovels.size() <= 0) {
            inflater.inflate(R.menu.toolbar_library, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new LibrarySearchQuery(this));
                searchView.setOnCloseListener(() -> {
                    setLibraryCards(libraryNovelCards);
                    return false;
                });
            }
        } else
            inflater.inflate(R.menu.toolbar_library_selected, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updater_now:
                Toast.makeText(getContext(), "In the future this will start a checking of each novel in this library", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.chapter_select_all:
                for (NovelCard novelChapter : libraryNovelCards)
                    if (!contains(novelChapter))
                        selectedNovels.add(novelChapter);
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_deselect_all:
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                onCreateOptionsMenu(menu, getInflater());
                return true;

            case R.id.remove_from_library:
                for (NovelCard novelCard : selectedNovels) {
                    Database.DatabaseLibrary.unBookmark(novelCard.novelURL);
                    libraryNovelCards.remove(novelCard);
                }
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;

            case R.id.source_migrate:
                Intent intent = new Intent(getActivity(), MigrationView.class);
                try {
                    intent.putExtra("selected", Database.serialize(selectedNovels));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra("target", 1);
                startActivity(intent);
                return true;

        }
        return false;
    }
}
