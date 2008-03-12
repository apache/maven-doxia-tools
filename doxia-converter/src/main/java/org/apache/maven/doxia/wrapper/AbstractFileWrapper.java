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

/**
 * Abstract File wrapper for Doxia converter.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractFileWrapper
    extends AbstractWrapper
{
    private File file;

    /**
     * @return the file
     */
    public File getFile()
    {
        return file;
    }

    /**
     * @param file new file.
     */
    void setFile( File file )
    {
        this.file = file;
    }

    /** {@inheritDoc} */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof AbstractFileWrapper ) )
        {
            return false;
        }

        AbstractFileWrapper that = (AbstractFileWrapper) other;
        boolean result = true;
        result = result && super.equals( other );
        result = result && ( getFile() == null ? that.getFile() == null : getFile().equals( that.getFile() ) );
        return result;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        int result = super.hashCode();
        result = 37 * result + ( getFile() != null ? getFile().hashCode() : 0 );
        return result;
    }

    /** {@inheritDoc} */
    public java.lang.String toString()
    {
        StringBuffer buf = new StringBuffer( super.toString() + "\n" );
        buf.append( "file= '" );
        buf.append( getFile() + "'" );
        return buf.toString();
    }
}
