package app.shosetsu.android.viewmodel.model.settings

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.common.com.consts.settings.SettingKey.*
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.handle
import app.shosetsu.common.com.enums.MarkingTypes.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 31 / 08 / 2020
 */
class ReaderSettingsViewModel(
		iSettingsRepository: ISettingsRepository,
		private val context: Context,
		private val reportExceptionUseCase: ReportExceptionUseCase,
		val loadReaderThemes: LoadReaderThemes
) : AReaderSettingsViewModel(iSettingsRepository) {

	override fun getReaderThemes(): LiveData<List<ColorChoiceUI>> =
			loadReaderThemes().asIOLiveData()

	override suspend fun settings(): List<SettingsItemData> = listOf(
			customSettingData(1) {
				title { "" }
			},
			spinnerSettingData(2) {
				title { R.string.paragraph_spacing }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_with_none)
				)
				iSettingsRepository.getInt(ReaderParagraphSpacing).handle {
					spinnerValue { it }
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						iSettingsRepository.setInt(ReaderParagraphSpacing, position)
					}
				}
			},
			spinnerSettingData(3) {
				title { R.string.text_size }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_no_none)
				)
				iSettingsRepository.getFloat(ReaderTextSize).handle {
					spinnerValue {
						when (it) {
							14f -> 0
							17f -> 1
							20f -> 2
							else -> 0
						}
					}
				}
				onSpinnerItemSelected { adapterView, _, i, _ ->
					if (i in 0..2) {
						var size = 14
						when (i) {
							0 -> {
							}
							1 -> size = 17
							2 -> size = 20
						}
						launchIO {
							iSettingsRepository.setFloat(ReaderTextSize, size.toFloat())
						}

						adapterView?.setSelection(i)
					}
				}
			},
			spinnerSettingData(4) {
				title { R.string.paragraph_indent }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_with_none)
				)
				iSettingsRepository.getInt(ReaderIndentSize).handle {
					spinnerValue { it }
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						iSettingsRepository.setInt(ReaderIndentSize, position)
					}
				}
			},
			customBottomSettingData(5) {
				title { R.string.reader_theme }
			},
			switchSettingData(6) {
				title { R.string.inverted_swipe }
				iSettingsRepository.getBoolean(ReaderIsInvertedSwipe).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ReaderIsInvertedSwipe, isChecked)
					}
				}
			},
			switchSettingData(7) {
				title { R.string.tap_to_scroll }
				iSettingsRepository.getBoolean(ReaderIsTapToScroll).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ReaderIsTapToScroll, isChecked)
					}
				}
			},

			switchSettingData(6) {
				title { R.string.mark_read_as_reading }
				description { R.string.mark_read_as_reading_desc }
				iSettingsRepository.getBoolean(ReaderMarkReadAsReading).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ReaderMarkReadAsReading, isChecked)
					}
				}
			},

			spinnerSettingData(0) {
				title { R.string.marking_mode }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.marking_names)
				)
				iSettingsRepository.getString(ReadingMarkingType).handle {
					valueOf(it).let {
						spinnerValue {
							when (it) {
								ONSCROLL -> 1
								ONVIEW -> 0
							}
						}

					}
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						when (position) {
							0 -> iSettingsRepository.setString(ReadingMarkingType, ONVIEW.name)
							1 -> iSettingsRepository.setString(ReadingMarkingType, ONSCROLL.name)
							else -> Log.e("MarkingMode", "UnknownType")
						}
					}
				}
			},

			switchSettingData(8) {
				title { "Resume first unread" }
				description {
					"Instead of resuming the first chapter reading/unread, " +
							"the app will open the first unread chapter"
				}
				iSettingsRepository.getBoolean(ChaptersResumeFirstUnread).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ChaptersResumeFirstUnread, isChecked)
					}
				}
			},
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}