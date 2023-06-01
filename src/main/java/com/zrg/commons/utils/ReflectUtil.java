package com.zrg.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reflect Util Helper
 *
 * @author zrg
 */
@Slf4j
public class ReflectUtil {
    /**
     * Get modify content
     *
     * @param source             Original Object
     * @param target             Target Object
     * @param comparedProperties Specify Object Array
     * @param isCompareSuper     Compare super object
     * @return
     */
    public static String objectModifyContent(Object source, Object target, String[] comparedProperties, Boolean isCompareSuper) {
        StringBuilder modifyContent = new StringBuilder();
        if (null == source || null == target) {
            return "";
        }
        // Get source class
        Class<?> sourceClass = source.getClass();
        Field[] sourceFields = sourceClass.getDeclaredFields();

        if (isCompareSuper) {
            Field[] sourceSuperFields = sourceClass.getSuperclass().getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(sourceFields));
            fieldList.addAll(Arrays.asList(sourceSuperFields));
            sourceFields = fieldList.toArray(new Field[0]);
        }

        for (Field srcField : sourceFields) {
            // Get srcField
            String srcName = srcField.getName();
            if (null == comparedProperties || (Arrays.asList(comparedProperties).contains(srcName))) {
                // Get srcField value
                Object srcFieldValue = getFieldValue(source, srcName, isCompareSuper);
                String srcValue = srcFieldValue == null ? "" : srcFieldValue.toString();
                // Get targetFiled value
                Object targetFieldValue = getFieldValue(target, srcName, isCompareSuper);
                String targetValue = targetFieldValue == null ? "" : targetFieldValue.toString();
                if (StringUtils.isEmpty(srcValue) && StringUtils.isEmpty(targetValue)) {
                    continue;
                }
                if (!srcValue.equals(targetValue)) {
                    String srcFieldAnnotationName = srcField.getAnnotation(XmlElement.class).name();
                    modifyContent.append(srcFieldAnnotationName)
                            .append("from:`")
                            .append(srcValue)
                            .append("`to:`")
                            .append(targetValue)
                            .append("`;");
                }
            }
        }
        return modifyContent.toString();
    }

    /**
     * Get filedName's value of object
     *
     * @param obj       Object
     * @param fieldName Field Name
     * @return fieldValue
     */
    private static Object getFieldValue(Object obj, String fieldName, Boolean isCompareSuper) {
        Object fieldValue = null;
        if (obj == null) {
            return null;
        }
        Method[] methods = obj.getClass().getDeclaredMethods();

        if (isCompareSuper) {
            Method[] sourceSuperMethods = obj.getClass().getSuperclass().getDeclaredMethods();
            List<Method> methodList = new ArrayList<>(Arrays.asList(methods));
            methodList.addAll(Arrays.asList(sourceSuperMethods));
            methods = methodList.toArray(new Method[0]);
        }

        for (Method method : methods) {
            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }
            if (methodName.startsWith("get") && methodName.substring(3).equalsIgnoreCase(fieldName)) {
                try {
                    fieldValue = method.invoke(obj);
                } catch (Exception e) {
                    log.error("Get value error, method name: {}", methodName);
                }
            }
        }
        return fieldValue;
    }
}
