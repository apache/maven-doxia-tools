package org.apache.maven.doxia;

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
 * Wrap an exception that occurs during the conversion.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ConverterException
    extends Exception
{
    /** The serialVersionUID */
    static final long serialVersionUID = -5251213141888260575L;

    /**
     * Construct a new ConverterException with the specified detail message.
     *
     * @param message The detailed message.
     * This can later be retrieved by the Throwable.getMessage() method.
     */
    public ConverterException( String message )
    {
        super( message );
    }

    /**
     * Construct a new ConverterException with the specified
     * detail message and cause.
     *
     * @param message The detailed message.
     * This can later be retrieved by the Throwable.getMessage() method.
     * @param cause the cause. This can be retrieved later by the
     * Throwable.getCause() method. (A null value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     */
    public ConverterException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
