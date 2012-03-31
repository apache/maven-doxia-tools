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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddLinkDialog;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddLinkDialog.Link;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * Abstract <code>link</code> action.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see IActionConstants#LINK_ACTION for the action Key
 */
public abstract class AbstractLinkAction
    extends TextEditorAction
{
    public AbstractLinkAction( ITextEditor editor )
    {
        super( CommonPluginMessages.getResourceBundle(), "Link.", editor );

        setId( IActionConstants.LINK_ACTION );

        ImageRegistry imageRegistry = CommonPlugin.getDefault().getImageRegistry();
        setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_LINK ) );
        // TODO activate me!
        setDisabledImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_LINK_DISABLED ) );
    }

    @Override
    public void run()
    {
        AddLinkDialog dialog = new AddLinkDialog( Display.getCurrent().getActiveShell() );
        dialog.open();
        if ( dialog.getReturnCode() == Window.OK )
        {
            String linkURL = dialog.getLink().getURL();
            if ( linkURL.length() > 0 )
            {
                addLink( dialog.getLink() );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * @param link
     * @return
     */
    protected abstract String generateLink( Link link );

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Apply link
     */
    private void addLink( Link link )
    {
        Assert.isTrue( TextEditor.class.isAssignableFrom( getTextEditor().getClass() ),
                       "getTextEditor() is not an instance of AbstractMultiPageEditorPart" );

        ISelection selection = getTextEditor().getEditorSite().getSelectionProvider().getSelection();

        Assert.isTrue( TextSelection.class.isAssignableFrom( selection.getClass() ),
                       "selection is not and instance of TextSelection" );
        TextSelection selectedText = (TextSelection) selection;

        int iCursorPosition = selectedText.getOffset();

        IDocument doc = getTextEditor().getDocumentProvider().getDocument( getTextEditor().getEditorInput() );

        if ( doc != null )
        {
            try
            {
                doc.replace( iCursorPosition, 0, generateLink( link ) );
            }
            catch ( BadLocationException e )
            {
                CommonPlugin.logError( "BadLocationException: " + e.getMessage(), e, true );
            }
        }
    }
}
