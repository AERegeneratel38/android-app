package app.shosetsu.android.view.uimodels.settings.base

import android.os.Build
import android.view.View
import android.view.View.VISIBLE
import android.widget.*
import androidx.annotation.CallSuper
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.android.common.consts.VISIBLE
import app.shosetsu.android.ui.settings.viewHolder.SettingsItem
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.textfield.TextInputEditText
import com.mikepenz.fastadapter.FastAdapter
import com.xw.repo.BubbleSeekBar

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
 * 25 / 06 / 2020
 * Data for [SettingsItem]
 */
abstract class SettingsItemData(
		val id: Int
) : BaseRecyclerItem<SettingsItemData.ViewHolder>() {
	lateinit var lifecycleOwner: LifecycleOwner

	/** Min version required for this setting to be visible */
	var minVersionCode: Int = Build.VERSION_CODES.Q

	var titleID: Int = -1
	var titleText: String = ""

	var descID: Int = -1
	var descText: String = ""

	@CallSuper
	override fun bindView(holder: ViewHolder, payloads: List<Any>) {
		super.bindView(holder, payloads)
		with(holder) {
			if (titleID != -1)
				itemTitle.setText(titleID)
			else
				itemTitle.text = titleText

			if (descID != -1) {
				itemDescription.isVisible = true
				itemDescription.setText(descID)
			} else if (descText.isNotEmpty()) {
				itemDescription.isVisible = true
				itemDescription.text = descText
			}
		}
	}

	@CallSuper
	override fun unbindView(holder: ViewHolder) {
		with(holder) {
			itemTitle.text = null
			itemDescription.text = null
		}
	}

	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SettingsItemData>(itemView) {
		/** Item main view */
		val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)

		/** Item title */
		val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)

		/** Item description */
		val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)

		/** @see Button */
		val button: Button = itemView.findViewById(R.id.button)

		/** @see Spinner */
		val spinner: Spinner = itemView.findViewById(R.id.spinner)

		/** @see TextView */
		val textView: TextView = itemView.findViewById(R.id.text)

		/** @see SwitchCompat */
		val switchView: SwitchCompat = itemView.findViewById(R.id.switchView)

		/** @see NumberPicker */
		val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPicker)

		/** @see View */
		val colorBox: View = itemView.findViewById(R.id.colorBox)

		/** @see CheckBox */
		val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

		/** @see BubbleSeekBar */
		val seekbar: BubbleSeekBar = itemView.findViewById(R.id.bubbleSeekBar)

		/** @see TextInputEditText */
		val textInputEditText: TextInputEditText = itemView.findViewById(R.id.textInputEditText)

		/** Contains fields on the right */
		val rightField: ConstraintLayout = itemView.findViewById(R.id.rightField)

		/** Contains fields on the bottom */
		val bottomField: ConstraintLayout = itemView.findViewById(R.id.bottomField)

		override fun bindView(item: SettingsItemData, payloads: List<Any>): Unit =
				item.bindView(this, payloads)

		override fun unbindView(item: SettingsItemData): Unit =
				item.unbindView(this)

	}

	override val layoutRes: Int = R.layout.settings_item
	override val type: Int = javaClass.hashCode()
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}
