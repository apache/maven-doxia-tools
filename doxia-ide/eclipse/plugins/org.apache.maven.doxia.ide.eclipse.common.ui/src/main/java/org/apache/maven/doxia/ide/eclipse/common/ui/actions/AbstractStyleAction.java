package org.apache.maven.doxia.ide.eclipse.common.ui.actions;

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

import java.util.ResourceBundle;

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * Abstract class for style actions.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractStyleAction
    extends TextEditorAction
{
    public AbstractStyleAction( ResourceBundle bundle, String prefix, ITextEditor editor )
    {
        super( bundle, prefix, editor );

        Assert.isNotNull( getStartMarkup(), "getStartMarkup() should be defined" );
        Assert.isNotNull( getEndMarkup(), "getEndMarkup() should be defined" );
    }

    @Override
    public void run()
    {
        addStyle();
    }

    /**
     * @return a start markup for the Doxia implementation.
     */
    public abstract String getStartMarkup();

    /**
     * @return a end markup for the Doxia implementation.
     */
    public abstract String getEndMarkup();

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * @param link
     * @return
     */
    protected String generateStyle( String strSelectedText )
    {
        return getStartMarkup() + strSelectedText + getEndMarkup();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Apply style
     */
    private void addStyle()
    {
        Assert.isTrue( TextEditor.class.isAssignableFrom( getTextEditor().getClass() ),
                       "getTextEditor() is not an instance of AbstractMultiPageEditorPart" );

        ISelection selection = getTextEditor().getEditorSite().getSelectionProvider().getSelection();

        Assert.isTrue( TextSelection.class.isAssignableFrom( selection.getClass() ),
                       "selection is not and instance of TextSelection" );
        TextSelection selectedText = (TextSelection) selection;

        String strSelectedText = selectedText.getText();
        if ( StringUtils.isEmpty( strSelectedText ) )
        {
            return;
        }

        int iCursorPosition = selectedText.getOffset();
        IDocument doc = getTextEditor().getDocumentProvider().getDocument( getTextEditor().getEditorInput() );

        if ( doc != null )
        {
            try
            {
                doc.replace( iCursorPosition, selectedText.getLength(), generateStyle( strSelectedText ) );
            }
            catch ( BadLocationException e )
            {
                CommonPlugin.logError( "BadLocationException: " + e.getMessage(), e, true );
            }
        }
    }
}
