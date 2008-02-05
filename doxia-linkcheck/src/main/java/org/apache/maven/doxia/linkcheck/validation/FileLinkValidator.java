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

import org.apache.maven.doxia.linkcheck.model.LinkcheckFileResult;

/**
 * A link validator solely for files on the local filesystem.
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public final class FileLinkValidator implements LinkValidator
{
    /** {@inheritDoc} */
    public LinkValidationResult validateLink( LinkValidationItem lvi )
    {
        File f = getFile( lvi );

        if ( f.exists() )
        {
            return new LinkValidationResult( LinkcheckFileResult.VALID_LEVEL, false, "" );
        }

        return new LinkValidationResult( LinkcheckFileResult.ERROR_LEVEL, false, "doesn't exist." );
    }

    /** {@inheritDoc} */
    public Object getResourceKey( LinkValidationItem lvi )
    {
        String link = lvi.getLink();

        // If we find an http(s) link or a mail link, it's not good
        // links starting with "/" should have a base URL pre-pended and be handled by OnlineHTTPLinkValidator.
        if ( link.toLowerCase().startsWith( "http://" ) || link.toLowerCase().startsWith( "https://" )
            || link.indexOf( '@' ) != -1 || link.startsWith( "/" ) )
        {
            return null;
        }

        return getFile( lvi ).getAbsolutePath();
    }

    /**
     * Returns the link of the given LinkValidationItem as a File.
     *
     * @param lvi The LinkValidationItem.
     * @return File the link as a File.
     */
    protected File getFile( LinkValidationItem lvi )
    {
        String link = lvi.getLink();

        if ( link.indexOf( '#' ) != -1 )
        {
            link = link.substring( 0, link.indexOf( '#' ) );

            // If the link was just #fred or similar, then the file is the file it came from
            // XXX: Theoretically we could even validate the anchor tag?
            if ( link.trim().length() == 0 )
            {
                return lvi.getSource();
            }
        }
        if ( link.indexOf( '?' ) != -1 )
        {
            link = link.substring( 0, link.indexOf( '?' ) );

            // If the link was just ?param=something or similar, then the file is the file it came from
            // XXX: Theoretically we could even validate the anchor tag?
            if ( link.trim().length() == 0 )
            {
                return lvi.getSource();
            }
        }

        return new File( lvi.getSource().getParentFile(), link );
    }

}
