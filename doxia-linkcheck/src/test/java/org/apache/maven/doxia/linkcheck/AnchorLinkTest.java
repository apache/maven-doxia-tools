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

import org.apache.maven.doxia.linkcheck.model.LinkcheckFile;
import org.apache.maven.doxia.linkcheck.model.LinkcheckModel;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Ben Walding
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id: LinkCheckTest.java 800044 2009-08-02 12:28:50Z vsiveton $
 */
public class AnchorLinkTest
    extends PlexusTestCase
{
    /**
     * @throws Exception
     */
    public void testScan()
        throws Exception
    {
        LinkCheck lc = (LinkCheck) lookup( LinkCheck.ROLE );
        assertNotNull( lc );

        lc.setOnline( true ); // TODO: check if online

        lc.setBasedir( new File( getBasedir(), "src/test/resources/anchorTest" ) ); // TODO

        lc.setReportOutput( new File( getBasedir(), "target/linkcheck/anchorTest/linkcheck.xml" ) );

        lc.setReportOutputEncoding( "UTF-8" );

        lc.setLinkCheckCache( new File( getBasedir(), "target/linkcheck/anchorTest/linkcheck.cache" ) ); // TODO

        String[] excludes = new String[]
        {
            "http://cvs.apache.org/viewcvs.cgi/maven-pluginszz/",
            "http://cvs.apache.org/viewcvs.cgi/mavenzz/"
        };

        lc.setExcludedLinks( excludes );

        LinkcheckModel result = lc.execute();

        Iterator iter = result.getFiles().iterator();

        Map map = new HashMap();

        while ( iter.hasNext() )
        {
            LinkcheckFile ftc = (LinkcheckFile) iter.next();
            map.put( ftc.getRelativePath(), ftc );
        }

        assertEquals( "files.size()", 1, result.getFiles().size() );

        LinkcheckFile ftc = check( map, "testAnchor.html", 1 );

        //System.out.println("anchor test " + ftc.getResults());

        assertEquals( "Should have matched!", 1, ftc.getSuccessful() );
        assertEquals( "Should have no failures!", 0, ftc.getUnsuccessful() );
    }

    private LinkcheckFile check( Map map, String name, int linkCount )
    {
        LinkcheckFile ftc = (LinkcheckFile) map.get( name );

        assertNotNull( name + " = null!", ftc );

        assertEquals( name + ".getResults().size()", linkCount, ftc.getResults().size() );

        return ftc;
    }
}
