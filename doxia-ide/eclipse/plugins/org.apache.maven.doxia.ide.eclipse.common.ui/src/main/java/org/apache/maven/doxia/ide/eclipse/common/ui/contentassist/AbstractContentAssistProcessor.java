package org.apache.maven.doxia.ide.eclipse.common.ui.contentassist;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

/**
 * Abstract class for the content assist processor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractContentAssistProcessor
    implements IContentAssistProcessor
{
    private final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];

    /**
     * Default constructor.
     */
    public AbstractContentAssistProcessor()
    {
        super();

        Assert.isNotNull( getStartMarkups(), "getStartMarkups() should be defined" );
        Assert.isNotNull( getEndMarkups(), "getEndMarkups() should be defined" );
        Assert.isNotNull( getImageMarkups(), "getImageMarkups() should be defined" );
        if ( ( getStartMarkups().length != getEndMarkups().length )
            && ( getStartMarkups().length != getImageMarkups().length ) )
        {
            Assert.isTrue( true, "getStartMarkups(), getEndMarkups(), getImageMarkups() have not the same size" );
        }
    }

    /** {@inheritDoc} */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        IDocument document = viewer.getDocument();

        String prefix = lastWord( document, offset );
        String startTag = getStartTag( document, offset, prefix );
        List<CompletionProposal> result = new ArrayList<CompletionProposal>();

        if ( startTag == null )
        {
            for ( int i = 0; i < getStartMarkups().length; i++ )
            {
                result.add( new CompletionProposal( prefix + getStartMarkups()[i], offset - prefix.length(), prefix
                    .length(), prefix.length() + getStartMarkups()[i].length(), getImageMarkups()[i],
                                                    getStartMarkups()[i], null, null ) );
            }
        }
        else
        {
            // Autocomplete for closing tags
            int closingTagId = -1;
            for ( int i = 0; i < getStartMarkups().length; i++ )
            {
                if ( getStartMarkups()[i].equals( startTag ) )
                {
                    closingTagId = i;
                }
            }

            if ( closingTagId != -1 )
            {
                result.add( new CompletionProposal( prefix + getEndMarkups()[closingTagId], offset - prefix.length(),
                                                    prefix.length(), prefix.length()
                                                        + getEndMarkups()[closingTagId].length(),
                                                    getImageMarkups()[closingTagId], getEndMarkups()[closingTagId],
                                                    null, null ) );

            }

            // Autocomplete for same tags
            for ( int i = 0; i < getStartMarkups().length; i++ )
            {
                if ( getStartMarkups()[i].startsWith( startTag ) && !getStartMarkups()[i].equals( startTag ) )
                {
                    result.add( new CompletionProposal( getStartMarkups()[i], offset - prefix.length(),
                                                        prefix.length(), getStartMarkups()[i].length(),
                                                        getImageMarkups()[i], getStartMarkups()[i], null, null ) );
                }
            }
        }

        return (ICompletionProposal[]) result.toArray( new ICompletionProposal[result.size()] );
    }

    /** {@inheritDoc} */
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return NO_CONTEXTS;
    }

    /** {@inheritDoc} */
    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getErrorMessage()
    {
        return null;
    }

    /** {@inheritDoc} */
    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }

    /** {@inheritDoc} */
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

    public abstract String[] getStartMarkups();

    public abstract String[] getEndMarkups();

    public abstract Image[] getImageMarkups();

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    protected String lastWord( IDocument doc, int offset )
    {
        try
        {
            int startPart = doc.getPartition( offset ).getOffset();
            String prefix = doc.get( startPart, offset - startPart );

            return stripLastWord( prefix );
        }
        catch ( BadLocationException e )
        {
            CommonPlugin.logError( "BadLocationException: " + e.getMessage(), e );
        }

        return "";
    }

    protected String getStartTag( IDocument doc, int offset, String prefix )
    {
        String startTag = null;
        for ( int i = getStartMarkups().length - 1; i >= 0; i-- )
        {
            if ( prefix.indexOf( getEndMarkups()[i] ) != -1 )
            {
                prefix = StringUtils.replace( prefix, getStartMarkups()[i], "" );
            }
        }

        for ( int i = getStartMarkups().length - 1; i >= 0; i-- )
        {
            if ( prefix.indexOf( getStartMarkups()[i] ) != -1 )
            {
                return getStartMarkups()[i];
            }
        }

        return startTag;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private static String stripLastWord( String prefix )
    {
        if ( StringUtils.isEmpty( prefix ) )
        {
            return prefix;
        }

        if ( Character.isWhitespace( prefix.charAt( prefix.length() - 1 ) ) )
        {
            return "";
        }
        else
        {
            char[] c = prefix.toCharArray();
            int start = 0;
            for ( int i = c.length - 1; i >= 0; i-- )
            {
                if ( Character.isWhitespace( c[i] ) )
                {
                    start = i + 1;
                    break;
                }
            }

            return prefix.substring( start, prefix.length() );
        }
    }
}
