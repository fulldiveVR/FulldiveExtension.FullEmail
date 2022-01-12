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

import java.io.IOException;
import java.util.Collection;
import java.util.List;


/**
 * An Error represents information extracted from a {@link Throwable}.
 */
@SuppressWarnings("ConstantConditions")
public class Error implements JsonStream.Streamable {

    private final ErrorInternal impl;
    private final Logger logger;

    Error(@NonNull ErrorInternal impl,
          @NonNull Logger logger) {
        this.impl = impl;
        this.logger = logger;
    }

    private void logNull(String property) {
        logger.e("Invalid null value supplied to error." + property + ", ignoring");
    }

    /**
     * Sets the fully-qualified class name of the {@link Throwable}
     */
    public void setErrorClass(@NonNull String errorClass) {
        if (errorClass != null) {
            impl.setErrorClass(errorClass);
        } else {
            logNull("errorClass");
        }
    }

    /**
     * Gets the fully-qualified class name of the {@link Throwable}
     */
    @NonNull
    public String getErrorClass() {
        return impl.getErrorClass();
    }

    /**
     * The message string from the {@link Throwable}
     */
    public void setErrorMessage(@Nullable String errorMessage) {
        impl.setErrorMessage(errorMessage);
    }

    /**
     * The message string from the {@link Throwable}
     */
    @Nullable
    public String getErrorMessage() {
        return impl.getErrorMessage();
    }

    /**
     * Sets the type of error based on the originating platform (intended for internal use only)
     */
    public void setType(@NonNull ErrorType type) {
        if (type != null) {
            impl.setType(type);
        } else {
            logNull("type");
        }
    }

    /**
     * Sets the type of error based on the originating platform (intended for internal use only)
     */
    @NonNull
    public ErrorType getType() {
        return impl.getType();
    }

    /**
     * Gets a representation of the stacktrace
     */
    @NonNull
    public List<Stackframe> getStacktrace() {
        return impl.getStacktrace();
    }

    @Override
    public void toStream(@NonNull JsonStream stream) throws IOException {
        impl.toStream(stream);
    }

    static List<Error> createError(@NonNull Throwable exc,
                                   @NonNull Collection<String> projectPackages,
                                   @NonNull Logger logger) {
        return ErrorInternal.Companion.createError(exc, projectPackages, logger);
    }
}
