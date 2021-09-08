package com.myspring.customSpring;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author clearlove
 * @ClassName MyAnnotationConfigApplicationContext.java
 * @Description ioc  容器
 * @createTime 2021年09月07日 22:09:00
 */
public class MyAnnotationConfigApplicationContext {

    Map<String, Object> ioc = new HashMap<>();

    public MyAnnotationConfigApplicationContext(String pack) {
        //获取到目标类的class和beanName beanName=helloDaoImpl, beanClass=class com.myspring.dao.HelloDaoImpl
        Set<BeanDefinition> definitions = findDefinitions(pack);

        //根据definitions 创建bean
        //根据原材料创建bean
        createObject(definitions);

        //自动装在 即Autowired
        autowireObject(definitions);
    }

    private void autowireObject(Set<BeanDefinition> definitions) {
        Iterator<BeanDefinition> iterator = definitions.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            Class beanClass = beanDefinition.getBeanClass();
            //遍历每一个属性 判断那些属性被autowired修饰 则自动注入
            try {
                //得到对象后需要对有value注解的属性进行设置值
                //拿到所有属性
                Field[] fields = beanClass.getDeclaredFields();
                //判断value注解
                for (Field field : fields) {
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    if (annotation != null) {
                        //查看是否指定
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);
                        //找到需要被赋值属性的对象
                        Object parent = getBean(beanDefinition.getBeanName());
                        //将属性赋值 -> 调用set方法
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        Method method = beanClass.getMethod(methodName, field.getType());
                        if (qualifier != null) {
                            //byName
                            Object bean = ioc.get(qualifier.value());
                            method.invoke(parent, bean);
                        } else {
                            //byType 通过遍历容器中所有类型找到对象
                            for (String beanName : ioc.keySet()) {
                                String iocTypeName = ioc.get(beanName).getClass().getTypeName();
                                String typeName = field.getType().getTypeName();
                                if (iocTypeName.equals(typeName)) {
                                    //在ioc容器中找到该类型一致的对象
                                    Object bean = ioc.get(beanName);
                                    method.invoke(parent, bean);
                                }
                            }
                        }
                    }
                }

            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void createObject(Set<BeanDefinition> definitions) {
        Iterator<BeanDefinition> iterator = definitions.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            Class beanClass = beanDefinition.getBeanClass();
            try {
                Object o = beanClass.getConstructor(null).newInstance();
                //得到对象后需要对有value注解的属性进行设置值
                //拿到所有属性
                Field[] fields = beanClass.getDeclaredFields();
                //判断value注解
                for (Field field : fields) {
                    Value annotation = field.getAnnotation(Value.class);
                    if (annotation != null) {
                        String value = annotation.value();
                        //如何将值设置到属性中  -> 找到set方法
                        //拼接出set方法
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        //参数类型 即字段类型
                        Method method = beanClass.getMethod(methodName, field.getType());
                        //调用方法 给属性设置 在此之前需要进行类型转化
                        String typeSimpleName = field.getType().getSimpleName();

                        Object val = null;
                        switch (typeSimpleName) {
                            case "Integer":
                                val = Integer.parseInt(value);
                                break;
                            case "String":
                                val = value;
                                break;
                            case "Float":
                                val = Float.parseFloat(value);
                                break;
                        }

                        method.invoke(o, val);
                    }
                }

                System.out.println(Arrays.toString(fields));
                //将创建好的bean对象和beanName放入容器
                ioc.put(beanDefinition.getBeanName(), o);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(String beanName) {
        return ioc.get(beanName);
    }

    private Set<BeanDefinition> findDefinitions(String pack) {
        //扫描该包下的class
        Set<Class<?>> classes = MyTools.getClasses(pack);
        //遍历这些类 找到被目标注解修饰的类
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for (Class<?> aClass : classes) {
            Component annotation = aClass.getAnnotation(Component.class);
            if (annotation != null) {
                //获取Component注解的值
                String beanName = annotation.value();
                if ("".equals(beanName)) {
                    //获取类名首字母小写
                    String className = aClass.getName().replaceAll(aClass.getPackage().getName() + ".", "");
                    beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                }
                //3、将这些类封装成BeanDefinition，装载到集合中
                beanDefinitions.add(new BeanDefinition(beanName, aClass));
            }
        }
        return beanDefinitions;
    }

    public static void main(String[] args) {
        MyAnnotationConfigApplicationContext myAnnotationConfigApplicationContext = new MyAnnotationConfigApplicationContext("com.myspring.spring.entity");
        System.out.println(myAnnotationConfigApplicationContext.getBean("account"));
    }
}
