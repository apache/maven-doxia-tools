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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class to test if some content matches the given HTML anchor
 */
public class Anchors
{
    /**
     * Returns true if the given anchor can be found in the content markup.
     *
     * @param content the content string.
     * @param anchor the anchor to match.
     *
     * @return true if the given anchor can be found in the content markup.
     */
    public static boolean matchesAnchor( String content, String anchor )
    {
        if ( content != null && anchor.length() > 0 ) {
            // can use name or id attributes and also can use single or double quotes with whitespace around the =
            String regex = "(name|id)\\s*=\\s*('|\")" + escapeBrackets( anchor ) + "('|\")";
            Pattern pattern = Pattern.compile( regex );
            Matcher matcher = pattern.matcher( content );
            return matcher.find();
        }
        return false;
    }

    // for javadoc links, see DOXIA-410
    private static String escapeBrackets( String content )
    {
        final String escaped = content.replace( "(", "\\(" ).replace( ")", "\\)" );
        return escaped.replace( "[", "\\[" ).replace( "]", "\\]" );
    }

    private Anchors()
    {
        // utility class
    }
}
