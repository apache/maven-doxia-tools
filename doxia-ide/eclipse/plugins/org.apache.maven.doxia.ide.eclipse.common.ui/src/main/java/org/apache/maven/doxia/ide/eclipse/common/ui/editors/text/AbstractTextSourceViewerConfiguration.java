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

import org.apache.maven.doxia.ide.eclipse.common.ui.ColorManager;
import org.apache.maven.doxia.ide.eclipse.common.ui.rules.AbstractTextPartitionScanner;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * Abstract class for the source viewer configuration.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractTextSourceViewerConfiguration
    extends TextSourceViewerConfiguration
{
    /**
     * Default constructor.
     */
    public AbstractTextSourceViewerConfiguration()
    {
        Assert.isNotNull( getScanner(), "getScanner() should be defined" );
    }

    @Override
    public String[] getConfiguredContentTypes( ISourceViewer sourceViewer )
    {
        return AbstractTextPartitionScanner.DOXIA_LEGAL_CONTENT_TYPES;
    }

    @Override
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer( getScanner() );
        reconciler.setDamager( dr, IDocument.DEFAULT_CONTENT_TYPE );
        reconciler.setRepairer( dr, IDocument.DEFAULT_CONTENT_TYPE );

        dr = new DefaultDamagerRepairer( getScanner() );
        reconciler.setDamager( dr, AbstractTextPartitionScanner.DOXIA_PARTITION_CONTENT );
        reconciler.setRepairer( dr, AbstractTextPartitionScanner.DOXIA_PARTITION_CONTENT );

        dr = new DefaultDamagerRepairer( new SingleTokenScanner( new TextAttribute( ColorManager.getInstance()
            .getColor( ColorManager.COMMENT ) ) ) );
        reconciler.setDamager( dr, AbstractTextPartitionScanner.DOXIA_PARTITION_COMMENT );
        reconciler.setRepairer( dr, AbstractTextPartitionScanner.DOXIA_PARTITION_COMMENT );

        return reconciler;
    }

    /**
     * @return the Doxia implementation scanner.
     */
    protected abstract RuleBasedScanner getScanner();

    /**
     * Single token scanner, used for scanning for multiline comments mainly.
     */
    static class SingleTokenScanner
        extends BufferedRuleBasedScanner
    {
        public SingleTokenScanner( TextAttribute attribute )
        {
            setDefaultReturnToken( new Token( attribute ) );
        }
    }
}
