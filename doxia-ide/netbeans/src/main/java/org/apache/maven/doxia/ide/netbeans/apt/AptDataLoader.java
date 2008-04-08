/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.maven.doxia.ide.netbeans.apt;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class AptDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/x-maven-apt";
    private static final long serialVersionUID = 1L;

    public AptDataLoader() {
        super("org.apache.maven.doxia.ide.netbeans.apt.AptDataObject");
    }

    @Override
    protected String defaultDisplayName() {
        return NbBundle.getMessage(AptDataLoader.class, "LBL_Apt_loader_name");
    }

    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new AptDataObject(primaryFile, this);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
