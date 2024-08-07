package org.rabbit.core.template;

import com.wclsolution.docpal.api.viewmodels.request.GenerateDocumentRequestDTO;
import com.wclsolution.docpal.api.viewmodels.response.DocumentDTO;
import org.nuxeo.client.NuxeoClient;

public interface IGenerateDocumentService {

    DocumentDTO generate(NuxeoClient client, GenerateDocumentRequestDTO requestDTO);

}
