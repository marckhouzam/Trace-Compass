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

package org.eclipse.tracecompass.ctf.core.event.types;

import java.util.Collection;
import java.util.List;

import org.eclipse.tracecompass.ctf.core.event.io.BitBuffer;
import org.eclipse.tracecompass.ctf.core.event.scope.IDefinitionScope;
import org.eclipse.tracecompass.ctf.core.trace.CTFReaderException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Multimap;

/**
 * A CTF sequence declaration.
 *
 * An array where the size is fixed but declared in the trace, unlike array
 * where it is declared with a literal
 * @deprecated use {@link org.eclipse.tracecompass.internal.ctf.core.event.types.SequenceDeclaration}
 * @version 1.0
 * @author Matthew Khouzam
 * @author Simon Marchi
 */
@Deprecated
public class SequenceDeclaration extends CompoundDeclaration {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private final IDeclaration fElemType;
    private final String fLengthName;
    private final Multimap<String, String> fPaths = ArrayListMultimap.<String, String>create();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructor
     *
     * @param lengthName
     *            the name of the field describing the length
     * @param elemType
     *            The element type
     */
    public SequenceDeclaration(String lengthName, IDeclaration elemType) {
        fElemType = elemType;
        fLengthName = lengthName;
    }

    // ------------------------------------------------------------------------
    // Getters/Setters/Predicates
    // ------------------------------------------------------------------------

    @Override
    public IDeclaration getElementType() {
        return fElemType;
    }

    /**
     * Gets the name of the length field
     *
     * @return the name of the length field
     */
    public String getLengthName() {
        return fLengthName;
    }

    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    /**
     * @since 3.0
     */
    @SuppressWarnings("null") // immutablelist
    @Override
    public SequenceDefinition createDefinition(
            IDefinitionScope definitionScope, String fieldName, BitBuffer input) throws CTFReaderException {
        IDefinition lenDef = null;

        if (definitionScope != null) {
            lenDef = definitionScope.lookupDefinition(getLengthName());
        }

        if (lenDef == null) {
            throw new CTFReaderException("Sequence length field not found"); //$NON-NLS-1$
        }

        if (!(lenDef instanceof IntegerDefinition)) {
            throw new CTFReaderException("Sequence length field not integer"); //$NON-NLS-1$
        }

        IntegerDefinition lengthDefinition = (IntegerDefinition) lenDef;

        if (lengthDefinition.getDeclaration().isSigned()) {
            throw new CTFReaderException("Sequence length must not be signed"); //$NON-NLS-1$
        }

        long length = lengthDefinition.getValue();
        if ((length > Integer.MAX_VALUE) || (!input.canRead((int) length * fElemType.getMaximumSize()))) {
            throw new CTFReaderException("Sequence length too long " + length); //$NON-NLS-1$
        }

        Collection<String> collection = fPaths.get(fieldName);
        while (collection.size() < length) {
            fPaths.put(fieldName, fieldName + '[' + collection.size() + ']');
        }
        List<String> paths = (List<String>) fPaths.get(fieldName);
        Builder<Definition> definitions = new ImmutableList.Builder<>();
        for (int i = 0; i < length; i++) {
            definitions.add(fElemType.createDefinition(definitionScope, paths.get(i), input));
        }
        return new SequenceDefinition(this, definitionScope, fieldName, definitions.build());
    }

    @Override
    public String toString() {
        /* Only used for debugging */
        return "[declaration] sequence[" + Integer.toHexString(hashCode()) + ']'; //$NON-NLS-1$
    }

    /**
     * @since 3.0
     */
    @Override
    public int getMaximumSize() {
        return Integer.MAX_VALUE;
    }

}
