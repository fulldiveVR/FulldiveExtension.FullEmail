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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class JsonStream extends JsonWriter {

    private final ObjectJsonStreamer objectJsonStreamer;

    public interface Streamable {
        void toStream(@NonNull JsonStream stream) throws IOException;
    }

    private final Writer out;

    /**
     * Constructs a JSONStream
     *
     * @param out the writer
     */
    public JsonStream(@NonNull Writer out) {
        super(out);
        setSerializeNulls(false);
        this.out = out;
        objectJsonStreamer = new ObjectJsonStreamer();
    }

    // Allow chaining name().value()
    @NonNull
    public JsonStream name(@Nullable String name) throws IOException {
        super.name(name);
        return this;
    }

    /**
     * Serialises an arbitrary object as JSON, handling primitive types as well as
     * Collections, Maps, and arrays.
     */
    public void value(@Nullable Object object, boolean shouldRedactKeys) throws IOException {
        if (object instanceof Streamable) {
            ((Streamable) object).toStream(this);
        } else {
            objectJsonStreamer.objectToStream(object, this, shouldRedactKeys);
        }
    }

    /**
     * Serialises an arbitrary object as JSON, handling primitive types as well as
     * Collections, Maps, and arrays.
     */
    public void value(@Nullable Object object) throws IOException {
        value(object, false);
    }

    /**
     * Writes a File (its content) into the stream
     */
    public void value(@NonNull File file) throws IOException {
        if (file == null || file.length() <= 0) {
            return;
        }

        super.flush();
        beforeValue(); // add comma if in array

        // Copy the file contents onto the stream
        Reader input = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            input = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            IOUtils.copy(input, out);
        } finally {
            IOUtils.closeQuietly(input);
        }

        out.flush();
    }
}
