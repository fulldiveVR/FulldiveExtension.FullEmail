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

import static com.bugsnag.android.SeverityReason.REASON_PROMISE_REJECTION;

import com.bugsnag.android.internal.ImmutableConfig;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

class DeliveryDelegate extends BaseObservable {

    final Logger logger;
    private final EventStore eventStore;
    private final ImmutableConfig immutableConfig;
    final BreadcrumbState breadcrumbState;
    private final Notifier notifier;
    final BackgroundTaskService backgroundTaskService;

    DeliveryDelegate(Logger logger,
                     EventStore eventStore,
                     ImmutableConfig immutableConfig,
                     BreadcrumbState breadcrumbState,
                     Notifier notifier,
                     BackgroundTaskService backgroundTaskService) {
        this.logger = logger;
        this.eventStore = eventStore;
        this.immutableConfig = immutableConfig;
        this.breadcrumbState = breadcrumbState;
        this.notifier = notifier;
        this.backgroundTaskService = backgroundTaskService;
    }

    void deliver(@NonNull Event event) {
        logger.d("DeliveryDelegate#deliver() - event being stored/delivered by Client");
        // Build the eventPayload
        String apiKey = event.getApiKey();
        EventPayload eventPayload = new EventPayload(apiKey, event, notifier, immutableConfig);
        Session session = event.getSession();

        if (session != null) {
            if (event.isUnhandled()) {
                event.setSession(session.incrementUnhandledAndCopy());
                updateState(StateEvent.NotifyUnhandled.INSTANCE);
            } else {
                event.setSession(session.incrementHandledAndCopy());
                updateState(StateEvent.NotifyHandled.INSTANCE);
            }
        }

        if (event.getImpl().getOriginalUnhandled()) {
            // should only send unhandled errors if they don't terminate the process (i.e. ANRs)
            String severityReasonType = event.getImpl().getSeverityReasonType();
            boolean promiseRejection = REASON_PROMISE_REJECTION.equals(severityReasonType);
            boolean anr = event.getImpl().isAnr(event);
            cacheEvent(event, anr || promiseRejection);
        } else {
            deliverPayloadAsync(event, eventPayload);
        }
    }

    private void deliverPayloadAsync(@NonNull Event event, EventPayload eventPayload) {
        final EventPayload finalEventPayload = eventPayload;
        final Event finalEvent = event;

        // Attempt to send the eventPayload in the background
        try {
            backgroundTaskService.submitTask(TaskType.ERROR_REQUEST, new Runnable() {
                @Override
                public void run() {
                    deliverPayloadInternal(finalEventPayload, finalEvent);
                }
            });
        } catch (RejectedExecutionException exception) {
            cacheEvent(event, false);
            logger.w("Exceeded max queue count, saving to disk to send later");
        }
    }

    @VisibleForTesting
    DeliveryStatus deliverPayloadInternal(@NonNull EventPayload payload, @NonNull Event event) {
        logger.d("DeliveryDelegate#deliverPayloadInternal() - attempting event delivery");
        DeliveryParams deliveryParams = immutableConfig.getErrorApiDeliveryParams(payload);
        Delivery delivery = immutableConfig.getDelivery();
        DeliveryStatus deliveryStatus = delivery.deliver(payload, deliveryParams);

        switch (deliveryStatus) {
            case DELIVERED:
                logger.i("Sent 1 new event to Bugsnag");
                leaveErrorBreadcrumb(event);
                break;
            case UNDELIVERED:
                logger.w("Could not send event(s) to Bugsnag,"
                        + " saving to disk to send later");
                cacheEvent(event, false);
                leaveErrorBreadcrumb(event);
                break;
            case FAILURE:
                logger.w("Problem sending event to Bugsnag");
                break;
            default:
                break;
        }
        return deliveryStatus;
    }

    private void cacheEvent(@NonNull Event event, boolean attemptSend) {
        eventStore.write(event);
        if (attemptSend) {
            eventStore.flushAsync();
        }
    }

    private void leaveErrorBreadcrumb(@NonNull Event event) {
        // Add a breadcrumb for this event occurring
        List<Error> errors = event.getErrors();

        if (errors.size() > 0) {
            String errorClass = errors.get(0).getErrorClass();
            String message = errors.get(0).getErrorMessage();

            Map<String, Object> data = new HashMap<>();
            data.put("errorClass", errorClass);
            data.put("message", message);
            data.put("unhandled", String.valueOf(event.isUnhandled()));
            data.put("severity", event.getSeverity().toString());
            breadcrumbState.add(new Breadcrumb(errorClass,
                    BreadcrumbType.ERROR, data, new Date(), logger));
        }
    }
}
