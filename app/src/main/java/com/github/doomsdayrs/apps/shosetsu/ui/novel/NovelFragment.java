package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentMain;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isOnline;

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragment extends Fragment {
    public int novelID;

    public NovelFragmentMain novelFragmentMain;
    public NovelFragmentChapters novelFragmentChapters;
    public ProgressBar progressBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ConstraintLayout errorView;
    public TextView errorMessage;
    public Button errorButton;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (StaticNovel.chapterLoader != null)
            StaticNovel.chapterLoader.cancel(true);
        if (StaticNovel.novelLoader != null)
            StaticNovel.novelLoader.cancel(true);

        StaticNovel.chapterLoader = null;
        StaticNovel.novelLoader = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("novelID", novelID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragment");
        View view = inflater.inflate(R.layout.fragment_novel, container, false);
        // Attach UI to program
        {
            progressBar = view.findViewById(R.id.fragment_novel_progress);
            viewPager = view.findViewById(R.id.fragment_novel_viewpager);
            tabLayout = view.findViewById(R.id.fragment_novel_tabLayout);
            errorView = view.findViewById(R.id.network_error);
            errorMessage = view.findViewById(R.id.error_message);
            errorButton = view.findViewById(R.id.error_button);
        }

        // Create sub-fragments
        {
            novelFragmentMain = new NovelFragmentMain();
            novelFragmentMain.setNovelFragment(this);
            novelFragmentChapters = new NovelFragmentChapters();
            novelFragmentChapters.setNovelFragment(this);
        }
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();

        if (savedInstanceState == null) {
            if (isOnline() && !Database.DatabaseNovels.inDatabase(StaticNovel.novelID)) {
                setViewPager();

                if (StaticNovel.novelLoader != null && !StaticNovel.novelLoader.isCancelled()) {
                    StaticNovel.novelLoader.setC(false);
                    StaticNovel.novelLoader.cancel(true);
                }
                StaticNovel.novelLoader = new NovelLoader(this, true);

                StaticNovel.novelLoader.execute(getActivity());
            } else {
                StaticNovel.novelPage = Database.DatabaseNovels.getNovelPage(StaticNovel.novelID);
                StaticNovel.status = Database.DatabaseNovels.getStatus(novelID);
                if (StaticNovel.novelPage != null)
                    Statics.mainActionBar.setTitle(StaticNovel.novelPage.title);
                setViewPager();
            }
        } else {
            novelID = savedInstanceState.getInt("novelID");
            setViewPager();
        }
        return view;
    }


    private void setViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        {
            Log.d("FragmentLoading", "Main");
            fragments.add(novelFragmentMain);
            Log.d("FragmentLoading", "Chapters");
            fragments.add(novelFragmentChapters);
        }

        NovelPagerAdapter pagerAdapter = new NovelPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));
    }

}
