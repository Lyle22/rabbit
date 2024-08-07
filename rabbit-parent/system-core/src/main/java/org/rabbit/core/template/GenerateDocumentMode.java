package org.rabbit.core.template;

public interface GenerateDocumentMode {

    // The use mode for create document
    String mode();

    <T> T value();

}
