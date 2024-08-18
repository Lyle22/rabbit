package org.rabbit.workflow.service.facade;

import org.rabbit.service.mail.impl.MailSendService;

/**
 * The class of explain list of supported functions
 *
 * @author Lyle
 */
public interface FunctionFacadeType {

    String WORKFLOW_USER_SERVICE = WorkflowUserService.class.getSimpleName().toLowerCase();

    String WORKFLOW_FORM_SERVICE = WorkflowFormService.class.getSimpleName().toLowerCase();

    String MAIL_SEND_SERVICE = MailSendService.class.getSimpleName().toLowerCase();

}
