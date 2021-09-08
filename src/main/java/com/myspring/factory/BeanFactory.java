package com.myspring.factory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author clearlove
 * @ClassName BeanFactory.java
 * @Description
 * @createTime 2021年09月05日 15:10:00
 */
public class BeanFactory {

    public static Properties properties = new Properties();

    public static Map<String, Object> cache = new HashMap<>();

    static {
        try {
            properties.load(BeanFactory.class.getClassLoader().getResourceAsStream("factory.properties"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static Object getDao(String name) {
        //dcl
        if (!cache.containsKey(name)) {
            synchronized (BeanFactory.class) {
                if (!cache.containsKey(name)) {
                    //拿到全限定类名
                    String val = properties.getProperty(name);

                    Class<?> aClass = null;
                    try {
                        aClass = Class.forName(val);
                        //获取无参构造调用
                        Object o = aClass.getConstructor(null).newInstance(null);
                        cache.put(name, o);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return cache.get(name);
    }
}
