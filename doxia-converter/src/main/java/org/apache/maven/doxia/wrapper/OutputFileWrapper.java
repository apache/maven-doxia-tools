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

import java.io.UnsupportedEncodingException;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * Wrapper for an output file.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class OutputFileWrapper
    extends AbstractFileWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 804499615902780116L;

    /**
     * Private constructor.
     *
     * @param file not null
     * @param format not null
     * @param charsetName could be null
     * @param supportedFormat not null.
     * @throws IllegalArgumentException if any.
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     */
    private OutputFileWrapper( String absolutePath, String format, String charsetName, String[] supportedFormat )
        throws UnsupportedEncodingException
    {
        super( absolutePath, format, charsetName, supportedFormat );

        if ( getFormat().equalsIgnoreCase( AUTO_FORMAT ) )
        {
            throw new IllegalArgumentException( "output format could not be " + AUTO_FORMAT );
        }
    }

    /**
     * @param absolutePath not null
     * @param format not null
     * @param supportedFormat not null
     * @return a type safe output writer
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     */
    public static OutputFileWrapper valueOf( String absolutePath, String format, String[] supportedFormat )
        throws UnsupportedEncodingException
    {
        return valueOf( absolutePath, format, WriterFactory.UTF_8, supportedFormat );
    }

    /**
     * @param absolutePath not null
     * @param format not null
     * @param charsetName could be null
     * @param supportedFormat not null
     * @return a type safe output writer
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     */
    public static OutputFileWrapper valueOf( String absolutePath, String format, String charsetName,
                                             String[] supportedFormat )
        throws UnsupportedEncodingException
    {
        if ( StringUtils.isEmpty( format ) )
        {
            throw new IllegalArgumentException( "output format is required" );
        }
        return new OutputFileWrapper( absolutePath, format, charsetName, supportedFormat );
    }
}
