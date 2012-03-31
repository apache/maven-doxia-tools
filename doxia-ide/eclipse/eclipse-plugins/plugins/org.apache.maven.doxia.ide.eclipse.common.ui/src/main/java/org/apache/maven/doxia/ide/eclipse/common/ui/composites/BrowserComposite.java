package org.apache.maven.doxia.ide.eclipse.common.ui.composites;

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
import java.io.IOException;
import java.io.Writer;

import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPluginMessages;
import org.apache.maven.doxia.ide.eclipse.common.ui.DoxiaWrapper;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;

/**
 * This composite creates a browser with actions like back, forward, stop and refresh.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class BrowserComposite
    extends Composite
{
    private static final String LS = System.getProperty( "line.separator" );

    /** The browser widget used for the preview */
    private Browser browser;

    /** The file used for the preview */
    private IFile file;

    /** The format used for the preview */
    private String format;

    /** The back action */
    private Action back;

    /** The forward action */
    private Action forward;

    /** The stop action */
    private Action stop;

    /**
     * Default constructor.
     *
     * @param parent
     * @param style
     * @param actionBars
     * @param file
     * @param format
     */
    public BrowserComposite( Composite parent, int style, IActionBars actionBars, IFile file, String format )
    {
        super( parent, style );

        this.file = file;
        this.format = format;

        init( actionBars );
    }

    /**
     * Generates the content of the view with Doxia.
     */
    public void convert()
    {
        setText( DoxiaWrapper.convert( file, format ) );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Init the actionBars
     *
     * @param actionBars
     */
    private void init( final IActionBars actionBars )
    {
        try
        {
            browser = new Browser( this, SWT.NONE );
        }
        catch ( SWTError e )
        {
            setLayout( new FillLayout() );
            Label label = new Label( this, SWT.CENTER | SWT.WRAP );
            label.setText( getString( "notCreated" ) );
            layout( true );
            return;
        }

        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 3;
        formLayout.marginWidth = 3;
        setLayout( formLayout );
        ToolBarManager manager = new ToolBarManager( SWT.FLAT );

        createBrowserToolBar( manager );

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.verticalAlignment = GridData.CENTER;
        gd.horizontalAlignment = GridData.FILL;

        ToolBar toolBar = manager.createControl( this );
        FormData data = new FormData();
        toolBar.setLayoutData( data );

        data = new FormData();
        data.left = new FormAttachment( 0, 0 );
        data.right = new FormAttachment( 100, 0 );
        data.top = new FormAttachment( toolBar, 0, SWT.DEFAULT );
        data.bottom = new FormAttachment( 100, 0 );

        browser.setLayoutData( data );
        browser.addProgressListener( new ProgressAdapter()
        {
            IProgressMonitor monitor = actionBars.getStatusLineManager().getProgressMonitor();

            boolean working = false;

            int workedSoFar;

            /** {@inheritDoc} */
            public void changed( ProgressEvent event )
            {
                if ( event.total == 0 )
                {
                    return;
                }
                if ( !working )
                {
                    if ( event.current == event.total )
                    {
                        return;
                    }
                    monitor.beginTask( "", event.total );
                    workedSoFar = 0;
                    working = true;
                    stop.setEnabled( true );
                }

                monitor.worked( event.current - workedSoFar );
                workedSoFar = event.current;
            }

            /** {@inheritDoc} */
            public void completed( ProgressEvent event )
            {
                monitor.done();
                working = false;
                stop.setEnabled( false );

                updateNavigationStatus();
            }
        } );
        browser.addStatusTextListener( new StatusTextListener()
        {
            IStatusLineManager status = actionBars.getStatusLineManager();

            /** {@inheritDoc} */
            public void changed( StatusTextEvent event )
            {
                status.setMessage( event.text );
            }
        } );
        browser.addLocationListener( new LocationAdapter()
        {
            /** {@inheritDoc} */
            public void changed( LocationEvent event )
            {
                updateNavigationStatus();
            }
        } );
    }

    /**
     * @param manager
     */
    private void createBrowserToolBar( ContributionManager manager )
    {
        back = new Action()
        {
            /** {@inheritDoc} */
            public void run()
            {
                if ( browser.back() )
                {
                    updateNavigationStatus();
                }
            }
        };
        back.setText( getString( "back.label" ) );
        back.setToolTipText( getString( "back.label" ) );
        ImageRegistry imageRegistry = CommonPlugin.getDefault().getImageRegistry();
        back.setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_BROWSER_BACK ) );
        back.setEnabled( false );
        manager.add( back );

        forward = new Action()
        {
            /** {@inheritDoc} */
            public void run()
            {
                if ( browser.forward() )
                {
                    updateNavigationStatus();
                }
            }
        };
        forward.setText( getString( "forward.label" ) );
        forward.setToolTipText( getString( "forward.label" ) );
        forward.setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_BROWSER_FORWARD ) );
        forward.setEnabled( false );
        manager.add( forward );

        stop = new Action()
        {
            /** {@inheritDoc} */
            public void run()
            {
                browser.stop();
            }
        };
        stop.setText( getString( "stop.label" ) );
        stop.setToolTipText( getString( "stop.label" ) );
        stop.setEnabled( false );
        stop.setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_BROWSER_STOP ) );
        manager.add( stop );

        Action refresh = new Action()
        {
            /** {@inheritDoc} */
            public void run()
            {
                browser.refresh();
            }
        };
        refresh.setText( getString( "refresh.label" ) );
        refresh.setToolTipText( getString( "refresh.label" ) );
        refresh.setImageDescriptor( imageRegistry.getDescriptor( CommonPlugin.IMG_BROWSER_REFRESH ) );
        manager.add( refresh );
        refresh.setEnabled( true );
        manager.update( false );
    }

    /**
     * @param string
     */
    private void setText( String text )
    {
        int oldHeight = getClientScrollTop();

        // small workaround to "remember" scrolling
        text = StringUtils.replace( text, "</head>", getScriptTag() + "</head>" );
        text = StringUtils.replace( text, "<body>", "<body onload=\"setScrollTop(" + oldHeight + ")\">" );

        // Using setUrl() since setText() could be buggy for anchor
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=117108
        File generatedHtml = FileUtils.createTempFile( "doxia_", ".html", null );
        generatedHtml.deleteOnExit();
        Writer w = null;
        try
        {
            w = WriterFactory.newPlatformWriter( generatedHtml );

            w.write( text );
        }
        catch ( IOException e )
        {
            String msg = ( StringUtils.isEmpty( e.getMessage() ) ? e.getClass().getName() : e.getMessage() );

            CommonPlugin.logError( "IOException: " + msg, e, true );

            browser.setText( "IOException: " + msg );
            return;
        }
        finally
        {
            IOUtil.close( w );
        }

        browser.setUrl( generatedHtml.toURI().toString() );

        updateNavigationStatus();
    }

    private void updateNavigationStatus()
    {
        back.setEnabled( browser.isBackEnabled() );
        forward.setEnabled( browser.isForwardEnabled() );
    }

    /**
     * Communicates with the client <code>window.status</code> to get the current scrollTop.
     *
     * @return the browser client scrollTop
     */
    private int getClientScrollTop()
    {
        final String STATUS_QUERY = "statusQuery";

        browser.addStatusTextListener( new StatusTextListener()
        {
            /** {@inheritDoc} */
            public void changed( StatusTextEvent event )
            {
                browser.setData( STATUS_QUERY, event.text );
            }
        } );

        browser.execute( "window.status=getScrollTop()" );

        try
        {
            return Integer.valueOf( (String) browser.getData( STATUS_QUERY ) ).intValue();
        }
        catch ( NumberFormatException e )
        {
            return 0;
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * @return HTML script definition
     */
    private static String getScriptTag()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<" + Tag.SCRIPT.toString() + ">" ).append( LS );
        sb.append( getScrollTopScriptGetter() ).append( LS );
        sb.append( getScrollTopScriptSetter() ).append( LS );
        sb.append( "</" + Tag.SCRIPT.toString() + ">" ).append( LS );

        return sb.toString();
    }

    /**
     * @return javascript getScrollTop() function
     */
    private static String getScrollTopScriptGetter()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "function getScrollTop() {" ).append( LS );
        sb.append( "  var scrOfY = 0;" ).append( LS );
        sb.append( "  if( typeof( window.pageYOffset ) == 'number' ) {" ).append( LS );
        sb.append( "    //Netscape compliant" ).append( LS );
        sb.append( "    scrOfY = window.pageYOffset;" ).append( LS );
        sb.append( "  } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {" )
            .append( LS );
        sb.append( "    //DOM compliant" ).append( LS );
        sb.append( "    scrOfY = document.body.scrollTop;" ).append( LS );
        sb.append(
                   "  } else if( document.documentElement && "
                       + "( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {" )
            .append( LS );
        sb.append( "    //IE6 standards compliant mode" ).append( LS );
        sb.append( "    scrOfY = document.documentElement.scrollTop;" ).append( LS );
        sb.append( "  }" ).append( LS );
        sb.append( "  return scrOfY;" ).append( LS );
        sb.append( "}" ).append( LS );

        return sb.toString();
    }

    /**
     * @return javascript setScrollTop() function
     */
    private static String getScrollTopScriptSetter()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "function setScrollTop(y) {" ).append( LS );
        sb.append( "  window.scrollTo(0, y);" ).append( LS );
        sb.append( "}" ).append( LS );

        return sb.toString();
    }

    private static String getString( String subkey )
    {
        return CommonPluginMessages.getString( "BrowserComposite." + subkey );
    }
}
