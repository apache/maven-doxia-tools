package org.apache.maven.doxia.ide.eclipse.common.ui;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides several colors mainly uses by the <code>IToken</code> classes.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class ColorManager
{
    /** green color */
    public static final RGB COMMENT = new RGB( 63, 127, 95 );

    /** red color */
    public static final RGB KEYWORD = new RGB( 127, 0, 85 );

    /** black color */
    public static final RGB STRING = new RGB( 0, 0, 0 );

    /** blue color */
    public static final RGB LINK = new RGB( 0, 0, 255 );

    /** cached map */
    protected Map<RGB, Color> colorMap = new HashMap<RGB, Color>( 5 );

    /** singleton pattern */
    private static final ColorManager INSTANCE = new ColorManager();

    private ColorManager()
    {
        // nop
    }

    public static ColorManager getInstance()
    {
        return INSTANCE;
    }

    /**
     * Disposes of the operating system resources associated with the color map.
     */
    public void dispose()
    {
        Iterator<Color> e = colorMap.values().iterator();
        while ( e.hasNext() )
        {
            e.next().dispose();
        }
    }

    /**
     * @param rgb
     * @return the color object from the rgb object
     */
    public Color getColor( RGB rgb )
    {
        Color color = (Color) colorMap.get( rgb );

        if ( color == null )
        {
            color = new Color( Display.getCurrent(), rgb );
            colorMap.put( rgb, color );
        }

        return color;
    }
}
