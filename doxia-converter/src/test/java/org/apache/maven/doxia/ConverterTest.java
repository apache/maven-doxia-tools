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

import org.apache.maven.doxia.convertor.model.InputFile;
import org.apache.maven.doxia.convertor.model.OutputFile;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ConverterTest
    extends PlexusTestCase
{
    Converter converter;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        converter = new DefaultConverter();
    }

    /**
     * @see Converter#getInputFormats()
     */
    public void testGetInputFormats()
    {
        assertNotNull( converter.getInputFormats() );
    }

    /**
     * @see Converter#getOutputFormats()
     */
    public void testGetOutputFormats()
    {
        assertNotNull( converter.getOutputFormats() );
    }

    /**
     * @see Converter#convert(InputFile, OutputFile)
     * @throws Exception
     */
    public void testConvert()
        throws Exception
    {
        String in = getBasedir() + "/src/test/resources/unit/Doxia.htm";
        String from = "xhtml";
        String out = getBasedir() + "/target/unit/";
        String to = "apt";

        InputFile input = InputFile.valueOf( in, from, converter.getInputFormats() );
        OutputFile output = OutputFile.valueOf( out, to, converter.getOutputFormats() );

        converter.convert( input, output );
        assertTrue( new File( out, "Doxia.htm.apt" ).exists() );

        // ----------------------------------------------------------------------

        FileUtils.deleteDirectory( new File( getBasedir() + "/target/unit/" ) );

        in = getBasedir() + "/src/test/resources/unit/Doxia.htm";
        from = "xhtml";
        out = getBasedir() + "/target/unit/Doxia.apt";
        to = "apt";

        input = InputFile.valueOf( in, from, converter.getInputFormats() );
        output = OutputFile.valueOf( out, to, converter.getOutputFormats() );

        converter.convert( input, output );
        assertTrue( new File( out ).exists() );

        // ----------------------------------------------------------------------

        FileUtils.deleteDirectory( new File( getBasedir() + "/target/unit/" ) );

        FileUtils.copyFile( new File( in ), new File( getBasedir(), "/target/unit/Doxia.xhtml" ) );

        in = getBasedir() + "/target/unit/Doxia.xhtml";
        from = null;
        out = getBasedir() + "/target/unit/Doxia.apt";
        to = "apt";

        input = InputFile.valueOf( in, from, converter.getInputFormats() );
        output = OutputFile.valueOf( out, to, converter.getOutputFormats() );

        converter.convert( input, output );
        assertTrue( new File( out ).exists() );

        // ----------------------------------------------------------------------

        FileUtils.deleteDirectory( new File( getBasedir() + "/target/unit/" ) );

        FileUtils.copyFile( new File( getBasedir() + "/src/test/resources/unit/Doxia.htm" ),
                            new File( getBasedir(), "/target/unit/Doxia.xhtml" ) );

        in = getBasedir() + "/target/unit/Doxia.xhtml";
        from = null;
        out = getBasedir() + "/target/unit/Doxia.apt";
        to = null;

        input = InputFile.valueOf( in, from, converter.getInputFormats() );
        output = OutputFile.valueOf( out, to, converter.getOutputFormats() );

        converter.convert( input, output );
        assertTrue( new File( out ).exists() );
    }
}
