/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.maven.doxia.ide.netbeans.apt;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;

public class AptDataObject extends MultiDataObject {

    public AptDataObject(FileObject pf, AptDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    @Override
    protected Node createNodeDelegate() {
        return new AptDataNode(this);
    }
}
