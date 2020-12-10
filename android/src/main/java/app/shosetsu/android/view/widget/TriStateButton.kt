package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater.from
import android.widget.FrameLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.TriStateButtonBinding

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
 * 23 / 11 / 2020
 */
class TriStateButton @JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null,
		defStyleAttr: Int = 0
) : FrameLayout(
		context,
		attrs,
		defStyleAttr
) {
	private val checkedRes: Int
	private val uncheckedRes: Int
	private val goneRes: Int

	private val binding: TriStateButtonBinding by lazy {
		TriStateButtonBinding.inflate(
				from(context), this, true
		)
	}

	var isChecked = false
		set(value) {
			setDrawable(isActive, value)
			field = value
		}

	var isActive = true
		set(value) {
			setDrawable(value, isChecked)
			field = value
		}

	init {
		context.theme.obtainStyledAttributes(attrs, R.styleable.TriStateButton, defStyleAttr, 0).apply {
			try {
				checkedRes = getResourceIdOrThrow(R.styleable.TriStateButton_button_unchecked)
				uncheckedRes = getResourceIdOrThrow(R.styleable.TriStateButton_button_checked)
				goneRes = getResourceId(R.styleable.TriStateButton_button_disabled, 0)
				binding.textView.text = getString(R.styleable.TriStateButton_android_text)
						?: ""
			} finally {
				recycle()
			}
		}
		setDrawable(isActive, isChecked)
	}

	fun toggle() {
		isChecked = !isChecked
	}

	fun toggleIsActive() {
		isActive = !isActive
	}

	private fun setDrawable(isActive: Boolean, isChecked: Boolean) {
		if (!isActive && goneRes != 0) {
			binding.imageView.setImageResource(goneRes)
			return
		} else binding.imageView.isVisible = isActive
		binding.imageView.setImageResource(if (isChecked) checkedRes else uncheckedRes)
	}

	fun isFacingSouth() = isChecked


}