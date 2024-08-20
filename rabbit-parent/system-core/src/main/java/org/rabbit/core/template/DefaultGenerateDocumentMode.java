package org.rabbit.core.template;

import org.rabbit.entity.template.DocumentTemplate;

/**
 * Use parent path and template variables to generate document
 *
 * @author nine rabbit
 */
public class DefaultGenerateDocumentMode implements GenerateDocumentMode {

    @Override
    public String mode() {
        return "default";
    }

    public DocumentTemplate value() {
        return new DocumentTemplate();
    }

}
