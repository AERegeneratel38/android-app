package app.shosetsu.android.backend.workers.onetime

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.Notifications.CHANNEL_DOWNLOAD
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import app.shosetsu.android.common.consts.WorkerTags.DOWNLOAD_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.toast
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.DownloadStatus
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params), KodeinAware {

	private val notificationManager by lazy {
		applicationContext.getSystemService<NotificationManager>()!!
	}
	private val progressNotification by lazy {
		if (SDK_INT >= VERSION_CODES.O) {
			Notification.Builder(applicationContext, CHANNEL_DOWNLOAD)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(applicationContext)
		}
			.setSmallIcon(R.drawable.download)
			.setContentTitle(applicationContext.getString(R.string.app_name))
			.setContentText("Downloading Chapters")
			.setOngoing(true)
	}
	override val kodein: Kodein by closestKodein(applicationContext)
	private val downloadsRepo by instance<IDownloadsRepository>()
	private val chapRepo by instance<IChaptersRepository>()
	private val extRepo by instance<IExtensionsRepository>()
	private val settingRepo by instance<ISettingsRepository>()

	/** How many jobs are currently running */
	@get:Synchronized
	@set:Synchronized
	private var activeJobs = 0

	/** Which extensions are currently working */
	@get:Synchronized
	private val activeExtensions = ArrayList<Int>()

	/** Retrieves the setting for if the download system is paused or not */
	private suspend fun isDownloadPaused(): Boolean =
		settingRepo.getBooleanOrDefault(IsDownloadPaused)

	/** Retrieves the setting for simultaneous download threads allowed */
	private suspend fun getDownloadThreads(): Int =
		settingRepo.getIntOrDefault(DownloadThreadPool)

	/** Retrieves the setting for simultaneous download threads allowed per extension */
	private suspend fun getDownloadThreadsPerExtension(): Int =
		settingRepo.getIntOrDefault(DownloadExtThreads)

	/** Loads the download count that is present currently */
	private suspend fun getDownloadCount(): Int =
		downloadsRepo.loadDownloadCount().unwrap() ?: -1


	private suspend fun download(downloadEntity: DownloadEntity): HResult<*> =
		chapRepo.getChapter(downloadEntity.chapterID).transform { chapterEntity ->
			extRepo.getIExtension(chapterEntity.extensionID).transform { formatterEntity ->
				chapRepo.getChapterPassage(formatterEntity, chapterEntity).transform { passage ->
					chapRepo.saveChapterPassageToStorage(
						chapterEntity,
						passage
					)
					successResult("Chapter Loaded")
				}
			}
		}

	@Synchronized
	private fun activeExt(id: Int): Int {
		var count = 0
		for (i in activeExtensions.size - 1 downTo 0) {
			try {
				val aEI = activeExtensions[i]
				if (aEI == id)
					count++
			} catch (e: NullPointerException) {
				// Ignoring this due to async
			} catch (e: IndexOutOfBoundsException) {
				// Ignoring this due to async
			}
		}
		return count
	}

	private suspend fun launchDownload() {
		downloadsRepo.loadFirstDownload().handle { downloadEntity ->
			val extID = downloadEntity.extensionID

			// This will loop until the download status is DOWNLOADING
			while (downloadEntity.status != DownloadStatus.DOWNLOADING) {
				// Will stop if download is paused
				if (isDownloadPaused()) {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.PENDING
						)
					)
					return
				}

				// Checks if there is space for the extension download
				// If space is free, will start the extension download and break out of the loop
				if (activeExt(extID) <= getDownloadThreadsPerExtension()) {
					downloadEntity.status = DownloadStatus.DOWNLOADING
					downloadsRepo.update(downloadEntity)
					break
				} else {
					// If the status is pending, it will now be waiting till the pool is open
					if (downloadEntity.status == DownloadStatus.PENDING) {
						downloadEntity.status = DownloadStatus.WAITING
						downloadsRepo.update(downloadEntity)
					}
				}
				delay(100)
			}

			// Adds the job as working
			activeExtensions.add(extID)
			activeJobs++

			logI("Downloading $downloadEntity")
			download(downloadEntity).handle(
				onError = {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.ERROR
						)
					)
					launchUI {
						toast { it.message }
					}
				},
				onEmpty = {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.ERROR
						)
					)
					launchUI {
						toast { "Empty Error" }
					}
				},
				onLoading = {
					throw Exception("Should not be loading")
				}
			) {
				downloadsRepo.deleteEntity(downloadEntity)
			}

			activeJobs-- // Drops active job count once completed task
			activeExtensions.remove(downloadEntity.extensionID)
		}
	}

	override suspend fun doWork(): Result {
		logI("Starting loop")
		if (isDownloadPaused())
			logI("Loop Paused")
		else {
			// Notifies the application is downloading chapters
			notificationManager.notify(ID_CHAPTER_DOWNLOAD, progressNotification.build())

			// Will not run if there are no downloads or if the download is paused
			while (getDownloadCount() >= 1 && !isDownloadPaused()) {
				// Launches a job as long as there are threads to download via
				val threadsAllowed = getDownloadThreads()
				if (activeJobs <= threadsAllowed) launchIO {
					launchDownload()
				}
			}

			// Downloads the chapters
			notificationManager.cancel(ID_CHAPTER_DOWNLOAD)
		}
		logI("Completed download loop")
		return Result.success()
	}

	/**
	 * Manager of [DownloadWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

		private suspend fun downloadOnMetered(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnMeteredConnection)

		private suspend fun downloadOnLowStorage(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnLowStorage)

		private suspend fun downloadOnLowBattery(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnLowBattery)

		private suspend fun downloadOnlyIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnlyWhenIdle)

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(DOWNLOAD_WORK_ID)
				.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start() {
			launchIO {
				workerManager.enqueueUniqueWork(
					DOWNLOAD_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<DownloadWorker>()
						.setConstraints(Constraints.Builder().apply {
							setRequiredNetworkType(
								if (downloadOnMetered()) {
									CONNECTED
								} else UNMETERED
							)
							setRequiresStorageNotLow(!downloadOnLowStorage())
							setRequiresBatteryNotLow(!downloadOnLowBattery())
							if (SDK_INT >= VERSION_CODES.M)
								setRequiresDeviceIdle(downloadOnlyIdle())
						}.build())
						.build()
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(DOWNLOAD_WORK_ID)
	}

}