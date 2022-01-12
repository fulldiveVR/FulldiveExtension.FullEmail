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

/**
 * Tracks the current context and allows observers to be notified whenever it changes.
 *
 * The default behaviour is to track [SessionTracker.getContextActivity]. However, any value
 * that the user sets via [Bugsnag.setContext] will override this and be returned instead.
 */
internal class ContextState : BaseObservable() {

    companion object {
        private const val MANUAL = "__BUGSNAG_MANUAL_CONTEXT__"
    }

    private var manualContext: String? = null
    private var automaticContext: String? = null

    fun setManualContext(context: String?) {
        manualContext = context
        automaticContext = MANUAL
        emitObservableEvent()
    }

    fun setAutomaticContext(context: String?) {
        if (automaticContext !== MANUAL) {
            automaticContext = context
            emitObservableEvent()
        }
    }

    fun getContext(): String? {
        return automaticContext.takeIf { it !== MANUAL } ?: manualContext
    }

    fun emitObservableEvent() = updateState { StateEvent.UpdateContext(getContext()) }
}
