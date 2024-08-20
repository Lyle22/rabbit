package org.rabbit.workflow.service.facade;

import lombok.extern.slf4j.Slf4j;
import org.rabbit.workflow.exception.WorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The class that function facade factory
 *
 * @author nine rabbit
 */
@Slf4j
@Component
public class FunctionFacadeFactory {

    @Autowired public List<FunctionFacadeType> types;
    @Autowired private List<IFunctionFacade> functionFacades;
    @Autowired private Map<String, IFunctionFacade> functionFacadeMap;

    public IFunctionFacade selectService(String functionName) {
        IFunctionFacade functionFacade = functionFacadeMap.get(functionName);
        if (functionFacade == null) {
            return null;
        } else {
            return functionFacade;
        }
    }

    public IFunctionFacade build(String functionName) {
        IFunctionFacade functionFacade = functionFacadeMap.get(functionName);
        if (functionFacade == null) {
            throw new WorkflowException("Not found function service");
        } else {
            return functionFacade;
        }
    }

}
