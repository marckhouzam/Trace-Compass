/*******************************************************************************
 * Copyright (c) 2009, 2011, 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   William Bourque - Initial API and implementation
 *   Yuriy Vashchuk - Heritage correction.
 *   Francois Chouinard - Cleanup and refactoring
 *   Francois Chouinard - Moved from LTTng to TMF
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.ui.views.histogram;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.TmfTimeRange;
import org.eclipse.linuxtools.tmf.core.request.ITmfDataRequest;
import org.eclipse.linuxtools.tmf.core.request.TmfEventRequest;
import org.eclipse.linuxtools.tmf.core.trace.TmfTrace;

/**
 * <b><u>HistogramRequest</u></b>
 * <p>
 */
public class HistogramRequest extends TmfEventRequest<ITmfEvent> {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    protected final HistogramDataModel fHistogram;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public HistogramRequest(HistogramDataModel histogram, TmfTimeRange range, int rank, int nbEvents, ITmfDataRequest.ExecutionType execType) {
        super(ITmfEvent.class, range, rank, nbEvents, TmfTrace.DEFAULT_INDEX_PAGE_SIZE, execType);
        fHistogram = histogram;
    }

    public HistogramRequest(HistogramDataModel histogram, TmfTimeRange range, ITmfDataRequest.ExecutionType execType) {
        this(histogram, range, 0, ALL_DATA, execType);
    }

    public HistogramRequest(HistogramDataModel histogram, TmfTimeRange range, int rank, ITmfDataRequest.ExecutionType execType) {
        this(histogram, range, rank, ALL_DATA, execType);
    }

    // ------------------------------------------------------------------------
    // TmfEventRequest
    // ------------------------------------------------------------------------

    @Override
    public void handleData(ITmfEvent event) {
        super.handleData(event);
        if (event != null) {
            long timestamp = event.getTimestamp().getValue();
            fHistogram.countEvent(getNbRead(), timestamp);
        }
    }

    @Override
    public void handleCompleted() {
        fHistogram.complete();
        super.handleCompleted();
    }

    @Override
    public void handleCancel() {
        super.handleCancel();
    }

}