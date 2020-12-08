package app.shosetsu.android.datasource.file.base

import app.shosetsu.common.com.consts.settings.SettingKey
import app.shosetsu.common.com.dto.HResult
import kotlinx.coroutines.flow.Flow

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
 * 17 / 09 / 2020
 */
interface IFileSettingsDataSource {

	fun observeLong(key: SettingKey<Long>): Flow<Long>

	fun observeString(key: SettingKey<String>): Flow<String>

	fun observeInt(key: SettingKey<Int>): Flow<Int>

	fun observeBoolean(key: SettingKey<Boolean>): Flow<Boolean>

	fun observeStringSet(key: SettingKey<Set<String>>): Flow<Set<String>>

	fun observeFloat(key: SettingKey<Float>): Flow<Float>


	suspend fun getLong(key: SettingKey<Long>): HResult<Long>

	suspend fun getString(key: SettingKey<String>): HResult<String>

	suspend fun getInt(key: SettingKey<Int>): HResult<Int>

	suspend fun getBoolean(key: SettingKey<Boolean>): HResult<Boolean>

	suspend fun getStringSet(key: SettingKey<Set<String>>): HResult<Set<String>>

	suspend fun getFloat(key: SettingKey<Float>): HResult<Float>


	suspend fun setLong(key: SettingKey<Long>, value: Long)

	suspend fun setString(key: SettingKey<String>, value: String)

	suspend fun setInt(key: SettingKey<Int>, value: Int)

	suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean)

	suspend fun setStringSet(key: SettingKey<Set<String>>, value: Set<String>)

	suspend fun setFloat(key: SettingKey<Float>, value: Float)

}