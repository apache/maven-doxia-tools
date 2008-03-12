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

import java.io.Writer;

import org.apache.maven.doxia.UnsupportedFormatException;
import org.apache.maven.doxia.util.FormatUtils;

/**
 * Wrapper for an output writer.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class OutputWriterWrapper
    extends AbstractFileWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 3329037527245430610L;

    private Writer writer;

    /**
     * Private constructor.
     *
     * @param format
     * @param supportedFormat
     */
    private OutputWriterWrapper( String format, String[] supportedFormat )
    {
        setFormat( format );
        setSupportedFormat( supportedFormat );
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
     * @throws IllegalArgumentException if any
     * @throws UnsupportedFormatException if any
     * @see FormatUtils#getSupportedFormat(String, String[])
     */
    public static OutputWriterWrapper valueOf( Writer writer, String format, String[] supportedFormat )
        throws UnsupportedFormatException
    {
        if ( writer == null )
        {
            throw new IllegalArgumentException( "writer is required" );
        }

        OutputWriterWrapper output = new OutputWriterWrapper(
                                                              FormatUtils.getSupportedFormat( format, supportedFormat ),
                                                              supportedFormat );
        output.writer = writer;

        return output;
    }
}
