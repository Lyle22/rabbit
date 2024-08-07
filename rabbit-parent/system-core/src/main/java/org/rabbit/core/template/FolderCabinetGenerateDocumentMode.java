package org.rabbit.core.template;

import com.wclsolution.docpal.api.dbmodel.docpal.cabinet.FolderCabinet;

/**
 * Use folder cabinet and request variables to generate document
 *
 * @author Lyle
 */
public class FolderCabinetGenerateDocumentMode implements GenerateDocumentMode {

    @Override
    public String mode() {
        return "FOLDER_CABINET";
    }

    @Override
    public FolderCabinet value() {
        return new FolderCabinet();
    }

}
