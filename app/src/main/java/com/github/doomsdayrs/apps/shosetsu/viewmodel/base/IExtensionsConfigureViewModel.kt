package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.base.SubscribeHandleViewModel

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
interface IExtensionsConfigureViewModel : SubscribeHandleViewModel<List<ExtensionUI>> {
	fun disableExtension(extensionEntity: ExtensionUI, callback: (ExtensionUI) -> Unit)
	fun enableExtension(extensionEntity: ExtensionUI, callback: (ExtensionUI) -> Unit)
	fun loadFormatterIfEnabled(extensionEntity: ExtensionUI): Formatter?
}