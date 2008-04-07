package org.apache.maven.doxia.ide.eclipse.apt.ui.editor;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.doxia.ide.eclipse.apt.ui.AptPlugin;
import org.apache.maven.doxia.ide.eclipse.apt.ui.editor.AptDocumentProvider.AptPartitionScanner;
import org.apache.maven.doxia.ide.eclipse.common.ui.ColorManager;
import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractBoldAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractItalicAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractLinkAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractMonospacedAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractTableAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.IActionConstants;
import org.apache.maven.doxia.ide.eclipse.common.ui.contentassist.AbstractContentAssistProcessor;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddLinkDialog.Link;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddTableDialog.Table;
import org.apache.maven.doxia.ide.eclipse.common.ui.editors.AbstractTextMultiPageEditorPart;
import org.apache.maven.doxia.ide.eclipse.common.ui.editors.text.AbstractTextEditor;
import org.apache.maven.doxia.ide.eclipse.common.ui.editors.text.AbstractTextSourceViewerConfiguration;
import org.apache.maven.doxia.ide.eclipse.common.ui.rules.AbstractTextScanner;
import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.module.apt.AptMarkup;
import org.apache.maven.doxia.module.apt.AptSink;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * APT editor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see <a href="http://maven.apache.org/doxia/references/apt-format.html">http://maven.apache.org/doxia/references/apt-format.html</a>
 */
