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
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
    protected static final Color KEYWORD_COLOR = ColorManager.getInstance().getColor( ColorManager.KEYWORD );

    protected static final Color COMMENT_COLOR = ColorManager.getInstance().getColor( ColorManager.COMMENT );

    protected static final Color STRING_COLOR = ColorManager.getInstance().getColor( ColorManager.STRING );

    protected static final IToken keywordToken = new Token( new TextAttribute( KEYWORD_COLOR, null, SWT.BOLD ) );

    protected static final IToken commentToken = new Token( new TextAttribute( COMMENT_COLOR ) );

    protected static final IToken stringToken = new Token( new TextAttribute( STRING_COLOR ) );

    protected static final IToken linkToken = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.LINK ) ) );

    protected static final IToken monospacedToken = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.STRING ), null, SWT.NORMAL, new Font( Display.getDefault(), "Courier", Display
        .getDefault().getSystemFont().getFontData()[0].getHeight(), SWT.NORMAL ) ) );

    protected static final IToken boldToken = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.STRING ), null, SWT.BOLD ) );

    protected static final IToken italicToken = new Token( new TextAttribute( ColorManager.getInstance()
        .getColor( ColorManager.STRING ), null, SWT.ITALIC ) );

    protected static final Token otherToken = new Token( null );

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

        setRules( (IRule[]) rules.toArray( new IRule[rules.size()] ) );
        setDefaultReturnToken( new Token( new TextAttribute( STRING_COLOR ) ) );
    }
}
