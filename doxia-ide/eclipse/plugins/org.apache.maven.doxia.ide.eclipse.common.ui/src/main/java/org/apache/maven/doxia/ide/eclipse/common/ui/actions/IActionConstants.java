package org.apache.maven.doxia.ide.eclipse.common.ui.actions;

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

import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Doxia key actions.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public interface IActionConstants
    extends ITextEditorActionConstants
{
    /**
     * Name of the standard global action "Bold"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.BoldAction"</code>).
     */
    String BOLD_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.BoldAction";

    /**
     * Name of the standard global action "Content Assist"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.ContentAssistAction"</code>).
     */
    String CONTENT_ASSIST_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.ContentAssistAction";

    /**
     * Name of the standard global action "Italic"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.ItalicAction"</code>).
     */
    String ITALIC_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.ItalicAction";

    /**
     * Name of the standard global action "link"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.LinkAction"</code>).
     */
    String LINK_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.LinkAction";

    /**
     * Name of the standard global action "Monospaced"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.MonospacedAction"</code>).
     */
    String MONOSPACED_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.MonospacedAction";

    /**
     * Name of the standard global action "table"
     * (value <code>"org.apache.maven.doxia.ide.eclipse.common.ui.actions.TableAction"</code>).
     */
    String TABLE_ACTION = "org.apache.maven.doxia.ide.eclipse.common.ui.actions.TableAction";
}
