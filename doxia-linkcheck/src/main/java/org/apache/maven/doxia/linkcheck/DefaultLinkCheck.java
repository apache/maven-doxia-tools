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
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.maven.doxia.linkcheck.model.LinkcheckFile;
import org.apache.maven.doxia.linkcheck.model.LinkcheckFileResult;
import org.apache.maven.doxia.linkcheck.model.LinkcheckModel;
import org.apache.maven.doxia.linkcheck.model.io.xpp3.LinkcheckModelXpp3Writer;
import org.apache.maven.doxia.linkcheck.validation.FileLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.HTTPLinkValidationResult;
import org.apache.maven.doxia.linkcheck.validation.LinkValidationItem;
import org.apache.maven.doxia.linkcheck.validation.LinkValidationResult;
import org.apache.maven.doxia.linkcheck.validation.LinkValidatorManager;
import org.apache.maven.doxia.linkcheck.validation.MailtoLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OfflineHTTPLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OnlineHTTPLinkValidator;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * The main bean to be called whenever a set of documents should have their links checked.
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
@Component( role = LinkCheck.class )
public final class DefaultLinkCheck
    implements LinkCheck
{
    /** Log. */
    private static final Log LOG = LogFactory.getLog( DefaultLinkCheck.class );

    /** One MegaByte. */
    private static final long MEG = 1024 * 1024;

    /** The basedir to check. */
    private File basedir;

    /** Linkcheck Cache. */
    private File linkCheckCache;

    /**
     * To exclude some links. Could contains a link, i.e. <code>http:&#47;&#47;maven.apache.org</code>,
     * or pattern links i.e. <code>http:&#47;&#47;maven.apache.org&#47;**&#47;*.html</code>
     */
    private String[] excludedLinks = null;

    /** To exclude some pages. */
    private String[] excludedPages = null;

    /**
     * Excluded http errors only in on line mode.
     *
     * @see {@link HttpStatus} for all defined values.
     */
    private int[] excludedHttpStatusErrors = null;

    /**
     * Excluded http warnings only in on line mode.
     *
     * @see {@link HttpStatus} for all defined values.
     */
    private int[] excludedHttpStatusWarnings = null;

    /** Online mode. */
    private boolean online;

    /** Bean enncapsuling some https parameters */
    private HttpBean http;

    /** Internal LinkValidatorManager. */
    private LinkValidatorManager lvm = null;

    /** Report output file for xml document. */
    private File reportOutput;

    /** Report output encoding for the xml document, UTF-8 by default. */
    private String reportOutputEncoding = "UTF-8";

    /** The base URL for links that start with '/'. */
    private String baseURL;

    /** The encoding used to process files, UTF-8 by default. */
    private String encoding = ReaderFactory.UTF_8;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void setBasedir( File base )
    {
        this.basedir = base;
    }

    /** {@inheritDoc} */
    public void setBaseURL( String url )
    {
        this.baseURL = url;
    }

    /** {@inheritDoc} */
    public void setExcludedHttpStatusErrors( int[] excl )
    {
        this.excludedHttpStatusErrors = excl;
    }

    /** {@inheritDoc} */
    public void setExcludedHttpStatusWarnings( int[] excl )
    {
        this.excludedHttpStatusWarnings = excl;
    }

    /** {@inheritDoc} */
    public void setExcludedLinks( String[] excl )
    {
        this.excludedLinks = excl;
    }

    /** {@inheritDoc} */
    public void setExcludedPages( String[] excl )
    {
        this.excludedPages = excl;
    }

    /** {@inheritDoc} */
    public void setHttp( HttpBean http )
    {
        this.http = http;
    }

    /** {@inheritDoc} */
    public void setLinkCheckCache( File cacheFile )
    {
        this.linkCheckCache = cacheFile;
    }

    /** {@inheritDoc} */
    public void setOnline( boolean onLine )
    {
        this.online = onLine;
    }

    /** {@inheritDoc} */
    public void setReportOutput( File file )
    {
        this.reportOutput = file;
    }

    /** {@inheritDoc} */
    public void setReportOutputEncoding( String encoding )
    {
        this.reportOutputEncoding = encoding;
    }

    /** {@inheritDoc} */
    public LinkcheckModel execute()
        throws LinkCheckException
    {
        if ( this.basedir == null )
        {
            LOG.error( "No base directory specified!" );

            throw new NullPointerException( "The basedir can't be null!" );
        }

        if ( this.reportOutput == null )
        {
            LOG.warn( "No output file specified! Results will not be written!" );
        }

        LinkcheckModel model = new LinkcheckModel();
        model.setModelEncoding( reportOutputEncoding );
        model.setFiles( new LinkedList<LinkcheckFile>() );

        displayMemoryConsumption();

        LinkValidatorManager validator = getLinkValidatorManager();
        try
        {
            validator.loadCache( this.linkCheckCache );
        }
        catch ( IOException e )
        {
            throw new LinkCheckException( "Could not load cache: " + e.getMessage(), e );
        }

        displayMemoryConsumption();

        LOG.info( "Begin to check links in files..." );

        try
        {
            findAndCheckFiles( this.basedir, model );
        }
        catch ( IOException e )
        {
            throw new LinkCheckException( "Could not scan base directory: " + basedir.getAbsolutePath(), e );
        }

        LOG.info( "Links checked." );

        displayMemoryConsumption();

        try
        {
            createDocument( model );
        }
        catch ( IOException e )
        {
            throw new LinkCheckException( "Could not write the linkcheck document: " + e.getMessage(), e );
        }

        try
        {
            validator.saveCache( this.linkCheckCache );
        }
        catch ( IOException e )
        {
            throw new LinkCheckException( "Could not save cache: " + e.getMessage(), e );
        }

        displayMemoryConsumption();

        return model;
    }

    /** {@inheritDoc} */
    public void setEncoding( String encoding )
    {
        if ( StringUtils.isEmpty( encoding ) )
        {
            throw new IllegalArgumentException( "encoding is required" );
        }
        try
        {
            Charset.forName( encoding );
        }
        catch ( UnsupportedCharsetException e )
        {
            throw new IllegalArgumentException( "encoding '" + encoding + "' is unsupported" );
        }

        this.encoding = encoding;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Whether links are checked in online mode.
     *
     * @return online
     */
    private boolean isOnline()
    {
        return this.online;
    }

    /**
     * Returns the excluded links.
     * Could contains a link, i.e. <code>http:&#47;&#47;maven.apache.org/</code>,
     * or pattern links i.e. <code>http:&#47;&#47;maven.apache.org&#47;**&#47;*.html</code>
     *
     * @return String[]
     */
    private String[] getExcludedLinks()
    {
        return this.excludedLinks;
    }

    /**
     * Gets the comma separated list of effective exclude patterns.
     *
     * @return The comma separated list of effective exclude patterns, never <code>null</code>.
     */
    private String getExcludedPages()
    {
        @SuppressWarnings( "unchecked" )
        LinkedList<String> patternList = new LinkedList<String>( FileUtils.getDefaultExcludesAsList() );

        if ( excludedPages != null )
        {
            patternList.addAll( Arrays.asList( excludedPages ) );
        }

        return StringUtils.join( patternList.iterator(), "," );
    }

    /**
     * Gets the comma separated list of effective include patterns.
     *
     * @return The comma separated list of effective include patterns, never <code>null</code>.
     */
    private String getIncludedPages()
    {
        return "**/*.html,**/*.htm";
    }

    /**
     * Returns the excluded HTTP errors, i.e. <code>404</code>.
     *
     * @return int[]
     * @see {@link HttpStatus} for all possible values.
     */
    private int[] getExcludedHttpStatusErrors()
    {
        return this.excludedHttpStatusErrors;
    }

    /**
     * Returns the excluded HTTP warnings, i.e. <code>301</code>.
     *
     * @return int[]
     * @see {@link HttpStatus} for all possible values.
     */
    private int[] getExcludedHttpStatusWarnings()
    {
        return this.excludedHttpStatusWarnings;
    }

    /**
     * Returns the LinkValidatorManager.
     * If this hasn't been set before with {@link #setLinkValidatorManager(LinkValidatorManager)}
     * a default LinkValidatorManager will be returned.
     *
     * @return the LinkValidatorManager
     */
    private LinkValidatorManager getLinkValidatorManager()
    {
        if ( this.lvm == null )
        {
            initDefaultLinkValidatorManager();
        }

        return this.lvm;
    }

    /**
     * Intializes the current LinkValidatorManager to a default value.
     */
    private void initDefaultLinkValidatorManager()
    {
        this.lvm = new LinkValidatorManager();

        if ( getExcludedLinks() != null )
        {
            this.lvm.setExcludedLinks( getExcludedLinks() );
        }

        this.lvm.addLinkValidator( new FileLinkValidator( encoding ) );

        if ( isOnline() )
        {
            OnlineHTTPLinkValidator olv = new OnlineHTTPLinkValidator( http );

            if ( this.baseURL != null )
            {
                olv.setBaseURL( baseURL );
            }

            this.lvm.addLinkValidator( olv );
        }
        else
        {
            this.lvm.addLinkValidator( new OfflineHTTPLinkValidator() );
        }

        this.lvm.addLinkValidator( new MailtoLinkValidator() );
    }

    /**
     * Recurses through the given base directory and adds/checks
     * files to the model that pass through the current filter.
     *
     * @param base the base directory to traverse.
     */
    @SuppressWarnings( "unchecked" )
    private void findAndCheckFiles( File base, LinkcheckModel model )
        throws IOException
    {
        for ( File file : (List<File>) FileUtils.getFiles( base, getIncludedPages(), getExcludedPages() ) )
        {
            checkFile( file, model );
        }
    }

    private void checkFile( File file, LinkcheckModel model )
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( " File - " + file );
        }

        String fileRelativePath = file.getAbsolutePath();

        if ( fileRelativePath.startsWith( this.basedir.getAbsolutePath() ) )
        {
            fileRelativePath = fileRelativePath.substring( this.basedir.getAbsolutePath().length() + 1 );
        }

        fileRelativePath = fileRelativePath.replace( '\\', '/' );

        LinkcheckFile linkcheckFile = new LinkcheckFile();
        linkcheckFile.setAbsolutePath( file.getAbsolutePath() );
        linkcheckFile.setRelativePath( fileRelativePath );

        check( linkcheckFile );

        model.addFile( linkcheckFile );

        if ( ( model.getFiles().size() % 100 == 0 ) && LOG.isInfoEnabled() )
        {
            LOG.info( "Found " + model.getFiles().size() + " files so far." );
        }
    }

    /**
     * Validates a linkcheck file.
     *
     * @param linkcheckFile the linkcheckFile object to validate
     */
    private void check( LinkcheckFile linkcheckFile )
    {
        linkcheckFile.setSuccessful( 0 );

        linkcheckFile.setUnsuccessful( 0 );

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Validating " + linkcheckFile.getRelativePath() );
        }

        final Set<String> hrefs;

        try
        {
            hrefs = LinkMatcher.match( new File( linkcheckFile.getAbsolutePath() ), encoding );
        }
        catch ( Throwable t )
        {
            // We catch Throwable, because there is a chance that the domReader will throw
            // a stack overflow exception for some files

            LOG.error( "Received: [" + t + "] in page [" + linkcheckFile.getRelativePath() + "]" );
            LOG.debug( t.getMessage(), t );

            LinkcheckFileResult lcr = new LinkcheckFileResult();

            lcr.setStatus( "PARSE FAILURE" );

            lcr.setTarget( "N/A" );

            linkcheckFile.addResult( lcr );

            return;
        }

        LinkcheckFileResult lcr;
        LinkValidationItem lvi;
        LinkValidationResult result;

        for ( String href : hrefs )
        {
            lcr = new LinkcheckFileResult();
            lvi = new LinkValidationItem( new File( linkcheckFile.getAbsolutePath() ), href );
            result = lvm.validateLink( lvi );
            lcr.setTarget( href );
            lcr.setErrorMessage( result.getErrorMessage() );

            switch ( result.getStatus() )
            {
                case LinkcheckFileResult.VALID_LEVEL:
                    linkcheckFile.setSuccessful( linkcheckFile.getSuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.VALID );

                    // At some point we won't want to store valid links. The tests require that we do at present.
                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.ERROR_LEVEL:
                    boolean ignoredError = false;
                    if ( result instanceof HTTPLinkValidationResult )
                    {
                        HTTPLinkValidationResult httpResult = (HTTPLinkValidationResult) result;

                        if ( httpResult.getHttpStatusCode() > 0
                            && getExcludedHttpStatusErrors() != null
                            && StringUtils.indexOfAny( String.valueOf( httpResult.getHttpStatusCode() ),
                                                       toStringArray( getExcludedHttpStatusErrors() ) ) >= 0 )
                        {
                            ignoredError = true;
                        }
                    }

                    if ( ignoredError )
                    {
                        linkcheckFile.setSuccessful( linkcheckFile.getSuccessful() + 1 );
                    }
                    else
                    {
                        linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );
                    }

                    lcr.setStatus( ignoredError ? LinkcheckFileResult.VALID : LinkcheckFileResult.ERROR );

                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.WARNING_LEVEL:
                    boolean ignoredWarning = false;
                    if ( result instanceof HTTPLinkValidationResult )
                    {
                        HTTPLinkValidationResult httpResult = (HTTPLinkValidationResult) result;

                        if ( httpResult.getHttpStatusCode() > 0
                            && getExcludedHttpStatusWarnings() != null
                            && StringUtils.indexOfAny( String.valueOf( httpResult.getHttpStatusCode() ),
                                                       toStringArray( getExcludedHttpStatusWarnings() ) ) >= 0 )
                        {
                            ignoredWarning = true;
                        }
                    }

                    if ( ignoredWarning )
                    {
                        linkcheckFile.setSuccessful( linkcheckFile.getSuccessful() + 1 );
                    }
                    else
                    {
                        linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );
                    }

                    lcr.setStatus( ignoredWarning ? LinkcheckFileResult.VALID : LinkcheckFileResult.WARNING );

                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.UNKNOWN_LEVEL:
                default:
                    linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.UNKNOWN );

                    linkcheckFile.addResult( lcr );

                    break;
            }
        }
    }

    /**
     * Writes some memory data to the log (if debug enabled).
     */
    private void displayMemoryConsumption()
    {
        if ( LOG.isDebugEnabled() )
        {
            Runtime r = Runtime.getRuntime();
            LOG.debug( "Memory: " + ( r.totalMemory() - r.freeMemory() ) / MEG + "M/" + r.totalMemory() / MEG
                + "M" );
        }
    }

    /**
     * Create the XML document from the currently available details.
     *
     * @throws IOException if any
     */
    private void createDocument( LinkcheckModel model )
        throws IOException
    {
        if ( this.reportOutput == null )
        {
            return;
        }

        File dir = this.reportOutput.getParentFile();
        if ( dir != null )
        {
            dir.mkdirs();
        }

        Writer writer = null;
        LinkcheckModelXpp3Writer xpp3Writer = new LinkcheckModelXpp3Writer();
        try
        {
            writer = WriterFactory.newXmlWriter( this.reportOutput );
            xpp3Writer.write( writer, model );
        }
        catch ( IllegalStateException e )
        {
            IOException ioe =
                new IOException( e.getMessage() + " Maybe try to specify an other encoding instead of '"
                    + encoding + "'." );
            ioe.initCause( e );
            throw ioe;
        }
        finally
        {
            IOUtil.close( writer );
        }

        dir = null;
    }

    private static String[] toStringArray( int[] array )
    {
        if ( array == null )
        {
            throw new IllegalArgumentException( "array could not be null" );
        }

        String[] result = new String[array.length];
        for ( int i = 0; i < array.length; i++ )
        {
            result[i] = String.valueOf( array[i] );
        }
        return result;
    }
}
