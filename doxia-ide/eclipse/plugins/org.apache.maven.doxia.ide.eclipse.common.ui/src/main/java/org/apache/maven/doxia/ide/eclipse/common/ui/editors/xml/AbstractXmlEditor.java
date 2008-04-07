package org.apache.maven.doxia.ide.eclipse.common.ui.editors.xml;

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

import org.eclipse.core.runtime.Assert;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * Doxia editor for xml file resources.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractXmlEditor
    extends StructuredTextEditor
{
    /**
     * Default constructor.
     */
    public AbstractXmlEditor()
    {
        super();

        Assert.isNotNull( getEditorId(), "getEditorId() should be defined" );
    }

    @Override
    protected void initializeEditor()
    {
        super.initializeEditor();

        // TODO Doesn't work yet: needs WSTP M6
        // see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=212330
        //      https://bugs.eclipse.org/bugs/show_bug.cgi?id=224040
        setEditorContextMenuId( getEditorId() + ".EditorContext" );
        setRulerContextMenuId( getEditorId() + ".RulerContext" );
    }

    /**
     * @return The ID of this editor like should be defined in plugin.xml
     */
    public abstract String getEditorId();
}
