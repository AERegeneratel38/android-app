package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Formatter.Companion.KEY_CHAPTER_URL
import app.shosetsu.lib.Formatter.Companion.KEY_NOVEL_URL
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.Intent
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.NovelUI

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
 * 01 / 09 / 2020
 *
 * Opens the chapter into
 */
class OpenInWebviewUseCase(
		private val repository: IExtensionsRepository,
		private val stringToastUseCase: StringToastUseCase,
		private val application: Application
) {
	private operator fun invoke(url: String) {
		Log.d(logID(), "Opening URL $url")
		val i = Intent(application, WebViewApp::class.java) {
			bundleOf(
					BundleKeys.BUNDLE_URL to url,
					BundleKeys.BUNDLE_ACTION to WebViewApp.Actions.VIEW.action
			)
		}.apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}
		application.startActivity(i)
	}


	suspend operator fun invoke(url: String, formatterID: Int, type: Int) {
		when (val fR: HResult<Formatter> = repository.loadFormatter(formatterID)) {
			is HResult.Success -> {
				val formatter = fR.data
				this(formatter.expandURL(url, type))
			}
			is HResult.Empty -> {
				Log.e(logID(), "Empty")
				stringToastUseCase { "Empty??" }
			}
			is HResult.Error -> {
				Log.e(logID(), "Error")
				stringToastUseCase { "$fR" }
			}
		}
	}

	suspend operator fun invoke(novelUI: NovelUI) = this(
			novelUI.novelURL,
			novelUI.formatterID,
			KEY_NOVEL_URL
	)

	suspend operator fun invoke(chapterUI: ChapterUI) = this(
			chapterUI.link,
			chapterUI.formatterID,
			KEY_CHAPTER_URL
	)
}