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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.doxia.ide.eclipse.common.ui.editors.text.AbstractTextEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Doxia scanner for text file resources.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see AbstractTextEditor
 */
public abstract class AbstractTextPartitionScanner
    extends RuleBasedPartitionScanner
{
    /** Doxia partition content */
    public static final String DOXIA_PARTITION_CONTENT = "__partition_content";

    /** Doxia partition comment */
    public static final String DOXIA_PARTITION_COMMENT = "__partition_comment";

    /** Doxia legal content types of this partitioner */
    public static final String[] DOXIA_LEGAL_CONTENT_TYPES = {
        IDocument.DEFAULT_CONTENT_TYPE,
        DOXIA_PARTITION_CONTENT,
        DOXIA_PARTITION_COMMENT };

    /** Partition token */
    protected static final IToken PARTITION_CONTENT_TOKEN = new Token( DOXIA_PARTITION_CONTENT );

    /** Comment token */
    protected static final IToken PARTITION_COMMENT_TOKEN = new Token( DOXIA_PARTITION_COMMENT );

    /**
     * Default constructor.
     *
     * @see #initialise()
     */
    public AbstractTextPartitionScanner()
    {
        Assert.isNotNull( getRules(), "getRules() should be initialized" );
        initialise();
    }

    /**
     * @return the rules implementation.
     */
    public abstract List<IRule> getRules();

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    protected void initialise()
    {
        List<IRule> rules = new LinkedList<IRule>();

        if ( getRules() != null )
        {
            rules.addAll( getRules() );
        }

        setPredicateRules( (IPredicateRule[]) rules.toArray( new IPredicateRule[rules.size()] ) );
    }
}
