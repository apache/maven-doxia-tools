package org.apache.maven.doxia.ide.eclipse.common.ui.dialogs;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract class to provide dialog with the user.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractDialog
    extends Dialog
{
    /** OK button */
    protected Button okButton;

    /** Canvel button */
    protected Button cancelButton;

    /**
     * Default constructor
     *
     * @param parent
     */
    public AbstractDialog( Shell parent )
    {
        super( parent );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @Override
    public Shell getShell()
    {
        Shell shell = super.getShell();
        shell.setImage( CommonPlugin.getImage( CommonPlugin.IMG_DOXIA ) );

        return shell;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = (Composite) super.createDialogArea( parent );
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.numColumns = 2;

        GridData gridData = (GridData) composite.getLayoutData();
        gridData.verticalIndent = 5;

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        okButton = super.getButton( IDialogConstants.OK_ID );
        cancelButton = super.getButton( IDialogConstants.CANCEL_ID );
    }
}
