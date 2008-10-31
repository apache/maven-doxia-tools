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

import java.io.Writer;

import org.apache.maven.doxia.UnsupportedFormatException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Utility class to play with Doxia objects.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ConverterUtil
{
    /**
     * @param plexus not null
     * @param format not null
     * @param supportedFormats not null
     * @return an instance of <code>Parser</code> depending on the format.
     * @throws ComponentLookupException if could not find the Parser for the given format.
     * @throws UnsupportedFormatException if the found parser is not instantiated.
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Parser getParser( PlexusContainer plexus, String format, String[] supportedFormats )
        throws ComponentLookupException, UnsupportedFormatException
    {
        if ( plexus == null )
        {
            throw new IllegalArgumentException( "plexus is required" );
        }
        if ( format == null )
        {
            throw new IllegalArgumentException( "format is required" );
        }
        if ( supportedFormats == null )
        {
            throw new IllegalArgumentException( "supportedFormats is required" );
        }

        Parser parser = null;
        for ( int i = 0; i < supportedFormats.length; i++ )
        {
            if ( format.equalsIgnoreCase( supportedFormats[i] ) )
            {
                parser = (Parser) plexus.lookup( Parser.ROLE, format );
            }
        }

        if ( parser == null )
        {
            throw new UnsupportedFormatException( format, supportedFormats );
        }

        return parser;
    }

    /**
     * @param plexus not null
     * @param format not null
     * @param writer could be null
     * @param supportedFormats not null
     * @return an instance of <code>Sink</code> depending on the given format.
     * @throws ComponentLookupException if could not find the SinkFactory for the given format.
     * @throws UnsupportedFormatException if the found sink is not instantiated.
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Sink getSink( PlexusContainer plexus, String format, Writer writer, String[] supportedFormats )
        throws ComponentLookupException, UnsupportedFormatException
    {
        if ( plexus == null )
        {
            throw new IllegalArgumentException( "plexus is required" );
        }
        if ( format == null )
        {
            throw new IllegalArgumentException( "format is required" );
        }
        if ( supportedFormats == null )
        {
            throw new IllegalArgumentException( "supportedFormats is required" );
        }

        SinkFactory factory = null;
        for ( int i = 0; i < supportedFormats.length; i++ )
        {
            if ( format.equalsIgnoreCase( supportedFormats[i] ) )
            {
                factory = (SinkFactory) plexus.lookup( SinkFactory.ROLE, format );
            }
        }

        if ( factory == null )
        {
            throw new UnsupportedFormatException( format, supportedFormats );
        }

        Sink sink = factory.createSink( writer );
        if ( sink == null )
        {
            throw new IllegalArgumentException( "Sink was not instanciated: " + format );
        }

        return sink;
    }
}
