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
import java.io.Writer;

import org.codehaus.plexus.util.StringUtils;

/**
 * Wrapper for an output writer.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class OutputWriterWrapper
    extends AbstractWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 3329037527245430610L;

    private Writer writer;

    /**
     * Private constructor.
     *
     * @param format not null
     * @param supportedFormat not null
     * @throws IllegalArgumentException if any.
     */
    private OutputWriterWrapper( Writer writer, String format, String[] supportedFormat )
    {
        super( format, supportedFormat );

        if ( getFormat().equalsIgnoreCase( AUTO_FORMAT ) )
        {
            throw new IllegalArgumentException( "output format is required" );
        }

        this.writer = writer;
    }

    /**
     * @return the writer
     */
    public Writer getWriter()
    {
        return this.writer;
    }

    /**
     * @param writer not null
     * @param format not null
     * @param supportedFormat not null
     * @return a type safe output writer
     * @throws IllegalArgumentException if any.
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     */
    public static OutputWriterWrapper valueOf( Writer writer, String format, String[] supportedFormat )
        throws IllegalArgumentException
    {
        if ( writer == null )
        {
            throw new IllegalArgumentException( "output writer is required" );
        }
        if ( StringUtils.isEmpty( format ) )
        {
            throw new IllegalArgumentException( "output format is required" );
        }

        return new OutputWriterWrapper( writer, format, supportedFormat );
    }
}
