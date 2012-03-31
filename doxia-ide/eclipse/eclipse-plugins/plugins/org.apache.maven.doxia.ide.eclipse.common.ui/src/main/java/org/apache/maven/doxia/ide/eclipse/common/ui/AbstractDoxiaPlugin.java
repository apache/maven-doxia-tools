package org.apache.maven.doxia.ide.eclipse.common.ui;

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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Abstract Doxia Eclipse Plugin.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractDoxiaPlugin
    extends AbstractUIPlugin
{
    // Images
    public static final String IMG_DOXIA = "icons/doxia.gif";

    public static final String IMG_BROWSER_FORWARD = "icons/forward.gif";

    public static final String IMG_BROWSER_BACK = "icons/back.gif";

    public static final String IMG_BROWSER_STOP = "icons/stop.gif";

    public static final String IMG_BROWSER_REFRESH = "icons/refresh.gif";

    public static final String IMG_BOLD = "icons/etool16/Bold.gif";

    public static final String IMG_BOLD_DISABLED = "icons/dtool16/Bold.gif";

    public static final String IMG_ITALIC = "icons/etool16/Italic.gif";

    public static final String IMG_ITALIC_DISABLED = "icons/dtool16/Italic.gif";

    public static final String IMG_MONOSPACED = "icons/etool16/Monospaced.gif";

    public static final String IMG_MONOSPACED_DISABLED = "icons/dtool16/Monospaced.gif";

    public static final String IMG_LINK = "icons/etool16/AddLink.gif";

    public static final String IMG_LINK_DISABLED = "icons/dtool16/AddLink.gif";

    public static final String IMG_TABLE = "icons/etool16/AddTable.gif";

    public static final String IMG_TABLE_DISABLED = "icons/dtool16/AddTable.gif";

    /** The shared instance */
    private static ImageRegistry IMAGE_REGISTRY;

    private static AbstractDoxiaPlugin plugin;

    /**
     * The constructor
     */
    public AbstractDoxiaPlugin()
    {
        plugin = this;
    }

    @Override
    protected void initializeImageRegistry( ImageRegistry registry )
    {
        if ( registry != null )
        {
            registry.put( IMG_DOXIA, registerImage( IMG_DOXIA ) );

            registry.put( IMG_BROWSER_FORWARD, registerImage( IMG_BROWSER_FORWARD ) );
            registry.put( IMG_BROWSER_BACK, registerImage( IMG_BROWSER_BACK ) );
            registry.put( IMG_BROWSER_STOP, registerImage( IMG_BROWSER_STOP ) );
            registry.put( IMG_BROWSER_REFRESH, registerImage( IMG_BROWSER_REFRESH ) );

            registry.put( IMG_BOLD, registerImage( IMG_BOLD ) );
            registry.put( IMG_BOLD_DISABLED, registerImage( IMG_BOLD_DISABLED ) );
            registry.put( IMG_ITALIC, registerImage( IMG_ITALIC ) );
            registry.put( IMG_ITALIC_DISABLED, registerImage( IMG_ITALIC_DISABLED ) );
            registry.put( IMG_MONOSPACED, registerImage( IMG_MONOSPACED ) );
            registry.put( IMG_MONOSPACED_DISABLED, registerImage( IMG_MONOSPACED_DISABLED ) );
            registry.put( IMG_LINK, registerImage( IMG_LINK ) );
            registry.put( IMG_LINK_DISABLED, registerImage( IMG_LINK_DISABLED ) );
            registry.put( IMG_TABLE, registerImage( IMG_TABLE ) );
            registry.put( IMG_TABLE_DISABLED, registerImage( IMG_TABLE_DISABLED ) );

            IMAGE_REGISTRY = registry;
        }
    }

    /**
     * @return the plugin ID
     */
    public static String getPluginId()
    {
        return plugin.getBundle().getSymbolicName();
    }

    public static Image getImage( String key )
    {
        Assert.isNotNull( IMAGE_REGISTRY, "IMAGE_REGISTRY is not initialized." );

        return IMAGE_REGISTRY.get( key );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /*
     * TODO:
     * This function gets the icons from a bundle path, not the classpath.
     * This in turn requires them to be in the root of the source of the project
     * to make the PDE work. It would be better, I think, to put them 
     * on classpath if Eclipse will let us.
     */
    
    
    private ImageDescriptor registerImage( String path )
    {
        Assert.isNotNull( path, "path is not initialized." );

        ImageDescriptor imageDescriptor = null;
        try
        {
            URL url = new URL( getBundle().getEntry( "/" ), path );

            imageDescriptor = ImageDescriptor.createFromURL( url );
        }
        catch ( MalformedURLException e )
        {
            log( e.getMessage(), e, IStatus.ERROR, true );
        }

        Assert.isNotNull( imageDescriptor, "imageDescriptor is not initialized." );

        return imageDescriptor;
    }

    // ----------------------------------------------------------------------
    // Utilities methods
    // ----------------------------------------------------------------------

    public static void log( String message, Throwable e, int severity, boolean tellUser )
    {
        final IStatus status = new Status( severity, getPluginId(), severity, message, e );

        plugin.getLog().log( status );

        if ( status.getException() != null )
        {
            status.getException().printStackTrace( System.err );
        }

        if ( tellUser )
        {
            Display.getDefault().syncExec( new Runnable()
            {
                /** {@inheritDoc} */
                public void run()
                {
                    MessageDialog.openError( null, " Error in " + getPluginId(), status.getMessage() );
                }
            } );
        }
    }

    public static void logInfo( String message, Throwable e )
    {
        log( message, e, IStatus.INFO, false );
    }

    public static void logInfo( String message, Throwable e, boolean tellUser )
    {
        log( message, e, IStatus.INFO, tellUser );
    }

    public static void logError( String message, Throwable e )
    {
        log( message, e, IStatus.ERROR, false );
    }

    public static void logError( String message, Throwable e, boolean tellUser )
    {
        log( message, e, IStatus.ERROR, tellUser );
    }

    public static void logWarning( String message, Throwable e )
    {
        log( message, e, IStatus.WARNING, false );
    }

    public static void logWarning( String message, Throwable e, boolean tellUser )
    {
        log( message, e, IStatus.WARNING, tellUser );
    }
}
