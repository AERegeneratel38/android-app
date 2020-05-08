package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.github.doomsdayrs.apps.shosetsu.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_ACTION
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_URL
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchController
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateChapterUI
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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
 * 06 / 05 / 2020
 */

fun Activity.openInBrowser(url: Uri) = startActivity(Intent(Intent.ACTION_VIEW, url))
fun Activity.openInBrowser(url: String) = openInBrowser(Uri.parse(url))

fun Activity.openInWebView(url: String) {
	val intent = Intent(this, WebViewApp::class.java)
	intent.putExtra(BUNDLE_URL, url)
	intent.putExtra(BUNDLE_ACTION, WebViewApp.Actions.VIEW.action)
	startActivity(intent)
}

fun Activity.search(query: String) {
	val mainActivity = this as MainActivity
	val searchFragment = SearchController(bundleOf(
			BundleKeys.BUNDLE_QUERY to query
	))
	mainActivity.transitionView(searchFragment)
}

/**
 * Like context toast, Except posts for the UI
 */
fun Activity.toastOnUI(string: String, duration: Int = Toast.LENGTH_SHORT) =
		runOnUiThread { Toast.makeText(this, string, duration).show() }


/**
 * Toasts on the UI thread
 */
fun Activity.toastOnUI(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) =
		runOnUiThread { Toast.makeText(this, resource, duration).show() }

/**
 * shosetsu
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 * Pre resquite requires chapter to already have been added to library
 *
 * @param activity     activity
 * @param cUI novel chapter
 */
fun Activity.openChapter(cUI: UpdateChapterUI) = openChapter(cUI.id, cUI.novelID, cUI.formatterID)

fun Activity.openChapter(cUI: ChapterUI) = openChapter(cUI.id, cUI.novelID, cUI.formatterID)

fun Activity.openChapter(chapterID: Int, novelID: Int, formatterID: Int) {
	val intent = Intent(this, ChapterReader::class.java)
	intent.putExtra("chapterID", chapterID)
	intent.putExtra("novelID", novelID)
	intent.putExtra("formatter", formatterID)
	startActivity(intent)
}

fun Activity.readAsset(name: String): String {
	val string = StringBuilder()
	try {
		val reader = BufferedReader(InputStreamReader(assets.open(name)))

		// do reading, usually loop until end of file reading
		var mLine: String? = reader.readLine()
		while (mLine != null) {
			string.append("\n").append(mLine)
			mLine = reader.readLine()
		}
		reader.close()
	} catch (e: IOException) {
		Log.e(javaClass.name, "Failed to read asset of $name", e)
	}
	return string.toString()
}

fun Activity.setActivityTitle(title: String?) {
	if (this is AppCompatActivity) this.supportActionBar?.let { it.title = title }
}

fun Activity.setActivityTitle(@StringRes title: Int) {
	if (this is AppCompatActivity) this.supportActionBar?.let { it.setTitle(title) }
}
