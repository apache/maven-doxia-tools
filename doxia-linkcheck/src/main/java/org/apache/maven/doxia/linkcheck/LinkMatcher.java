package org.apache.maven.doxia.linkcheck;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Link matcher. Reads the contents of a file and tries to match the following: <code>
 * <a href=""....
 * <link href=""....
 * <img src=""....
 * <script src=""....
 * </code>
 *
 * @author <a href="mailto:mac@apache.org">Ignacio G. Mac Dowell </a>
 * @version $Id$
 */
class LinkMatcher
{

    /** Regexp for link matching. */
    private static final Pattern MATCH_PATTERN =
        Pattern.compile( "<(?>link|a|img|script)[^>]*?(?>href|src)\\s*?=\\s*?[\\\"'](.*?)[\\\"'][^>]*?",
                         Pattern.CASE_INSENSITIVE );

    /** No need to create a new object each time a file is processed. Just clear it. */
    private static final Set LINK_LIST = new TreeSet();

    /**
     * Reads a file and returns a StringBuffer with its contents.
     *
     * TODO: Check for encoding issues
     *
     * @param file the file we are reading
     * @return a StringBuffer with file's contents.
     * @throws IOException if something goes wrong.
     */
    private static StringBuffer fileToStringBuffer( File file ) throws IOException
    {
        BufferedReader reader = null;

        final StringBuffer pageBuffer = new StringBuffer();

        try
        {
            reader = new BufferedReader( new FileReader( file ) );

            String line;

            while ( ( line = reader.readLine() ) != null )
            {
                pageBuffer.append( line );
            }
        }
        finally
        {
            reader.close();
        }

        return pageBuffer;
    }

    /**
     * Performs the actual matching.
     *
     * @param file the file to check
     * @return a set with all links to check
     * @throws IOException if something goes wrong
     */
    static Set match( File file ) throws IOException
    {
        LINK_LIST.clear();

        final Matcher m = MATCH_PATTERN.matcher( fileToStringBuffer( file ) );

        String link;

        while ( m.find() )
        {
            link = m.group( 1 ).trim();

            if ( link.length() < 1 )
            {
                continue;
            }
            else if ( link.toLowerCase().indexOf( "javascript" ) != -1 )
            {
                continue;
            }
            // else if (link.toLowerCase().indexOf("mailto:") != -1) {
            // continue;
            // }

            LINK_LIST.add( link );
        }

        return LINK_LIST;
    }

}
