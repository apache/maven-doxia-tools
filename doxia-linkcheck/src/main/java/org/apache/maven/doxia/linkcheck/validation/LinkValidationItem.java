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

import java.io.File;

/**
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public class LinkValidationItem
{
    /** The source file. */
    private final File source;

    /** The link. */
    private final String link;

    /**
     * Constructor: initializes the source and link.
     *
     * @param src The source file, cannot be null.
     * @param lnk The link, cannot be null.
     */
    public LinkValidationItem( File src, String lnk )
    {
        if ( src == null )
        {
            throw new NullPointerException( "source can't be null" );
        }

        if ( lnk == null )
        {
            throw new NullPointerException( "link can't be null" );
        }

        this.source = src;
        this.link = lnk;
    }

    /**
     * Returns the link.
     *
     * @return String
     */
    public String getLink()
    {
        return this.link;
    }

    /**
     * Returns the source file.
     *
     * @return File
     */
    public File getSource()
    {
        return this.source;
    }

    /** {@inheritDoc} */
    public boolean equals( Object obj )
    {
        LinkValidationItem lvi = (LinkValidationItem) obj;

        if ( !lvi.link.equals( this.link ) )
        {
            return false;
        }

        if ( !lvi.source.equals( this.source ) )
        {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return this.source.hashCode() ^ this.link.hashCode();
    }

}
