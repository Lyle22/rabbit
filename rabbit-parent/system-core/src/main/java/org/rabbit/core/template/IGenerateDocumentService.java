package org.rabbit.core.template;

import org.nuxeo.client.NuxeoClient;
import org.rabbit.core.models.DocumentDTO;
import org.rabbit.core.models.GenerateDocumentRequestDTO;

/**
 * The class of generate document service
 *
 * @author Lyle
 */
public interface IGenerateDocumentService {

    DocumentDTO generate(NuxeoClient client, GenerateDocumentRequestDTO requestDTO);
}
