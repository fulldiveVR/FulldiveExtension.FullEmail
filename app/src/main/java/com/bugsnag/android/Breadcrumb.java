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
import java.util.Date;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class Breadcrumb implements JsonStream.Streamable {

    // non-private to allow direct field access optimizations
    final BreadcrumbInternal impl;
    private final Logger logger;

    Breadcrumb(@NonNull String message, @NonNull Logger logger) {
        this.impl = new BreadcrumbInternal(message);
        this.logger = logger;
    }

    Breadcrumb(@NonNull String message,
               @NonNull BreadcrumbType type,
               @Nullable Map<String, Object> metadata,
               @NonNull Date timestamp,
               @NonNull Logger logger) {
        this.impl = new BreadcrumbInternal(message, type, metadata, timestamp);
        this.logger = logger;
    }

    private void logNull(String property) {
        logger.e("Invalid null value supplied to breadcrumb." + property + ", ignoring");
    }

    /**
     * Sets the description of the breadcrumb
     */
    public void setMessage(@NonNull String message) {
        if (message != null) {
            impl.message = message;
        } else {
            logNull("message");
        }
    }

    /**
     * Gets the description of the breadcrumb
     */
    @NonNull
    public String getMessage() {
        return impl.message;
    }

    /**
     * Sets the type of breadcrumb left - one of those enabled in
     * {@link Configuration#getEnabledBreadcrumbTypes()}
     */
    public void setType(@NonNull BreadcrumbType type) {
        if (type != null) {
            impl.type = type;
        } else {
            logNull("type");
        }
    }

    /**
     * Gets the type of breadcrumb left - one of those enabled in
     * {@link Configuration#getEnabledBreadcrumbTypes()}
     */
    @NonNull
    public BreadcrumbType getType() {
        return impl.type;
    }

    /**
     * Sets diagnostic data relating to the breadcrumb
     */
    public void setMetadata(@Nullable Map<String, Object> metadata) {
        impl.metadata = metadata;
    }

    /**
     * Gets diagnostic data relating to the breadcrumb
     */
    @Nullable
    public Map<String, Object> getMetadata() {
        return impl.metadata;
    }

    /**
     * The timestamp that the breadcrumb was left
     */
    @NonNull
    public Date getTimestamp() {
        return impl.timestamp;
    }

    @NonNull
    String getStringTimestamp() {
        return DateUtils.toIso8601(impl.timestamp);
    }

    @Override
    public void toStream(@NonNull JsonStream stream) throws IOException {
        impl.toStream(stream);
    }
}
