package org.rabbit.workflow.service.facade;

import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.contains.Result;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The class of form service.
 *
 * @author nine rabbit
 **/
@Slf4j
@Service
public class WorkflowFormService  implements IFunctionFacade {

    @Override
    public Result<String> validate(Map<String, String> reqParam, String funcPoint) {
        return null;
    }

}
