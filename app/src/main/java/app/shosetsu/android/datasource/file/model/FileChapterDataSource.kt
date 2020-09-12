package app.shosetsu.android.datasource.file.model

import android.content.Context
import android.util.Log
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.datasource.file.base.IFileChapterDataSource
import app.shosetsu.android.domain.model.local.ChapterEntity
import java.io.File
import java.io.FileNotFoundException

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
 * 12 / 05 / 2020
 * @param context Context of application
 */
class FileChapterDataSource(val context: Context) : IFileChapterDataSource {
	private val ap = context.getExternalFilesDir(null)!!.also {
		Log.d(logID(), it.toString())
	}

	/** Makes path */
	private fun makePath(ce: ChapterEntity): String =
			"$ap/download/${ce.formatterID}/${ce.novelID}/${ce.id}.txt"

	override suspend fun saveChapterPassageToStorage(
			chapterEntity: ChapterEntity,
			passage: String,
	): HResult<*> {
		File(makePath(chapterEntity)).also {
			it.parentFile?.mkdirs()
		}.writeText(passage)
		return successResult("")
	}

	override suspend fun loadChapterPassageFromStorage(chapterEntity: ChapterEntity): HResult<String> =
			try {
				successResult(File(makePath(chapterEntity)).readText())
			} catch (e: FileNotFoundException) {
				errorResult(ERROR_NOT_FOUND, e)
			} catch (e: Exception) {
				errorResult(ERROR_GENERAL, e)
			}

	override suspend fun deleteChapter(chapterEntity: ChapterEntity): HResult<*> {
		File(makePath(chapterEntity)).takeIf { it.exists() }?.delete()
				?: return errorResult(ERROR_NOT_FOUND, "Chapter not found")
		return successResult("")
	}
}