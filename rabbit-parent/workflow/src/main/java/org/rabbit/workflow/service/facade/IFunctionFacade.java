package org.rabbit.workflow.service.facade;

import org.rabbit.common.contains.Result;

import java.util.Map;

/**
 * Define function facade
 *
 * @author Lyle
 */
public interface IFunctionFacade extends FunctionFacadeType {

    Result<String> validate(Map<String, String> reqParam, String funcPoint);

}
