package org.apache.maven.doxia.ide.eclipse.common.ui.editors;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.actions.IActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractEditorContributor
    extends MultiPageEditorActionBarContributor
{
    private IEditorPart activeEditorPart;

    /**
     * Creates a multi-page contributor.
     */
    public AbstractEditorContributor()
    {
        super();
    }

    /**
     * Returns the action registed with the given text editor.
     * @return IAction or null if editor is null.
     */
    protected IAction getAction( ITextEditor editor, String actionID )
    {
        return ( editor == null ? null : editor.getAction( actionID ) );
    }

    @Override
    public void setActivePage( IEditorPart part )
    {
        if ( activeEditorPart == part )
        {
            return;
        }

        activeEditorPart = part;

        IActionBars actionBars = getActionBars();
        if ( actionBars != null )
        {
            ITextEditor editor = ( part instanceof ITextEditor ) ? (ITextEditor) part : null;

            // Generic
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(),
                                               getAction( editor, ITextEditorActionConstants.DELETE ) );
            actionBars
                .setGlobalActionHandler( ActionFactory.UNDO.getId(),
                                         getAction( editor, ITextEditorActionConstants.UNDO ) );
            actionBars
                .setGlobalActionHandler( ActionFactory.REDO.getId(),
                                         getAction( editor, ITextEditorActionConstants.REDO ) );
            actionBars.setGlobalActionHandler( ActionFactory.CUT.getId(), getAction( editor,
                                                                                     ITextEditorActionConstants.CUT ) );
            actionBars
                .setGlobalActionHandler( ActionFactory.COPY.getId(),
                                         getAction( editor, ITextEditorActionConstants.COPY ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(),
                                               getAction( editor, ITextEditorActionConstants.PASTE ) );
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(),
                                               getAction( editor, ITextEditorActionConstants.SELECT_ALL ) );
            actionBars
                .setGlobalActionHandler( ActionFactory.FIND.getId(),
                                         getAction( editor, ITextEditorActionConstants.FIND ) );
            actionBars.setGlobalActionHandler( IDEActionFactory.BOOKMARK.getId(), getAction( editor,
                                                                                             IDEActionFactory.BOOKMARK
                                                                                                 .getId() ) );

            // Doxia Specific actions
            actionBars.setGlobalActionHandler( IActionConstants.BOLD_ACTION, getAction( editor,
                                                                                        IActionConstants.BOLD_ACTION ) );
            actionBars.setGlobalActionHandler( IActionConstants.ITALIC_ACTION,
                                               getAction( editor, IActionConstants.ITALIC_ACTION ) );
            actionBars.setGlobalActionHandler( IActionConstants.MONOSPACED_ACTION,
                                               getAction( editor, IActionConstants.MONOSPACED_ACTION ) );
            actionBars.setGlobalActionHandler( IActionConstants.LINK_ACTION, getAction( editor,
                                                                                        IActionConstants.LINK_ACTION ) );
            actionBars
                .setGlobalActionHandler( IActionConstants.TABLE_ACTION, getAction( editor,
                                                                                   IActionConstants.TABLE_ACTION ) );

            actionBars.updateActionBars();
        }
    }

    @Override
    public void contributeToMenu( IMenuManager menuManager )
    {
        super.contributeToMenu( menuManager );

        // Using extensions in plugin.xml instead of
    }

    @Override
    public void contributeToToolBar( IToolBarManager toolBarManager )
    {
        super.contributeToToolBar( toolBarManager );

        // Using extensions in plugin.xml instead of
    }
}
