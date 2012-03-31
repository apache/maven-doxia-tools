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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.doxia.linkcheck.model.LinkcheckFileResult;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A LinkValidator manager which manages validators with a cache.
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class LinkValidatorManager
    implements Serializable
{
    /** serialVersionUID. */
    private static final long serialVersionUID = 2467928182206500945L;

    /** Log for debug output. */
    private static final Log LOG = LogFactory.getLog( LinkValidatorManager.class );

    /** validators. */
    private List<LinkValidator> validators = new LinkedList<LinkValidator>();

    /** excludes. */
    private String[] excludedLinks = new String[0];

    /** cache. */
    private Map<Object, LinkValidationResult> cache = new HashMap<Object, LinkValidationResult>();

    /**
     * Returns the list of validators.
     *
     * @return List
     */
    public List<LinkValidator> getValidators()
    {
        return this.validators;
    }

    /**
     * Returns the excludedLinks.
     * Could contains a link, i.e. <code>http:&#47;&#47;maven.apache.org/</code>,
     * or pattern links i.e. <code>http:&#47;&#47;maven.apache.org&#47;**&#47;*.html</code>
     *
     * @return String[]
     */
    public String[] getExcludedLinks()
    {
        return this.excludedLinks;
    }

    /**
     * Sets the excludedLinks.
     * Could contains a link, i.e. <code>http:&#47;&#47;maven.apache.org/</code>,
     * or pattern links i.e. <code>http:&#47;&#47;maven.apache.org&#47;**&#47;*.html</code>
     *
     * @param excl The excludes to set.
     */
    public void setExcludedLinks( String[] excl )
    {
        this.excludedLinks = excl;
    }

    /**
     * Adds a LinkValidator to this manager.
     *
     * @param lv The LinkValidator to add.
     */
    public void addLinkValidator( LinkValidator lv )
    {
        this.validators.add( lv );
    }

    /**
     * Validates the links of the given LinkValidationItem.
     *
     * @param lvi The LinkValidationItem to validate.
     * @return A LinkValidationResult.
     */
    public LinkValidationResult validateLink( LinkValidationItem lvi )
    {
        LinkValidationResult cachedResult = getCachedResult( lvi );

        if ( cachedResult != null )
        {
            return cachedResult;
        }

        for ( int i = 0; i < this.excludedLinks.length; i++ )
        {
            if ( this.excludedLinks[i] != null && matchPattern( lvi.getLink(), this.excludedLinks[i] ) )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Excluded " + lvi.getLink() );
                }

                return new LinkValidationResult( LinkcheckFileResult.VALID_LEVEL, false, "" );
            }
        }

        for ( LinkValidator lv : this.validators )
        {
            Object resourceKey = lv.getResourceKey( lvi );

            if ( resourceKey != null )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( lv.getClass().getName() + " - Checking link " + lvi.getLink() );
                }

                LinkValidationResult lvr = lv.validateLink( lvi );

                if ( lvr.getStatus() == LinkValidationResult.NOTMINE )
                {
                    continue;
                }

                setCachedResult( resourceKey, lvr );

                return lvr;
            }
        }

        if ( LOG.isErrorEnabled() )
        {
            LOG.error( "Unable to validate link : " + lvi.getLink() );
        }

        return new LinkValidationResult( LinkcheckFileResult.UNKNOWN_LEVEL, false, "No validator found for this link" );
    }

    /**
     * Loads a cache file.
     *
     * @param cacheFile The cache file.
     * May be null, in which case the request is ignored.
     * @throws IOException if any
     */
    @SuppressWarnings( "unchecked" )
    public void loadCache( File cacheFile )
        throws IOException
    {
        if ( cacheFile == null )
        {
            LOG.debug( "No cache file specified! Ignoring request to load." );
            return;
        }

        if ( !cacheFile.exists() )
        {
            LOG.debug( "Specified cache file does not exist! Ignoring request to load." );
            return;
        }

        if ( cacheFile.isDirectory() )
        {
            LOG.debug( "Cache file is a directory! Ignoring request to load." );
            return;
        }

        ObjectInputStream is = null;
        try
        {
            is = new ObjectInputStream( new FileInputStream( cacheFile ) );

            this.cache = (Map<Object, LinkValidationResult>) is.readObject();

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( "Cache file loaded: " + cacheFile.getAbsolutePath() );
            }
        }
        catch ( InvalidClassException e )
        {
            LOG.warn( "Your cache is incompatible with this version of linkcheck. It will be recreated." );
        }
        catch ( ClassNotFoundException e )
        {
            if ( LOG.isErrorEnabled() )
            {
                LOG.error( "Unable to load the cache: " + cacheFile.getAbsolutePath(), e );
            }
        }
        finally
        {
            IOUtil.close( is );
        }
    }

    /**
     * Saves a cache file.
     *
     * @param cacheFile The name of the cache file.
     * May be null, in which case the request is ignored.
     * @throws IOException if any
     */
    public void saveCache( File cacheFile )
        throws IOException
    {
        if ( cacheFile == null )
        {
            LOG.warn( "No cache file specified! Ignoring request to store results." );
            return;
        }

        if ( cacheFile.isDirectory() )
        {
            LOG.debug( "Cache file is a directory! Ignoring request to load." );
            return;
        }

        // Remove non-persistent items from cache
        Map<Object, LinkValidationResult> persistentCache = new HashMap<Object, LinkValidationResult>();

        for ( Map.Entry<Object, LinkValidationResult> resource : this.cache.entrySet() )
        {
            if ( resource.getValue().isPersistent() )
            {
                persistentCache.put( resource.getKey(), resource.getValue() );

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "[" + resource.getKey() + "] with result [" + resource.getValue()
                        + "] is stored in the cache." );
                }
            }
        }

        File dir = cacheFile.getParentFile();
        if ( dir != null )
        {
            dir.mkdirs();
        }

        ObjectOutputStream os = null;
        try
        {
            os = new ObjectOutputStream( new FileOutputStream( cacheFile ) );

            os.writeObject( persistentCache );
        }
        finally
        {
            IOUtil.close( os );
        }
    }

    /**
     * Returns a LinkValidationResult for the given LinkValidationItem
     * if it has been cached from a previous run, returns null otherwise.
     *
     * @param lvi The LinkValidationItem.
     * @return LinkValidationResult
     */
    public LinkValidationResult getCachedResult( LinkValidationItem lvi )
    {
        for ( LinkValidator lv :  getValidators() )
        {
            Object resourceKey = lv.getResourceKey( lvi );

            if ( resourceKey != null && this.cache.containsKey( resourceKey ) )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "The cache returns for [" + resourceKey + "] the result ["
                        + this.cache.get( resourceKey ) + "]." );
                }

                return (LinkValidationResult) this.cache.get( resourceKey );
            }
        }

        return null;
    }

    /**
     * Puts the given LinkValidationResult into the cache.
     *
     * @param resourceKey The key to retrieve the result.
     * @param lvr the LinkValidationResult to cache.
     */
    public void setCachedResult( Object resourceKey, LinkValidationResult lvr )
    {
        this.cache.put( resourceKey, lvr );
    }

    /**
     * @param link not null
     * @param pattern not null
     * @return true if pattern match
     */
    protected static boolean matchPattern( String link, String pattern )
    {
        if ( StringUtils.isEmpty( pattern ) )
        {
            return StringUtils.isEmpty( link );
        }

        if ( pattern.indexOf( '*' ) == -1 )
        {
            if ( pattern.endsWith( "/" ) )
            {
                return link.indexOf( pattern.substring( 0, pattern.lastIndexOf( '/' ) ) ) != -1;
            }

            return link.indexOf( pattern ) != -1;
        }

        try
        {
            URI uri = new URI( link );

            if ( uri.getScheme() != null && !pattern.startsWith( uri.getScheme() ) )
            {
                return false;
            }
        }
        catch ( URISyntaxException ex )
        {
            LOG.debug( "Trying to check link to illegal URI: " + link, ex );
        }

        if ( pattern.matches( "\\*+/?.*" ) && !link.startsWith( "/" ) && !link.startsWith( "./" ) )
        {
            link = "./" + link;
        }
        String diff = StringUtils.difference( link, pattern );
        if ( diff.startsWith( "/" ) )
        {
            return SelectorUtils.match( pattern, link + "/" );
        }

        return SelectorUtils.match( pattern, link );
    }
}
