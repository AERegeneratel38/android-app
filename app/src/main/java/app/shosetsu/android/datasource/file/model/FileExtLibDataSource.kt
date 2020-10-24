package app.shosetsu.android.datasource.file.model

import app.shosetsu.android.common.consts.LIBRARY_DIR
import app.shosetsu.android.common.consts.SOURCE_DIR
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.enums.InternalFileDir.FILES
import app.shosetsu.android.datasource.file.base.IFileExtLibDataSource
import app.shosetsu.android.providers.file.base.IFileSystemProvider

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
 */
class FileExtLibDataSource(
        private val iFileSystemProvider: IFileSystemProvider,
) : IFileExtLibDataSource {

    private fun makeLibraryFile(fileName: String): String =
            "$SOURCE_DIR$LIBRARY_DIR$fileName.lua"

    override suspend fun writeExtLib(fileName: String, data: String): HResult<*> =
            iFileSystemProvider.writeInternalFile(
                    FILES,
                    makeLibraryFile(fileName),
                    data
            )

    override suspend fun loadExtLib(fileName: String): HResult<String> =
            blockingLoadLib(fileName)

    override fun blockingLoadLib(fileName: String): HResult<String> =
            iFileSystemProvider.readInternalFile(FILES, makeLibraryFile(fileName))

    override suspend fun deleteExtLib(fileName: String): HResult<*> =
            iFileSystemProvider.deleteInternalFile(FILES, makeLibraryFile(fileName))
}