package org.rabbit.workflow.constants;

import org.springframework.stereotype.Component;

/**
 * ConstantsForFolderStructureImport
 *
 * @author weltuser
 */
@Component
public class ConstantsForFolderStructureImport {

    public static int MAX_RETRY_TIME;
    public static final String TEMP_FOLDER_PREFIX = "FolderStructureImport";
    public static final String ACTIVITY_FOLDER_CREATION= "folderCreationInDocumentCreationSubProcess";
    public static final String ACTIVITY_START_DOCUMENT_CREATION_SUBPROCESS= "startDocumentCreationSubProcess";
    public static final String PREFIX_FOLDER_STRUCTURE = "folder_structure";
    public static final String PREFIX_REPORT = "ReportOfFolderImport";
    public static final String PREFIX_ERROR_LOG = "Error_Log";
    public static final String REPORT_COLUMN_NAME = "Name";
    public static final String REPORT_COLUMN_ISFOLDER = "Is Folder";
    public static final String REPORT_COLUMN_RELATIVEPATH = "Relative Path In Zip Package";
    public static final String REPORT_COLUMN_DOCPALPATH = "Docpal Path";
    public static final String REPORT_COLUMN_HASSUCCESSCREATED = "Has Success Created";

}
