package com.github.doomsdayrs.apps.shosetsu.ui.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_URL
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.OpenInBrowserUseCase
import kotlinx.android.synthetic.main.webview.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.*

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
 */

/**
 * shosetsu
 * 31 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 *
 * Opens a URL in the apps internal webview
 * This allows cross saving cookies, allowing the app to access features such as logins
 */
class WebViewApp : AppCompatActivity(R.layout.webview), KodeinAware {
	override val kodein: Kodein by closestKodein()
	private val openInBrowserUseCase: OpenInBrowserUseCase by instance()

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> finish()
			R.id.open_browser -> launchIO {
				openInBrowserUseCase(intent.getStringExtra(BUNDLE_URL)!!)
				finish()
			}
		}
		return super.onOptionsItemSelected(item)
	}


	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.webview_menu, menu)
		return true
	}

	@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.setSupportActionBar(toolbar)
		supportActionBar?.apply {
			setDisplayHomeAsUpEnabled(true)
		}

		val action = Actions.actions[intent.getIntExtra("action", 0)]
		webview.settings.javaScriptEnabled = true
		when (action) {
			Actions.VIEW -> webview.webViewClient = WebViewClient()
			Actions.CLOUD_FLARE -> {
				// webview.addJavascriptInterface(JSInterface(), "HtmlViewer")
				webview.webViewClient = object : WebViewClient() {
					override fun onPageFinished(view: WebView, url: String) {
						webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
								"('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
						finish()
					}
				}
			}
		}
		webview.loadUrl(intent.getStringExtra(BUNDLE_URL))

	}

	enum class Actions(val action: Int) {
		VIEW(0), CLOUD_FLARE(1);

		companion object {
			val actions: ArrayList<Actions> = ArrayList<Actions>()

			init {
				actions.add(VIEW)
				actions.add(CLOUD_FLARE)
			}
		}
	}
}