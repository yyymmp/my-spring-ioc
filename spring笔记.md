### 手写springIoc原理与Aop实现原理

#### Ioc的思想

在不使用spring的情况下, 在servlet中使用service时,需要在程序中控制对象创建

```java
public HelloService helloService = new HelloServiceImpl();
```

在service中,需要手动创建dao对象

```java
public HelloDao helloDao = new HelloDao();		
```

当需求发生变更的时候(比如数据库变更,比如换了一个数据源)，可能需要频繁修改 Java 代码，效率很低，如何解决？

使用静态工厂

工厂:

```java
public class BeanFactory {

    public static HelloDao getDao() {
        return new HelloDaoImpl();
    }
}
```



```java
//直接使用new的方式
//    public HelloDao helloDaoImpl = new HelloDaoImpl();

    //使用静态工厂
    public HelloDao helloDao = BeanFactory.getDao();
```

但是没有从根本上解决问题,当需求变更时,需要修改java代码?

如何解决这种问题?

**使用外部配置文件的方式**

将具体的实现类写到配置文件中，Java 程序只需要读取配置文件即可。

配置文件:在配置文件中定义好实现类

```properties
helloDao=com.myspring.dao.HelloDaoImpl
```

在工厂类中读取该配置文件利用反射生成对象

```java
    public static Properties properties = new Properties();;

    static {
        try {
            properties.load(BeanFactory.class.getClassLoader().getResourceAsStream("factory.properties"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    public static Object getDao() {
        //拿到全限定类名
        String val = properties.getProperty("helloDao");

        Class<?> aClass = null;
        try {
            aClass = Class.forName(val);
            //获取无参构造调用
            Object o = aClass.getConstructor(null).newInstance(null);
            return o;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
```

**小总结: 使用配置文件+工厂模式,来实现解耦**

优化:

工厂中的所得实现类应该是单例的,使用缓存解决

```java
public static Map<String, Object> cache = new HashMap<>();
```



```java
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
```

对比起来,松耦合,只需要改配置文件,无需修改代码 重新编译

自己放弃了创建对象的控制权,将创建对象的方式交给了BeanFactory,这种方式思想就是控制反转

#### 使用spring注解注入

XML 和注解，XML 已经被淘汰了，目前主流的是基于注解的方式，Spring Boot 就是基于注解的方式。

```java
@Data
@Component
// @Component("order") 指定名称
public class Order {

    @Value("xxx123")
    private String orderId;

    @Value("1000.0")
    private Float price;
}

```

```java
@Data
@Component
public class Account {

    @Value("1")
    private Integer id;
    @Value("张三")
    private String name;
    @Value("22")
    private Integer age;
    @Autowired  //通过类型注入
//    @Qualifier("order")  //指定名称  @Component("order") 给Order类指定名称
    private Order myOrder;
}
```

测试:

```java
        //传入被扫描的包  
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.myspring.spring.entity");

        System.out.println(applicationContext.getBean("account"));
```

j基于注解的指定原理:

spring中含有扫描包的组件,找出带有目标注解的目标类, 将class和beanname封装成Beandefintions对象,然后根据Beandefintions动态创建对象放入到IOC容器(k,v)中



#### 手动实现一个IOC容器

1、自定义一个 MyAnnotationConfigApplicationContext，构造器中传入要扫描的包。

2、获取这个包下的所有类。

3、遍历这些类，找出添加了 @Component 注解的类，获取它的 Class 和对应的 beanName，封装成一个 BeanDefinition，存入集合 Set，这个机会就是 IoC 自动装载的原材料。

4、遍历 Set 集合，通过反射机制创建对象，同时检测属性有没有添加 @Value 注解，如果有还需要给属性赋值，再将这些动态创建的对象以 k-v 的形式存入缓存区。

5、提供 getBean 等方法，通过 beanName 取出对应的 bean 即可。

com.myspring.customSpring下是实现了自定义的注解与ioc容器,主要实现步骤:

1.扫包封装为BeanDefinition集合 类名 ,class对象

```java
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
```

2.根据BeanDefinition集合,扫描是否被目标注解修饰,决定是否创建该bean,如果需要,根据class对象创建对象,使用beanName作为key存入ioc容器

并扫描value注解 将value注解中的值设置到bean属性中,另外,在设置属性时,通过拼出set方法名然后通过反射调用该set方法从而实现属性赋值,所以set方法非常重要,另外,在value属性进行赋值时,可能会出现类型不匹配,因为value注解中的值始终时字符串,所以下方进行了switch匹配从未进行强转

```java
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
    //将创建好的bean对象和beanName放入容器
    ioc.put(beanDefinition.getBeanName(), o);
```

3.自动装配 byName 与byType

```java
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
```



#### Spring 实现控制反转ioc的大概思路:

首先,传入配置类或者xml配置文件,这里以配置类为例,配置类上面配置了要扫描的包compontScan,所以得到该包名,扫描该包下的所有.class文件, 

通过**应用类加载器**加载加载得到class对象,然后构造出beanDefintion对象,里面可简单**描述bean的定义**,如scope,是否单例还是原型,class对象,一个beanDefintion就是对一个bean的定义描述,

然后将这些beanDefintion存入一个容器map,其中key为beanName,value就是class对象, 一直至扫描结束,得到一个对bean的描述的map集合

容器启动时,单例bean需要全部创建好,所以需要准备一个单例池map(String,Object),遍历第一步得到的beanDefintions容器,看看哪个bean是单例,单例是则创建该bean(通过class对象)并放入单例池map中,在getBean时直接取出容器中已存在的bean即可,

