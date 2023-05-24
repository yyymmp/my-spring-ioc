package com.myspring.design.combat.strategy.practice;

import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * @author jlz
 * @date 2023年05月22日 23:03
 */
public class Factory {

    public static Map<String,Handle> handleMap = new HashMap<>();

    public static Handle getStrategy(String name){
        return handleMap.get(name);
    }

    public static void register(String name,Handle handle){
        if (null != name && null != handle){
            handleMap.put(name,handle);
        }
    }
}
