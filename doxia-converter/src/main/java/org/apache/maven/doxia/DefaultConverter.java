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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.maven.doxia.convertor.model.InputFile;
import org.apache.maven.doxia.convertor.model.OutputFile;
import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.module.apt.AptSink;
import org.apache.maven.doxia.module.confluence.ConfluenceParser;
import org.apache.maven.doxia.module.docbook.DocBookParser;
import org.apache.maven.doxia.module.docbook.DocBookSink;
import org.apache.maven.doxia.module.fml.FmlParser;
import org.apache.maven.doxia.module.fo.FoSink;
import org.apache.maven.doxia.module.itext.ITextSink;
import org.apache.maven.doxia.module.latex.LatexSink;
import org.apache.maven.doxia.module.rtf.RtfSink;
import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.module.xdoc.XdocParser;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.module.xhtml.XhtmlParser;
import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Default implementation of <code>Converter</code>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DefaultConverter
    implements Converter
{
    private static final String APT_PARSER = getParserFormat( AptParser.class );

    private static final String CONFLUENCE_PARSER = getParserFormat( ConfluenceParser.class );

    private static final String DOCBOOK_PARSER = getParserFormat( DocBookParser.class );

    private static final String FML_PARSER = getParserFormat( FmlParser.class );

    private static final String TWIKI_PARSER = getParserFormat( TWikiParser.class );

    private static final String XDOC_PARSER = getParserFormat( XdocParser.class );

    private static final String XHTML_PARSER = getParserFormat( XhtmlParser.class );

    /** Supported input format, i.e. supported Doxia parser */
    public static final String[] SUPPORTED_FROM_FORMAT = {
        APT_PARSER,
        CONFLUENCE_PARSER,
        DOCBOOK_PARSER,
        FML_PARSER,
        TWIKI_PARSER,
        XDOC_PARSER,
        XHTML_PARSER };

    private static final String APT_SINK = getSinkFormat( AptSink.class );

    private static final String DOCBOOK_SINK = getSinkFormat( DocBookSink.class );

    private static final String FO_SINK = getSinkFormat( FoSink.class );

    private static final String ITEXT_SINK = getSinkFormat( ITextSink.class );

    private static final String LATEX_SINK = getSinkFormat( LatexSink.class );

    private static final String RTF_SINK = getSinkFormat( RtfSink.class );

    private static final String XDOC_SINK = getSinkFormat( XdocSink.class );

    private static final String XHTML_SINK = getSinkFormat( XhtmlSink.class );

    /** Supported output format, i.e. supported Doxia Sink */
    public static final String[] SUPPORTED_TO_FORMAT = {
        APT_SINK,
        DOCBOOK_SINK,
        FO_SINK,
        ITEXT_SINK,
        LATEX_SINK,
        RTF_SINK,
        XDOC_SINK,
        XHTML_SINK };

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
    public void convert( InputFile input, OutputFile output )
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

        Parser parser = getParser( input.getFormat() );

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Parser used: " + parser.getClass().getName() );
        }

        StringWriter stringWriter = new StringWriter();
        Sink sink = getSink( output.getFormat(), stringWriter );

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Sink used: " + sink.getClass().getName() );
        }

        if ( input.getFile().isFile() )
        {
            parse( input.getFile(), output, parser, sink, stringWriter );
        }
        else
        {
            List files;
            try
            {
                files = FileUtils.getFiles( input.getFile(), "**/*." + input.getFormat(), StringUtils.join( FileUtils
                    .getDefaultExcludes(), ", " ) );
            }
            catch ( IOException e )
            {
                throw new ConverterException( "IOException: " + e.getMessage(), e );
            }

            for ( Iterator it = files.iterator(); it.hasNext(); )
            {
                File f = (File) it.next();

                parse( f, output, parser, sink, stringWriter );
            }
        }
    }

    /**
     * @param clazz an implementation of <code>Parser</code> with the pattern <code>&lt;format&gt;Parser</code>
     * @return the parser format in lower case.
     * @see Parser implementations
     */
    private static String getParserFormat( Class clazz )
    {
        return StringUtils.replace( ClassUtils.getShortClassName( clazz ).toLowerCase(), "parser", "" );
    }

    /**
     * @param clazz an implementation of <code>Sink</code> with the pattern <code>&lt;format&gt;Sink</code>
     * @return the sink format in lower case.
     * @see Sink implementations
     */
    private static String getSinkFormat( Class clazz )
    {
        return StringUtils.replace( ClassUtils.getShortClassName( clazz ).toLowerCase(), "sink", "" );
    }

    /**
     * @param format
     * @return an instance of <code>Parser</code> depending the format.
     * @throws IllegalArgumentException if any
     */
    private static Parser getParser( String format )
    {
        if ( format.equals( APT_PARSER ) )
        {
            return new AptParser();
        }
        else if ( format.equals( CONFLUENCE_PARSER ) )
        {
            return new ConfluenceParser();
        }
        else if ( format.equals( DOCBOOK_PARSER ) )
        {
            return new DocBookParser();
        }
        else if ( format.equals( FML_PARSER ) )
        {
            return new FmlParser();
        }
        else if ( format.equals( TWIKI_PARSER ) )
        {
            return new TWikiParser();
        }
        else if ( format.equals( XDOC_PARSER ) )
        {
            return new XdocParser();
        }
        else if ( format.equals( XHTML_PARSER ) )
        {
            return new XhtmlParser();
        }

        throw new IllegalArgumentException( "Parser not found for: " + format );
    }

    /**
     * @param format
     * @return an instance of <code>Sink</code> depending the format.
     * @throws IllegalArgumentException if any
     */
    private static Sink getSink( String format, StringWriter stringWriter )
    {
        if ( format.equals( APT_SINK ) )
        {
            return new AptSink( stringWriter );
        }
        else if ( format.equals( DOCBOOK_SINK ) )
        {
            return new DocBookSink( stringWriter );
        }
        else if ( format.equals( FO_SINK ) )
        {
            return new FoSink( stringWriter );
        }
        else if ( format.equals( ITEXT_SINK ) )
        {
            return new ITextSink( stringWriter );
        }
        else if ( format.equals( LATEX_SINK ) )
        {
            return new LatexSink( stringWriter );
        }
        else if ( format.equals( RTF_SINK ) )
        {
            // TODO
            //return  new RtfSink( s );
        }
        else if ( format.equals( XDOC_SINK ) )
        {
            return new XdocSink( stringWriter );
        }
        else if ( format.equals( XHTML_SINK ) )
        {
            return new XhtmlSink( stringWriter );
        }

        throw new IllegalArgumentException( "Sink not found for: " + format );
    }

    /**
     * @param inputFile
     * @param output
     * @param parser
     * @param sink
     * @param stringWriter
     * @throws ConverterException if any
     */
    private void parse( File inputFile, OutputFile output, Parser parser, Sink sink, StringWriter stringWriter )
        throws ConverterException
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug(
                            "Parsing file from '" + inputFile.getAbsolutePath() + "' to '" + output.getAbsolutePath()
                                + "'" );
        }

        Reader reader = null;
        try
        {
            reader = new FileReader( inputFile );
            parser.parse( reader, sink );
        }
        catch ( IOException e )
        {
            throw new ConverterException( "IOException: " + e.getMessage(), e );
        }
        catch ( ParseException e )
        {
            throw new ConverterException( "ParseException: " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        Writer writer = null;
        try
        {
            if ( output.getFile().exists() && output.getFile().isDirectory() )
            {
                writer = new FileWriter( new File( output.getFile(), inputFile.getName() + "." + output.getFormat() ) );
            }
            else
            {
                if ( !SelectorUtils.match( "**.*", output.getFile().getName() ) )
                {
                    // assume it is a directory
                    output.getFile().mkdirs();
                    writer = new FileWriter(
                                             new File( output.getFile(), inputFile.getName() + "." + output.getFormat() ) );
                }
                else
                {
                    output.getFile().getParentFile().mkdirs();
                    writer = new FileWriter( output.getFile() );
                }
            }

            IOUtil.copy( stringWriter.toString(), writer );
        }
        catch ( IOException e )
        {
            throw new ConverterException( "IOException: " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( writer );
            IOUtil.close( stringWriter );
        }
    }
}
