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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.apache.maven.doxia.ide.eclipse.common.ui.DoxiaWrapper;
import org.apache.maven.doxia.ide.eclipse.common.ui.composites.BrowserComposite;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * Abstract Doxia multipage editor.
 * <br/>
 * Doxia editor has 2 pages:
 * <dl>
 * <dt>Page 0: edit</dt>
 * <dd>Nested text editor.</dd>
 * <dt>page 1: view</dt>
 * <dd>Nested browser.</dd>
 * </dl>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractMultiPageEditorPart
    extends MultiPageEditorPart
    implements IResourceChangeListener, IPropertyListener
{
    /** The nested text editor used in page 0. */
    private TextEditor textEditor;

    /** The editor page id, should be 0. */
    private int editorPageId;

    /** The nested browser used in page 1. */
    private BrowserComposite browser;

    /** The browser page id, should be 1. */
    private int browserPageId;

    /**
     * Creates a multi-page editor.
     */
    public AbstractMultiPageEditorPart()
    {
        super();

        ResourcesPlugin.getWorkspace().addResourceChangeListener( this );

        Assert.isNotNull( getFormat(), "getFormat() should be defined" );
        Assert.isNotNull( getTextEditor(), "getTextEditor() should be defined" );
    }

    @Override
    protected void createPages()
    {
        try
        {
            setPartName( getEditorInput().getName() );

            createEditPage();
            createBrowserPage();

            // TODO add a design page
        }
        catch ( PartInitException e )
        {
            String msg = ( StringUtils.isEmpty( e.getMessage() ) ? e.getClass().getName() : e.getMessage() );
            ErrorDialog.openError( getSite().getShell(), getString( "PartInitException.text" ), msg, e.getStatus() );

            throw new RuntimeException( e );
        }

        try
        {
            browser.convert();
        }
        catch ( Throwable e )
        {
            String msg = ( StringUtils.isEmpty( e.getMessage() ) ? e.getClass().getName() : e.getMessage() );
            ErrorDialog.openError( getSite().getShell(), getString( "ConverterThrowable.message.text" ), msg, null );

            throw new RuntimeException( e );
        }
    }

    @Override
    public void init( IEditorSite site, IEditorInput editorInput )
        throws PartInitException
    {
        if ( !( editorInput instanceof IFileEditorInput ) )
        {
            throw new PartInitException( "Invalid Input: Must be IFileEditorInput" );
        }

        super.init( site, editorInput );
    }

    @Override
    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );

        super.dispose();
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        if ( !isSaveAsAllowed() )
        {
            boolean okToSave = MessageDialog.openConfirm( getSite().getShell(),
                                                          getString( "doSave.openConfirm.title.text" ),
                                                          getString( "doSave.openConfirm.message.text" ) );
            if ( !okToSave )
            {
                return;
            }
        }

        getEditor( editorPageId ).doSave( monitor );

        try
        {
            browser.convert();
        }
        catch ( Throwable e )
        {
            String msg = ( StringUtils.isEmpty( e.getMessage() ) ? e.getClass().getName() : e.getMessage() );
            ErrorDialog.openError( getSite().getShell(), getString( "ConverterThrowable.message.text" ), msg, null );

            throw new RuntimeException( e );
        }
    }

    @Override
    public void doSaveAs()
    {
        if ( !isSaveAsAllowed() )
        {
            boolean okToSave = MessageDialog.openConfirm( getSite().getShell(),
                                                          getString( "doSave.openConfirm.title.text" ),
                                                          getString( "doSave.openConfirm.message.text" ) );
            if ( !okToSave )
            {
                return;
            }
        }

        IEditorPart editor = getEditor( editorPageId );
        editor.doSaveAs();
        setPageText( editorPageId, editor.getTitle() );
        setInput( editor.getEditorInput() );

        try
        {
            browser.convert();
        }
        catch ( Throwable e )
        {
            String msg = ( StringUtils.isEmpty( e.getMessage() ) ? e.getClass().getName() : e.getMessage() );
            ErrorDialog.openError( getSite().getShell(), getString( "ConverterThrowable.message.text" ), msg, null );

            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        try
        {
            return getDoxiaFile().findMarkers( null, true, IResource.DEPTH_ZERO ).length == 0;
        }
        catch ( CoreException ce )
        {
            String msg = ( StringUtils.isEmpty( ce.getMessage() ) ? ce.getClass().getName() : ce.getMessage() );
            ErrorDialog
                .openError( getSite().getShell(), getString( "CoreException.message.text" ), msg, ce.getStatus() );

            return false;
        }
    }

    @Override
    protected void pageChange( int newPageIndex )
    {
        IMarker[] markers = null;
        try
        {
            markers = getDoxiaFile().findMarkers( IMarker.PROBLEM, true, IResource.DEPTH_INFINITE );
        }
        catch ( CoreException ce )
        {
            String msg = ( StringUtils.isEmpty( ce.getMessage() ) ? ce.getClass().getName() : ce.getMessage() );
            ErrorDialog
                .openError( getSite().getShell(), getString( "CoreException.message.text" ), msg, ce.getStatus() );
        }

        if ( newPageIndex == browserPageId )
        {
            updateToolbar( false );
            updateEditMenu( false );
        }
        else
        {
            updateToolbar( true );
            updateEditMenu( true );
        }

        if ( newPageIndex == browserPageId && isDirty() )
        {
            boolean okToSave = MessageDialog.openConfirm( getSite().getShell(),
                                                          getString( "pageChange.openConfirm.title.text" ),
                                                          getString( "pageChange.openConfirm.message.text" ) );
            if ( okToSave )
            {
                IProgressMonitor monitor = null;
                try
                {
                    if ( monitor == null )
                    {
                        monitor = new NullProgressMonitor();
                        monitor.beginTask( "Save content...", 1 );
                    }
                    doSave( monitor );
                    monitor.worked( 1 );
                }
                finally
                {
                    monitor.done();
                }
            }
            else
            {
                setActivePage( editorPageId );
            }
        }
        else if ( newPageIndex == browserPageId && markers != null && markers.length > 0 )
        {
            MessageDialog.openError( getSite().getShell(), getString( "pageChange.openConfirm.title.text" ),
                                     getString( "pageChange.openConfirm.message.text" ) );

            setActivePage( editorPageId );

            try
            {
                Object owner;
                for ( int i = markers.length - 1; i >= 0; i-- )
                {
                    IMarker marker = markers[i];
                    owner = marker.getAttribute( CommonPlugin.PLUGIN_ID );

                    if ( owner != null && owner instanceof String )
                    {
                        if ( owner.equals( CommonPlugin.PLUGIN_ID ) )
                        {
                            IGotoMarker gotoMarkerAdapter = (IGotoMarker) textEditor.getAdapter( IGotoMarker.class );
                            if ( gotoMarkerAdapter != null )
                            {
                                gotoMarkerAdapter.gotoMarker( marker );
                                return;
                            }
                        }
                    }
                }
            }
            catch ( CoreException ce )
            {
                String msg = ( StringUtils.isEmpty( ce.getMessage() ) ? ce.getClass().getName() : ce.getMessage() );
                ErrorDialog.openError( getSite().getShell(), getString( "CoreException.message.text" ), msg, ce
                    .getStatus() );
            }
        }
        else
        {
            if ( newPageIndex != browserPageId )
            {
                super.pageChange( newPageIndex );
            }
        }
    }

    /** {@inheritDoc} */
    public void resourceChanged( final IResourceChangeEvent event )
    {
        if ( event.getType() == IResourceChangeEvent.PRE_CLOSE )
        {
            Display.getDefault().asyncExec( new Runnable()
            {
                /** {@inheritDoc} */
                public void run()
                {
                    IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                    for ( int i = 0; i < pages.length; i++ )
                    {
                        if ( ( (FileEditorInput) getEditorInput() ).getFile().getProject().equals( event.getResource() ) )
                        {
                            IEditorPart editorPart = pages[i].findEditor( getEditorInput() );
                            pages[i].closeEditor( editorPart, true );
                        }
                    }
                }
            } );
        }
    }

    /** {@inheritDoc} */
    public void propertyChanged( Object source, int propId )
    {
        if ( propId == IWorkbenchPartConstants.PROP_PART_NAME )
        {
            if ( source instanceof TextEditor )
            {
                TextEditor te = (TextEditor) source;
                setPartName( te.getPartName() );
            }
        }
    }

    /**
     * @return the browser page
     */
    public BrowserComposite getBrowser()
    {
        return browser;
    }

    /**
     * @return the Doxia implementation used for the Text editor page.
     */
    public abstract TextEditor getTextEditor();

    /**
     * @return the Doxia format for the given Text editor page, i.e. <code>apt</code>.
     */
    public abstract String getFormat();

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Contains a text editor.
     */
    private void createEditPage()
        throws PartInitException
    {
        textEditor = getTextEditor();
        editorPageId = addPage( textEditor, getEditorInput() );
        setPageText( editorPageId, getString( "edit.label" ) );
        textEditor.addPropertyListener( this );

        IDocument document = textEditor.getDocumentProvider().getDocument( getEditorInput() );
        document.addDocumentListener( new DoxiaDocumentListener( document ) );
    }

    /**
     * Contains the generated page from the editor.
     */
    private void createBrowserPage()
        throws PartInitException
    {
        browser = new BrowserComposite( getContainer(), SWT.NONE, getEditorSite().getActionBars(), getDoxiaFile(),
                                        getFormat() );
        browserPageId = addPage( browser );
        setPageText( browserPageId, getString( "view.label" ) );
    }

    /**
     * @return IFile from the editorInput
     * @see #init(IEditorSite, IEditorInput)
     */
    private IFile getDoxiaFile()
    {
        return ( (FileEditorInput) getEditorInput() ).getFile();
    }

    /**
     * Enabled or disabled action in the toolbar
     *
     * @param enabled
     */
    private void updateToolbar( boolean enabled )
    {
        for ( int i = 0; i < textEditor.getEditorSite().getActionBars().getToolBarManager().getItems().length; i++ )
        {
            IContributionItem item = textEditor.getEditorSite().getActionBars().getToolBarManager().getItems()[i];

            if ( item.isSeparator() )
            {
                continue;
            }

            if ( ActionContributionItem.class.isAssignableFrom( item.getClass() ) )
            {
                ActionContributionItem action = (ActionContributionItem) item;
                action.getAction().setEnabled( enabled );
            }
        }
    }

    /**
     * Enabled or disabled action in the toolbar
     *
     * @param enabled
     */
    private void updateEditMenu( boolean enabled )
    {
        IMenuManager editMenu = getEditorSite().getActionBars().getMenuManager()
            .findMenuUsingPath( IWorkbenchActionConstants.M_EDIT );

        for ( int i = 0; i < editMenu.getItems().length; i++ )
        {
            IContributionItem item = editMenu.getItems()[i];

            if ( item.isSeparator() )
            {
                continue;
            }

            if ( ActionContributionItem.class.isAssignableFrom( item.getClass() ) )
            {
                ActionContributionItem actionContributionItem = (ActionContributionItem) item;
                actionContributionItem.getAction().setEnabled( enabled );
            }

            if ( SubContributionItem.class.isAssignableFrom( item.getClass() ) )
            {
                // TODO don't work
                SubContributionItem subContributionItem = (SubContributionItem) item;
                subContributionItem.getInnerItem().setVisible( enabled );
            }
        }
    }

    private static String getString( String subkey )
    {
        return CommonPluginMessages.getString( "AbstractMultiPageEditorPart." + subkey );
    }

    /**
     * This document listener converts the content of the input text editor.
     *
     * @see DoxiaWrapper#convert(String, IFile, String)
     */
    class DoxiaDocumentListener
        implements IDocumentListener
    {
        private IDocument document;

        public DoxiaDocumentListener( IDocument document )
        {
            this.document = document;
        }

        /** {@inheritDoc} */
        public void documentChanged( DocumentEvent event )
        {
            DoxiaWrapper.convert( document.get(), getDoxiaFile(), getFormat() );
        }

        /** {@inheritDoc} */
        public void documentAboutToBeChanged( DocumentEvent event )
        {
            DoxiaWrapper.convert( document.get(), getDoxiaFile(), getFormat() );
        }
    }
}
