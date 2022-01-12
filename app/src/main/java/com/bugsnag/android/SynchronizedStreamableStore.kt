/*
 * This file is part of FairEmail.
 *     FairEmail is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     FairEmail is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.
 *     Copyright 2018-2021 by Marcel Bokhorst (M66B)
 */

package com.bugsnag.android

import android.util.JsonReader
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * Persists and loads a [Streamable] object to the file system. This is intended for use
 * primarily as a replacement for primitive value stores such as [SharedPreferences].
 *
 * This class is made thread safe through the use of a [ReadWriteLock].
 */
internal class SynchronizedStreamableStore<T : JsonStream.Streamable>(
    private val file: File
) {

    private val lock = ReentrantReadWriteLock()

    @Throws(IOException::class)
    fun persist(streamable: T) {
        lock.writeLock().withLock {
            file.writer().buffered().use {
                streamable.toStream(JsonStream(it))
                true
            }
        }
    }

    @Throws(IOException::class)
    fun load(loadCallback: (JsonReader) -> T): T {
        lock.readLock().withLock {
            return file.reader().buffered().use {
                loadCallback(JsonReader(it))
            }
        }
    }
}
