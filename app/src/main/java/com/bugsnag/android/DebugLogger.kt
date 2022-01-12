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

import android.util.Log

internal object DebugLogger : Logger {

    private const val TAG = "Bugsnag"

    override fun e(msg: String) {
        Log.e(TAG, msg)
    }

    override fun e(msg: String, throwable: Throwable) {
        Log.e(TAG, msg, throwable)
    }

    override fun w(msg: String) {
        Log.w(TAG, msg)
    }

    override fun w(msg: String, throwable: Throwable) {
        Log.w(TAG, msg, throwable)
    }

    override fun i(msg: String) {
        Log.i(TAG, msg)
    }

    override fun i(msg: String, throwable: Throwable) {
        Log.i(TAG, msg, throwable)
    }

    override fun d(msg: String) {
        Log.d(TAG, msg)
    }

    override fun d(msg: String, throwable: Throwable) {
        Log.d(TAG, msg, throwable)
    }
}
