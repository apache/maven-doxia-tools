package org.apache.maven.doxia.linkcheck;

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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.maven.doxia.linkcheck.model.LinkcheckModel;

/**
 * Tool to check links from html files in a given directory.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface LinkCheck
{
    /** Plexus Role */
    String ROLE = LinkCheck.class.getName();

    /**
     * Set the base directory for the files to be linkchecked.
     *
     * @param base the base directory
     */
    public void setBasedir( File base );

    /**
     * Sets the base URL. This is pre-pended to links that start with '/'.
     *
     * @param url the base URL.
     */
    public void setBaseURL( String url );

    /**
     * Sets the excluded HTTP errors, i.e. <code>404</code>, a int[] with excluded errors.
     *
     * @param excl The excludes to set
     * @see {@link HttpStatus} for all possible values.
     */
    public void setExcludedHttpStatusErrors( int[] excl );

    /**
     * Sets the excluded HTTP warnings, i.e. <code>301</code>, a int[] with excluded errors.
     *
     * @param excl The excludes to set
     * @see {@link HttpStatus} for all possible values.
     */
    public void setExcludedHttpStatusWarnings( int[] excl );

    /**
     * Sets the excluded links, a String[] with excluded locations.
     * Could contains a link, i.e. <code>http:&#47;&#47;maven.apache.org/</code>,
     * or pattern links i.e. <code>http:&#47;&#47;maven.apache.org&#47;**&#47;*.html</code>
     *
     * @param excl The excludes to set
     */
    public void setExcludedLinks( String[] excl );

    /**
     * Sets the excluded pages, a String[] with excluded locations.
     *
     * @param excl The excludes to set
     */
    public void setExcludedPages( String[] excl );

    /**
     * Sets the http parameters bean.
     *
     * @param http parameters bean.
     */
    public void setHttp( HttpBean http );

    /**
     * Sets the cache File.
     *
     * @param cacheFile The cacheFile to set. Set this to null to ignore storing the cache.
     */
    public void setLinkCheckCache( File cacheFile );

    /**
     * Set the online mode.
     *
     * @param onLine online mode.
     */
    public void setOnline( boolean onLine );

    /**
     * Set the output file for the results.
     * If this is null, no output will be written.
     *
     * @param file the output file.
     */
    public void setReportOutput( File file );

    /**
     * Sets the outputEncoding.
     *
     * @param encoding The outputEncoding to set.
     */
    public void setReportOutputEncoding( String encoding );

    /**
     * Execute the link check.
     *
     * @return the analysis in a <code>LinkCheck</code> model.
     */
    public LinkcheckModel execute();
}
