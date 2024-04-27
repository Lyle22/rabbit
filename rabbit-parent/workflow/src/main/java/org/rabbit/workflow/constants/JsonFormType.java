package org.rabbit.workflow.constants;

import org.flowable.engine.form.AbstractFormType;
import org.rabbit.common.utils.JsonHelper;

import java.util.Map;

/**
 * The customize type of custom form
 */
public class JsonFormType extends AbstractFormType {

    // 定义表单类型的标识符
    @Override
    public String getName() {
        return "json";
    }

    // 把表单中的值转换为实际的对象（实际处理逻辑根据具体业务而定）
    @Override
    public Map<String, Object> convertFormValueToModelValue(String propertyValue) {
        return JsonHelper.read(propertyValue, Map.class);
    }

    // 把实际对象的值转换为表单中的值（实际处理逻辑根据具体业务而定）
    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return JsonHelper.write(modelValue);
    }

}
