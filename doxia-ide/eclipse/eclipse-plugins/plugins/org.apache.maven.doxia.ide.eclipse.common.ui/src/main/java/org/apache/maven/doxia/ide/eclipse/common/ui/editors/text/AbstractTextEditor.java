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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.IActionConstants;
import org.apache.maven.doxia.ide.eclipse.common.ui.editors.text.source.TextPairMatcher;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

/**
 * Doxia editor for text file resources.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractTextEditor
    extends TextEditor
{
    private TextPairMatcher doxiaTextPairMatcher;

    /**
     * Default constructor.
     */
    public AbstractTextEditor()
    {
        super();

        Assert.isNotNull( getEditorId(), "getEditorId() should be defined" );
        Assert.isNotNull( getTextSourceViewerConfiguration(), "getTextSourceViewerConfiguration() should be defined" );
        Assert.isNotNull( getTextSourceViewerConfiguration(), "getTextSourceViewerConfiguration() should be defined" );

        doxiaTextPairMatcher = new TextPairMatcher();

        setSourceViewerConfiguration( getTextSourceViewerConfiguration() );
        setDocumentProvider( getFileDocumentProvider() );
    }

    @Override
    protected void initializeEditor()
    {
        super.initializeEditor();

        // see http://www.eclipse.org/articles/article.php?file=Article-action-contribution/index.html
        setEditorContextMenuId( getEditorId() + ".EditorContext" );
        setRulerContextMenuId( getEditorId() + ".RulerContext" );
    }

    /**
     * @return The ID of this editor like should be defined in plugin.xml
     */
    public abstract String getEditorId();

    /**
     * @return the implementation of the document provider for this editor.
     */
    public abstract IDocumentProvider getFileDocumentProvider();

    /**
     * @return the implementation of the source viewer configuration for this editor.
     */
    public abstract TextSourceViewerConfiguration getTextSourceViewerConfiguration();

    @Override
    protected void createActions()
    {
        super.createActions();

        // TODO create TextOperationAction?

        IAction action = new ContentAssistAction( CommonPluginMessages.getResourceBundle(), AbstractTextEditor.class
            .getName()
            + ".contentAssist.", this );
        action.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
        setAction( IActionConstants.CONTENT_ASSIST_ACTION, action );
    }

    @Override
    protected void configureSourceViewerDecorationSupport( SourceViewerDecorationSupport support )
    {
        support.setCharacterPairMatcher( doxiaTextPairMatcher );

        super.configureSourceViewerDecorationSupport( support );
    }
}
