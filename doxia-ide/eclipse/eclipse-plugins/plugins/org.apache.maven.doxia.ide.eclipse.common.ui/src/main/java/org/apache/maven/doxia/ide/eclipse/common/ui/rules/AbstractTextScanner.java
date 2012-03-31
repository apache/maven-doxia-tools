package org.apache.maven.doxia.ide.eclipse.common.ui.rules;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.ide.eclipse.common.ui.ColorManager;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractTextScanner
    extends BufferedRuleBasedScanner
{
    /** Default token with {@link ColorManager#DEFAULT_COLOR} */
    protected static final Token DEFAULT_TOKEN = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.DEFAULT_COLOR ) ) );

    /** Token for keywords with {@link ColorManager#KEYWORD_COLOR} */
    protected static final Token KEYWORD_TOKEN = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.KEYWORD_COLOR ) ) );

    /** Unsupported token with {@link ColorManager#UNSUPPORTED_COLOR} and bold style */
    protected static final Token UNSUPPORTED_TOKEN = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.UNSUPPORTED_COLOR ), null, SWT.BOLD ) );

    /**
     * Default constructor.
     */
    public AbstractTextScanner()
    {
        Assert.isNotNull( getRules(), "getRules() should be initialized" );

        initialise();
    }

    /**
     * @return the rules implementations.
     */
    public abstract List<IRule> getRules();

    /**
     * @param fDocument the document, not null
     * @param fOffset the current offset
     * @return true if the precedent line from <code>fOffset</code> is empty, false otherwise.
     * @throws BadLocationException if any
     */
    public static boolean isEmptyPrecedentLine( IDocument fDocument, int fOffset )
        throws BadLocationException
    {
        Assert.isNotNull( fDocument );

        int precedentLine = fDocument.getLineOfOffset( fOffset - 2 );
        int startPrecedentLineOffset = fDocument.getLineInformation( precedentLine ).getOffset();
        String line = fDocument.get( startPrecedentLineOffset, fDocument.getLineInformation( precedentLine )
            .getLength() );
        if ( StringUtils.isEmpty( line ) )
        {
            return true;
        }

        return false;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    protected void initialise()
    {
        List<IRule> rules = new ArrayList<IRule>();

        if ( getRules() != null )
        {
            rules.addAll( getRules() );
        }

        setRules( rules.toArray( new IRule[rules.size()] ) );
        setDefaultReturnToken( DEFAULT_TOKEN );
    }

    /**
     * Default Tokens for Doxia Sink events
     *
     * @see ColorManager.SinkColor
     */
    public interface SinkToken
    {
        /**
         * @see Sink#anchor(String)
         * @see Sink#anchor(SinkEventAttributes)
         * @see Sink#anchor_()
         * @see ColorManager.SinkColor#ANCHOR_COLOR
         */
        Token ANCHOR_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.ANCHOR_COLOR ) );

        /**
         * @see Sink#author()
         * @see Sink#author(SinkEventAttributes)
         * @see Sink#author_()
         * @see ColorManager.SinkColor#AUTHOR_COLOR
         */
        Token AUTHOR_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.AUTHOR_COLOR ) );

        /**
         * @see Sink#body()
         * @see Sink#body(SinkEventAttributes)
         * @see Sink#body_()
         * @see ColorManager.SinkColor#BODY_COLOR
         */
        Token BODY_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.BODY_COLOR ) );

        /**
         * @see Sink#bold()
         * @see Sink#bold(SinkEventAttributes)
         * @see Sink#bold_()
         * @see ColorManager.SinkColor#BOLD_COLOR
         */
        Token BOLD_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.BOLD_COLOR, null, SWT.BOLD ) );

        /**
         * @see Sink#comment(String)
         * @see ColorManager.SinkColor#COMMENT_COLOR
         */
        Token COMMENT_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.COMMENT_COLOR ) );

        /**
         * @see Sink#date()
         * @see Sink#date(SinkEventAttributes)
         * @see Sink#date_()
         * @see ColorManager.SinkColor#DATE_COLOR
         */
        Token DATE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.DATE_COLOR ) );

        /**
         * @see Sink#definedTerm()
         * @see Sink#definedTerm(SinkEventAttributes)
         * @see Sink#definedTerm_()
         * @see ColorManager.SinkColor#DEFINEDTERM_COLOR
         */
        Token DEFINEDTERM_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.DEFINEDTERM_COLOR, null, SWT.BOLD  ) );

        /**
         * @see Sink#definition()
         * @see Sink#definition(SinkEventAttributes)
         * @see Sink#definition_()
         * @see ColorManager.SinkColor#DEFINITION_COLOR
         */
        Token DEFINITION_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.DEFINITION_COLOR, null, SWT.BOLD ) );

        /**
         * @see Sink#definitionList()
         * @see Sink#definitionList(SinkEventAttributes)
         * @see Sink#definitionList_()
         * @see ColorManager.SinkColor#DEFINITIONLIST_COLOR
         */
        Token DEFINITIONLIST_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.DEFINITIONLIST_COLOR, null, SWT.BOLD  ) );

        /**
         * @see Sink#definitionListItem()
         * @see Sink#definitionListItem(SinkEventAttributes)
         * @see Sink#definitionListItem_()
         * @see ColorManager.SinkColor#DEFINITIONLISTITEM_COLOR
         */
        Token DEFINITIONLISTITEM_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.DEFINITIONLISTITEM_COLOR, null, SWT.BOLD  ) );

        /**
         * @see Sink#figure()
         * @see Sink#figure(SinkEventAttributes)
         * @see Sink#figure_()
         * @see ColorManager.SinkColor#FIGURE_COLOR
         */
        Token FIGURE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.FIGURE_COLOR ) );

        /**
         * @see Sink#figureCaption()
         * @see Sink#figureCaption(SinkEventAttributes)
         * @see Sink#figureCaption_()
         * @see ColorManager.SinkColor#FIGURECAPTION_COLOR
         */
        Token FIGURECAPTION_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.FIGURECAPTION_COLOR ) );

        /**
         * @see Sink#figureGraphics(String)
         * @see Sink#figureGraphics(String, SinkEventAttributes)
         * @see ColorManager.SinkColor#FIGUREGRAPHICS_COLOR
         */
        Token FIGUREGRAPHICS_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.FIGUREGRAPHICS_COLOR ) );

        /**
         * @see Sink#head()
         * @see Sink#head(SinkEventAttributes)
         * @see Sink#head_()
         * @see ColorManager.SinkColor#HEAD_COLOR
         */
        Token HEAD_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.HEAD_COLOR ) );

        /**
         * @see Sink#horizontalRule()
         * @see Sink#horizontalRule(SinkEventAttributes)
         * @see ColorManager.SinkColor#HORIZONTALRULE_COLOR
         */
        Token HORIZONTALRULE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.HORIZONTALRULE_COLOR, null,
                                                                   SWT.BOLD ) );

        /**
         * @see Sink#italic()
         * @see Sink#italic(SinkEventAttributes)
         * @see Sink#italic_()
         * @see ColorManager.SinkColor#ITALIC_COLOR
         */
        Token ITALIC_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.ITALIC_COLOR, null, SWT.ITALIC ) );

        /**
         * @see Sink#lineBreak()
         * @see Sink#lineBreak(SinkEventAttributes)
         * @see ColorManager.SinkColor#LINEBREAK_COLOR
         */
        Token LINEBREAK_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.LINEBREAK_COLOR ) );

        /**
         * @see Sink#link(String)
         * @see Sink#link(String, SinkEventAttributes)
         * @see Sink#link_()
         * @see ColorManager.SinkColor#LINK_COLOR
         */
        Token LINK_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.LINK_COLOR ) );

        /**
         * @see Sink#list()
         * @see Sink#list(SinkEventAttributes)
         * @see Sink#list_()
         * @see ColorManager.SinkColor#LIST_COLOR
         */
        Token LIST_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.LIST_COLOR ) );

        /**
         * @see Sink#listItem()
         * @see Sink#listItem(SinkEventAttributes)
         * @see Sink#listItem_()
         * @see ColorManager.SinkColor#LISTITEM_COLOR
         */
        Token LISTITEM_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.LISTITEM_COLOR, null, SWT.BOLD ) );

        /**
         * @see Sink#monospaced()
         * @see Sink#monospaced(SinkEventAttributes)
         * @see Sink#monospaced_()
         * @see ColorManager.SinkColor#MONOSPACED_COLOR
         */
        Token MONOSPACED_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.MONOSPACED_COLOR, null,
                                                               SWT.NORMAL,
                                                               new Font( Display.getDefault(), "Courier", Display
                                                                   .getDefault().getSystemFont().getFontData()[0]
                                                                   .getHeight(), SWT.NORMAL ) ) );

        /**
         * @see Sink#nonBreakingSpace()
         * @see ColorManager.SinkColor#NONBREAKINGSPACE_COLOR
         */
        Token NONBREAKINGSPACE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.NONBREAKINGSPACE_COLOR ) );

        /**
         * @see Sink#numberedList(int)
         * @see Sink#numberedList(int, SinkEventAttributes)
         * @see Sink#numberedList_()
         * @see ColorManager.SinkColor#NUMBEREDLIST_COLOR
         */
        Token NUMBEREDLIST_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.NUMBEREDLIST_COLOR ) );

        /**
         * @see Sink#numberedListItem()
         * @see Sink#numberedListItem(SinkEventAttributes)
         * @see Sink#numberedListItem_()
         * @see ColorManager.SinkColor#NUMBEREDLISTITEM_COLOR
         */
        Token NUMBEREDLISTITEM_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.NUMBEREDLISTITEM_COLOR,
                                                                     null, SWT.BOLD ) );

        /**
         * @see Sink#pageBreak()
         * @see ColorManager.SinkColor#PAGEBREAK_COLOR
         */
        Token PAGEBREAK_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.PAGEBREAK_COLOR ) );

        /**
         * @see Sink#paragraph()
         * @see Sink#paragraph(SinkEventAttributes)
         * @see Sink#paragraph_()
         * @see ColorManager.SinkColor#PARAGRAPH_COLOR
         */
        Token PARAGRAPH_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.PARAGRAPH_COLOR ) );

        /**
         * @see Sink#section(int, SinkEventAttributes)
         * @see Sink#section_(int)
         * @see ColorManager.SinkColor#SECTION_COLOR
         */
        Token SECTION_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION_COLOR ) );

        /**
         * @see Sink#section1()
         * @see Sink#section1_()
         * @see ColorManager.SinkColor#SECTION1_COLOR
         */
        Token SECTION1_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION1_COLOR ) );

        /**
         * @see Sink#section2()
         * @see Sink#section2_()
         * @see ColorManager.SinkColor#SECTION2_COLOR
         */
        Token SECTION2_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION2_COLOR ) );

        /**
         * @see Sink#section3()
         * @see Sink#section3_()
         * @see ColorManager.SinkColor#SECTION3_COLOR
         */
        Token SECTION3_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION3_COLOR ) );

        /**
         * @see Sink#section4()
         * @see Sink#section4_()
         * @see ColorManager.SinkColor#SECTION4_COLOR
         */
        Token SECTION4_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION4_COLOR ) );

        /**
         * @see Sink#section5()
         * @see Sink#section5_()
         * @see ColorManager.SinkColor#SECTION5_COLOR
         */
        Token SECTION5_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTION5_COLOR ) );

        /**
         * @see Sink#sectionTitle()
         * @see Sink#sectionTitle(int, SinkEventAttributes)
         * @see Sink#sectionTitle_()
         * @see Sink#sectionTitle_(int)
         * @see ColorManager.SinkColor#SECTIONTITLE_COLOR
         */
        Token SECTIONTITLE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE_COLOR, null,
                                                                 SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                     .getDefault().getSystemFont().getFontData()[0]
                                                                     .getName(), Display.getDefault().getSystemFont()
                                                                     .getFontData()[0].getHeight() + 6, SWT.BOLD ) ) );

        /**
         * @see Sink#sectionTitle1()
         * @see Sink#sectionTitle1_()
         * @see ColorManager.SinkColor#SECTIONTITLE1_COLOR
         */
        Token SECTIONTITLE1_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE1_COLOR, null,
                                                                  SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                      .getDefault().getSystemFont().getFontData()[0]
                                                                      .getName(), Display.getDefault().getSystemFont()
                                                                      .getFontData()[0].getHeight() + 5, SWT.BOLD ) ) );

        /**
         * @see Sink#sectionTitle2()
         * @see Sink#sectionTitle2_()
         * @see ColorManager.SinkColor#SECTIONTITLE2_COLOR
         */
        Token SECTIONTITLE2_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE2_COLOR, null,
                                                                  SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                      .getDefault().getSystemFont().getFontData()[0]
                                                                      .getName(), Display.getDefault().getSystemFont()
                                                                      .getFontData()[0].getHeight() + 4, SWT.BOLD ) ) );

        /**
         * @see Sink#sectionTitle3()
         * @see Sink#sectionTitle3_()
         * @see ColorManager.SinkColor#SECTIONTITLE3_COLOR
         */
        Token SECTIONTITLE3_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE3_COLOR, null,
                                                                  SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                      .getDefault().getSystemFont().getFontData()[0]
                                                                      .getName(), Display.getDefault().getSystemFont()
                                                                      .getFontData()[0].getHeight() + 3, SWT.BOLD ) ) );

        /**
         * @see Sink#sectionTitle4()
         * @see Sink#sectionTitle4_()
         * @see ColorManager.SinkColor#SECTIONTITLE4_COLOR
         */
        Token SECTIONTITLE4_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE4_COLOR, null,
                                                                  SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                      .getDefault().getSystemFont().getFontData()[0]
                                                                      .getName(), Display.getDefault().getSystemFont()
                                                                      .getFontData()[0].getHeight() + 2, SWT.BOLD ) ) );

        /**
         * @see Sink#sectionTitle5()
         * @see Sink#sectionTitle5_()
         * @see ColorManager.SinkColor#SECTIONTITLE5_COLOR
         */
        Token SECTIONTITLE5_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.SECTIONTITLE5_COLOR, null,
                                                                  SWT.NORMAL, new Font( Display.getDefault(), Display
                                                                      .getDefault().getSystemFont().getFontData()[0]
                                                                      .getName(), Display.getDefault().getSystemFont()
                                                                      .getFontData()[0].getHeight() + 1, SWT.BOLD ) ) );

        /**
         * @see Sink#table()
         * @see Sink#table(SinkEventAttributes)
         * @see Sink#table_()
         * @see ColorManager.SinkColor#TABLE_COLOR
         */
        Token TABLE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TABLE_COLOR ) );

        /**
         * @see Sink#tableCaption()
         * @see Sink#tableCaption(SinkEventAttributes)
         * @see Sink#tableCaption_()
         * @see ColorManager.SinkColor#TABLECAPTION_COLOR
         */
        Token TABLECAPTION_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TABLECAPTION_COLOR ) );

        /**
         * @see Sink#tableCell()
         * @see Sink#tableCell(SinkEventAttributes)
         * @see Sink#tableCell_()
         * @see Sink#tableCell(String)
         * @see ColorManager.SinkColor#TABLECELL_COLOR
         */
        Token TABLECELL_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TABLECELL_COLOR ) );

        /**
         * @see Sink#tableHeaderCell()
         * @see Sink#tableHeaderCell(SinkEventAttributes)
         * @see Sink#tableHeaderCell_()
         * @see Sink#tableHeaderCell(String)
         * @see ColorManager.SinkColor#TABLEHEADERCELL_COLOR
         */
        Token TABLEHEADERCELL_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TABLEHEADERCELL_COLOR ) );

        /**
         * @see Sink#tableRow()
         * @see Sink#tableRow(SinkEventAttributes)
         * @see Sink#tableRow_()
         * @see Sink#tableRows(int[], boolean)
         * @see Sink#tableRows_()
         * @see ColorManager.SinkColor#TABLEROW_COLOR
         */
        Token TABLEROW_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TABLEROW_COLOR ) );

        /**
         * @see Sink#title()
         * @see Sink#title(SinkEventAttributes)
         * @see Sink#title_()
         * @see ColorManager.SinkColor#TITLE_COLOR
         */
        Token TITLE_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.TITLE_COLOR ) );

        /**
         * @see Sink#verbatim(boolean)
         * @see Sink#verbatim(SinkEventAttributes)
         * @see Sink#verbatim_()
         * @see ColorManager.SinkColor#VERBATIM_COLOR
         */
        Token VERBATIM_TOKEN = new Token( new TextAttribute( ColorManager.SinkColor.VERBATIM_COLOR ) );
    }
}
