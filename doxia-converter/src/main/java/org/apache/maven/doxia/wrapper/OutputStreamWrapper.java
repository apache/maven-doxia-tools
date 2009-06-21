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

import java.io.OutputStream;

import org.codehaus.plexus.util.StringUtils;

/**
 * Wrapper for an output stream.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class OutputStreamWrapper
    extends AbstractWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 3329037527245430610L;

    private OutputStream out;

    private String encoding;

    /**
     * Private constructor.
     *
     * @param format not null
     * @param supportedFormat not null
     * @throws IllegalArgumentException if any.
     */
    private OutputStreamWrapper( OutputStream out, String format, String encoding, String[] supportedFormat )
    {
        super( format, supportedFormat );

        if ( getFormat().equalsIgnoreCase( AUTO_FORMAT ) )
        {
            throw new IllegalArgumentException( "output format is required" );
        }

        this.out = out;
        this.encoding = encoding;
    }

    /**
     * @return the output stream
     */
    public OutputStream getOutputStream()
    {
        return this.out;
    }

    /**
     * @return the encoding
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param out not null
     * @param format not null
     * @param encoding not null
     * @param supportedFormat not null
     * @return a type safe output stream wrapper
     */
    public static OutputStreamWrapper valueOf( OutputStream out, String format, String encoding,
            String[] supportedFormat )
    {
        if ( out == null )
        {
            throw new IllegalArgumentException( "output writer is required" );
        }
        if ( StringUtils.isEmpty( format ) )
        {
            throw new IllegalArgumentException( "output format is required" );
        }

        return new OutputStreamWrapper( out, format, encoding, supportedFormat );
    }
}
