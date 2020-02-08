package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.variables.Notifications.ID_CHAPTER_UPDATE
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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
 * ====================================================================
 */

/**
 * shosetsu
 * 07 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class LibraryUpdateService : Service() {
    companion object {
        const val KEY_TARGET = "Target"
        const val KEY_CHAPTERS = "Novels"

        const val KEY_NOVELS = 0x00
        const val KEY_CATEGORY = 0x01


        fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
            val className = serviceClass.name
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            @Suppress("DEPRECATION")
            return manager.getRunningServices(Integer.MAX_VALUE)
                    .any { className == it.service.className }
        }

        /**
         * Returns the status of the service.
         *
         * @param context the application context.
         * @return true if the service is running, false otherwise.
         */
        fun isRunning(context: Context): Boolean {
            return context.isServiceRunning(LibraryUpdateService::class.java)
        }

        /**
         * Starts the service. It will be started only if there isn't another instance already
         * running.
         *
         * @param context the application context.
         * @param category a specific category to update, or null for global update.
         * @param target defines what should be updated.
         */
        fun start(context: Context, category: Int, novelIDs: ArrayList<Int>) {
            if (!isRunning(context)) {
                val intent = Intent(context, LibraryUpdateService::class.java)
                intent.putExtra(KEY_TARGET, category)
                intent.putIntegerArrayListExtra(KEY_CHAPTERS, novelIDs)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    context.startService(intent)
                } else {
                    context.startForegroundService(intent)
                }
            }
        }

        /**
         * Stops the service.
         *
         * @param context the application context.
         */
        fun stop(context: Context) {
            context.stopService(Intent(context, LibraryUpdateService::class.java))
        }
    }

    /**
     * Wake lock that will be held until the service is destroyed.
     */
    //  private lateinit var wakeLock: PowerManager.WakeLock

    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    private val progressNotification by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_UPDATE, "Shosetsu Update", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            Notification.Builder(this, CHANNEL_UPDATE)
        } else {
            // Suppressed due to lower API
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
                .setSmallIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Update in progress")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
    }

    private var job: CoroutineContext? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(ID_CHAPTER_UPDATE, progressNotification.build())
        //   wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LibraryUpdateService:WakeLock")
        //       wakeLock.acquire(60 * 60 * 1000L /*10 minutes*/)
    }

    override fun onDestroy() {
        job?.cancel()
        //     if (wakeLock.isHeld) wakeLock.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun updateManga(intent: Intent) {
        val updatedNovels = ArrayList<NovelCard>()
        val novelCards = intent.getIntegerArrayListExtra(KEY_CHAPTERS)!!

        // Main process
        for (x in novelCards.indices) {

            val novelCard = Database.DatabaseNovels.getNovel(novelCards[x])
            val novelID = Database.DatabaseIdentification.getNovelIDFromNovelURL(novelCard.novelURL)
            val formatter = DefaultScrapers.getByID(novelCard.formatterID)

            if (formatter != DefaultScrapers.unknown) {
                // Updates notification
                progressNotification.setContentText(novelCard.title)
                progressNotification.setProgress(novelCards.size, x + 1, false)
                notificationManager.notify(ID_CHAPTER_UPDATE, progressNotification.build())

                // Runs process
                ChapterLoader(object : ChapterLoader.ChapterLoaderAction {
                    override fun onPreExecute() {
                    }

                    override fun onPostExecute(result: Boolean, finalChapters: ArrayList<Novel.Chapter>) {
                    }

                    override fun onJustBeforePost(finalChapters: ArrayList<Novel.Chapter>) {
                        for ((mangaCount, chapter) in finalChapters.withIndex()) {
                            add(updatedNovels, mangaCount, novelID, chapter, novelCard)
                        }
                    }

                    override fun onIncrementingProgress(page: Int, max: Int) {
                    }

                    override fun errorReceived(errorString: String) {
                        Log.e("ChapterUpdater", errorString)
                    }
                }, formatter, novelCard.novelURL).doInBackground()
                Utilities.wait(1000)
            } else {
                progressNotification.setProgress(novelCards.size, x + 1, false)
                notificationManager.notify(ID_CHAPTER_UPDATE, progressNotification.build())
            }
        }

        // Completion
        val stringBuilder = StringBuilder()
        when {
            updatedNovels.size > 0 -> {
                progressNotification.setContentTitle(getString(R.string.update_complete))
                for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
                progressNotification.style = Notification.BigTextStyle()
            }
            else -> stringBuilder.append(getString(R.string.update_not_found))
        }
        progressNotification.setContentText(stringBuilder.toString())
        progressNotification.setProgress(0, 0, false)
        progressNotification.setOngoing(false)
        notificationManager.notify(ID_CHAPTER_UPDATE, progressNotification.build())
    }

    fun updateCategory() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job?.cancel()
        job = GlobalScope.launch {
            when (intent?.getIntExtra(KEY_TARGET, KEY_NOVELS) ?: KEY_NOVELS) {
                KEY_NOVELS -> {
                    updateManga(intent
                            ?: Intent().putIntegerArrayListExtra(KEY_CHAPTERS, Database.DatabaseNovels.getIntLibrary()))
                }
                KEY_CATEGORY -> {
                    updateCategory()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun add(updatedNovels: ArrayList<NovelCard>, mangaCount: Int, novelID: Int, novelChapter: Novel.Chapter, novelCard: NovelCard) {
        if (Database.DatabaseChapter.isNotInChapters(novelChapter.link)) {
            Log.i("ChaperUpdater", "add #$mangaCount\t: ${novelChapter.link} ")
            Database.DatabaseChapter.addToChapters(novelID, novelChapter)
            Database.DatabaseUpdates.addToUpdates(novelID, novelChapter.link, System.currentTimeMillis())
            if (!updatedNovels.contains(novelCard)) updatedNovels.add(novelCard)
        } else {
            Database.DatabaseChapter.updateChapter(novelChapter)
        }
        if (Settings.isDownloadOnUpdateEnabled)
            DownloadManager.addToDownload(this.applicationContext as Activity, DownloadItem(DefaultScrapers.getByID(novelCard.formatterID), novelCard.title, novelChapter.title, Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link)))
    }

}