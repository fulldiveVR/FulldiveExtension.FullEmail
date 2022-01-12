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

import java.io.IOException;
import java.util.List;

/**
 * A representation of a thread recorded in an {@link Event}
 */
@SuppressWarnings("ConstantConditions")
public class Thread implements JsonStream.Streamable {

    private final ThreadInternal impl;
    private final Logger logger;

    Thread(
            long id,
            @NonNull String name,
            @NonNull ThreadType type,
            boolean errorReportingThread,
            @NonNull Stacktrace stacktrace,
            @NonNull Logger logger) {
        this.impl = new ThreadInternal(id, name, type, errorReportingThread, stacktrace);
        this.logger = logger;
    }

    private void logNull(String property) {
        logger.e("Invalid null value supplied to thread." + property + ", ignoring");
    }

    /**
     * Sets the unique ID of the thread (from {@link java.lang.Thread})
     */
    public void setId(long id) {
        impl.setId(id);
    }

    /**
     * Gets the unique ID of the thread (from {@link java.lang.Thread})
     */
    public long getId() {
        return impl.getId();
    }

    /**
     * Sets the name of the thread (from {@link java.lang.Thread})
     */
    public void setName(@NonNull String name) {
        if (name != null) {
            impl.setName(name);
        } else {
            logNull("name");
        }
    }

    /**
     * Gets the name of the thread (from {@link java.lang.Thread})
     */
    @NonNull
    public String getName() {
        return impl.getName();
    }

    /**
     * Sets the type of thread based on the originating platform (intended for internal use only)
     */
    public void setType(@NonNull ThreadType type) {
        if (type != null) {
            impl.setType(type);
        } else {
            logNull("type");
        }
    }

    /**
     * Gets the type of thread based on the originating platform (intended for internal use only)
     */
    @NonNull
    public ThreadType getType() {
        return impl.getType();
    }

    /**
     * Gets whether the thread was the thread that caused the event
     */
    public boolean getErrorReportingThread() {
        return impl.isErrorReportingThread();
    }

    /**
     * Sets a representation of the thread's stacktrace
     */
    public void setStacktrace(@NonNull List<Stackframe> stacktrace) {
        if (!CollectionUtils.containsNullElements(stacktrace)) {
            impl.setStacktrace(stacktrace);
        } else {
            logNull("stacktrace");
        }
    }

    /**
     * Gets a representation of the thread's stacktrace
     */
    @NonNull
    public List<Stackframe> getStacktrace() {
        return impl.getStacktrace();
    }

    @Override
    public void toStream(@NonNull JsonStream stream) throws IOException {
        impl.toStream(stream);
    }
}
