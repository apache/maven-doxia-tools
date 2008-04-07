package org.apache.maven.doxia.ide.eclipse.confluence.ui;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.AbstractDoxiaPlugin;
import org.codehaus.plexus.util.StringUtils;
import org.osgi.framework.BundleContext;

/**
 * Doxia Confluence plug-in.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class ConfluencePlugin
    extends AbstractDoxiaPlugin
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.maven.doxia.ide.eclipse.confluence.ui";

    /** The shared instance */
    private static ConfluencePlugin plugin;

    /**
     * The constructor
     */
    public ConfluencePlugin()
    {
        plugin = this;
    }

    @Override
    public void start( BundleContext context )
        throws Exception
    {
        super.start( context );
        plugin = this;
    }

    @Override
    public void stop( BundleContext context )
        throws Exception
    {
        plugin = null;
        super.stop( context );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ConfluencePlugin getDefault()
    {
        return plugin;
    }

    /**
     * @return the plugin ID
     */
    public static String getPluginId()
    {
        if ( StringUtils.isEmpty( getDefault().getBundle().getSymbolicName() ) )
        {
            return PLUGIN_ID;
        }

        return getDefault().getBundle().getSymbolicName();
    }

    /**
     * @return the Doxia format
     */
    public static String getDoxiaFormat()
    {
        return "confluence";
    }
}
