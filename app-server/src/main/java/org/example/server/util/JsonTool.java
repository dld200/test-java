package org.example.server.util;

import com.alibaba.fastjson.JSON;

public class JsonTool {

    public static String toString(Object object){
        if (object == null) {
            return null;
        }
        if (object instanceof String
                || object instanceof Number
                || object instanceof Boolean
                || object instanceof Character) {
            return object.toString();
        }
        return JSON.toJSONString(object);
    }
}
