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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
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
    /** Default color (black) */
    public static final RGB DEFAULT_COLOR = new RGB( 0, 0, 0 );

    /** Default color (blue) for keywords */
    public static final RGB KEYWORD_COLOR = new RGB( 128, 128, 160 );

    /** Default color (red) for unsupported elements */
    public static final RGB UNSUPPORTED_COLOR = new RGB( 255, 0, 0 );

    /** cached map */
    protected Map<RGB, Color> colorMap = new HashMap<RGB, Color>( 5 );

    /** singleton pattern */
    private static final ColorManager INSTANCE = new ColorManager();

    private ColorManager()
    {
        // nop
    }

    /**
     * @return a new instance of <code>ColorManager</code>
     */
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
     * @return the color object for the rgb object
     */
    public Color getColor( RGB rgb )
    {
        Color color = colorMap.get( rgb );

        if ( color == null )
        {
            color = new Color( Display.getCurrent(), rgb );
            colorMap.put( rgb, color );
        }

        return color;
    }

    /**
     * Default Colors for Doxia Sink events
     *
     * @see Sink
     */
    public interface SinkColor
    {
        /**
         * Blue, RGB( 0, 0, 255 )
         *
         * @see Sink#anchor(String)
         * @see Sink#anchor(SinkEventAttributes)
         * @see Sink#anchor_()
         */
        Color ANCHOR_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 255 ) );

        /**
         * @see Sink#author()
         * @see Sink#author(SinkEventAttributes)
         * @see Sink#author_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color AUTHOR_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#body()
         * @see Sink#body(SinkEventAttributes)
         * @see Sink#body_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color BODY_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#bold()
         * @see Sink#bold(SinkEventAttributes)
         * @see Sink#bold_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color BOLD_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Green, RGB( 0, 140, 0 )
         *
         * @see Sink#comment(String)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color COMMENT_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 140, 0 ) );

        /**
         * Green, RGB( 63,127, 95 )
         *
         * @see Sink#date()
         * @see Sink#date(SinkEventAttributes)
         * @see Sink#date_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color DATE_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#definedTerm()
         * @see Sink#definedTerm(SinkEventAttributes)
         * @see Sink#definedTerm_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color DEFINEDTERM_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#definition()
         * @see Sink#definition(SinkEventAttributes)
         * @see Sink#definition_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color DEFINITION_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#definitionList()
         * @see Sink#definitionList(SinkEventAttributes)
         * @see Sink#definitionList_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color DEFINITIONLIST_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#definitionListItem()
         * @see Sink#definitionListItem(SinkEventAttributes)
         * @see Sink#definitionListItem_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color DEFINITIONLISTITEM_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#figure()
         * @see Sink#figure(SinkEventAttributes)
         * @see Sink#figure_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color FIGURE_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#figureCaption()
         * @see Sink#figureCaption(SinkEventAttributes)
         * @see Sink#figureCaption_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color FIGURECAPTION_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Blue, RGB( 0, 0, 255 )
         *
         * @see Sink#figureGraphics(String)
         * @see Sink#figureGraphics(String, SinkEventAttributes)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color FIGUREGRAPHICS_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 255 ) );

        /**
         * Violet, RGB( 139, 38, 201 )
         *
         * @see Sink#head()
         * @see Sink#head(SinkEventAttributes)
         * @see Sink#head_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color HEAD_COLOR = ColorManager.getInstance().getColor( new RGB( 139, 38, 201 ) );

        /**
         * Grey, RGB( 192, 192, 192 )
         *
         * @see Sink#horizontalRule()
         * @see Sink#horizontalRule(SinkEventAttributes)
         */
        Color HORIZONTALRULE_COLOR = ColorManager.getInstance().getColor( new RGB( 192, 192, 192 ) );

        /**
         * @see Sink#italic()
         * @see Sink#italic(SinkEventAttributes)
         * @see Sink#italic_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color ITALIC_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#lineBreak()
         * @see Sink#lineBreak(SinkEventAttributes)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color LINEBREAK_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Blue, RGB( 0, 0, 255 )
         *
         * @see Sink#link(String)
         * @see Sink#link(String, SinkEventAttributes)
         * @see Sink#link_()
         */
        Color LINK_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 255 ) );

        /**
         * @see Sink#list()
         * @see Sink#list(SinkEventAttributes)
         * @see Sink#list_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color LIST_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#listItem()
         * @see Sink#listItem(SinkEventAttributes)
         * @see Sink#listItem_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color LISTITEM_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#monospaced()
         * @see Sink#monospaced(SinkEventAttributes)
         * @see Sink#monospaced_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color MONOSPACED_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#nonBreakingSpace()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color NONBREAKINGSPACE_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#numberedList(int)
         * @see Sink#numberedList(int, SinkEventAttributes)
         * @see Sink#numberedList_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color NUMBEREDLIST_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#numberedListItem()
         * @see Sink#numberedListItem(SinkEventAttributes)
         * @see Sink#numberedListItem_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color NUMBEREDLISTITEM_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#pageBreak()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color PAGEBREAK_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#paragraph()
         * @see Sink#paragraph(SinkEventAttributes)
         * @see Sink#paragraph_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color PARAGRAPH_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section(int, SinkEventAttributes)
         * @see Sink#section_(int)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section1()
         * @see Sink#section1_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION1_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section2()
         * @see Sink#section2_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION2_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section3()
         * @see Sink#section3_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION3_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section4()
         * @see Sink#section4_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION4_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#section5()
         * @see Sink#section5_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTION5_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle()
         * @see Sink#sectionTitle(int, SinkEventAttributes)
         * @see Sink#sectionTitle_()
         * @see Sink#sectionTitle_(int)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle1()
         * @see Sink#sectionTitle1_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE1_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle2()
         * @see Sink#sectionTitle2_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE2_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle3()
         * @see Sink#sectionTitle3_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE3_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle4()
         * @see Sink#sectionTitle4_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE4_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * @see Sink#sectionTitle5()
         * @see Sink#sectionTitle5_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color SECTIONTITLE5_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Blue, RGB( 0, 0, 150 )
         *
         * @see Sink#table()
         * @see Sink#table(SinkEventAttributes)
         * @see Sink#table_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TABLE_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 150 ) );

        /**
         * @see Sink#tableCaption()
         * @see Sink#tableCaption(SinkEventAttributes)
         * @see Sink#tableCaption_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TABLECAPTION_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Blue, RGB( 0, 0, 150 )
         *
         * @see Sink#tableCell()
         * @see Sink#tableCell(SinkEventAttributes)
         * @see Sink#tableCell_()
         * @see Sink#tableCell(String)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TABLECELL_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 150 ) );

        /**
         * Blue, RGB( 0, 0, 150 )
         *
         * @see Sink#tableHeaderCell()
         * @see Sink#tableHeaderCell(SinkEventAttributes)
         * @see Sink#tableHeaderCell_()
         * @see Sink#tableHeaderCell(String)
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TABLEHEADERCELL_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 150 ) );

        /**
         * Blue, RGB( 0, 0, 150 )
         *
         * @see Sink#tableRow()
         * @see Sink#tableRow(SinkEventAttributes)
         * @see Sink#tableRow_()
         * @see Sink#tableRows(int[], boolean)
         * @see Sink#tableRows_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TABLEROW_COLOR = ColorManager.getInstance().getColor( new RGB( 0, 0, 150 ) );

        /**
         * @see Sink#title()
         * @see Sink#title(SinkEventAttributes)
         * @see Sink#title_()
         * @see ColorManager#DEFAULT_COLOR
         */
        Color TITLE_COLOR = ColorManager.getInstance().getColor( ColorManager.DEFAULT_COLOR );

        /**
         * Grey, RGB( 100, 100, 100 )
         *
         * @see Sink#verbatim(boolean)
         * @see Sink#verbatim(SinkEventAttributes)
         * @see Sink#verbatim_()
         */
        Color VERBATIM_COLOR = ColorManager.getInstance().getColor( new RGB( 100, 100, 100 ) );
    }
}
