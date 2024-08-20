package org.rabbit.workflow.service.facade;

import org.rabbit.service.email.SendEmailService;

/**
 * The class of explain list of supported functions
 *
 * @author nine rabbit
 */
public interface FunctionFacadeType {

    String WORKFLOW_USER_SERVICE = WorkflowUserService.class.getSimpleName().toLowerCase();

    String WORKFLOW_FORM_SERVICE = WorkflowFormService.class.getSimpleName().toLowerCase();

    String MAIL_SEND_SERVICE = SendEmailService.class.getSimpleName().toLowerCase();

}
