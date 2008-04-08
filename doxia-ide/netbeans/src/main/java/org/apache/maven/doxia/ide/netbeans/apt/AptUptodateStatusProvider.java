/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.maven.doxia.ide.netbeans.apt;

import javax.swing.text.Document;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProviderFactory;

/**
 *
 * @author mkleint
 */
public class AptUptodateStatusProvider implements UpToDateStatusProviderFactory {

    public UpToDateStatusProvider createUpToDateStatusProvider(Document doc) {
        return new Prov();
    }

    private class Prov extends UpToDateStatusProvider {

        @Override
        public UpToDateStatus getUpToDate() {
            //TODO, check for hints, errors from here..
            System.out.println("checking uptodate status");
            return UpToDateStatus.UP_TO_DATE_OK;
        }
    }
}
