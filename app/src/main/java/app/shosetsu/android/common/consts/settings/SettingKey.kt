package app.shosetsu.android.common.consts.settings

import app.shosetsu.android.common.enums.MarkingTypes
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 23 / 06 / 2020
 */

sealed class SettingKey<T : Any>(val name: String, val default: T) {
	open inner class StringSettingKey(k: String, default: String) : SettingKey<String>(k, default)

	object ReaderTheme : SettingKey<Int>("readerTheme", -1)

	object FirstTime : SettingKey<Boolean>("first_time", true)


	// How things look in Reader
	object ReaderUserThemes : SettingKey<Set<String>>("readerThemes", setOf())


	object ReaderTextSize : SettingKey<Float>("readerTextSize", 14f)
	object ReaderParagraphSpacing : SettingKey<Int>("readerParagraphSpacing", 1)
	object ReaderIndentSize : SettingKey<Int>("readerIndentSize", 1)

	//- How things act in Reader
	object ReaderIsTapToScroll : SettingKey<Boolean>("tapToScroll", false)
	object ReaderIsInvertedSwipe : SettingKey<Boolean>("invertedSwipe", false)
	object ReadingMarkingType : SettingKey<String>("readingMarkingType", MarkingTypes.ONVIEW.name)

	//- Some things
	object ChaptersResumeFirstUnread : SettingKey<Boolean>("readerResumeFirstUnread", false)

	// Download options
	object IsDownloadPaused : SettingKey<Boolean>("isDownloadPaused", false)


	/**
	 * Which chapter to delete after reading
	 * If -1, then does nothing
	 * If 0, then deletes the read chapter
	 * If 1+, deletes the chapter of READ CHAPTER - [deletePreviousChapter]
	 */
	object DeleteReadChapter : SettingKey<Int>("deleteReadChapter", -1)
	object DownloadOnLowStorage : SettingKey<Boolean>("downloadNotLowStorage", false)
	object DownloadOnLowBattery : SettingKey<Boolean>("downloadNotLowBattery", false)
	object DownloadOnMeteredConnection : SettingKey<Boolean>("downloadNotMetered", false)
	object DownloadOnlyWhenIdle : SettingKey<Boolean>("downloadIdle", false)

	// Update options
	object IsDownloadOnUpdate : SettingKey<Boolean>("isDownloadOnUpdate", false)
	object OnlyUpdateOngoing : SettingKey<Boolean>("onlyUpdateOngoing", false)
	object UpdateOnStartup : SettingKey<Boolean>("updateOnStartup", true)
	object UpdateCycle : SettingKey<Int>("updateCycle", 1)
	object UpdateOnLowStorage : SettingKey<Boolean>("updateLowStorage", false)
	object UpdateOnLowBattery : SettingKey<Boolean>("updateLowBattery", false)
	object UpdateOnMeteredConnection : SettingKey<Boolean>("updateMetered", false)
	object UpdateOnlyWhenIdle : SettingKey<Boolean>("updateIdle", false)

	// App Update Options
	object AppUpdateOnStartup : SettingKey<Boolean>("appUpdateOnStartup", true)
	object AppUpdateOnMeteredConnection : SettingKey<Boolean>("appUpdateMetered", false)
	object AppUpdateOnlyWhenIdle : SettingKey<Boolean>("appUpdateIdle", false)
	object AppUpdateCycle : SettingKey<Int>("appUpdateCycle", 1)


	// View options
	object ChapterColumnsInPortait : SettingKey<Int>("columnsInNovelsViewP", 3)
	object ChapterColumnsInLandscape : SettingKey<Int>("columnsInNovelsViewH", 6)
	object NovelCardType : SettingKey<Int>("novelCardType", 0)
	object NavStyle : SettingKey<Int>("navigationStyle", 0)

	// Backup Options
	object BackupChapters : SettingKey<Boolean>("backupChapters", true)
	object BackupSettings : SettingKey<Boolean>("backupSettings", false)
	object BackupQuick : SettingKey<Boolean>("backupQuick", false)

	// Download Options
	object CustomExportDirectory : SettingKey<String>("downloadDirectory", "")


