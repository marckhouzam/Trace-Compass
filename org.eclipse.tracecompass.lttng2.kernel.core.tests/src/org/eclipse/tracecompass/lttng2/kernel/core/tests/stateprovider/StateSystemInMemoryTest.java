/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alexandre Montplaisir - Initial API and implementation
 *   Bernd Hufmann - Use state system analysis module instead of factory
 ******************************************************************************/

package org.eclipse.tracecompass.lttng2.kernel.core.tests.stateprovider;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.tracecompass.internal.lttng2.kernel.core.stateprovider.LttngKernelStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfAnalysisException;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ctf.core.CtfTmfTrace;
import org.junit.After;

/**
 * State system tests using the in-memory back-end.
 *
 * @author Alexandre Montplaisir
 */
public class StateSystemInMemoryTest extends StateSystemTest {

    private TestLttngKernelAnalysisModule module;

    @Override
    protected ITmfStateSystem initialize() {
        module = new TestLttngKernelAnalysisModule();
        try {
            module.setTrace(testTrace.getTrace());
        } catch (TmfAnalysisException e) {
            fail();
        }
        module.schedule();
        assertTrue(module.waitForCompletion());
        return module.getStateSystem();
    }

    /**
     * Class cleanup
     */
    @After
    public void cleanup() {
        if (module != null) {
            module.dispose();
        }
    }

    private static class TestLttngKernelAnalysisModule extends TmfStateSystemAnalysisModule {

        /**
         * Constructor adding the views to the analysis
         */
        public TestLttngKernelAnalysisModule() {
            super();
        }

        @Override
        public void setTrace(ITmfTrace trace) throws TmfAnalysisException {
            if (!(trace instanceof CtfTmfTrace)) {
                throw new IllegalStateException("TestLttngKernelAnalysisModule: trace should be of type CtfTmfTrace"); //$NON-NLS-1$
            }
            super.setTrace(trace);
        }

        @Override
        protected ITmfStateProvider createStateProvider() {
            return new LttngKernelStateProvider(getTrace());
        }

        @Override
        protected StateSystemBackendType getBackendType() {
            return StateSystemBackendType.INMEM;
        }
    }
}
