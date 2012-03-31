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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to add a table in a Doxia document.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class AddTableDialog
    extends AbstractDialog
{
    private static final String[] TABLE_STYLE_LABELS = {
        getString( "style.centered.label" ),
        getString( "style.leftAligned.label" ),
        getString( "style.rightAligned.label" ) };

    /** Default rows number */
    private static final int DEFAULT_ROWS = 2;

    /** Default columns number */
    private static final int DEFAULT_COLUMNS = 2;

    private Table table = new Table();

    private Text rowsText;

    private Text colsText;

    private Combo tableTypeCombo;

    private Text captionText;

    private ModifyListener modifyListener = new ModifyListener()
    {
        /** {@inheritDoc} */
        public void modifyText( ModifyEvent event )
        {
            if ( okButton != null )
            {
                try
                {
                    int rows = Integer.parseInt( rowsText.getText().trim() );
                    int cols = Integer.parseInt( colsText.getText().trim() );

                    okButton.setEnabled( rows > 0 && cols > 0 );
                }
                catch ( Exception e )
                {
                    okButton.setEnabled( false );
                }
            }
        }
    };

    /**
     * Default constructor.
     *
     * @param parent
     */
    public AddTableDialog( Shell parent )
    {
        super( parent );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * Gets the user specified table.
     *
     * @return an <code>Table</code> object
     */
    public Table getTable()
    {
        return table;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = (Composite) super.createDialogArea( parent );

        Label rowsLabel = new Label( composite, SWT.NONE );
        rowsLabel.setText( getString( "rows.label" ) );

        rowsText = new Text( composite, SWT.BORDER );
        rowsText.setTextLimit( 2 );
        rowsText.setText( String.valueOf( DEFAULT_ROWS ) );
        {
            GridData gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
            rowsText.setLayoutData( gridData );
        }
        rowsText.addModifyListener( modifyListener );

        Label colsLabel = new Label( composite, SWT.NONE );
        colsLabel.setText( getString( "columns.label" ) );

        colsText = new Text( composite, SWT.BORDER );
        colsText.setTextLimit( 2 );
        colsText.setText( String.valueOf( DEFAULT_COLUMNS ) );
        {
            GridData gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
            colsText.setLayoutData( gridData );
        }
        colsText.addModifyListener( modifyListener );

        Label headerTypeLabel = new Label( composite, SWT.NONE );
        headerTypeLabel.setText( getString( "style.label" ) );

        tableTypeCombo = new Combo( composite, SWT.BORDER | SWT.READ_ONLY );
        tableTypeCombo.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        tableTypeCombo.setItems( TABLE_STYLE_LABELS );
        tableTypeCombo.setText( TABLE_STYLE_LABELS[1] );

        Label captionLabel = new Label( composite, SWT.NONE );
        captionLabel.setText( getString( "caption.label" ) );
        captionText = new Text( composite, SWT.BORDER );
        {
            GridData gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
            captionText.setLayoutData( gridData );
        }

        super.getShell().setText( getString( "title.label" ) );

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );
    }

    @Override
    protected void okPressed()
    {
        String rowsValue = rowsText.getText();
        if ( rowsValue != null && rowsValue.length() > 0 )
        {
            try
            {
                int rows = Integer.parseInt( rowsValue );
                table.setRows( rows );
            }
            catch ( Exception e )
            {
                table.setRows( DEFAULT_ROWS );
            }
        }

        String colsValue = colsText.getText();
        if ( colsValue != null && colsValue.length() > 0 )
        {
            try
            {
                int cols = Integer.parseInt( colsValue );
                table.setColumns( cols );
            }
            catch ( Exception e )
            {
                table.setColumns( DEFAULT_COLUMNS );
            }
        }

        table.setCaption( captionText.getText().trim() );
        table.setTableStyle( tableTypeCombo.getSelectionIndex() );

        super.okPressed();
    }

    /**
     * Table bean.
     */
    public class Table
    {
        public static final int TABLE_STYLE_CENTERED = 0;

        public static final int TABLE_STYLE_LEFTALIGNED = 1;

        public static final int TABLE_STYLE_RIGHTALIGNED = 2;

        private int rows = 2;

        private int columns = 2;

        private int tableStyle = 0;

        private String caption;

        public Table()
        {
        }

        /**
         * @return the number of rows
         */
        public int getRows()
        {
            return rows;
        }

        /**
         * @param rows the number of rows
         */
        public void setRows( int rows )
        {
            this.rows = rows;
        }

        /**
         * @return the number of columns
         */
        public int getColumns()
        {
            return columns;
        }

        /**
         * @param cols the number of columns
         */
        public void setColumns( int cols )
        {
            this.columns = cols;
        }

        /**
         * @return the table caption
         */
        public String getCaption()
        {
            return caption;
        }

        /**
         * @param caption the table caption
         */
        public void setCaption( String caption )
        {
            this.caption = caption;
        }

        /**
         * @return the table style.
         */
        public int getTableStyle()
        {
            return tableStyle;
        }

        /**
         * @param tableStyle
         */
        public void setTableStyle( int tableStyle )
        {
            this.tableStyle = tableStyle;
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private static String getString( String subkey )
    {
        return CommonPluginMessages.getString( "AddTableDialog." + subkey );
    }
}