	companion object {
		val KEYS: ArrayList<SettingKey<*>> by lazy {
			arrayListOf(
					ReaderTheme,
					FirstTime,


					// How things look in Reader
					ReaderUserThemes,


					ReaderTextSize,
					ReaderParagraphSpacing,
					ReaderIndentSize,

					//- How things act in Reader
					ReaderIsTapToScroll,
					ReaderIsInvertedSwipe,
					ReadingMarkingType,

					//- Some things
					ChaptersResumeFirstUnread,

					// Download options
					IsDownloadPaused,

					DeleteReadChapter,
					DownloadOnLowStorage,
					DownloadOnLowBattery,
					DownloadOnMeteredConnection,
					DownloadOnlyWhenIdle,

					// Update options
					IsDownloadOnUpdate,
					OnlyUpdateOngoing,
					UpdateOnStartup,
					UpdateCycle,
					UpdateOnLowStorage,
					UpdateOnLowBattery,
					UpdateOnMeteredConnection,
					UpdateOnlyWhenIdle,

					// App Update Options
					AppUpdateOnStartup,
					AppUpdateOnMeteredConnection,
					AppUpdateOnlyWhenIdle,
					AppUpdateCycle,


					// View options
					ChapterColumnsInPortait,
					ChapterColumnsInLandscape,
					NovelCardType,
					NavStyle,

					// Backup Options
					BackupChapters,
					BackupSettings,
					BackupQuick,

					// Download Options
					CustomExportDirectory,
			)
		}

		fun getKey(key: String): SettingKey<*> = KEYS.find { it.name == key }!!
	}
}

// Constant keys
const val LISTING_KEY: String = "listing"
const val FIRST_TIME: String = "first_time"


// How things look in Reader
const val READER_THEME: String = "readerTheme"
const val READER_USER_THEMES: String = "readerThemes"


const val READER_TEXT_SIZE: String = "readerTextSize"
const val READER_TEXT_SPACING: String = "readerParagraphSpacing"
const val READER_TEXT_INDENT: String = "readerIndentSize"

//- How things act in Reader
const val READER_IS_TAP_TO_SCROLL: String = "tapToScroll"
const val READER_IS_INVERTED_SWIPE: String = "invertedSwipe"
const val READER_MARKING_TYPE: String = "readerMarkingType"

//- Some things
const val READER_RESUME_FIRST_UNREAD: String = "readerResumeFirstUnread"

// Download options
const val IS_DOWNLOAD_PAUSED: String = "isDownloadPaused"

const val DISABLED_FORMATTERS: String = "disabledFormatters"
const val DELETE_READ_CHAPTER: String = "deleteReadChapter"
const val DOWNLOAD_LOW_STORAGE: String = "downloadNotLowStorage"
const val DOWNLOAD_LOW_BATTERY: String = "downloadNotLowBattery"
const val DOWNLOAD_METERED: String = "downloadNotMetered"
const val DOWNLOAD_IDLE: String = "downloadIdle"

// Update options
const val IS_DOWNLOAD_ON_UPDATE: String = "isDownloadOnUpdate"
const val ONLY_UPDATE_ONGOING: String = "onlyUpdateOngoing"
const val UPDATE_STARTUP: String = "updateOnStartup"
const val UPDATE_CYCLE: String = "updateCycle"
const val UPDATE_LOW_STORAGE: String = "updateLowStorage"
const val UPDATE_LOW_BATTERY: String = "updateLowBattery"
const val UPDATE_METERED: String = "updateMetered"
const val UPDATE_IDLE: String = "updateIdle"

// App Update Options
const val APP_UPDATE_STARTUP: String = "appUpdateOnStartup"
const val APP_UPDATE_METERED: String = "appUpdateMetered"
const val APP_UPDATE_IDLE: String = "appUpdateIdle"
const val APP_UPDATE_CYCLE: String = "appUpdateCycle"


// View options
const val C_IN_NOVELS_P: String = "columnsInNovelsViewP"
const val C_IN_NOVELS_H: String = "columnsInNovelsViewH"
const val NOVEL_CARD_TYPE: String = "novelCardType"
const val NAVIGATION_STYLE: String = "navigationStyle"

// Backup Options
const val BACKUP_CHAPTERS: String = "backupChapters"
const val BACKUP_SETTINGS: String = "backupSettings"
const val BACKUP_QUICK: String = "backupQuick"

// Download Options
const val DOWNLOAD_DIRECTORY: String = "downloadDirectory"