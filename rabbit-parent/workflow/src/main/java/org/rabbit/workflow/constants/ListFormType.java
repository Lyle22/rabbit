package org.rabbit.workflow.constants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.flowable.engine.form.AbstractFormType;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.workflow.exception.WorkflowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The customize type of custom form
 */
public class ListFormType extends AbstractFormType {

    // 定义表单类型的标识符
    @Override
    public String getName() {
        return "list";
    }

    // 把表单中的值转换为实际的对象（实际处理逻辑根据具体业务而定）
    @Override
    public List<Map<String, Object>> convertFormValueToModelValue(String propertyValue) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            JavaType clazzType = TypeFactory.defaultInstance().constructParametricType(ArrayList.class, HashMap.class);
            return mapper.readValue(propertyValue, clazzType);
        } catch (JsonProcessingException e) {
            throw new WorkflowException(ErrorCode.GLOBAL, "Parse variable was failure. value:: " + propertyValue);
        }
    }

    // 把实际对象的值转换为表单中的值（实际处理逻辑根据具体业务而定）
    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return JsonHelper.write(modelValue);
    }

}
