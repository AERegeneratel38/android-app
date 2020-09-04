package com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted

import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IsOnlineCheckViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.SubscribeHandleViewModel

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class IExtensionsViewModel
	: ViewModel(), SubscribeHandleViewModel<List<ExtensionUI>>, IsOnlineCheckViewModel {

	/** Refreshes the repositories and data values */
	abstract fun refreshRepository()

	/** Installs an extension (can also update it) */
	abstract fun installExtension(extensionUI: ExtensionUI)

	/** Uninstalls an extension */
	abstract fun uninstallExtension(extensionUI: ExtensionUI)
}