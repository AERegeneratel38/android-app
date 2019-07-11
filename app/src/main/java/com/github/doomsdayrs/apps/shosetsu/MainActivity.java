package com.github.doomsdayrs.apps.shosetsu;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.MainActivityNavSwapFrag;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CataloguesFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.SettingsFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.navigation.NavigationView;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

    public final LibraryFragment libraryFragment = new LibraryFragment();
    public final CataloguesFragment cataloguesFragment = new CataloguesFragment();
    public final SettingsFragment settingsFragment = new SettingsFragment();
    public final DownloadsFragment downloadsFragment = new DownloadsFragment();


    /**
     * Main activity
     *
     * @param savedInstanceState savedData from destruction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        SettingsController.view = getSharedPreferences("view", 0);
        SettingsController.download = getSharedPreferences("download", 0);
        SettingsController.advanced = getSharedPreferences("advanced", 0);
        SettingsController.tracking = getSharedPreferences("tracking", 0);
        SettingsController.backup = getSharedPreferences("backup", 0);
        SettingsController.init();

        switch (Settings.themeMode) {
            case 0:
                setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar);
                break;
            case 1:
                setTheme(R.style.Theme_MaterialComponents_NoActionBar);
                break;
            case 2:
                setTheme(R.style.ThemeOverlay_MaterialComponents_Dark);
        }
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        AppUpdater appUpdater = new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("Doomsdyars", "shosetsu")
                .setDisplay(Display.DIALOG)
                .setDisplay(Display.NOTIFICATION)
                .setDisplay(Display.SNACKBAR);

        appUpdater.start();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        // Settings setup
        Settings.connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Set the content view
        setContentView(R.layout.activity_main);

        //Sets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Statics.mainActionBar = getSupportActionBar();
        //Sets up the sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new MainActivityNavSwapFrag(this));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sets up DB
        DBHelper helper = new DBHelper(this);
        Database.library = helper.getWritableDatabase();

        Download_Manager.init();

        //Prevent the frag from changing on rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, libraryFragment).commit();
            navigationView.setCheckedItem(R.id.nav_library);
        }
    }

    /**
     * When the back button while drawer is open, close it.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

}
