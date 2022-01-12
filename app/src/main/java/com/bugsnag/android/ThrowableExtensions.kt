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

@file:JvmName("ThrowableUtils")
package com.bugsnag.android

/**
 * Unroll the list of causes for this Throwable, handling any recursion that may appear within
 * the chain. The first element returned will be this Throwable, and the last will be the root
 * cause or last non-recursive Throwable.
 */
internal fun Throwable.safeUnrollCauses(): List<Throwable> {
    val causes = LinkedHashSet<Throwable>()
    var currentEx: Throwable? = this

    // Set.add will return false if we have already "seen" currentEx
    while (currentEx != null && causes.add(currentEx)) {
        currentEx = currentEx.cause
    }

    return causes.toList()
}
