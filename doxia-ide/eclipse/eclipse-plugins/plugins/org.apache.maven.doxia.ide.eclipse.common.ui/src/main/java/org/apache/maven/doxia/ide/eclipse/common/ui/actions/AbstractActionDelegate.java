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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractActionDelegate
    extends ActionDelegate
    implements IEditorActionDelegate
{
    public AbstractActionDelegate()
    {
        Assert.isNotNull( getActionId(), "getActionId() should be defined" );
    }

    @Override
    public void run( IAction action )
    {
        // see AbstractEditorContributor#setActivePage(IEditorPart)
        action = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite()
            .getActionBars().getGlobalActionHandler( getActionId() );
        Assert.isNotNull( action, "action should be defined" );
        action.run();
    }

    @Override
    public void init( IAction action )
    {
        super.init( action );

        Assert.isNotNull( getBundleKey(), "getBundleKey() should be defined" );
        Assert.isNotNull( getImageDescriptorKey(), "getImageDescriptorKey() should be defined" );
        Assert.isTrue( getImageDescriptorKey().length == 2, "getImageDescriptorKey() should be an array of 2 elements" );

        // Standardize common texts, icons
        ImageRegistry imageRegistry = CommonPlugin.getDefault().getImageRegistry();
        action.setImageDescriptor( imageRegistry.getDescriptor( getImageDescriptorKey()[0] ) );
        // TODO activate me!
        action.setDisabledImageDescriptor( imageRegistry.getDescriptor( getImageDescriptorKey()[1] ) );

        action.setText( getString( "action." + getBundleKey() + ".label" ) );
        action.setToolTipText( getString( "action." + getBundleKey() + ".tooltip" ) );
    }

    /** {@inheritDoc} */
    public void setActiveEditor( IAction action, IEditorPart targetEditor )
    {
    }

    /**
     * @return the given actionId
     */
    public abstract String getActionId();

    /**
     * @return the given bundle key in the plugin.properties
     */
    public abstract String getBundleKey();

    /**
     * @return an array of 2 elements for the enabled and disabled images descriptor.
     */
    public abstract String[] getImageDescriptorKey();

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private static String getString( String key )
    {
        Assert.isNotNull( key, "key is not specified." );

        InputStream is = null;
        try
        {
            URL url = Platform.getBundle( CommonPlugin.PLUGIN_ID ).getResource( "plugin.properties" );
            is = url.openStream();
            ResourceBundle bundle = new PropertyResourceBundle( is );
            return bundle.getString( key );
        }
        catch ( MalformedURLException e )
        {
            CommonPlugin.log( e.getMessage(), e, IStatus.ERROR, true );
        }
        catch ( IOException e )
        {
            CommonPlugin.log( e.getMessage(), e, IStatus.ERROR, true );
        }
        finally
        {
            IOUtil.close( is );
        }

        return "!" + key + "!";
    }
}
