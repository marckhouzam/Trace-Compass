/*******************************************************************************
 * Copyright (c) 2009, 2012 Ericsson.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon - Initial API and implementation
 *   Patrick Tasse - Refactoring
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.ui.widgets.timegraph.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.linuxtools.internal.tmf.ui.Messages;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.ITimeGraphProvider;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphProvider.StateColor;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.widgets.TimeGraphColorScheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


public class TimeGraphLegend extends TitleAreaDialog {

    public static final int interactionColors[] = {
        TimeGraphColorScheme.TI_START_THREAD,
        TimeGraphColorScheme.TI_NOTIFY_JOINED, TimeGraphColorScheme.TI_NOTIFY,
        TimeGraphColorScheme.TI_INTERRUPT, TimeGraphColorScheme.TI_HANDOFF_LOCK };

    protected TimeGraphColorScheme colors;
    private ITimeGraphProvider ifUtil;

    public static void open(Shell parent, ITimeGraphProvider rifUtil) {
        (new TimeGraphLegend(parent, rifUtil)).open();
    }

    public TimeGraphLegend(Shell parent, ITimeGraphProvider rifUtil) {
        super(parent);
        colors = new TimeGraphColorScheme();
        this.ifUtil = rifUtil;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dlgArea = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(dlgArea, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayoutData(gd);

        createThreadStatesGroup(composite);

        setMessage(Messages.TmfTimeLegend_LEGEND);
        setTitle(Messages.TmfTimeLegend_TRACE_STATES_TITLE);
        setDialogHelpAvailable(false);
        setHelpAvailable(false);

        return composite;
    }

    private void createThreadStatesGroup(Composite composite) {
        Group gs = new Group(composite, SWT.NONE);
        gs.setText(Messages.TmfTimeLegend_TRACE_STATES);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gs.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 20;
        layout.marginBottom = 10;
        gs.setLayout(layout);

        // Go through all the defined colors and only add the ones you need. 
        // This will not handle several colors assigned to a color, we have 
        // 16 mil colors, and should not pick two to mean the same thing. 
        for (int i = 0; i <  TimeGraphColorScheme.getStateColors().length; i++) {
            //Get the color enum related to the index
            StateColor stateColor = TimeGraphColorScheme.getStateColors()[i];
            //Get the given name, provided by the interface to the application
            String stateName = ifUtil.getStateName(stateColor);
            if( stateName != "Not mapped" ) { //$NON-NLS-1$
                Bar bar = new Bar(gs, i);
                gd = new GridData();
                gd.widthHint = 40;
                gd.heightHint = 20;
                gd.verticalIndent = 8;
                bar.setLayoutData(gd);
                Label name = new Label(gs, SWT.NONE);
                name.setText(stateName);
                gd = new GridData();
                gd.horizontalIndent = 10;
                gd.verticalIndent = 8;
                name.setLayoutData(gd);
            }
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.TmfTimeLegend_WINDOW_TITLE);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }

    class Bar extends Canvas {
        private Color color;

        public Bar(Composite parent, int colorIdx) {
            super(parent, SWT.NONE);

            color = colors.getColor(colorIdx);
            addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    draw(event.gc);
                }
            });
        }

        private void draw(GC gc) {
            Rectangle r = getClientArea();
            gc.setBackground(color);
            gc.fillRectangle(r);
            gc.setForeground(colors.getColor(TimeGraphColorScheme.BLACK));
            gc.drawRectangle(0, 0, r.width - 1, r.height - 1);
        }
    }

    class Arrow extends Canvas {
        public final static int HEIGHT = 12;
        public final static int DX = 3;

        private Color color;

        public Arrow(Composite parent, int colorIdx) {
            super(parent, SWT.NONE);

            color = colors.getColor(colorIdx);
            addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    draw(event.gc);
                }
            });
        }

        private void draw(GC gc) {
            Rectangle r = getClientArea();
            gc.setForeground(color);

            int y0, y1;
            if (r.height > HEIGHT) {
                y0 = (r.height - HEIGHT) / 2;
                y1 = y0 + HEIGHT;
            } else {
                y0 = 0;
                y1 = r.height;
            }

            gc.drawLine(DX, y0, DX, y1);

            gc.drawLine(0, y0 + 3, DX, y0);
            gc.drawLine(2 * DX, y0 + 3, DX, y0);
        }
    }

    class Mark extends Canvas {
        public final static int DX = 3;

        private Color color;

        public Mark(Composite parent, int colorIdx) {
            super(parent, SWT.NONE);

            color = colors.getColor(colorIdx);
            addListener(SWT.Paint, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    draw(event.gc);
                }
            });
        }

        private void draw(GC gc) {
            Rectangle r = getClientArea();
            gc.setBackground(color);

            int y = (r.height - DX) / 2;
            int c[] = { 0, y, DX, y + DX, 2 * DX, y };
            gc.fillPolygon(c);
        }
    }
}