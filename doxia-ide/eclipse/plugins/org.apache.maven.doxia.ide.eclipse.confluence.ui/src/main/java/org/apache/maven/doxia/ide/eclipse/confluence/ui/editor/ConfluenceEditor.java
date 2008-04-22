package org.apache.maven.doxia.ide.eclipse.confluence.ui.editor;

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
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

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
import org.apache.maven.doxia.ide.eclipse.confluence.ui.ConfluencePlugin;
import org.apache.maven.doxia.ide.eclipse.confluence.ui.editor.ConfluenceDocumentProvider.ConfluencePartitionScanner;
import org.apache.maven.doxia.markup.Markup;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
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
 * Confluence editor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see <a href="http://confluence.atlassian.com/renderer/notationhelp.action?section=all">
 *      http://confluence.atlassian.com/renderer/notationhelp.action?section=all</a>
 */
public class ConfluenceEditor
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

        editor = new ConfluenceTextEditor();
        editor.setAction( IActionConstants.BOLD_ACTION, new AbstractBoldAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "*";
            }

            @Override
            public String getEndMarkup()
            {
                return "*";
            }
        } );
        editor.setAction( IActionConstants.ITALIC_ACTION, new AbstractItalicAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "_";
            }

            @Override
            public String getEndMarkup()
            {
                return "_";
            }
        } );
        editor.setAction( IActionConstants.MONOSPACED_ACTION, new AbstractMonospacedAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "{{";
            }

            @Override
            public String getEndMarkup()
            {
                return "}}";
            }
        } );
        editor.setAction( IActionConstants.LINK_ACTION, new AbstractLinkAction( editor )
        {
            @Override
            protected String generateLink( Link link )
            {
                if ( StringUtils.isEmpty( link.getName() ) )
                {
                    return "[" + link.getURL() + "]";
                }

                return "[[" + link.getName() + "|" + link.getURL() + "]]";
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
                    sb.append( "|" );

                    for ( int j = 0; j < table.getColumns(); j++ )
                    {
                        sb.append( DEFAULT_CELL_TEXT );
                        sb.append( "|" );
                    }
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
        return ConfluencePlugin.getDoxiaFormat();
    }

    class ConfluenceTextEditor
        extends AbstractTextEditor
    {
        @Override
        public String getEditorId()
        {
            return "org.apache.maven.doxia.ide.eclipse.confluence.ui.editor.ConfluenceEditor";
        }

        @Override
        public IDocumentProvider getFileDocumentProvider()
        {
            return new ConfluenceDocumentProvider();
        }

        @Override
        public TextSourceViewerConfiguration getTextSourceViewerConfiguration()
        {
            return new ConfluenceSourceViewerConfiguration();
        }
    }

    class ConfluenceSourceViewerConfiguration
        extends AbstractTextSourceViewerConfiguration
    {
        /**
         * Default constructor.
         */
        public ConfluenceSourceViewerConfiguration()
        {
            super();
        }

        @Override
        protected RuleBasedScanner getScanner()
        {
            return new ConfluenceScanner();
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

            IContentAssistProcessor processor = new ConfluenceContentAssistProcessor();
            assistant.setContentAssistProcessor( processor, IDocument.DEFAULT_CONTENT_TYPE );
            assistant.setContentAssistProcessor( processor, ConfluencePartitionScanner.DOXIA_PARTITION_CONTENT );

            assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
            assistant.setInformationControlCreator( getInformationControlCreator( sourceViewer ) );

            return assistant;
        }

        /**
         * Based on <code>URLHyperlinkDetector</code> with a small fix in the <code>tokenizer</code> implementation
         * to handle as well <code>[</code> and <code>]</code>.
         *
         */
        class AptURLHyperlinkDetector
            extends URLHyperlinkDetector
        {
            /**
             * Default constructor.
             */
            public AptURLHyperlinkDetector()
            {
                super();
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
                                                                     " \t\n\r\f<>[]", false ); //$NON-NLS-1$
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

    class ConfluenceScanner
        extends AbstractTextScanner
    {
        /**
         * Default constructor.
         */
        public ConfluenceScanner()
        {
            super();
        }

        @Override
        public List<IRule> getRules()
        {
            List<IRule> rules = new LinkedList<IRule>();

            // sections title rule
            rules.add( new SectionTitleEndOfLineRule() );

            // horizontal rule
            rules.add( new EndOfLineRule( "----", SinkToken.HORIZONTALRULE_TOKEN ) );

            // monospaced rule
            rules.add( new SingleLineRule( "{{", "}}", SinkToken.MONOSPACED_TOKEN ) );

            // bold rule or list rule
            rules.add( new SingleLineRule( "*", " ", SinkToken.BOLD_TOKEN ) );

            // italic rule
            rules.add( new SingleLineRule( "_", "_", SinkToken.ITALIC_TOKEN ) );

            // link rule
            rules.add( new SingleLineRule( "[", "]", SinkToken.LINK_TOKEN ) );

            // numbered list rule
            rules.add( new SingleLineRule( "#", " ", SinkToken.BOLD_TOKEN ) );

            // definition list rule
            rules.add( new MultiLineRule( "{note", "{note}", SinkToken.DEFINITION_TOKEN ) );
            rules.add( new MultiLineRule( "{quote}", "{quote}", SinkToken.DEFINITION_TOKEN ) );
            rules.add( new MultiLineRule( "{info", "{info}", SinkToken.DEFINITION_TOKEN ) );
            rules.add( new MultiLineRule( "{tip", "{tip}", SinkToken.DEFINITION_TOKEN ) );

            // verbatim rule
            rules.add( new MultiLineRule( "{noformat}", "{noformat}", SinkToken.VERBATIM_TOKEN ) );
            rules.add( new MultiLineRule( "{code", "{code}", SinkToken.VERBATIM_TOKEN ) );

            // table rule
            rules.add( new EndOfLineRule( "|", SinkToken.TABLE_TOKEN ) );

            // TODO better unsupported
            // some text effects unsupported by Doxia
            rules.add( new SingleLineRule( "??", "??", UNSUPPORTED_TOKEN ) );
            rules.add( new SingleLineRule( "-", "-", UNSUPPORTED_TOKEN ) );
            rules.add( new SingleLineRule( "+", "+", UNSUPPORTED_TOKEN ) );
            rules.add( new SingleLineRule( "^", "^", UNSUPPORTED_TOKEN ) );
            rules.add( new SingleLineRule( "~", "~", UNSUPPORTED_TOKEN ) );

            // some other advanced formatting unsupported by Doxia
            rules.add( new MultiLineRule( "{color", "{color}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{panel", "{panel}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{warning", "{warning}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{composition-setup", "{composition-setup}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{float", "{float}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{cloak", "{cloak}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{deck", "{deck}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{card", "{card}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{show-card", "{show-card}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{cache", "{cache}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{beanshell", "{beanshell}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{sql", "{sql}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{slideshow", "{slideshow}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{slide", "{slide}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{rsvp", "{rsvp}", UNSUPPORTED_TOKEN ) );

            // Other Misc unsupported by Doxia
            rules.add( new MultiLineRule( "{vote", "{vote}", UNSUPPORTED_TOKEN ) );
            rules.add( new MultiLineRule( "{survey", "{survey}", UNSUPPORTED_TOKEN ) );

            // escaped character rule
            rules.add( new EscapedCharacterRule( KEYWORD_TOKEN ) );

            return rules;
        }

        class SectionTitleEndOfLineRule
            extends EndOfLineRule
        {
            /**
             * Default constructor.
             */
            public SectionTitleEndOfLineRule()
            {
                super( "h", Token.UNDEFINED );
            }

            @Override
            protected IToken doEvaluate( ICharacterScanner scanner, boolean resume )
            {
                if ( resume )
                {
                    if ( endSequenceDetected( scanner ) )
                    {
                        return fToken;
                    }
                }
                else
                {
                    try
                    {
                        char c = (char) scanner.read();
                        if ( c == 'h' )
                        {
                            // last line should be empty
                            if ( ( isEmptyPrecedentLine( fDocument, fOffset ) ) )
                            {
                                c = (char) scanner.read();
                                if ( Character.isDigit( c ) )
                                {
                                    if ( endSequenceDetected( scanner ) )
                                    {
                                        switch ( c )
                                        {
                                            case '1':
                                                fToken = SinkToken.SECTIONTITLE1_TOKEN;
                                                break;
                                            case '2':
                                                fToken = SinkToken.SECTIONTITLE2_TOKEN;
                                                break;
                                            case '3':
                                                fToken = SinkToken.SECTIONTITLE3_TOKEN;
                                                break;
                                            case '4':
                                                fToken = SinkToken.SECTIONTITLE4_TOKEN;
                                                break;
                                            case '5':
                                                fToken = SinkToken.SECTIONTITLE5_TOKEN;
                                                break;
                                            default:
                                                fToken = Token.UNDEFINED;
                                                break;
                                        }
                                        return fToken;
                                    }
                                }
                            }
                        }
                    }
                    catch ( BadLocationException e )
                    {
                        // nop
                    }
                }

                scanner.unread();
                return Token.UNDEFINED;
            }
        }

        class EscapedCharacterRule
            implements IRule
        {
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

            /** {@inheritDoc} */
            public synchronized IToken evaluate( ICharacterScanner scanner )
            {
                char character = (char) scanner.read();
                if ( character == '\\' )
                {
                    character = (char) scanner.read();
                    if ( Character.isDefined( character ) )
                    {
                        return token;
                    }

                    scanner.unread();

                    return Token.UNDEFINED;
                }

                scanner.unread();

                return Token.UNDEFINED;
            }
        }
    }

    class ConfluenceContentAssistProcessor
        extends AbstractContentAssistProcessor
    {
        public ConfluenceContentAssistProcessor()
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
                CommonPlugin.getImage( CommonPlugin.IMG_LINK ) };
        }

        @Override
        public String[] getStartMarkups()
        {
            return new String[] { "_", "*", "{{", "[[" };
        }

        @Override
        public String[] getEndMarkups()
        {
            return new String[] { "_", "*", "}}", "]]" };
        }
    }
}
