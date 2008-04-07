package org.apache.maven.doxia.ide.eclipse.common.ui.editors;

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

import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractBoldAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractItalicAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractLinkAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractMonospacedAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.AbstractTableAction;
import org.apache.maven.doxia.ide.eclipse.common.ui.actions.IActionConstants;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddLinkDialog.Link;
import org.apache.maven.doxia.ide.eclipse.common.ui.dialogs.AddTableDialog.Table;
import org.apache.maven.doxia.ide.eclipse.common.ui.editors.xml.AbstractXmlEditor;
import org.apache.maven.doxia.markup.Markup;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;

/**
 * Abstract multipage editor for Doxia xml files.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractXmlMultiPageEditorPart
    extends AbstractMultiPageEditorPart
{
    private XmlEditor editor;

    @Override
    public TextEditor getTextEditor()
    {
        if ( editor != null )
        {
            return editor;
        }

        editor = new XmlEditor();
        editor.setAction( IActionConstants.BOLD_ACTION, new AbstractBoldAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "<b>";
            }

            @Override
            public String getEndMarkup()
            {
                return "</b>";
            }
        } );
        editor.setAction( IActionConstants.ITALIC_ACTION, new AbstractItalicAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "<i>";
            }

            @Override
            public String getEndMarkup()
            {
                return "</i>";
            }
        } );
        editor.setAction( IActionConstants.MONOSPACED_ACTION, new AbstractMonospacedAction( editor )
        {
            @Override
            public String getStartMarkup()
            {
                return "<tt>";
            }

            @Override
            public String getEndMarkup()
            {
                return "</tt>";
            }
        } );
        editor.setAction( IActionConstants.LINK_ACTION, new AbstractLinkAction( editor )
        {
            @Override
            protected String generateLink( Link link )
            {
                if ( StringUtils.isEmpty( link.getName() ) )
                {
                    return "<a href=\"" + link.getURL() + "\">" + link.getURL() + "</a>";
                }

                return "<a href=\"" + link.getURL() + "\" name=\"" + link.getName() + "\">" + link.getName() + "</a>";
            }
        } );
        editor.setAction( IActionConstants.TABLE_ACTION, new AbstractTableAction( editor )
        {
            @Override
            protected String generateTable( Table table )
            {
                StringBuffer sb = new StringBuffer();

                sb.append( "<table>" );
                sb.append( Markup.EOL );
                for ( int i = 0; i < table.getRows(); i++ )
                {
                    sb.append( "<tr>" );
                    sb.append( Markup.EOL );

                    for ( int j = 0; j < table.getColumns(); j++ )
                    {
                        String align;
                        switch ( table.getTableStyle() )
                        {
                            case Table.TABLE_STYLE_CENTERED:
                                align = "center";
                                break;

                            case Table.TABLE_STYLE_LEFTALIGNED:
                                align = "left";
                                break;

                            case Table.TABLE_STYLE_RIGHTALIGNED:
                                align = "right";
                                break;

                            default:
                                align = "right";
                                break;
                        }
                        sb.append( "<td align=\"" ).append( align ).append( "\">" );
                        sb.append( DEFAULT_CELL_TEXT );
                        sb.append( "</td>" );
                        sb.append( Markup.EOL );
                    }
                    sb.append( "</tr>" );
                    sb.append( Markup.EOL );
                }
                sb.append( "</table>" );
                sb.append( Markup.EOL );

                return sb.toString();
            }
        } );

        return editor;
    }

    protected abstract String getEditorId();

    @Override
    protected IEditorSite createSite( IEditorPart page )
    {
        // see http://www.eclipse.org/webtools/wst/components/sse/tutorials/multipage-editor-tutorial.html
        IEditorSite site = null;
        if ( page == getTextEditor() )
        {
            site = new MultiPageEditorSite( this, page )
            {
                @Override
                public String getId()
                {
                    // Sets this ID so nested editor is configured for XML source
                    return ContentTypeIdForXML.ContentTypeID_XML + ".source";
                }
            };
        }
        else
        {
            site = super.createSite( page );
        }

        return site;
    }

    class XmlEditor
        extends AbstractXmlEditor
    {
        @Override
        public String getEditorId()
        {
            return AbstractXmlMultiPageEditorPart.this.getEditorId();
        }
    }
}
