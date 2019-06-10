package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.novel.SlidingNovelPageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NovelFragment extends Fragment {
    static View view;
    static FragmentManager fragmentManager;

    boolean incrementChapters;

    static Formatter formatter;
    static String URL;
    static NovelPage novelPage;

    NovelFragmentMain novelFragmentMain;
    NovelFragmentChapters novelFragmentChapters;

    ViewPager viewPager;
    static SlidingNovelPageAdapter pagerAdapter;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        NovelFragment.formatter = formatter;
        incrementChapters = formatter.isIncrementingChapterList();
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragment");
        view = inflater.inflate(R.layout.fragment_novel, container, false);
        if (savedInstanceState == null) {
            fragmentManager = getChildFragmentManager();
            new fillData().execute();
            setViewPager();
        } else setViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    void setViewPager() {
        viewPager = view.findViewById(R.id.fragment_novel_viewpager);

        List<Fragment> fragments = new ArrayList<>();

        novelFragmentChapters = new NovelFragmentChapters();
        novelFragmentChapters.setFormatter(formatter);
        novelFragmentChapters.setURL(URL);
        novelFragmentChapters.setFragmentManager(fragmentManager);

        novelFragmentMain = new NovelFragmentMain();
        novelFragmentMain.setURL(URL);
        novelFragmentMain.setFormatter(formatter);
        novelFragmentMain.setNovelFragmentChapters(novelFragmentChapters);


        fragments.add(novelFragmentMain);
        fragments.add(novelFragmentChapters);

        Log.d("Fragments", fragments.toString());

        pagerAdapter = new SlidingNovelPageAdapter(fragmentManager, fragments);
        viewPager.setAdapter(pagerAdapter);
    }

    static class fillData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                novelPage = formatter.parseNovel("http://novelfull.com" + URL);
                NovelFragmentMain.novelPage = novelPage;
                Log.d("Novel", novelPage.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
