/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.maven.doxia.linkcheck.validation;

import junit.framework.TestCase;

public class AnchorsTest extends TestCase
{

    public void testAnchorMatching()
    {
        assertAnchorMatches( "hello  <h1 id='foo'>Foo</h1> there", "foo", true );
        assertAnchorMatches( "hello  <h1 id = 'foo'>Foo</h1> there", "foo", true );
        assertAnchorMatches( "hello  <h1 id=\"foo\">Foo</h1> there", "foo", true );
        assertAnchorMatches( "hello  <h1 id='foo2'>Foo</h1> there", "foo", false );
        final String apiAnchor = "assertEqualArrays(java.lang.Object[], java.lang.Object[])";
        assertAnchorMatches( "hello  <h1 id='" + apiAnchor + "'>Foo</h1> there", apiAnchor, true );

        assertAnchorMatches( "<html>\n"
            + "<body>\n"
            + "\n"
            + "<h1 id='foo'>Foo</h1>\n"
            + "<p>Some text</p>\n"
            + "\n"
            + "<h2>Something</h2>\n"
            + "<p>Lets try using a link: <a href=\"testAnchor.html#foo\">FooLink</a></p>\n"
            + "\n"
            + "</body>\n"
            + "</html>", "foo", true );
    }

    protected void assertAnchorMatches( String content, String anchor, boolean expected )
    {
        boolean actual = Anchors.matchesAnchor( content, anchor );
        assertEquals( "anchor: " + anchor + " in: " + content, expected, actual );
    }
}
