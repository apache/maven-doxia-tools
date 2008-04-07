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

import org.apache.maven.doxia.ide.eclipse.common.ui.CommonPlugin;

/**
 * Delegates to <code>link</code> action.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @see IActionConstants#LINK_ACTION for the action Key
 */
public class LinkActionDelegate
    extends AbstractActionDelegate
{
    public LinkActionDelegate()
    {
        super();
    }

    @Override
    public String getActionId()
    {
        return IActionConstants.LINK_ACTION;
    }

    @Override
    public String getBundleKey()
    {
        return "link";
    }

    @Override
    public String[] getImageDescriptorKey()
    {
        return new String[] { CommonPlugin.IMG_LINK, CommonPlugin.IMG_LINK_DISABLED };
    }
}
