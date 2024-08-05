package org.rabbit.workflow.constants;


import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class GlobalProperties {

    // flowElementType
    public static final String FLOWELEMENTTYPE_USERTASK = "UserTask";
    public static final String FLOWELEMENTTYPE_STARTEVENT = "StartEvent";

    //workFlow approval
    public static final String CONTRAC_TAPPROVAL = "contractApproval";
    public static final String CUSTOMER_TAPPROVAL = "customerApproval";
    public static final String BULK_DOCUMENTS_UPLOAD = "bulkDocumentsUpload";
    public static final String ADHOC_APPROVAL = "adhocApproval";

    // nuxeo root path
    public static final String ROOTPATH = "/SAM/workspaces";


    // contract nature type
    public static final String CHANGE_REQUEST = "change_request";
    public static final String NEW_CASE = "new_case";
    public static final String RENEWAL = "renewal";

    // startByMessage
    public static final String BULK_UPLOAD_MESSAGE_START = "BulkUploadMessageStart";


    // workflow category
    public static final String BUSINESS_PROCESSES = "business_processes";
    public static final String SYSTEM_PROCESSES = "system_processes";
    public static final String SEMI_AUTOMATIC_PROCESSES = "semi_automatic_processes";

    // custom workflow app
    public static final String WORKFLOW_APP_CUSTOM_WORKFLOWS = "Custom Workflows";

    public static List<String> defaultTargetNamespaces() {
        return Arrays.asList(GlobalProperties.BUSINESS_PROCESSES, GlobalProperties.SYSTEM_PROCESSES, GlobalProperties.SEMI_AUTOMATIC_PROCESSES);
    }

}
