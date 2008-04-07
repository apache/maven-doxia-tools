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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.doxia.ide.eclipse.common.ui.editors.text.AbstractTextDocumentProvider;
import org.apache.maven.doxia.ide.eclipse.common.ui.rules.AbstractTextPartitionScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IRule;

/**
 * Confluence document provider.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class ConfluenceDocumentProvider
    extends AbstractTextDocumentProvider
{
    @Override
    public IPartitionTokenScanner getScanner()
    {
        return new ConfluencePartitionScanner();
    }

    class ConfluencePartitionScanner
        extends AbstractTextPartitionScanner
    {
        public ConfluencePartitionScanner()
        {
            super();
        }

        @Override
        public List<IRule> getRules()
        {
            List<IRule> rules = new LinkedList<IRule>();

            return rules;
        }
    }
}
