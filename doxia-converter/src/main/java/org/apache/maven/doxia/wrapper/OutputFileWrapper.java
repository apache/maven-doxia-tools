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

import java.io.File;

import org.apache.maven.doxia.UnsupportedFormatException;
import org.apache.maven.doxia.util.FormatUtils;
import org.codehaus.plexus.util.StringUtils;

import com.ibm.icu.text.CharsetDetector;

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
     * @param file
     * @param format
     * @param charsetName could be null
     * @param supportedFormat
     */
    private OutputFileWrapper( File file, String format, String charsetName, String[] supportedFormat )
    {
        setFile( file );
        setFormat( format );
        setEncoding( charsetName );
        setSupportedFormat( supportedFormat );
    }

    /**
     * @param absolutePath not null
     * @param format not null
     * @param charsetName could be null
     * @param supportedFormat not null
     * @return a type safe output writer
     * @throws IllegalArgumentException if any
     * @throws UnsupportedFormatException if any
     * @see FormatUtils#getSupportedFormat(String, String, String[])
     */
    public static OutputFileWrapper valueOf( String absolutePath, String format, String charsetName, String[] supportedFormat )
        throws UnsupportedFormatException
    {
        if ( StringUtils.isEmpty( absolutePath ) )
        {
            throw new IllegalArgumentException( "absolutePath is required" );
        }
        if ( StringUtils.isNotEmpty( charsetName ) && !validateEncoding( charsetName ) )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "The encoding '" + charsetName + "' is not a valid. The supported charsets are: " );
            msg.append( StringUtils.join( CharsetDetector.getAllDetectableCharsets(), ", " ) );
            throw new IllegalArgumentException( msg.toString() );
        }

        File file = new File( absolutePath );

        if ( !file.isAbsolute() )
        {
            file = new File( new File( "" ).getAbsolutePath(), absolutePath );
        }

        return new OutputFileWrapper( file, FormatUtils.getSupportedFormat( file.getAbsolutePath(), format,
                                                                            supportedFormat ), charsetName,
                                      supportedFormat );
    }
}
