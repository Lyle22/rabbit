package org.rabbit.workflow.service.bpmn;

import org.rabbit.workflow.models.ProcessInstanceDTO;

import java.util.Map;

/**
 * The class that process instance service
 * <p>它的职责: 负责定义流程实例的行为实现服务</p>
 *
 * @author nine rabbit
 */
public interface IProcessInstanceService {

    /**
     * Start New Process Instance
     *
     * @param processKey  the process definition key
     * @param businessKey the business key
     * @param variables   the request variables
     * @return ProcessInstanceDTO
     */
    ProcessInstanceDTO start(String processKey, String businessKey, Map<String, Object> variables);
}
