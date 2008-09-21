package org.apache.maven.doxia.wrapper;

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

import java.io.Reader;

/**
 * Wrapper for an input reader.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class InputReaderWrapper
    extends AbstractWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 3260213754615748766L;

    private Reader reader;

    /**
     * Private constructor.
     *
     * @param format not null
     * @param supportedFormat not null
     * @throws IllegalArgumentException if the format equals AUTO_FORMAT.
     */
    private InputReaderWrapper( Reader reader, String format, String[] supportedFormat )
    {
        super( format, supportedFormat );

        if ( getFormat().equalsIgnoreCase( AUTO_FORMAT ) )
        {
            throw new IllegalArgumentException( "input format is required" );
        }

        if ( reader == null )
        {
            throw new IllegalArgumentException( "input reader is required" );
        }
        this.reader = reader;
    }

    /**
     * @return the reader
     */
    public Reader getReader()
    {
        return this.reader;
    }

    /**
     * @param reader not null
     * @param format not null
     * @param supportedFormat not null
     * @return a type safe input reader
     * @throws IllegalArgumentException if any
     */
    public static InputReaderWrapper valueOf( Reader reader, String format, String[] supportedFormat )
        throws IllegalArgumentException
    {
        return new InputReaderWrapper( reader, format, supportedFormat );
    }
}
