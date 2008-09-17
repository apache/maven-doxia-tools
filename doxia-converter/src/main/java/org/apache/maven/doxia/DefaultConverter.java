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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
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

    private static final String DOCBOOK_SINK = "docbook";

    private static final String FO_SINK = "fo";

    private static final String ITEXT_SINK = "itext";

    private static final String LATEX_SINK = "latex";

    private static final String RTF_SINK = "rtf";

    private static final String XDOC_SINK = "xdoc";

    private static final String XHTML_SINK = "xhtml";

    /** Supported output format, i.e. supported Doxia Sink */
    public static final String[] SUPPORTED_TO_FORMAT =
        { APT_SINK, DOCBOOK_SINK, FO_SINK, ITEXT_SINK, LATEX_SINK, RTF_SINK, XDOC_SINK, XHTML_SINK };

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
        try
        {
            startPlexusContainer();
        }
        catch ( PlexusContainerException e )
        {
            throw new ConverterException( "PlexusContainerException: " + e.getMessage(), e );
        }

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
            Parser parser;
            try
            {
                parser = getParser( plexus, input.getFormat() );
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

            if ( input.getFile().isFile() )
            {
                parse( input.getFile(), input.getEncoding(), output, parser );
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

                for ( Iterator it = files.iterator(); it.hasNext(); )
                {
                    File f = (File) it.next();

                    parse( f, input.getEncoding(), output, parser );
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
        try
        {
            startPlexusContainer();
        }
        catch ( PlexusContainerException e )
        {
            throw new ConverterException( "PlexusContainerException: " + e.getMessage(), e );
        }

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
            Parser parser;
            try
            {
                parser = getParser( plexus, input.getFormat() );
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
                sink = getSink( plexus, output.getFormat(), output.getWriter() );
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

            try
            {
                parser.parse( input.getReader(), sink );
            }
            catch ( ParseException e )
            {
                throw new ConverterException( "ParseException: " + e.getMessage(), e );
            }
            finally
            {
                IOUtil.close( input.getReader() );
                sink.flush();
                sink.close();
                IOUtil.close( output.getWriter() );
            }
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
     * @param plexus
     * @param format
     * @return an instance of <code>Parser</code> depending the format.
     * @throws ComponentLookupException if any
     * @throws IllegalArgumentException if any
     */
    private static Parser getParser( PlexusContainer plexus, String format )
        throws ComponentLookupException
    {
        Parser parser = null;
        if ( format.equals( APT_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "apt" );
        }
        else if ( format.equals( CONFLUENCE_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "confluence" );
        }
        else if ( format.equals( DOCBOOK_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "doc-book" );
        }
        else if ( format.equals( FML_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "fml" );
        }
        else if ( format.equals( TWIKI_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "twiki" );
        }
        else if ( format.equals( XDOC_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "xdoc" );
        }
        else if ( format.equals( XHTML_PARSER ) )
        {
            parser = (Parser) plexus.lookup( Parser.ROLE, "xhtml" );
        }

        if ( parser == null )
        {
            throw new IllegalArgumentException( "Parser not found for: " + format );
        }

        return parser;
    }

    /**
     * @param format
     * @param writer
     * @return an instance of <code>Sink</code> depending the format.
        throws ComponentLookupException if any
     * @throws IllegalArgumentException if any
     */
    private static Sink getSink( PlexusContainer plexus, String format, Writer writer )
        throws ComponentLookupException
    {
        SinkFactory factory = (SinkFactory) plexus.lookup( SinkFactory.ROLE, format );

        if ( factory == null )
        {
            throw new IllegalArgumentException( "SinkFactory not found for: " + format );
        }

        Sink sink = factory.createSink( writer );

        if ( sink == null )
        {
            throw new IllegalArgumentException( "Sink was not instanciated: " + format );
        }

        return sink;
    }

    /**
     * @param inputFile not null
     * @param inputEncoding could be null
     * @param output not null
     * @param parser not null
     * @throws ConverterException if any
     */
    private void parse( File inputFile, String inputEncoding, OutputFileWrapper output, Parser parser )
        throws ConverterException
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug(
                            "Parsing file from '" + inputFile.getAbsolutePath() + "' with the encoding '"
                                + inputEncoding + "' to '" + output.getFile().getAbsolutePath()
                                + "' with the encoding '" + output.getEncoding() + "'" );
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
        FileInputStream is = null;
        try
        {
            is = new FileInputStream( inputFile );

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
        finally
        {
            IOUtil.close( is );
        }

        Writer writer;
        try
        {
            String outputEncoding;
            if ( StringUtils.isEmpty( output.getEncoding() ) )
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
            sink = getSink( plexus, output.getFormat(), writer );
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
}
