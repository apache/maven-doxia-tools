package org.apache.maven.doxia.ide.eclipse.fml.ui.editor;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.editors.AbstractXmlMultiPageEditorPart;
import org.apache.maven.doxia.ide.eclipse.fml.ui.FmlPlugin;

/**
 * FML editor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see <a href="http://maven.apache.org/doxia/references/fml-format.html">
 * http://maven.apache.org/doxia/references/fml-format.html</a>
 */
public class FmlEditor
    extends AbstractXmlMultiPageEditorPart
{
    public FmlEditor()
    {
        super();
    }

    @Override
    public String getFormat()
    {
        return FmlPlugin.getDoxiaFormat();
    }

    @Override
    public String getEditorId()
    {
        return "org.apache.maven.doxia.ide.eclipse.fml.ui.editor.FmlEditor";
    }
}
