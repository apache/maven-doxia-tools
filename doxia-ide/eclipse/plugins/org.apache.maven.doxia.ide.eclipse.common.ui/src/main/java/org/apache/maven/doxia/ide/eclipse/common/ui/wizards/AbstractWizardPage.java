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

import java.io.File;

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" Doxia wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWizardPage
    extends WizardPage
{
    private Text containerText;

    private Text fileText;

    private ISelection selection;

    /**
     * Constructor for AbstractWizardPage.
     *
     * @param pageName
     */
    public AbstractWizardPage( ISelection selection )
    {
        super( "wizardPage" );

        Assert.isNotNull( getExtension(), "getExtension() should be initialized" );

        setTitle( getString( "title" ) );
        setDescription( getFormattedString( "description", getExtension() ) );
        this.selection = selection;
    }

    /** {@inheritDoc} */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label( container, SWT.NULL );
        label.setText( getString( "label.container.text" ) );

        containerText = new Text( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        containerText.setLayoutData( gd );
        containerText.addModifyListener( new ModifyListener()
        {
            /** {@inheritDoc} */
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );

        Button button = new Button( container, SWT.PUSH );
        button.setText( getString( "button.text" ) );
        button.addSelectionListener( new SelectionAdapter()
        {
            /** {@inheritDoc} */
            public void widgetSelected( SelectionEvent e )
            {
                handleBrowse();
            }
        } );
        label = new Label( container, SWT.NULL );
        label.setText( getString( "label.fileName.text" ) );

        fileText = new Text( container, SWT.BORDER | SWT.SINGLE );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        fileText.setLayoutData( gd );
        fileText.addModifyListener( new ModifyListener()
        {
            /** {@inheritDoc} */
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        initialize();
        dialogChanged();
        setControl( container );
    }

    public String getContainerName()
    {
        return containerText.getText();
    }

    public String getFileName()
    {
        return fileText.getText();
    }

    public abstract String getExtension();

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    private void initialize()
    {
        if ( selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection )
        {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if ( ssel.size() > 1 )
            {
                return;
            }

            Object obj = ssel.getFirstElement();
            if ( obj instanceof IResource )
            {
                IContainer container;
                if ( obj instanceof IContainer )
                {
                    container = (IContainer) obj;
                }
                else
                {
                    container = ( (IResource) obj ).getParent();
                }
                containerText.setText( container.getFullPath().toString() );
            }
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleBrowse()
    {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog( getShell(), ResourcesPlugin.getWorkspace()
            .getRoot(), false, getString( "dialog.text" ) );
        if ( dialog.open() == ContainerSelectionDialog.OK )
        {
            Object[] result = dialog.getResult();
            if ( result.length == 1 )
            {
                containerText.setText( ( (Path) result[0] ).toString() );
            }
        }
    }

    /**
     * Ensures that both text fields are set.
     */
    private void dialogChanged()
    {
        IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember( new Path( getContainerName() ) );
        String fileName = getFileName();

        if ( getContainerName().length() == 0 )
        {
            updateStatus( getString( "errorMessage.missingContainerName" ) );
            return;
        }
        if ( container == null || ( container.getType() & ( IResource.PROJECT | IResource.FOLDER ) ) == 0 )
        {
            updateStatus( getString( "errorMessage.missingContainer" ) );
            return;
        }
        if ( !container.isAccessible() )
        {
            updateStatus( getString( "errorMessage.nonWritable" ) );
            return;
        }
        if ( fileName.length() == 0 )
        {
            updateStatus( getString( "errorMessage.missingFile" ) );
            return;
        }
        if ( fileName.replace( '\\', '/' ).indexOf( '/', 1 ) > 0 )
        {
            updateStatus( getString( "errorMessage.nonValidFile" ) );
            return;
        }
        if ( fileName.indexOf( '.' ) == -1 )
        {
            updateStatus( getString( "errorMessage.nonValidFile" ) );
            return;
        }
        int dotLoc = fileName.lastIndexOf( '.' );
        if ( dotLoc != -1 )
        {
            String ext = fileName.substring( dotLoc + 1 );
            if ( ext.equalsIgnoreCase( getExtension() ) == false )
            {
                updateStatus( getFormattedString( "errorMessage.nonValidExtension", getExtension() ) );
                return;
            }
        }
        File file = new File( container.getLocation().toFile(), fileName );
        if ( file.exists() )
        {
            updateStatus( getString( "errorMessage.duplicatedFile" ) );
            return;
        }
        updateStatus( null );
    }

    private void updateStatus( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }

    private static String getString( String subkey )
    {
        return CommonPluginMessages.getString( "AbstractDoxiaWizardPage." + subkey );
    }

    private static String getFormattedString( String subkey, String obj )
    {
        return CommonPluginMessages.getFormattedString( "AbstractDoxiaWizardPage." + subkey, obj );
    }
}