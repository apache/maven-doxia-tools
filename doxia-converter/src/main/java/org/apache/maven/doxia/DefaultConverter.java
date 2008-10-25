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
package org.apache.maven.doxia;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.ConverterUtil;
import org.apache.maven.doxia.wrapper.InputFileWrapper;
import org.apache.maven.doxia.wrapper.InputReaderWrapper;
import org.apache.maven.doxia.wrapper.OutputFileWrapper;
import org.apache.maven.doxia.wrapper.OutputWriterWrapper;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * Default implementation of <code>Converter</code>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DefaultConverter
    implements Converter
{
    private static final String APT_PARSER = "apt";

    private static final String CONFLUENCE_PARSER = "confluence";

    private static final String DOCBOOK_PARSER = "docbook";

    private static final String FML_PARSER = "fml";

    private static final String TWIKI_PARSER = "twiki";

    private static final String XDOC_PARSER = "xdoc";

    private static final String XHTML_PARSER = "xhtml";

    /** Supported input format, i.e. supported Doxia parser */
    public static final String[] SUPPORTED_FROM_FORMAT =
        { APT_PARSER, CONFLUENCE_PARSER, DOCBOOK_PARSER, FML_PARSER, TWIKI_PARSER, XDOC_PARSER, XHTML_PARSER };

    private static final String APT_SINK = "apt";

    private static final String CONFLUENCE_SINK = "confluence";

    private static final String DOCBOOK_SINK = "docbook";

    private static final String FO_SINK = "fo";

    private static final String ITEXT_SINK = "itext";

    private static final String LATEX_SINK = "latex";

    private static final String RTF_SINK = "rtf";

    private static final String TWIKI_SINK = "twiki";

    private static final String XDOC_SINK = "xdoc";

    private static final String XHTML_SINK = "xhtml";

    /** Supported output format, i.e. supported Doxia Sink */
    public static final String[] SUPPORTED_TO_FORMAT =
        { APT_SINK, CONFLUENCE_SINK, DOCBOOK_SINK, FO_SINK, ITEXT_SINK, LATEX_SINK, RTF_SINK, TWIKI_SINK, XDOC_SINK, XHTML_SINK };

    /** Plexus container */
    private PlexusContainer plexus;

    /** Doxia logger */
    private Log log;

    /** {@inheritDoc} */
    public void enableLogging( Log log )
    {
        this.log = log;
    }

    /**
     * Returns a logger for this sink.
     * If no logger has been configured, a new SystemStreamLog is returned.
     *
     * @return Log
     */
    protected Log getLog()
    {
        if ( log == null )
        {
            log = new SystemStreamLog();
        }

        return log;
    }

    /** {@inheritDoc} */
    public String[] getInputFormats()
    {
        return SUPPORTED_FROM_FORMAT;
    }

    /** {@inheritDoc} */
    public String[] getOutputFormats()
    {
        return SUPPORTED_TO_FORMAT;
    }

    /** {@inheritDoc} */
    public void convert( InputFileWrapper input, OutputFileWrapper output )
        throws UnsupportedFormatException, ConverterException
    {
        if ( input == null )
        {
            throw new IllegalArgumentException( "input is required" );
        }
        if ( output == null )
        {
            throw new IllegalArgumentException( "output is required" );
        }

        try
        {
            startPlexusContainer();
        }
        catch ( PlexusContainerException e )
        {
            throw new ConverterException( "PlexusContainerException: " + e.getMessage(), e );
        }

        try
        {
            if ( input.getFile().isFile() )
            {
                parse( input.getFile(), input.getEncoding(), input.getFormat(), output );
            }
            else
            {
                List files;
                try
                {
                    files =
                        FileUtils.getFiles( input.getFile(), "**/*." + input.getFormat(),
                                            StringUtils.join( FileUtils.getDefaultExcludes(), ", " ) );
                }
                catch ( IOException e )
                {
                    throw new ConverterException( "IOException: " + e.getMessage(), e );
                }
                catch ( IllegalStateException e )
                {
                    throw new ConverterException( "IllegalStateException: " + e.getMessage(), e );
                }

                for ( Iterator it = files.iterator(); it.hasNext(); )
                {
                    File f = (File) it.next();

                    parse( f, input.getEncoding(), input.getFormat(), output );
                }
            }
        }
        finally
        {
            stopPlexusContainer();
        }
    }

    /** {@inheritDoc} */
    public void convert( InputReaderWrapper input, OutputWriterWrapper output )
        throws UnsupportedFormatException, ConverterException
    {
        if ( input == null )
        {
            throw new IllegalArgumentException( "input is required" );
        }
        if ( output == null )
        {
            throw new IllegalArgumentException( "output is required" );
        }

        try
        {
            startPlexusContainer();
        }
        catch ( PlexusContainerException e )
        {
            throw new ConverterException( "PlexusContainerException: " + e.getMessage(), e );
        }

        try
        {
            Parser parser;
            try
            {
                parser = ConverterUtil.getParser( plexus, input.getFormat(), SUPPORTED_FROM_FORMAT );
                parser.enableLogging( log );
            }
            catch ( ComponentLookupException e )
            {
                throw new ConverterException( "ComponentLookupException: " + e.getMessage(), e );
            }

            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Parser used: " + parser.getClass().getName() );
            }

            Sink sink;
            try
            {
                sink = ConverterUtil.getSink( plexus, output.getFormat(), output.getWriter(), SUPPORTED_TO_FORMAT );
            }
            catch ( ComponentLookupException e )
            {
                throw new ConverterException( "ComponentLookupException: " + e.getMessage(), e );
            }
            sink.enableLogging( log );

            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Sink used: " + sink.getClass().getName() );
            }

            parse( parser, input.getFormat(), input.getReader(), sink, output.getWriter() );
        }
        finally
        {
            stopPlexusContainer();
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * @param inputFile a not null existing file.
     * @param inputEncoding a not null supported encoding or {@link InputFileWrapper#AUTO_ENCODING}
     * @param inputFormat  a not null supported format or {@link InputFileWrapper#AUTO_FORMAT}
     * @param output not null OutputFileWrapper object
     * @throws ConverterException if any
     * @throws UnsupportedFormatException if any
     */
    private void parse( File inputFile, String inputEncoding, String inputFormat, OutputFileWrapper output )
        throws ConverterException, UnsupportedFormatException
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug(
                            "Parsing file from '" + inputFile.getAbsolutePath() + "' with the encoding '"
                                + inputEncoding + "' to '" + output.getFile().getAbsolutePath()
                                + "' with the encoding '" + output.getEncoding() + "'" );
        }

        if ( inputEncoding.equals( InputFileWrapper.AUTO_ENCODING ) )
        {
            inputEncoding = autoDetectEncoding( inputFile );
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Auto detect encoding: " + inputEncoding );
            }
        }

        if ( inputFormat.equals( InputFileWrapper.AUTO_FORMAT ) )
        {
            inputFormat = autoDetectFormat( inputFile, inputEncoding );
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Auto detect input format: " + inputFormat );
            }
        }

        Parser parser;
        try
        {
            parser = ConverterUtil.getParser( plexus, inputFormat, SUPPORTED_FROM_FORMAT );
            parser.enableLogging( log );
        }
        catch ( ComponentLookupException e )
        {
            throw new ConverterException( "ComponentLookupException: " + e.getMessage(), e );
        }

        File outputFile;
        if ( output.getFile().exists() && output.getFile().isDirectory() )
        {
            outputFile = new File( output.getFile(), inputFile.getName() + "." + output.getFormat() );
        }
        else
        {
            if ( !SelectorUtils.match( "**.*", output.getFile().getName() ) )
            {
                // assume it is a directory
                output.getFile().mkdirs();
                outputFile = new File( output.getFile(), inputFile.getName() + "." + output.getFormat() );
            }
            else
            {
                output.getFile().getParentFile().mkdirs();
                outputFile = output.getFile();
            }
        }

        Reader reader;
        try
        {
            if ( inputEncoding != null )
            {
                if ( parser.getType() == Parser.XML_TYPE )
                {
                    reader = ReaderFactory.newXmlReader( inputFile );
                }
                else
                {
                    reader = ReaderFactory.newReader( inputFile, inputEncoding );
                }
            }
            else
            {
                reader = ReaderFactory.newPlatformReader( inputFile );
            }
        }
        catch ( IOException e )
        {
            throw new ConverterException( "IOException: " + e.getMessage(), e );
        }

        Writer writer;
        try
        {
            String outputEncoding;
            if ( StringUtils.isEmpty( output.getEncoding() )
                || output.getEncoding().equals( OutputFileWrapper.AUTO_ENCODING ) )
            {
                outputEncoding = inputEncoding;
            }
            else
            {
                outputEncoding = output.getEncoding();
            }

            writer = WriterFactory.newWriter( outputFile, outputEncoding );
        }
        catch ( IOException e )
        {
            throw new ConverterException( "IOException: " + e.getMessage(), e );
        }

        Sink sink;
        try
        {
            sink = ConverterUtil.getSink( plexus, output.getFormat(), writer, SUPPORTED_TO_FORMAT );
        }
        catch ( ComponentLookupException e )
        {
            throw new ConverterException( "ComponentLookupException: " + e.getMessage(), e );
        }
        sink.enableLogging( log );

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Sink used: " + sink.getClass().getName() );
        }

        parse( parser, inputFormat, reader, sink, writer );
    }

    /**
     * @param parser not null
     * @param reader not null
     * @param sink not null
     * @param writer not null
     * @throws ConverterException if any
     */
    private void parse( Parser parser, String inputFormat, Reader reader, Sink sink, Writer writer )
        throws ConverterException
    {
        // add warnings
        if ( inputFormat.equals( DOCBOOK_PARSER ) && getLog().isWarnEnabled() )
        {
            getLog().warn(
                           "Docbook parser has some known issues. "
                               + "Please refer to http://jira.codehaus.org/browse/DOXIA-184" );
        }

        try
        {
            parser.parse( reader, sink );
        }
        catch ( ParseException e )
        {
            throw new ConverterException( "ParseException: " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( reader );
            sink.flush();
            sink.close();
            IOUtil.close( writer );
        }
    }

    /**
     * Start the Plexus container.
     *
     * @throws PlexusContainerException if any
     */
    private void startPlexusContainer()
        throws PlexusContainerException
    {
        if ( plexus != null )
        {
            return;
        }

        Map context = new HashMap();
        context.put( "basedir", new File( "" ).getAbsolutePath() );

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration();
        containerConfiguration.setName( "Doxia" );
        containerConfiguration.setContext( context );

        plexus = new DefaultPlexusContainer( containerConfiguration );
    }

    /**
     * Stop the Plexus container.
     */
    private void stopPlexusContainer()
    {
        if ( plexus == null )
        {
            return;
        }

        plexus.dispose();
        plexus = null;
    }

    /**
     * @param f not null file
     * @return the detected encoding for f or <code>null</code> if not able to detect it.
     * @throws IllegalArgumentException if f is not a file.
     * @throws UnsupportedOperationException if could not detect the file encoding.
     * @see {@link XmlStreamReader#getEncoding()} for xml files
     * @see {@link CharsetDetector#detect()} for text files
     */
    private static String autoDetectEncoding( File f )
    {
        if ( !f.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + f.getAbsolutePath()
                + "' is not a file, could not detect encoding." );
        }

        Reader reader = null;
        InputStream is = null;
        try
        {
            if ( isXML( f ) )
            {
                reader = ReaderFactory.newXmlReader( f );
                return ( (XmlStreamReader) reader ).getEncoding();
            }

            is = new BufferedInputStream( new FileInputStream( f ) );
            CharsetDetector detector = new CharsetDetector();
            detector.setText( is );
            CharsetMatch match = detector.detect();

            return match.getName().toUpperCase( Locale.ENGLISH );
        }
        catch ( IOException e )
        {
            // nop
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( is );
        }

        StringBuffer msg = new StringBuffer();
        msg.append( "Could not detect the encoding for file: " );
        msg.append( f.getAbsolutePath() );
        msg.append( "\n Specify explicitly the encoding." );
        throw new UnsupportedOperationException( msg.toString() );
    }

    /**
     * Determines if a given File shall be handled as XML.
     *
     * @param f not null file
     * @return <code>true</code> if the given file has XML content, <code>false</code> otherwise.
     */
    private static boolean isXML( File f )
    {
        if ( !f.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + f.getAbsolutePath() + "' is not a file." );
        }

        Reader reader = null;
        try
        {
            reader = new XmlStreamReader( f );
            XmlPullParser parser = new MXParser();
            parser.setInput( reader );
            parser.nextToken();

            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    /**
     * Auto detect Doxia format for the given file depending:
     * <ul>
     * <li>the file name for TextMarkup based Doxia files</li>
     * <li>the file content for XMLMarkup based Doxia files</li>
     * </ul>
     *
     * @param f not null file
     * @param encoding a not null encoding.
     * @return the detected encoding from f.
     * @throws IllegalArgumentException if f is not a file.
     * @throws UnsupportedOperationException if could not detect the Doxia format.
     */
    private static String autoDetectFormat( File f, String encoding )
    {
        if ( !f.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + f.getAbsolutePath()
                + "' is not a file, could not detect format." );
        }

        for ( int i = 0; i < SUPPORTED_FROM_FORMAT.length; i++ )
        {
            String supportedFromFormat = SUPPORTED_FROM_FORMAT[i];

            // Handle Doxia text files
            if ( supportedFromFormat.equalsIgnoreCase( APT_PARSER )
                && isDoxiaFileName( f, supportedFromFormat ) )
            {
                return supportedFromFormat;
            }
            else if ( supportedFromFormat.equalsIgnoreCase( CONFLUENCE_PARSER )
                && isDoxiaFileName( f, supportedFromFormat ) )
            {
                return supportedFromFormat;
            }
            else if ( supportedFromFormat.equalsIgnoreCase( TWIKI_PARSER )
                && isDoxiaFileName( f, supportedFromFormat ) )
            {
                return supportedFromFormat;
            }

            // Handle Doxia xml files
            String firstTag = getFirstTag( f );
            if ( firstTag == null )
            {
                continue;
            }
            else if ( firstTag.equals( "article" )
                && supportedFromFormat.equalsIgnoreCase( DOCBOOK_PARSER ) )
            {
                return supportedFromFormat;
            }
            else if ( firstTag.equals( "faqs" )
                && supportedFromFormat.equalsIgnoreCase( FML_PARSER ) )
            {
                return supportedFromFormat;
            }
            else if ( firstTag.equals( "document" )
                && supportedFromFormat.equalsIgnoreCase( XDOC_PARSER ) )
            {
                return supportedFromFormat;
            }
            else if ( firstTag.equals( "html" )
                && supportedFromFormat.equalsIgnoreCase( XHTML_PARSER ) )
            {
                return supportedFromFormat;
            }
        }

        StringBuffer msg = new StringBuffer();
        msg.append( "Could not detect the Doxia format for file: " );
        msg.append( f.getAbsolutePath() );
        msg.append( "\n Specify explicitly the Doxia format." );
        throw new UnsupportedOperationException( msg.toString() );
    }

    /**
     * @param f not null
     * @param format could be null
     * @return <code>true</code> if the file name computes the format.
     */
    private static boolean isDoxiaFileName( File f, String format )
    {
        if ( f == null )
        {
            throw new IllegalArgumentException( "f is required." );
        }

        Pattern pattern = Pattern.compile( "(.*?)\\." + format.toLowerCase( Locale.ENGLISH ) + "$" );
        Matcher matcher = pattern.matcher( f.getName().toLowerCase( Locale.ENGLISH ) );

        return matcher.matches();
    }

    /**
     * @param xmlFile not null and should be a file.
     * @return the first tag name if found, <code>null</code> in other case.
     */
    private static String getFirstTag( File xmlFile )
    {
        if ( xmlFile == null )
        {
            throw new IllegalArgumentException( "xmlFile is required." );
        }
        if ( !xmlFile.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + xmlFile.getAbsolutePath() + "' is not a file." );
        }

        Reader reader = null;
        try
        {
            reader = ReaderFactory.newXmlReader( xmlFile );
            XmlPullParser parser = new MXParser();
            parser.setInput( reader );
            int eventType = parser.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT )
            {
                if ( eventType == XmlPullParser.START_TAG )
                {
                    return parser.getName();
                }
                eventType = parser.nextToken();
            }
        }
        catch ( FileNotFoundException e )
        {
            return null;
        }
        catch ( XmlPullParserException e )
        {
            return null;
        }
        catch ( IOException e )
        {
            return null;
        }
        finally
        {
            IOUtil.close( reader );
        }

        return null;
    }
}
