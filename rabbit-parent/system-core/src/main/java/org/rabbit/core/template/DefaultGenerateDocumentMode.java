package org.rabbit.core.template;

import com.wclsolution.docpal.api.dbmodel.docpal.DocumentTemplate;

/**
 * Use parent path and template variables to generate document
 *
 * @author Lyle
 */
public class DefaultGenerateDocumentMode implements GenerateDocumentMode {

    @Override
    public String mode() {
        return "default";
    }

    @Override
    public DocumentTemplate value() {
        return new DocumentTemplate();
    }

}
