package org.rabbit.workflow.service.delegate;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FieldExtension;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.DelegateHelper;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Abstract java delegate
 *
 * @author Lyle
 */
public abstract class AbstractJavaDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(AbstractJavaDelegate.class);

    protected boolean getBoolean(Expression expression, DelegateExecution execution) {
        if (expression == null) {
            return false;
        }
        Object obj = expression.getValue(execution);
        if (obj == null) {
            return false;
        } else if (obj instanceof String) {
            return Boolean.parseBoolean(obj.toString());
        }
        return (boolean) obj;
    }

    protected Date getDate(Expression expression, DelegateExecution execution) {
        if (expression == null) {
            return null;
        }
        Object obj = expression.getValue(execution);
        return obj == null ? null : (Date) obj;
    }

    protected String getString(Expression expression, DelegateExecution execution) {
        if (expression == null) {
            return null;
        }
        Object obj = expression.getValue(execution);
        return obj == null ? null : (String) obj;
    }

    protected String extractValue(DelegateExecution execution, String fieldName) {
        // First, Obtain value from field expression of this class [Recommend way]
        String fieldValue = execution.getVariable(fieldName, String.class);
        if (StringUtils.isNotBlank(fieldValue)) {
            return fieldValue;
        }
        try {
            // Second,Obtain value from field expression of this class [UnRecommend way]
            Field field = this.getClass().getDeclaredField(fieldName);
            Expression expression = (Expression) field.get(this);
            if (null != expression && null != expression.getValue(execution)) {
                String expressionValue = (String) expression.getValue(execution);
                if (StringUtils.isNotBlank(expressionValue)) {
                    return expressionValue;
                }
            }
        } catch (Exception ignored) {
            logger.warn("Can not obtain value from field expression of this class ：" + fieldName);
        }
        // Three, obtain value from variables [reserve way]
        FieldExtension fieldExtension = DelegateHelper.getField(execution, fieldName);
        if (null != fieldExtension) {
            String fieldExpression = fieldExtension.getExpression();
            Object value = extractVariableFormExpression(execution.getProcessInstanceId(), fieldExpression);
            if (null != value && StringUtils.isNotBlank(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private Object extractVariableFormExpression(String processInstanceId, String exp) {
        Expression expression = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager().createExpression(exp);
        RuntimeService runtimeService = CommandContextUtil.getProcessEngineConfiguration().getRuntimeService();
        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).includeProcessVariables().singleResult();
        return expression.getValue(executionEntity);
    }

}
