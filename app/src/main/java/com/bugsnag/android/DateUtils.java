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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class DateUtils {
    // SimpleDateFormat isn't thread safe, cache one instance per thread as needed.
    private static final ThreadLocal<DateFormat> iso8601Holder = new ThreadLocal<DateFormat>() {
        @NonNull
        @Override
        protected DateFormat initialValue() {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            iso8601.setTimeZone(tz);
            return iso8601;
        }
    };

    @NonNull
    static String toIso8601(@NonNull Date date) {
        DateFormat dateFormat = iso8601Holder.get();
        if (dateFormat == null) {
            throw new IllegalStateException("Unable to find valid dateformatter");
        }
        return dateFormat.format(date);
    }

    @NonNull
    static Date fromIso8601(@NonNull String date) {
        try {
            return iso8601Holder.get().parse(date);
        } catch (ParseException exc) {
            throw new IllegalArgumentException("Failed to parse timestamp", exc);
        }
    }
}
