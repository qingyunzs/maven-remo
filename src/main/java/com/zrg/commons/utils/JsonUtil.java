package com.zrg.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zrg
 * @date 2021/8/3 11:32
 */
public class JsonUtil {
    /**
     * Get Entity
     *
     * @param jsonData
     * @param prototype
     * @param <T>
     * @return
     */
    public static <T> T getEntity(String jsonData, Class<T> prototype) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        objectMapper.registerModule(javaTimeModule);
        try {
            return objectMapper.readValue(jsonData, prototype);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get list entity
     * @param jsonData
     * @param <T>
     * @return
     */
    public static <T> List<T> getListEntity(String jsonData){
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            return objectMapper.readValue(jsonData,new TypeReference<T>(){});
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    /**
     * Get value from json data
     *
     *  例如:
     *   params  为  {"park":{"gateId1":"gateName1","gateId2":"gateName2"},"equipment":["camera","phone","door"],"owner":"self"}
     *
     *  key     为  ["park"]              返回的结果     为   {"gateId1":"gateName1","gateId2":"gateName2"}
     *  key     为  ["park","gateId1"]    返回的结果     为   "gateName1"
     *  key     为  ["equipment"]         返回的结果     为   ["camera","phone","door"]
     *  key     为  ["owner"]             返回的结果     为   "self"
     * @param jsonData data
     * @param keys 想要获取的数据的 key 的层级列表
     *
     * @return
     */
    public static String parametersParse(String jsonData,String[] keys){
        if (StringUtils.isBlank(jsonData) || keys.length < 1){
            return null;
        }
        HashMap jsonMap = new HashMap<>(JSONObject.fromObject(jsonData));
        Object object = jsonMap.get(keys[0]);
        if (object == null){
            return null;
        }
        if (keys.length == 1 ){
            return object.toString();
        }
        String[] keys1 = new String[keys.length - 1];
        System.arraycopy(keys,1,keys1,0,keys1.length);
        return parametersParse(object.toString(),keys1);
    }

    /**
     * Update value from json data
     * @param jsonData data
     * @param keys 想要修改的数据的 key 的层级列表
     * @param newValue 想要该成的值
     * @return
     */
    public static String setValue(String jsonData,String[] keys,String newValue) throws JsonProcessingException {
        if (StringUtils.isBlank(jsonData) || keys.length < 1){
            throw new RuntimeException("json参数不可为空");
        }
        Map<String, Object> jsonMap = new HashMap<>(JSONObject.fromObject(jsonData));
        int length=keys.length;
        Map jsonObjet = jsonMap;

        for (int i = 0; i < length-1 ; i++) {
            jsonObjet = (JSONObject) jsonObjet.get(keys[i]);
        }
        jsonObjet.put(keys[keys.length-1],newValue);

        ObjectMapper ob = new ObjectMapper();
        return ob.writeValueAsString(jsonMap);
    }
}
