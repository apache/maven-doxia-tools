package org.apache.maven.doxia.util;

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

import org.apache.maven.doxia.UnsupportedFormatException;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Utility class to play with Doxia formats.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class FormatUtils
{
    /**
     * @param from a supported format
     * @param supportedFormat an array of supported formats in Doxia
     * @return Doxia supported format for <code>from</code>
     * @throws IllegalArgumentException if any
     * @throws UnsupportedFormatException if any
     */
    public static String getSupportedFormat( String from, String[] supportedFormat )
        throws UnsupportedFormatException
    {
        if ( supportedFormat == null )
        {
            throw new IllegalArgumentException( "supportedFormat is required" );
        }

        if ( StringUtils.isEmpty( from ) )
        {
            throw new IllegalArgumentException( "from is required" );
        }

        String fromFormat = from;
        String supportedString = StringUtils.join( supportedFormat, ", " );
        if ( supportedString.indexOf( fromFormat.toLowerCase() ) == -1 )
        {
            throw new UnsupportedFormatException( fromFormat, supportedFormat );
        }

        return fromFormat.toLowerCase();
    }

    /**
     * @param absolutePath an absolute path
     * @param from a supported format
     * @param supportedFormat an array of supported formats in Doxia
     * @return Doxia supported format for <code>from</code> or the extension of <code>in</code>
     * @throws IllegalArgumentException if any
     * @throws UnsupportedFormatException if any
     */
    public static String getSupportedFormat( String absolutePath, String from, String[] supportedFormat )
        throws UnsupportedFormatException
    {
        if ( supportedFormat == null )
        {
            throw new IllegalArgumentException( "supportedFormat is required" );
        }

        String fromFormat = null;
        if ( StringUtils.isEmpty( from ) )
        {
            if ( absolutePath == null || absolutePath.trim().length() == 0 )
            {
                throw new IllegalArgumentException( "absolutePath is required" );
            }

            // try to detect format
            if ( SelectorUtils.match( "**.*", absolutePath ) )
            {
                fromFormat = absolutePath.substring( absolutePath.lastIndexOf( '.' ) + 1, absolutePath.length() );
            }
            else
            {
                throw new IllegalArgumentException( "'" + absolutePath + "' is directory: cannot detect the format" );
            }
        }
        else
        {
            fromFormat = from;
        }

        String supportedString = StringUtils.join( supportedFormat, ", " );
        if ( supportedString.indexOf( fromFormat.toLowerCase() ) == -1 )
        {
            throw new UnsupportedFormatException( fromFormat, supportedFormat );
        }

        return fromFormat.toLowerCase();
    }
}
