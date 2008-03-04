package org.apache.maven.doxia;

import org.codehaus.plexus.util.StringUtils;

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

/**
 * Wrap an exception that occurs if a format is not supported.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class UnsupportedFormatException
    extends Exception
{
    /** serialVersionUID */
    static final long serialVersionUID = -4334290887832961366L;

    /**
     * Constructs an UnsupportedFormatException with the specified
     * detail message.
     *
     * @param format the unsupported format
     * @param supportedFormat the supported formats
     */
    public UnsupportedFormatException( String format, String[] supportedFormat )
    {
        super( "Unsupported format '" + format + "'. The allowed format are: "
            + StringUtils.join( supportedFormat, ", " ) );
    }

    /**
     * Construct a new UnsupportedFormatException with the specified
     * detail message and cause.
     *
     * @param format the unsupported format
     * @param supportedFormat the supported formats
     * This can later be retrieved by the Throwable.getMessage() method.
     * @param cause the cause. This can be retrieved later by the
     * Throwable.getCause() method. (A null value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     */
    public UnsupportedFormatException( String format, String[] supportedFormat, Throwable cause )
    {
        super( "Unsupported format '" + format + "'. The allowed format are: "
            + StringUtils.join( supportedFormat, ", " ), cause );
    }
}