如果不是单例模式,则getBean时,会使用beanDefintion中的类描述信息class对象创建该bean对象

对于依赖注入,通过class对象拿到字段,找到被目标注解(如Autowired)修饰的字段,通过反射调用set方法设置属性的值

**对于spring的```aware```接口**,其实当通过反射实例化该对象时,判断该对象是否实现```aware```接口,如果实现了,则调用该方法,但是该方法的具体执行还是程序员自己定义

```java
   public void setBeanName(String beanName) {
        //ID保存BeanName的值	
        id=beanName;
    }
```

**对于spring的```InitializingBean```接口**,同样的道理,在实例化得到对象后,判断该对象是否实现了该接口,如果实现了,则调用该方法afterPropertiesSet.而方法具体内容是又程序员定义的,spring只是会调用该方法

```java
    public void afterPropertiesSet() throws Exception {
        System.out.println("ceshi InitializingBean");        
    }
```

**对于spring的```BeanPostProcessor ```接口**,bean的后置处理器,也是在实例化之后,初始化之前和初始化,并且这不是针对单个bean而言的,而是**所有**的bean都会调用此接口方法,并且可以定义多个这样的接口实现,**可以在方法内部判断当前bean的类型再各自进行具体的操作**

```java
public interface BeanPostProcessor {
    //初始化前
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
	
    //初始化后
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

```

#### springAop实现

springAop实现,也是利用BeanPostProcessor实现的,在初始化后方法(创建bean最后一个过程)中,使用动态代理为传进来的bean对象创建代理类,然后spring在getBean的时候,其实获取的是该代理对象,在执行方法时,先执行代理类的逻辑,再执行真实bean方法

### spring经典面试题源码详解

#### bean的生命周期

bean创建的生命周期:

class对象(A.class)------->推断构造方法(存在多个构造方法时)--->实例化-->对象

---->属性填充(依赖注入)

----->初始化(afterPropertiesSet方法,属性填充之后执行),不关心该方法具体内容,只是调用,具体逻辑由程序员决定,通常可以用来验证填充的属性是否符合要求

----->aop

---->Bean对象

源码流程：

核心方法：doCreateBean

```java
	protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
			throws BeanCreationException {

		// Instantiate the bean.
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
         //这里实例化bean对象 此时对象的属性均为null
		Object bean = instanceWrapper.getWrappedInstance();
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}
        //省去中间代码
        Object exposedObject = bean;
		try {
             //填充属性  实现autowired功能
			populateBean(beanName, mbd, instanceWrapper);
             //执行初始化方法  和 beanPostPrecessor 正常aop 
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
```

initializeBean方法：

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
   if (System.getSecurityManager() != null) {
      AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
         invokeAwareMethods(beanName, bean);
         return null;
      }, getAccessControlContext());
   }
   else {
      //执行Aware接口方法
      invokeAwareMethods(beanName, bean);
   }

   Object wrappedBean = bean;
   if (mbd == null || !mbd.isSynthetic()) {
       //初始化前
      wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
   }

   try {
       
      //初始化方法  判断是否实现初始化接口然后实现afterPropertiesSet 该方法内部调用afterPropertiesSet方法  ((InitializingBean) bean).afterPropertiesSet();
      invokeInitMethods(beanName, wrappedBean, mbd);
   }
   catch (Throwable ex) {
      throw new BeanCreationException(
            (mbd != null ? mbd.getResourceDescription() : null),
            beanName, "Invocation of init method failed", ex);
   }
   if (mbd == null || !mbd.isSynthetic()) {
       
      //初始化后 aop入口  判断是否需要进行aop 若需要aop 则返回代理对象 若不需要 则直接返回当前创建的bean对象
      wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
   }

   return wrappedBean;
}
```

进行aop的方法实现：找出所有切面，检查当前对象是否有切点符合，如果有，则需要进行aop，生成代理对象

```java
/**
 * Create a proxy with the configured interceptors if the bean is
 * identified as one to proxy by the subclass.
 * @see #getAdvicesAndAdvisorsForBean
 */
@Override
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
   if (bean != null) {
      Object cacheKey = getCacheKey(bean.getClass(), beanName);
      if (this.earlyProxyReferences.remove(cacheKey) != bean) {
          //wrapIfNecessary： 是否需要进行aop
         return wrapIfNecessary(bean, beanName, cacheKey);
      }
   }
   return bean;
}
```

wrapIfNecessary：方法：

```java
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
   if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
      return bean;
   }
   if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
      return bean;
   }
   if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
      this.advisedBeans.put(cacheKey, Boolean.FALSE);
      return bean;
   }

   // Create proxy if we have advice.
   //获取当前对象的切点切面
   Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
   if (specificInterceptors != DO_NOT_PROXY) {
      this.advisedBeans.put(cacheKey, Boolean.TRUE);
       //创建代理对象  jdk或cglib 
      Object proxy = createProxy(
            bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
      this.proxyTypes.put(cacheKey, proxy.getClass());
      return proxy;
   }

   this.advisedBeans.put(cacheKey, Boolean.FALSE);
   return bean;
}
```

cjlib(通过继承生成子类方式)生成代理对象：生成目标类的子类，重写目标类的方法，在目标类的执行前后进行一些逻辑操作

补充spring知识:在spring获取bean的时候,是通过byType和byName的方式来确定bean对象的,一个同一个beanname可能对应多个类型的bean对象,而一个类型的bean对象对应多个name,因为容器中注入了多个该对象实例,所以根据类型和名称来确定

```java
Account account = applicationContext.getBean("account");
```

#### sping事务传播级别







