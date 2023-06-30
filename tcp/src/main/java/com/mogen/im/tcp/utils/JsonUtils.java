package com.mogen.im.tcp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    public static ObjectMapper INSTANCE = new ObjectMapper();

    private JsonUtils(){}

    public static ObjectMapper getInstance(){
        return INSTANCE;
    }

}
