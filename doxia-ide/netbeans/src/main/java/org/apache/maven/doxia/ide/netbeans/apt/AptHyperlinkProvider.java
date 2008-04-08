/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.maven.doxia.ide.netbeans.apt;

import javax.swing.text.Document;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;

/**
 *
 * @author mkleint
 */
public class AptHyperlinkProvider implements HyperlinkProvider {

    public boolean isHyperlinkPoint(Document doc, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[] getHyperlinkSpan(Document doc, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performClickAction(Document doc, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
