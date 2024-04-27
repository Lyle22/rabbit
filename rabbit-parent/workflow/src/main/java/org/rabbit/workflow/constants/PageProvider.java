package org.rabbit.workflow.constants;

import lombok.experimental.UtilityClass;

/**
 * Page providers for search
 */
@UtilityClass
public class PageProvider {
    /**
     * Page provider for retrieving children
     */
    public final String CURRENT_DOCUMENT_CHILDREN =  "CURRENT_DOC_CHILDREN";

    /**
     * Page provider for retrieving content collection
     */
    public final String DEFAULT_CONTENT_COLLECTION = "default_content_collection";

    /**
     * The default search provider
     */
    public final String DEFAULT_SEARCH = "default_search";

    /**
     * Page provider for domain document
     */
    public final String DOMAIN_DOCUMENTS = "domain_documents";

    /**
     * The NXQL query page provider
     */
    public final String NXQL_SEARCH = "nxql_search";
}
