package org.apache.maven.doxia.ide.eclipse.common.ui.editors.text;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.ide.eclipse.common.ui.rules.AbstractTextPartitionScanner;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractTextDocumentProvider
    extends TextFileDocumentProvider
{
    /**
     * Default constructor.
     */
    public AbstractTextDocumentProvider()
    {
        Assert.isNotNull( getScanner(), "getScanner() should be defined" );
    }

    @Override
    public IDocument getDocument( final Object element )
    {
        final IDocument document = getParentDocument( element );
        if ( document != null )
        {
            IDocumentPartitioner partitioner = new FastPartitioner(
                                                                    getScanner(),
                                                                    AbstractTextPartitionScanner.DOXIA_LEGAL_CONTENT_TYPES );
            partitioner.connect( document );
            document.setDocumentPartitioner( partitioner );
        }
        return document;
    }

    protected IDocument getParentDocument( Object element )
    {
        return super.getDocument( element );
    }

    public abstract IPartitionTokenScanner getScanner();
}
