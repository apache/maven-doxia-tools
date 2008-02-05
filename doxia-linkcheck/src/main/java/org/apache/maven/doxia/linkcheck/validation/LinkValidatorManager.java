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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */

public class LinkValidatorManager implements Serializable
{
    /** serialVersionUID. */
    private static final long serialVersionUID = 2467928182206500945L;

    /** Log for debug output. */
    private static final Log LOG = LogFactory.getLog( LinkValidatorManager.class );

    /** validators. */
    private List validators = new LinkedList();

    /** excludes. */
    private String[] excludedLinks = new String[0];

    /** cache. */
    private Map cache = new HashMap();

    /**
     * Returns the list of validators.
     *
     * @return List
     */
    public List getValidators()
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

        Iterator iter = this.validators.iterator();

        LinkValidator lv;

        Object resourceKey;

        LinkValidationResult lvr;

        while ( iter.hasNext() )
        {
            lv = (LinkValidator) iter.next();

            resourceKey = lv.getResourceKey( lvi );

            if ( resourceKey != null )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( lv.getClass().getName() + " - Checking link " + lvi.getLink() );
                }

                lvr = lv.validateLink( lvi );

                if ( lvr.getStatus() == LinkValidationResult.NOTMINE )
                {
                    continue;
                }

                setCachedResult( resourceKey, lvr );

                return lvr;
            }
        }

        lv = null;

        resourceKey = null;

        lvr = null;

        LOG.error( "Unable to validate link : " + lvi.getLink() );

        return new LinkValidationResult( LinkcheckFileResult.UNKNOWN_LEVEL, false, "No validator found for this link" );
    }

    /**
     * Loads a cache file.
     *
     * @param cacheFile The cache file.
     * May be null, in which case the request is ignored.
     */
    public void loadCache( File cacheFile )
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

        ObjectInputStream is = null;

        try
        {
            is = new ObjectInputStream( new FileInputStream( cacheFile ) );

            this.cache = (Map) is.readObject();

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
            LOG.error( "Unable to load the cache: " + cacheFile.getAbsolutePath(), e );
        }
        catch ( IOException t )
        {
            LOG.error( "Unable to load the cache: " + cacheFile.getAbsolutePath(), t );
        }
        finally
        {
            try
            {
                is.close();
            }
            catch ( IOException e )
            {
                LOG.debug( "Unable to close stream!", e );

                is = null;
            }
        }
    }

    /**
     * Saves a cache file.
     *
     * @param cacheFile The name of the cache file.
     * May be null, in which case the request is ignored.
     */
    public void saveCache( File cacheFile )
    {
        if ( cacheFile == null )
        {
            LOG.warn( "No cache file specified! Ignoring request to store results." );
            return;
        }

        // Remove non-persistent items from cache
        Map persistentCache = new HashMap();

        Iterator iter = this.cache.keySet().iterator();

        Object resourceKey;

        while ( iter.hasNext() )
        {
            resourceKey = iter.next();

            if ( ( (LinkValidationResult) this.cache.get( resourceKey ) ).isPersistent() )
            {
                persistentCache.put( resourceKey, this.cache.get( resourceKey ) );

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "[" + resourceKey + "] with result [" + this.cache.get( resourceKey )
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
        catch ( IOException e )
        {
            LOG.error( "Unable to save the cache: " + cacheFile.getAbsolutePath(), e );
        }
        finally
        {
            persistentCache = null;

            iter = null;

            resourceKey = null;

            cacheFile = null;

            dir = null;

            try
            {
                os.close();
            }
            catch ( IOException e )
            {
                LOG.debug( "Unable to close stream!", e );

                os = null;
            }
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
        Iterator iter = getValidators().iterator();

        LinkValidator lv;

        Object resourceKey;

        while ( iter.hasNext() )
        {
            lv = (LinkValidator) iter.next();

            resourceKey = lv.getResourceKey( lvi );

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

        lv = null;

        resourceKey = null;

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

    protected static boolean matchPattern( String link, String pattern )
    {
        if ( pattern.indexOf( '*' ) == -1 )
        {
            if ( pattern.endsWith( "/" ) )
            {
                return link.indexOf( pattern.substring( 0, pattern.lastIndexOf( '/' ) ) ) != -1;
            }

            return link.indexOf( pattern ) != -1;
        }

        String diff = StringUtils.difference( link, pattern );
        if ( diff.startsWith( "/" ) )
        {
            return SelectorUtils.match( pattern, link + "/" );
        }

        return SelectorUtils.match( pattern, link );
    }
}
