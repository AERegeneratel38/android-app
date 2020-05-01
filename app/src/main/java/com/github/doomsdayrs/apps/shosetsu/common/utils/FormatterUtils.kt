package com.github.doomsdayrs.apps.shosetsu.common.utils

import android.content.Context
import android.util.Log
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.common.utils.base.IFormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionLibraryEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.RepositoryDao
import com.github.doomsdayrs.apps.shosetsu.common.ext.getMeta
import com.github.doomsdayrs.apps.shosetsu.common.ext.md5
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.FileNotFoundException
import java.sql.SQLException
import java.util.*


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
 * ====================================================================
 */

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 * TODO Turn this into a service
 */
class FormatterUtils(
		val extensionsDao: ExtensionsDao,
		val repositoryDao: RepositoryDao
) : IFormatterUtils {
	companion object {
		const val scriptDirectory = "/scripts/"
		const val libraryDirectory = "/libraries/"
		const val sourceFolder = "/src/"
		const val repoFolderStruct = "/src/main/resources/"
		val unknown = object : Formatter {
			override val formatterID: Int = -1

			override val baseURL: String
				get() = throw Exception("Unknown Formatter")
			override val hasCloudFlare: Boolean
				get() = throw Exception("Unknown Formatter")
			override val hasSearch: Boolean
				get() = throw Exception("Unknown Formatter")
			override val imageURL: String
				get() = throw Exception("Unknown Formatter")
			override val listings: Array<Formatter.Listing>
				get() = throw Exception("Unknown Formatter")
			override val name: String
				get() = throw Exception("Unknown Formatter")
			override val searchFilters: Array<Filter<*>>
				get() = throw Exception("Unknown Formatter")
			override val settings: Array<Filter<*>>
				get() = throw Exception("Unknown Formatter")

			override fun getPassage(chapterURL: String): String = throw Exception("Unknown Formatter")

			override fun parseNovel(novelURL: String, loadChapters: Boolean, reporter: (status: String) -> Unit) =
					throw Exception("Unknown Formatter")

			override fun search(data: Array<*>, reporter: (status: String) -> Unit) =
					throw Exception("Unknown Formatter")

			override fun updateSetting(id: Int, value: Any?): Unit = throw Exception("Unknown Formatter")
		}
		val formatters = ArrayList<Formatter>()

		fun removeByID(formatterID: Int) {
			formatters.forEachIndexed { index, formatter ->
				if (formatter.formatterID == formatterID) {
					formatters.removeAt(index)
					return
				}
			}
		}

		fun addFormatter(formatter: Formatter) {
			removeByID(formatter.formatterID)
			formatters.add(formatter)
			formatters.sortBy { it.name }
		}

		fun getByID(ID: Int): Formatter = formatters.firstOrNull { it.formatterID == ID } ?: unknown

		private var filesDir: String? = null

		private fun ap(context: Context?): String {
			filesDir?.let { return it }
			filesDir = context!!.filesDir.absolutePath
			return filesDir!!
		}

		fun makeLibraryFile(context: Context? = null, le: ExtensionLibraryEntity): File =
				makeLibraryFile(context, le.scriptName)

		fun makeLibraryFile(context: Context? = null, scriptName: String): File {
			val f = File("${ap(context)}$sourceFolder$libraryDirectory${scriptName}.lua")
			f.parentFile?.let { if (!it.exists()) it.mkdirs() }
			return f
		}

		fun makeFormatterFile(context: Context? = null, fe: ExtensionEntity): File =
				makeFormatterFile(context, fe.fileName)

		fun makeFormatterFile(context: Context? = null, fileName: String): File {
			val f = File("${ap(context)}$sourceFolder$scriptDirectory${fileName}.lua")
			f.parentFile?.let { if (!it.exists()) it.mkdirs() }
			return f
		}

		fun makeLibraryURL(repo: RepositoryEntity, le: ExtensionLibraryEntity): String =
				"${repo.url}$repoFolderStruct/lib/${le.scriptName}.lua"

		fun makeFormatterURL(repo: RepositoryEntity, fe: ExtensionEntity): String =
				"${repo.url}$repoFolderStruct/src/${fe.lang}/${fe.fileName}.lua"

		private fun splitVersion(version: String): Array<String> =
				version.split(".").toTypedArray()

		/**
		 * @return [Boolean] true if version difference
		 */
		fun compareVersions(ver1: String, ver2: String): Boolean {
			if (ver1 == ver2)
				return false

			val version1 = splitVersion(ver1)
			val version2 = splitVersion(ver2)

			if (version1.size != version2.size)
				return false

			version1.forEachIndexed { index, s ->
				if (version2[index] != s)
					return true
			}
			return false
		}

		/**
		 * A quick way to get a response
		 */
		fun quickResponse(url: String) =
				OkHttpClient().newCall(Request.Builder()
						.url(url)
						.build()
				).execute()
	}

	@Throws(FileNotFoundException::class, JSONException::class, SQLException::class)
	fun trustScript(file: File) {
		val name = file.nameWithoutExtension
		val meta = LuaFormatter(file).getMetaData()!!
		val md5 = file.readText().md5()!!
		val id = meta.getInt("id")
		val repo = meta.getJSONObject("repo")
		GlobalScope.launch {
			extensionsDao.insertReplace(
					ExtensionEntity(
							id = id,
							repoID = repositoryDao
									.createIfNotExist(RepositoryEntity(
											url = repo.getString("URL"),
											name = repo.getString("name")
									)),
							fileName = file.name,
							installed = true,
							name = name,
							enabled = true
					)
			)
		}
	}

	/**
	 * Dynamic MD5 checking
	 */
	@Throws(JSONException::class, MissingResourceException::class)
	fun confirm(file: File, pass: () -> Unit, fail: () -> Unit, noMeta: () -> Unit): Boolean {
		val meta = file.getMeta()

		// Checks MD5 sum
		val sum = extensionsDao
				.loadFormatterMD5(meta.getInt("id"))

		require(sum.isNotEmpty())

		val fileSum = file.readText().md5()

		Log.i("FormatterInit", "${file.name}:\tSum required:{$sum}\tSum found:\t{$fileSum}")

		return if (sum == fileSum) {
			pass()
			true
		} else {
			fail()
			false
		}
	}

	/**
	 * Installs the library
	 */
	fun downloadLibrary(
			extensionLibraryEntity: ExtensionLibraryEntity,
			context: Context,
			file: File = makeLibraryFile(context, extensionLibraryEntity),
			repo: RepositoryEntity = repositoryDao.loadRepositoryFromID(
					extensionLibraryEntity.repoID
			)
	): Boolean {
		return quickResponse(makeLibraryURL(repo, extensionLibraryEntity)).body?.let {
			file.writeText(it.string())
			true
		} ?: false
	}

	/**
	 * Installs the extension in question
	 */
	fun installExtension(
			extensionEntity: ExtensionEntity,
			context: Context,
			file: File = makeFormatterFile(context, extensionEntity),
			repo: RepositoryEntity = repositoryDao.loadRepositoryFromID(
					extensionEntity.repoID
			)
	): Boolean {
		return quickResponse(makeFormatterURL(repo, extensionEntity)).body?.let {
			file.writeText(it.string())
			addFormatter(LuaFormatter(file))
			true
		} ?: false
	}

	fun deleteFormatter(extensionEntity: ExtensionEntity, context: Context) {
		removeByID(extensionEntity.id)
		makeFormatterFile(context, extensionEntity).takeIf { it.exists() }?.delete()
	}

	/**
	 * Loads the formatters
	 */
	fun load(context: Context) {
		val fileNames = extensionsDao
				.loadPoweredFormatterFileNames()
		fileNames.forEach {
			formatters.add(
					LuaFormatter(makeFormatterFile(context, it))
			)
		}
	}


	fun getAsCards(): ArrayList<FormatterCard> {
		val catalogueCards = ArrayList<FormatterCard>()
		for (formatter in formatters) catalogueCards.add(FormatterCard(formatter))
		catalogueCards.sortedWith(compareBy { it.title })
		return catalogueCards
	}

}