public class AptEditor
    extends AbstractTextMultiPageEditorPart
{
    private TextEditor editor;

    @Override
    public TextEditor getTextEditor()
    {
        if ( editor != null )
        {
            return editor;
        }

        editor = new AptTextEditor();
        editor.setAction( IActionConstants.BOLD_ACTION, new AbstractBoldAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return AptSink.BOLD_START_MARKUP;
            }

            @Override
            public String getEndMarkup()
            {
                return AptSink.BOLD_END_MARKUP;
            }
        } );
        editor.setAction( IActionConstants.ITALIC_ACTION, new AbstractItalicAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return AptSink.ITALIC_START_MARKUP;
            }

            @Override
            public String getEndMarkup()
            {
                return AptSink.ITALIC_END_MARKUP;
            }
        } );
        editor.setAction( IActionConstants.MONOSPACED_ACTION, new AbstractMonospacedAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return AptSink.MONOSPACED_START_MARKUP;
            }

            @Override
            public String getEndMarkup()
            {
                return AptSink.MONOSPACED_END_MARKUP;
            }
        } );
        editor.setAction( IActionConstants.LINK_ACTION, new AbstractLinkAction( editor )
        {
            @Override
            protected String generateLink( Link link )
            {
                if ( StringUtils.isEmpty( link.getName() ) )
                {
                    return "{{" + link.getURL() + "}}";
                }

                return "{{{" + link.getURL() + "}" + link.getName() + "}}";
            }
        } );
        editor.setAction( IActionConstants.TABLE_ACTION, new AbstractTableAction( editor )
        {
            @Override
            protected String generateTable( Table table )
            {
                StringBuffer sb = new StringBuffer();

                for ( int i = 0; i < table.getRows(); i++ )
                {
                    sb.append( AptMarkup.TABLE_ROW_START_MARKUP );

                    for ( int j = 0; j < table.getColumns(); j++ )
                    {
                        String text;
                        switch ( table.getTableStyle() )
                        {
                            case Table.TABLE_STYLE_CENTERED:
                                text = AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP;
                                break;

                            case Table.TABLE_STYLE_LEFTALIGNED:
                                text = AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP;
                                break;

                            case Table.TABLE_STYLE_RIGHTALIGNED:
                                text = AptMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP;
                                break;

                            default:
                                text = AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP;
                                break;
                        }
                        sb.append( text );
                        if ( j != table.getColumns() - 1 )
                        {
                            sb.append( "----" );
                        }
                    }

                    sb.append( "" );
                    sb.append( Markup.EOL );

                    for ( int j = 0; j < table.getColumns(); j++ )
                    {
                        sb.append( AptMarkup.TABLE_ROW_SEPARATOR_MARKUP );
                        sb.append( " " + DEFAULT_CELL_TEXT + " " );
                        if ( j == table.getColumns() - 1 )
                        {
                            sb.append( AptMarkup.TABLE_ROW_SEPARATOR_MARKUP );
                        }
                    }

                    sb.append( Markup.EOL );
                }

                sb.append( AptMarkup.TABLE_ROW_START_MARKUP );
                for ( int j = 0; j < table.getColumns(); j++ )
                {
                    String text;
                    switch ( table.getTableStyle() )
                    {
                        case Table.TABLE_STYLE_CENTERED:
                            text = AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP;
                            break;

                        case Table.TABLE_STYLE_LEFTALIGNED:
                            text = AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP;
                            break;

                        case Table.TABLE_STYLE_RIGHTALIGNED:
                            text = AptMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP;
                            break;

                        default:
                            text = AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP;
                            break;
                    }
                    sb.append( text );
                    if ( j != table.getColumns() - 1 )
                    {
                        sb.append( "----" );
                    }
                }
                sb.append( Markup.EOL );

                if ( StringUtils.isNotEmpty( table.getCaption() ) )
                {
                    sb.append( table.getCaption() );
                    sb.append( Markup.EOL );
                }

                return sb.toString();
            }
        } );

        return editor;
    }

    @Override
    public String getFormat()
    {
        return AptPlugin.getDoxiaFormat();
    }

    class AptTextEditor
        extends AbstractTextEditor
    {
        @Override
        public String getEditorId()
        {
            return "org.apache.maven.doxia.ide.eclipse.apt.ui.editor.AptEditor";
        }

        @Override
        public IDocumentProvider getFileDocumentProvider()
        {
            return new AptDocumentProvider();
        }

        @Override
        public TextSourceViewerConfiguration getTextSourceViewerConfiguration()
        {
            return new AptSourceViewerConfiguration();
        }
    }

    class AptSourceViewerConfiguration
        extends AbstractTextSourceViewerConfiguration
    {
        public AptSourceViewerConfiguration()
        {
            super();
        }

        @Override
        protected RuleBasedScanner getScanner()
        {
            return new AptScanner();
        }

        @Override
        public IHyperlinkDetector[] getHyperlinkDetectors( ISourceViewer sourceViewer )
        {
            return new IHyperlinkDetector[] { new AptURLHyperlinkDetector() };
        }

        @Override
        public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
        {
            ContentAssistant assistant = new ContentAssistant();
            assistant.setDocumentPartitioning( getConfiguredDocumentPartitioning( sourceViewer ) );

            IContentAssistProcessor processor = new AptContentAssistProcessor();
            assistant.setContentAssistProcessor( processor, IDocument.DEFAULT_CONTENT_TYPE );
            assistant.setContentAssistProcessor( processor, AptPartitionScanner.DOXIA_PARTITION_CONTENT );

            assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
            assistant.setInformationControlCreator( getInformationControlCreator( sourceViewer ) );

            return assistant;
        }

        /**
         * Based on <code>URLHyperlinkDetector</code> with a small fix in the <code>tokenizer</code> implementation
         * to handle as well <code>{</code> and <code>}</code>.
         *
         * @see org.eclipse.jface.text.hyperlink.URLHyperlinkDetector
         */
        class AptURLHyperlinkDetector
            extends URLHyperlinkDetector
        {

            public AptURLHyperlinkDetector()
            {
            }

            @Override
            public IHyperlink[] detectHyperlinks( ITextViewer textViewer, IRegion region,
                                                  boolean canShowMultipleHyperlinks )
            {
                if ( region == null || textViewer == null )
                    return null;

                IDocument document = textViewer.getDocument();

                int offset = region.getOffset();

                String urlString = null;
                if ( document == null )
                    return null;

                IRegion lineInfo;
                String line;
                try
                {
                    lineInfo = document.getLineInformationOfOffset( offset );
                    line = document.get( lineInfo.getOffset(), lineInfo.getLength() );
                }
                catch ( BadLocationException ex )
                {
                    return null;
                }

                int offsetInLine = offset - lineInfo.getOffset();

                boolean startDoubleQuote = false;
                int urlOffsetInLine = 0;
                int urlLength = 0;

                int urlSeparatorOffset = line.indexOf( "://" ); //$NON-NLS-1$
                while ( urlSeparatorOffset >= 0 )
                {

                    // URL protocol (left to "://")
                    urlOffsetInLine = urlSeparatorOffset;
                    char ch;
                    do
                    {
                        urlOffsetInLine--;
                        ch = ' ';
                        if ( urlOffsetInLine > -1 )
                            ch = line.charAt( urlOffsetInLine );
                        startDoubleQuote = ch == '"';
                    }
                    while ( Character.isUnicodeIdentifierStart( ch ) );
                    urlOffsetInLine++;

                    // Right to "://"
                    // APT FIX
                    StringTokenizer tokenizer = new StringTokenizer( line.substring( urlSeparatorOffset + 3 ),
                                                                     " \t\n\r\f<>{}", false ); //$NON-NLS-1$
                    if ( !tokenizer.hasMoreTokens() )
                        return null;

                    urlLength = tokenizer.nextToken().length() + 3 + urlSeparatorOffset - urlOffsetInLine;
                    if ( offsetInLine >= urlOffsetInLine && offsetInLine <= urlOffsetInLine + urlLength )
                        break;

                    urlSeparatorOffset = line.indexOf( "://", urlSeparatorOffset + 1 ); //$NON-NLS-1$
                }

                if ( urlSeparatorOffset < 0 )
                    return null;

                if ( startDoubleQuote )
                {
                    int endOffset = -1;
                    int nextDoubleQuote = line.indexOf( '"', urlOffsetInLine );
                    int nextWhitespace = line.indexOf( ' ', urlOffsetInLine );
                    if ( nextDoubleQuote != -1 && nextWhitespace != -1 )
                        endOffset = Math.min( nextDoubleQuote, nextWhitespace );
                    else if ( nextDoubleQuote != -1 )
                        endOffset = nextDoubleQuote;
                    else if ( nextWhitespace != -1 )
                        endOffset = nextWhitespace;
                    if ( endOffset != -1 )
                        urlLength = endOffset - urlOffsetInLine;
                }

                // Set and validate URL string
                try
                {
                    urlString = line.substring( urlOffsetInLine, urlOffsetInLine + urlLength );
                    new URL( urlString );
                }
                catch ( MalformedURLException ex )
                {
                    urlString = null;
                    return null;
                }

                IRegion urlRegion = new Region( lineInfo.getOffset() + urlOffsetInLine, urlLength );
                return new IHyperlink[] { new URLHyperlink( urlRegion, urlString ) };
            }
        }
    }

    class AptScanner
        extends AbstractTextScanner
    {
        public AptScanner()
        {
            super();
        }

        @Override
        public List<IRule> getRules()
        {
            List<IRule> rules = new ArrayList<IRule>();

            // TODO add more rules and review color!

            // comment rule
            rules.add( new EndOfLineRule( AptMarkup.COMMENT + "" + AptMarkup.COMMENT, commentToken ) );

            // horizontal rule
            rules.add( new EndOfLineRule( AptMarkup.HORIZONTAL_RULE_MARKUP, new Token( new TextAttribute( ColorManager
                .getInstance().getColor( ColorManager.LINK ) ) ) ) );

            // verbatim rules
            rules.add( new MultiLineRule( AptMarkup.BOXED_VERBATIM_START_MARKUP, AptMarkup.BOXED_VERBATIM_END_MARKUP,
                                          new Token( new TextAttribute( ColorManager.getInstance()
                                              .getColor( ColorManager.STRING ) ) ) ) );
            rules.add( new MultiLineRule( AptMarkup.NON_BOXED_VERBATIM_START_MARKUP,
                                          AptMarkup.NON_BOXED_VERBATIM_END_MARKUP,
                                          new Token( new TextAttribute( ColorManager.getInstance()
                                              .getColor( ColorManager.STRING ) ) ) ) );

            // monospaced rule
            rules.add( new SingleLineRule( AptMarkup.MONOSPACED_START_MARKUP, AptMarkup.MONOSPACED_END_MARKUP,
                                           monospacedToken ) );

            // bold rule
            rules.add( new SingleLineRule( AptMarkup.BOLD_START_MARKUP, AptMarkup.BOLD_END_MARKUP, boldToken ) );

            // italic rule
            rules.add( new SingleLineRule( AptMarkup.ITALIC_START_MARKUP, AptMarkup.ITALIC_END_MARKUP, italicToken ) );

            // link rule
            rules.add( new SingleLineRule( "{{{", "}}", linkToken ) );
            rules.add( new SingleLineRule( "{{", "}}", linkToken ) );
            rules.add( new SingleLineRule( "{", "}", linkToken ) );

            // escaped character rule
            rules.add( new EscapedCharacterRule( new Token( new TextAttribute( ColorManager.getInstance()
                .getColor( ColorManager.STRING ) ) ) ) );

            return rules;
        }

        class EscapedCharacterRule
            implements IRule
        {
            /**
             * The associated token.
             */
            private IToken token;

            /**
             * Constructor.
             *
             * @param token the associated token
             */
            public EscapedCharacterRule( IToken token )
            {
                this.token = token;
            }

            /**
             * @param character Character to determine whether it is an escaped character
             * @return <code>true</code> if the character is a escaped char, <code>false</code> otherwise.
             */
            boolean isEscapedCharacter( char character )
            {
                /** APT separators */
                char[] APT_ESCAPED_CHARACTER = { '~', '=', '-', '+', '*', '[', ']', '<', '>', '{', '}', '\\' };

                for ( int index = 0; index < APT_ESCAPED_CHARACTER.length; index++ )
                {
                    if ( APT_ESCAPED_CHARACTER[index] == character )
                    {
                        return true;
                    }
                }

                return false;
            }

            /** {@inheritDoc} */
            public synchronized IToken evaluate( ICharacterScanner scanner )
            {
                char character = (char) scanner.read();
                if ( character == '\\' )
                {
                    character = (char) scanner.read();
                    if ( isEscapedCharacter( character ) )
                    {
                        do
                        {
                            character = (char) scanner.read();
                        }
                        while ( isEscapedCharacter( character ) );

                        scanner.unread();

                        return token;
                    }
                    else
                    {
                        scanner.unread();

                        return Token.UNDEFINED;
                    }
                }

                scanner.unread();

                return Token.UNDEFINED;
            }
        }
    }

    class AptContentAssistProcessor
        extends AbstractContentAssistProcessor
    {
        public AptContentAssistProcessor()
        {
            super();
        }

        @Override
        public Image[] getImageMarkups()
        {
            return new Image[] {
                CommonPlugin.getImage( CommonPlugin.IMG_ITALIC ),
                CommonPlugin.getImage( CommonPlugin.IMG_BOLD ),
                CommonPlugin.getImage( CommonPlugin.IMG_MONOSPACED ),
                CommonPlugin.getImage( CommonPlugin.IMG_LINK ),
                CommonPlugin.getImage( CommonPlugin.IMG_LINK ),
                CommonPlugin.getImage( CommonPlugin.IMG_LINK ) };
        }

        @Override
        public String[] getStartMarkups()
        {
            return new String[] { "<", "<<", "<<<", "{", "{{", "{{{" };
        }

        @Override
        public String[] getEndMarkups()
        {
            return new String[] { ">", ">>", ">>>", "}", "}}", "}}}" };
        }
    }
}
