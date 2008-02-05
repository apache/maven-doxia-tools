package org.apache.maven.doxia.linkcheck.validation;

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
 * This class is used to return HTTP status responses from the validation handlers.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class HTTPLinkValidationResult
    extends LinkValidationResult
{
    /** serialVersionUID. */
    static final long serialVersionUID = 3541710617127916373L;

    /** The httpStatusCode. */
    private final int httpStatusCode;

    /**
     * Constructor: initializes status, persistent and errorMessage.
     * Using this constructor, the HTTP status code is by default <code>-1</code>.
     *
     * @param stat The status.
     * @param persistent The persistent.
     * @param message The errorMessage.
     */
    public HTTPLinkValidationResult( int stat, boolean persistent, String message )
    {
        super( stat, persistent, message );

        this.httpStatusCode = -1;
    }

    /**
     * Constructor: initializes status, persistent, httpStatusCode and errorMessage.
     *
     * @param stat The status.
     * @param persistent The persistent.
     * @param httpStatusCode The httpStatusCode returned.
     * @param message The errorMessage.
     */
    public HTTPLinkValidationResult( int stat, boolean persistent, int httpStatusCode, String message )
    {
        super( stat, persistent, message );

        this.httpStatusCode = httpStatusCode;
    }

    /** {@inheritDoc} */
    public String getErrorMessage()
    {
        return this.httpStatusCode + " " + super.getErrorMessage();
    }

    /**
     * Returns the httpStatusCode.
     *
     * @return int
     */
    public int getHttpStatusCode()
    {
        return this.httpStatusCode;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        StringBuffer sb = new StringBuffer( super.toString() );

        sb.append( '\n' );
        sb.append( "httpStatusCode=" ).append( this.httpStatusCode );

        return sb.toString();
    }
}
