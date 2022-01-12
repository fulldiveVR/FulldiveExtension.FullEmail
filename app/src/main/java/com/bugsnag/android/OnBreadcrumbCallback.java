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

/**
 * Add a "on breadcrumb" callback, to execute code before every
 * breadcrumb captured by Bugsnag.
 * <p>
 * You can use this to modify breadcrumbs before they are stored by Bugsnag.
 * You can also return <code>false</code> from any callback to ignore a breadcrumb.
 * <p>
 * For example:
 * <p>
 * Bugsnag.onBreadcrumb(new OnBreadcrumbCallback() {
 * public boolean onBreadcrumb(Breadcrumb breadcrumb) {
 * return false; // ignore the breadcrumb
 * }
 * })
 */
public interface OnBreadcrumbCallback {

    /**
     * Runs the "on breadcrumb" callback. If the callback returns
     * <code>false</code> any further OnBreadcrumbCallback callbacks will not be called
     * and the breadcrumb will not be captured by Bugsnag.
     *
     * @param breadcrumb the breadcrumb to be captured by Bugsnag
     * @see Breadcrumb
     */
    boolean onBreadcrumb(@NonNull Breadcrumb breadcrumb);

}
