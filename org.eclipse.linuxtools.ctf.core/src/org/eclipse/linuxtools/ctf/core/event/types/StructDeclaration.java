/*******************************************************************************
 * Copyright (c) 2011-2012 Ericsson, Ecole Polytechnique de Montreal and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Matthew Khouzam - Initial API and implementation
 * Contributors: Simon Marchi - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.ctf.core.event.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <b><u>StructDeclaration</u></b>
 */
public class StructDeclaration implements IDeclaration {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private final HashMap<String, IDeclaration> fields = new HashMap<String, IDeclaration>();
    private final List<String> fieldsList = new LinkedList<String>();
    private long maxAlign;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public StructDeclaration(long align) {
        this.maxAlign = Math.max(align, 1);
    }

    // ------------------------------------------------------------------------
    // Getters/Setters/Predicates
    // ------------------------------------------------------------------------

    public long getMaxAlign() {
        return maxAlign;
    }

    public boolean hasField(String name) {
        return this.fields.containsKey(name);
    }

    public HashMap<String, IDeclaration> getFields() {
        return this.fields;
    }

    public List<String> getFieldsList() {
        return this.fieldsList;
    }

    @Override
    public long getAlignment() {
        return this.maxAlign;
    }
    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    @Override
    public StructDefinition createDefinition(IDefinitionScope definitionScope,
            String fieldName) {
        return new StructDefinition(this, definitionScope, fieldName);
    }

    public void addField(String name, IDeclaration declaration) {
        this.fields.put(name, declaration);
        this.fieldsList.add(name);
        maxAlign = Math.max(maxAlign, declaration.getAlignment());
        if( maxAlign == 1 )
        {
            maxAlign =1;
        }
    }

    @Override
    public String toString() {
        /* Only used for debugging */
        return "[declaration] struct[" + Integer.toHexString(hashCode()) + ']'; //$NON-NLS-1$
    }

}
