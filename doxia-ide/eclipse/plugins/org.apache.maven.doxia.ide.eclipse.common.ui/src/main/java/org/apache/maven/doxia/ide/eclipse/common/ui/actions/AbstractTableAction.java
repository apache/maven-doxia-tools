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
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddTableDialog;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddTableDialog.Table;
import org.apache.maven.doxia.markup.Markup;
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
 * Abstract <code>table</code> action.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see IActionConstants#TABLE_ACTION for the action Key
 */
public abstract class AbstractTableAction
    extends TextEditorAction
{
    /** Default text in the generated cell */
    protected static final String DEFAULT_CELL_TEXT = "cell";

    /**
     * Default constructor.
     *
     * @param editor
     */
    public AbstractTableAction( ITextEditor editor )
    {
        super( CommonPluginMessages.getResourceBundle(), "Table.", editor );

        setId( IActionConstants.TABLE_ACTION );

        ImageRegistry imageRegistry = CommonPlugin.getDefault().getImageRegistry();
        setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_TABLE ) );
        // TODO activate me!
        setDisabledImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_TABLE_DISABLED ) );
    }

    @Override
    public void run()
    {
        AddTableDialog dialog = new AddTableDialog( Display.getCurrent().getActiveShell() );
        dialog.open();
        if ( dialog.getReturnCode() == Window.OK )
        {
            Table table = dialog.getTable();

            int rows = table.getRows();
            int cols = table.getColumns();
            if ( rows > 0 && cols > 0 )
            {
                addTable( table );
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
    protected abstract String generateTable( Table table );

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Apply table
     */
    private void addTable( Table table )
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
                int iNextLinePosition = doc.getLineOffset( selectedText.getStartLine() )
                    + doc.getLineLength( selectedText.getStartLine() );

                if ( iCursorPosition + 1 != iNextLinePosition || iCursorPosition != iNextLinePosition )
                {
                    doc.replace( iNextLinePosition, 0, Markup.EOL + generateTable( table ) + Markup.EOL );
                }
                else
                {
                    doc.replace( iCursorPosition, 0, generateTable( table ) );
                }
            }
            catch ( BadLocationException e )
            {
                CommonPlugin.logError( "BadLocationException: " + e.getMessage(), e, true );
            }
        }
    }
}
