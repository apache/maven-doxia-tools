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
package org.apache.maven.doxia.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.maven.doxia.DefaultConverter;
import org.codehaus.plexus.util.StringUtils;

import com.ibm.icu.text.CharsetDetector;

/**
 * Manager for Doxia converter CLI options.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
class CLIManager
{
    /** h character */
    static final char HELP = 'h';

    /** v character */
    static final char VERSION = 'v';

    /** in String */
    static final String IN = "in";

    /** out String */
    static final String OUT = "out";

    /** from String */
    static final String FROM = "from";

    /** to String */
    static final String TO = "to";

    /** inEncoding String */
    static final String INENCODING = "inEncoding";

    /** X character */
    static final char DEBUG = 'X';

    /** e character */
    static final char ERRORS = 'e';

    private static final Options options;

    static
    {
        options = new Options();

        options.addOption( OptionBuilder.withLongOpt( "help" ).withDescription( "Display help information." )
                                        .create( HELP ) );
        options.addOption( OptionBuilder.withLongOpt( "version" ).withDescription( "Display version information." )
                                        .create( VERSION ) );

        options.addOption( OptionBuilder.withLongOpt( "input" ).withDescription( "Input file or directory." )
                                        .hasArg().create( IN ) );
        options.addOption( OptionBuilder.withLongOpt( "output" ).withDescription( "Output file or directory." )
                                        .hasArg().create( OUT ) );
        options.addOption( OptionBuilder.withDescription( "From format. If not specified, try to autodetect it." )
                                        .hasArg().create( FROM ) );
        options.addOption( OptionBuilder.withDescription( "To format." ).hasArg().create( TO ) );
        options.addOption( OptionBuilder.withLongOpt( "inputEncoding" )
                                        .withDescription(
                                                          "Input file encoding. "
                                                              + "If not specified, try to autodetect it." )
                                        .hasArg().create( INENCODING ) );

        options.addOption( OptionBuilder.withLongOpt( "debug" )
                                        .withDescription( "Produce execution debug output." ).create( DEBUG ) );
        options.addOption( OptionBuilder.withLongOpt( "errors" )
                                        .withDescription( "Produce execution error messages." ).create( ERRORS ) );
    }

    /**
     * @param args not null.
     * @return a not null command line.
     * @throws ParseException if any
     * @throws IllegalArgumentException is args is null
     */
    CommandLine parse( String[] args )
        throws ParseException
    {
        if ( args == null )
        {
            throw new IllegalArgumentException( "args is required." );
        }

        // We need to eat any quotes surrounding arguments...
        String[] cleanArgs = cleanArgs( args );

        CommandLineParser parser = new GnuParser();
        return parser.parse( options, cleanArgs );
    }

    static void displayHelp()
    {
        System.out.println();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "doxia [options] -in <arg> [-from <arg>] [-inEncoding <arg>] -out <arg> "
            + "-to <arg>\n", "\nOptions:", options, getSupportedFormatAndEncoding() );
    }

    private static String getSupportedFormatAndEncoding()
    {
        return getSupportedFormat() + "\n" + getSupportedEncoding();
    }

    private static String getSupportedFormat()
    {
        return "\nSupported Formats:\n from: " + StringUtils.join( DefaultConverter.SUPPORTED_FROM_FORMAT, ", " )
            + " or autodetect" + "\n out: " + StringUtils.join( DefaultConverter.SUPPORTED_TO_FORMAT, ", " )
            + "\n";
    }

    private static String getSupportedEncoding()
    {
        return "\nSupported Encoding:\n " + StringUtils.join( CharsetDetector.getAllDetectableCharsets(), ", " );
    }

    private String[] cleanArgs( String[] args )
    {
        List cleaned = new ArrayList();

        StringBuffer currentArg = null;

        for ( int i = 0; i < args.length; i++ )
        {
            String arg = args[i];

            boolean addedToBuffer = false;

            if ( arg.startsWith( "\"" ) )
            {
                // if we're in the process of building up another arg, push it and start over.
                // this is for the case: "-Dfoo=bar "-Dfoo2=bar two" (note the first unterminated quote)
                if ( currentArg != null )
                {
                    cleaned.add( currentArg.toString() );
                }

                // start building an argument here.
                currentArg = new StringBuffer( arg.substring( 1 ) );
                addedToBuffer = true;
            }

            // this has to be a separate "if" statement, to capture the case of: "-Dfoo=bar"
            if ( arg.endsWith( "\"" ) )
            {
                String cleanArgPart = arg.substring( 0, arg.length() - 1 );

                // if we're building an argument, keep doing so.
                if ( currentArg != null )
                {
                    // if this is the case of "-Dfoo=bar", then we need to adjust the buffer.
                    if ( addedToBuffer )
                    {
                        currentArg.setLength( currentArg.length() - 1 );
                    }
                    // otherwise, we trim the trailing " and append to the buffer.
                    else
                    {
                        // TODO: introducing a space here...not sure what else to do but collapse whitespace
                        currentArg.append( ' ' ).append( cleanArgPart );
                    }

                    // we're done with this argument, so add it.
                    cleaned.add( currentArg.toString() );
                }
                else
                {
                    // this is a simple argument...just add it.
                    cleaned.add( cleanArgPart );
                }

                // the currentArg MUST be finished when this completes.
                currentArg = null;
                continue;
            }

            // if we haven't added this arg to the buffer, and we ARE building an argument
            // buffer, then append it with a preceding space...again, not sure what else to
            // do other than collapse whitespace.
            // NOTE: The case of a trailing quote is handled by nullifying the arg buffer.
            if ( !addedToBuffer )
            {
                // append to the argument we're building, collapsing whitespace to a single space.
                if ( currentArg != null )
                {
                    currentArg.append( ' ' ).append( arg );
                }
                // this is a loner, just add it directly.
                else
                {
                    cleaned.add( arg );
                }
            }
        }

        // clean up.
        if ( currentArg != null )
        {
            cleaned.add( currentArg.toString() );
        }

        int cleanedSz = cleaned.size();
        String[] cleanArgs = null;

        if ( cleanedSz == 0 )
        {
            // if we didn't have any arguments to clean, simply pass the original array through
            cleanArgs = args;
        }
        else
        {
            cleanArgs = (String[]) cleaned.toArray( new String[cleanedSz] );
        }

        return cleanArgs;
    }
}
