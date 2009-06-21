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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Wrapper for an input file.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class InputFileWrapper
    extends AbstractFileWrapper
{
    /** serialVersionUID */
    static final long serialVersionUID = 6510443036267371188L;

    /**
     * Private constructor.
     *
     * @param file not null
     * @param format could be null
     * @param charsetName could be null
     * @param supportedFormat not null
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     */
    private InputFileWrapper( String absolutePath, String format, String charsetName, String[] supportedFormat )
        throws UnsupportedEncodingException, FileNotFoundException
    {
        super( absolutePath, format, charsetName, supportedFormat );

        if ( !getFile().exists() )
        {
            throw new FileNotFoundException( "The file '" + getFile().getAbsolutePath() + "' doesn't exist." );
        }
    }

    /**
     * @param absolutePath for a file or a directory not null.
     * @param format could be null
     * @param supportedFormat not null
     * @return a type safe input reader
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     * @see #valueOf(String, String, String, String[]) using AUTO_FORMAT
     */
    public static InputFileWrapper valueOf( String absolutePath, String format, String[] supportedFormat )
        throws UnsupportedEncodingException, FileNotFoundException
    {
        return valueOf( absolutePath, format, AUTO_FORMAT, supportedFormat );
    }

    /**
     * @param absolutePath for a wanted file or a wanted directory, not null.
     * @param format could be null
     * @param charsetName could be null
     * @param supportedFormat not null
     * @return a type safe input reader
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     */
    public static InputFileWrapper valueOf( String absolutePath, String format, String charsetName,
                                            String[] supportedFormat )
        throws UnsupportedEncodingException, FileNotFoundException
    {
        return new InputFileWrapper( absolutePath, format, charsetName, supportedFormat );
    }
}
