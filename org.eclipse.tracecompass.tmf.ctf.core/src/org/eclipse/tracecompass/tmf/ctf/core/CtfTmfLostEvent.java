/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alexandre Montplaisir - Initial API and implementation
 *******************************************************************************/

package org.eclipse.tracecompass.tmf.ctf.core;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.ctf.core.event.EventDefinition;
import org.eclipse.tracecompass.ctf.core.event.IEventDeclaration;
import org.eclipse.tracecompass.tmf.core.event.ITmfLostEvent;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimeRange;

/**
 * An implementation of {@link ITmfLostEvent} for use in the CTF adaptor.
 *
 * @author Alexandre Montplaisir
 * @since 2.2
 */
public class CtfTmfLostEvent extends CtfTmfEvent implements ITmfLostEvent {

    private final TmfTimeRange fTimeRange;
    private final long fNbLost;

    /**
     * Constructor. Only {@link CtfTmfEventFactory} should call this.
     *
     * @param trace
     *            The origin trace
     * @param rank
     *            The rank of the event in the trace
     * @param content
     *            The event's payload (fields). In case this event has some.
     * @param fileName
     *            The name of the trace file from which this event comes
     * @param cpu
     *            The CPU on which this event happened
     * @param declaration
     *            The CTF Event Declaration object that created this event
     * @param timeRange
     *            The time range of lost events indicated by this one
     * @param nbLost
     *            The number of lost events in the range
     */
    CtfTmfLostEvent(CtfTmfTrace trace,
            long rank,
            String fileName,
            int cpu,
            IEventDeclaration declaration,
            TmfTimeRange timeRange,
            long nbLost,
            @NonNull EventDefinition def) {
        /*
         * Only the factory should call this method, the case to
         * (CtfTmfTimestamp) should be safe.
         */
        super(trace, rank, (CtfTmfTimestamp) timeRange.getStartTime(), fileName, cpu, declaration, def);
        fTimeRange = timeRange;
        fNbLost = nbLost;
    }

    @Override
    public TmfTimeRange getTimeRange() {
        return fTimeRange;
    }

    @Override
    public long getNbLostEvents() {
        return fNbLost;
    }

}
