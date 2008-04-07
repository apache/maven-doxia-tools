package org.apache.maven.doxia.ide.eclipse.xdoc.ui.wizard;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.wizards.AbstractWizard;
import org.apache.maven.doxia.ide.eclipse.common.ui.wizards.AbstractWizardPage;
import org.apache.maven.doxia.ide.eclipse.xdoc.ui.XdocPlugin;
import org.eclipse.jface.viewers.ISelection;

/**
 * Xdoc wizard.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocWizard
    extends AbstractWizard
{
    @Override
    public AbstractWizardPage getWizardPage( ISelection selection )
    {
        return new XdocWizardPage( selection );
    }

    @Override
    public String getWizardTemplate()
    {
        return "new.xdoc";
    }

    class XdocWizardPage
        extends AbstractWizardPage
    {
        public XdocWizardPage( ISelection selection )
        {
            super( selection );
        }

        @Override
        public String getExtension()
        {
            return XdocPlugin.getDoxiaFormat();
        }
    }
}
