package org.apache.maven.doxia.ide.eclipse.common.ui.wizards;

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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This is a new Doxia wizard. Its role is to create a new file resource in the provided container.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWizard
    extends Wizard
    implements INewWizard
{
    private AbstractWizardPage page;

    private ISelection selection;

    /**
     * Constructor for AbstractDoxiaWizard.
     */
    public AbstractWizard()
    {
        super();

        Assert.isNotNull( getWizardTemplate(), "getWizardTemplate() should be initialized" );

        setNeedsProgressMonitor( true );
    }

    @Override
    public void addPages()
    {
        page = getWizardPage( selection );
        Assert.isNotNull( page, "page should be initialized" );
        addPage( page );
    }

    @Override
    public boolean performFinish()
    {
        final String containerName = page.getContainerName();
        final String fileName = page.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            /** {@inheritDoc} */
            public void run( IProgressMonitor monitor )
                throws InvocationTargetException
            {
                try
                {
                    doFinish( containerName, fileName, monitor );
                }
                catch ( CoreException e )
                {
                    throw new InvocationTargetException( e );
                }
                finally
                {
                    try
                    {
                        IFile file = getFile( containerName, fileName );
                        file.delete( true, new NullProgressMonitor() );
                    }
                    catch ( CoreException e )
                    {
                        throw new InvocationTargetException( e );
                    }

                    monitor.done();
                }
            }
        };

        try
        {
            getContainer().run( true, false, op );
        }
        catch ( InterruptedException e )
        {
            CommonPlugin.logError( "InterruptedException: " + e.getMessage(), e );

            return false;
        }
        catch ( InvocationTargetException e )
        {
            CommonPlugin.logError( "InvocationTargetException: " + e.getMessage(), e );

            Throwable realException = e.getTargetException();
            MessageDialog.openError( getShell(), "Error", realException.getMessage() );

            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        this.selection = selection;
    }

    /**
     * @param selection
     * @return the implementation of an wizard page for a Doxia editor.
     */
    public abstract AbstractWizardPage getWizardPage( ISelection selection );

    /**
     * @return the wizard template resource name.
     * @see #openContentStream()
     */
    public abstract String getWizardTemplate();

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * The worker method. It will find the container, create the
     * file if missing or just replace its contents, and open
     * the editor on the newly created file.
     */
    private void doFinish( String containerName, String fileName, IProgressMonitor monitor )
        throws CoreException
    {
        // create a sample file
        monitor.beginTask( "Creating " + fileName, 2 );
        final IFile file = getFile( containerName, fileName );
        Assert.isNotNull( file, "file " + fileName + " was not found" );
        try
        {
            InputStream stream = openContentStream();
            Assert.isNotNull( file, "stream was not found" );
            if ( file.exists() )
            {
                file.setContents( stream, true, true, monitor );
            }
            else
            {
                file.create( stream, true, monitor );
            }
            stream.close();
        }
        catch ( IOException e )
        {
        }
        monitor.worked( 1 );
        monitor.setTaskName( "Opening file for editing..." );
        getShell().getDisplay().asyncExec( new Runnable()
        {
            /** {@inheritDoc} */
            public void run()
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try
                {
                    IDE.openEditor( page, file, true );
                }
                catch ( PartInitException e )
                {
                    CommonPlugin.logError( "PartInitException: " + e.getMessage(), e );
                }
            }
        } );
        monitor.worked( 1 );
    }

    private IFile getFile( String containerName, String fileName )
        throws CoreException
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember( new Path( containerName ) );
        if ( !resource.exists() || !( resource instanceof IContainer ) )
        {
            throwCoreException( "Container \"" + containerName + "\" does not exist." );
        }
        IContainer container = (IContainer) resource;
        return container.getFile( new Path( fileName ) );
    }

    /**
     * Finds a resource with a given name.
     *
     * @return the inputstream for the wizard or null if not found.
     */
    private InputStream openContentStream()
    {
        return getClass().getResourceAsStream( getWizardTemplate() );
    }

    private void throwCoreException( String message )
        throws CoreException
    {
        IStatus status = new Status( IStatus.ERROR, CommonPlugin.PLUGIN_ID, IStatus.OK, message, null );
        throw new CoreException( status );
    }
}