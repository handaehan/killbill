/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.overdue.wrapper;

import com.ning.billing.account.api.Account;
import com.ning.billing.overdue.OverdueApiException;
import com.ning.billing.overdue.OverdueService;
import com.ning.billing.overdue.OverdueState;
import com.ning.billing.overdue.applicator.OverdueStateApplicator;
import com.ning.billing.overdue.calculator.BillingStateCalculator;
import com.ning.billing.overdue.config.api.BillingState;
import com.ning.billing.overdue.config.api.OverdueException;
import com.ning.billing.overdue.config.api.OverdueStateSet;
import com.ning.billing.callcontext.InternalCallContext;
import com.ning.billing.callcontext.InternalTenantContext;
import com.ning.billing.clock.Clock;
import com.ning.billing.junction.BlockingInternalApi;

public class OverdueWrapper {
    private final Account overdueable;
    private final BlockingInternalApi api;
    private final Clock clock;
    private final OverdueStateSet overdueStateSet;
    private final BillingStateCalculator billingStateCalcuator;
    private final OverdueStateApplicator overdueStateApplicator;

    public OverdueWrapper(final Account overdueable, final BlockingInternalApi api,
                          final OverdueStateSet overdueStateSet,
                          final Clock clock,
                          final BillingStateCalculator billingStateCalcuator,
                          final OverdueStateApplicator overdueStateApplicator) {
        this.overdueable = overdueable;
        this.overdueStateSet = overdueStateSet;
        this.api = api;
        this.clock = clock;
        this.billingStateCalcuator = billingStateCalcuator;
        this.overdueStateApplicator = overdueStateApplicator;
    }

    public OverdueState refresh(final InternalCallContext context) throws OverdueException, OverdueApiException {
        if (overdueStateSet.size() < 1) { // No configuration available
            return overdueStateSet.getClearState();
        }

        final BillingState billingState = billingState(context);
        final String previousOverdueStateName = api.getBlockingStateForService(overdueable, OverdueService.OVERDUE_SERVICE_NAME, context).getStateName();

        final OverdueState currentOverdueState = overdueStateSet.findState(previousOverdueStateName);
        final OverdueState nextOverdueState = overdueStateSet.calculateOverdueState(billingState, clock.getToday(billingState.getAccountTimeZone()));

        overdueStateApplicator.apply(overdueStateSet.getFirstState(), billingState, overdueable, currentOverdueState, nextOverdueState, context);

        return nextOverdueState;
    }

    public void clear(final InternalCallContext context) throws OverdueException, OverdueApiException {
        final String previousOverdueStateName = api.getBlockingStateForService(overdueable, OverdueService.OVERDUE_SERVICE_NAME, context).getStateName();
        final OverdueState previousOverdueState = overdueStateSet.findState(previousOverdueStateName);
        overdueStateApplicator.clear(overdueable, previousOverdueState, overdueStateSet.getClearState(), context);
    }

    public BillingState billingState(final InternalTenantContext context) throws OverdueException {
        return billingStateCalcuator.calculateBillingState(overdueable, context);
    }
}
