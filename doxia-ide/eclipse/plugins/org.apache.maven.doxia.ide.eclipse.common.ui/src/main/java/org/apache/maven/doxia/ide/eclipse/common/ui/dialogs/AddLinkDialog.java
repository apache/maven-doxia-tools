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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to add a link in a Doxia document.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class AddLinkDialog
    extends AbstractDialog
{
    protected Link link = new Link();

    protected Text urlText;

    protected Text urlDisplayNameText;

    protected ModifyListener urlTextModifyListener = new ModifyListener()
    {
        /** {@inheritDoc} */
        public void modifyText( ModifyEvent e )
        {
            if ( okButton != null )
            {
                okButton.setEnabled( urlText.getText().trim().length() > 0 );
            }
        }
    };

    /**
     * Default constructor
     *
     * @param parent
     */
    public AddLinkDialog( Shell parent )
    {
        super( parent );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * @return an <code>Link</code> object
     */
    public Link getLink()
    {
        return link;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = (Composite) super.createDialogArea( parent );

        Label urlLabel = new Label( composite, SWT.NONE );
        urlLabel.setText( getString( "url.label" ) );
        urlText = new Text( composite, SWT.BORDER );
        GridData gridDataUrl = new GridData( GridData.FILL_HORIZONTAL );
        gridDataUrl.widthHint = 500;
        urlText.setLayoutData( gridDataUrl );
        urlText.addModifyListener( urlTextModifyListener );

        Label urlDisplayNameLabel = new Label( composite, SWT.NONE );
        urlDisplayNameLabel.setText( getString( "name.label" ) );
        urlDisplayNameText = new Text( composite, SWT.BORDER );
        GridData gridDataName = new GridData( GridData.FILL_HORIZONTAL );
        gridDataName.widthHint = 500;
        urlDisplayNameText.setLayoutData( gridDataName );

        super.getShell().setText( getString( "title.label" ) );

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );

        okButton.setEnabled( false );
    }

    @Override
    protected void okPressed()
    {
        String url = urlText.getText();
        if ( url != null && url.length() > 0 )
        {
            link.setURL( url );
            link.setName( urlDisplayNameText.getText() );
        }

        super.okPressed();
    }

    /**
     * Link bean.
     */
    public class Link
    {
        private String name;

        private String url;

        public Link()
        {
        }

        /**
         * @return the link name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name the link name
         */
        public void setName( String name )
        {
            this.name = name;
        }

        /**
         * @return the link URL
         */
        public String getURL()
        {
            return url;
        }

        /**
         * @param url the link URL
         */
        public void setURL( String url )
        {
            this.url = url;
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private static String getString( String subkey )
    {
        return CommonPluginMessages.getString( "AddLinkDialog." + subkey );
    }
}
