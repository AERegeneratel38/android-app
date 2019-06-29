package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * 14 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "database.db";


    static final String chaptersCreate = "create table if not exists " + Database.Tables.CHAPTERS + "(" +
            Database.Columns.NOVEL_URL + " text not null," +
            // The chapter chapterURL
            Database.Columns.CHAPTER_URL + " text not null unique," +

            // Unsure if i should keep this or not
            Database.Columns.SAVED_DATA + " text," +

            // Saved Data
            // > Scroll position, either 0 for top, or X for the position
            Database.Columns.Y + " integer not null," +
            // > Either 0 for none, or an incremented count
            Database.Columns.READ_CHAPTER + " integer not null," +
            // > Either 0 for false or 1 for true.
            Database.Columns.BOOKMARKED + " integer not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Database.Columns.IS_SAVED + " integer not null," +
            Database.Columns.SAVE_PATH + " text)";

    //TODO Figure out a legitimate way to structure all this data

    // Library that the user has saved their novels to
    static final String libraryCreate = "create TABLE if not exists " + Database.Tables.LIBRARY + " (" +
            Database.Columns.NOVEL_URL + " text not null unique, " +
            Database.Columns.NOVEL_PAGE + " text not null," +
            Database.Columns.FORMATTER_ID + " integer not null)";

    @Deprecated
    static final String bookmarksCreate = "create TABLE if not exists " + Database.Tables.BOOKMARKS + "(" +
            Database.Columns.CHAPTER_URL + " text unique not null, " +
            Database.Columns.SAVED_DATA + " text)";

    // Watches download listing
    static final String downloadsCreate = "create TABLE if not exists " + Database.Tables.DOWNLOADS + "(" +
            Database.Columns.FORMATTER_ID + " integer not null," +
            Database.Columns.NOVEL_URL + " text not null," +
            Database.Columns.CHAPTER_URL + " text not null," +

            Database.Columns.NOVEL_NAME + " text not null," +
            Database.Columns.CHAPTER_NAME + " text not null," +
            Database.Columns.PAUSED + " integer not null)";


    /**
     * Constructor
     *
     * @param context main context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 4);
    }


    /**
     * Creates DB things
     *
     * @param db db to fill
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(libraryCreate);
        db.execSQL(downloadsCreate);
        db.execSQL(chaptersCreate);
    }

    /**
     * Upgrades database
     *
     * @param db         database to alter
     * @param oldVersion previous version ID
     * @param newVersion new version ID
     */
    //TODO Actually save data between db versions
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(libraryCreate);
            db.execSQL(bookmarksCreate);
            db.execSQL("drop table if exists " + Database.Tables.LIBRARY);
            db.execSQL("drop table if exists " + Database.Tables.BOOKMARKS);
            db.execSQL(libraryCreate);
        }

        if (oldVersion < 3) {
            db.execSQL(libraryCreate);
            db.execSQL(downloadsCreate);
            db.execSQL("drop table if exists " + Database.Tables.DOWNLOADS);
            db.execSQL("drop table if exists " + Database.Tables.BOOKMARKS);
            db.execSQL("drop table if exists " + Database.Tables.LIBRARY);
            db.execSQL(libraryCreate);
            db.execSQL(chaptersCreate);
        }

        if (oldVersion < 4) {
            db.execSQL(downloadsCreate);
            db.execSQL("drop table if exists " + Database.Tables.CHAPTERS);
            db.execSQL("drop table if exists " + Database.Tables.LIBRARY);

            db.execSQL(libraryCreate);
            db.execSQL(chaptersCreate);
        }
    }
}