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

package com.bugsnag.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLConnection;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    static void closeQuietly(@Nullable final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (@NonNull final Exception ioe) {
            // ignore
        }
    }

    static int copy(@NonNull final Reader input,
                    @NonNull final Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int read;
        while (EOF != (read = input.read(buffer))) {
            output.write(buffer, 0, read);
            count += read;
        }

        if (count > Integer.MAX_VALUE) {
            return -1;
        }

        return (int) count;
    }

    static void deleteFile(File file, Logger logger) {
        try {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        } catch (Exception ex) {
            logger.w("Failed to delete file", ex);
        }
    }
}
