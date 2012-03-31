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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class CommonPluginMessages
{
    private static final String RESOURCE_BUNDLE = CommonPluginMessages.class.getName();

    private static ResourceBundle singleton = ResourceBundle.getBundle( RESOURCE_BUNDLE );

    /**
     * Private constructor.
     */
    private CommonPluginMessages()
    {
        // nop
    }

    /**
     * @param key
     * @return the value of the key
     * @see ResourceBundle#getString(String)
     */
    public static String getString( String key )
    {
        try
        {
            return singleton.getString( key );
        }
        catch ( MissingResourceException e )
        {
            CommonPlugin.logError( "MissingResourceException: " + e.getMessage(), e, true );
            return key;
        }
    }

    /**
     * @param key
     * @param arg
     * @return
     * @see #getFormattedString(String, Object[])
     */
    public static String getFormattedString( String key, Object arg )
    {
        return getFormattedString( key, new Object[] { arg } );
    }

    /**
     * @param key
     * @param args
     * @return
     * @see MessageFormat#format(String, Object...)
     */
    public static String getFormattedString( String key, Object[] args )
    {
        return MessageFormat.format( getString( key ), args );
    }

    /**
     * @return an instance of {@link ResourceBundle}
     */
    public static ResourceBundle getResourceBundle()
    {
        return singleton;
    }
}
