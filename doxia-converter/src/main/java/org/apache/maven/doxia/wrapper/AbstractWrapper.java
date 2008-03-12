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

import java.io.Serializable;

/**
 * Abstract wrapper for Doxia converter.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractWrapper
    implements Serializable
{
    private String format;

    private String[] supportedFormat;

    /**
     * @return the wanted format.
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * @param format The wanted format.
     */
    void setFormat( String format )
    {
        this.format = format;
    }

    /**
     * @return the supportedFormat
     */
    public String[] getSupportedFormat()
    {
        return supportedFormat;
    }

    /**
     * @param supportedFormat the supportedFormat to set
     */
    void setSupportedFormat( String[] supportedFormat )
    {
        this.supportedFormat = supportedFormat;
    }

    /** {@inheritDoc} */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof AbstractWrapper ) )
        {
            return false;
        }

        AbstractWrapper that = (AbstractWrapper) other;
        boolean result = true;
        result = result && ( getFormat() == null ? that.getFormat() == null : getFormat().equals( that.getFormat() ) );
        return result;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + ( format != null ? format.hashCode() : 0 );
        return result;
    }

    /** {@inheritDoc} */
    public java.lang.String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "format = '" );
        buf.append( getFormat() + "'" );
        return buf.toString();
    }
}
