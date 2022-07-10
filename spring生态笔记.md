面试答题技巧:

总:当前问题要回答的具体的点

分:1,2,3,4 不清楚的忽略过去,突出一些技术名词(核心概念,接口,类,关键方法)



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
helloDao=HelloDaoImpl
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

基于注解的指定原理:

spring中含有扫描包的组件,找出带有目标注解的目标类, 将class和beanname封装成Beandefintions对象,然后根据Beandefintions动态创建对象放入到IOC容器(k,v)中



#### 手动实现一个IOC容器

1、自定义一个 MyAnnotationConfigApplicationContext，构造器中传入要扫描的包。

2、获取这个包下的所有类。

3、遍历这些类，找出添加了 @Component 注解的类，获取它的 Class 和对应的 beanName，封装成一个 BeanDefinition，存入集合 Set，这个集合就是 IoC 自动装载的原材料。

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

#### 为什么循环依赖解决不了构造方法注入

循环依赖的解决主要依靠提前暴露的机制, 他是实例化对象,提前暴露放入三级缓存,属性赋值,  如果在实例化部分调用构造函数依然存在bean对象依赖,那还是会出现循环依赖问题

#### 为什么jdk是基于接口,而cjlib是基于类的

使用jdk实现时,被代理类和代理类都要实现相同的接口,使用jdk动态代理时,最终会创建一个子类,会生成一个$Proxy0,这个类本身就要继承Proxy类,由于java单继承多实现的的原因,所以jdk动态代理只能基于接口而不能基于类,将会有强制转化异常

而cjlib是使用实现子类的方式,不需要代理对象接口,只需要能被继承就可以,借助于字节码技术



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

#### BeanFactory是什么,和FactoryBean的区别是什么?

**访问 Spring bean 容器的根接口**, BeanFactory是接口，提供了IOC容器最基本的形式，给具体的IOC容器的实现提供了规范,**ApplicationContext**也是由BeanFactory派生而来

详解:BeanFactory，以Factory结尾，表示它是一个工厂类(接口)， **它负责生产和管理bean的一个工厂**。在Spring中，**BeanFactory是IOC容器的核心接口，它的职责包括：实例化、定位、配置应用程序中的对象及建立这些对象间的依赖。BeanFactory只是个接口，并不是IOC容器的具体实现，但是Spring容器给出了很多种实现，如 DefaultListableBeanFactory、XmlBeanFactory、ApplicationContext等，其中****XmlBeanFactory就是常用的一个，该实现将以XML方式描述组成应用的对象及对象间的依赖关系**。**

**BeanFactory和ApplicationContext就是spring框架的两个IOC容器，现在一般使用ApplicationnContext，其不但包含了BeanFactory的作用，同时还进行更多的扩展,BeanFacotry是spring中比较原始的Factory。如XMLBeanFactory就是一种典型的BeanFactory**

而FactoryBean是一个bean,一个能生产或者修饰对象生成的工厂Bean,正常来说.spring产生一个对象过程是比较复杂的,所以提供了一个工厂bean接口,用户可以通过实现该接口定制实例化Bean的逻辑。

#### 循环依赖如何解决

关键词: 三级缓存,提前暴露对象,aop(总)

什么是循环依赖问题: a b相互依赖

说明bean的创建过程,a属性填充时找b,找不到则创建b,创建b时属性填充a,找不到a则创建a,进入循环,形成闭环,在创建b寻找a时,会发现a对象是存在的,但是此时a对象不是一个完整的状态,只是进行了实例化而未进行初始化,所以可以将一个非完整状态优先赋值,等待后续操作完成赋值,相当于提前暴露了某个不完整对象的引用,使得实例化和初始化分开操作,这也是解决循环依赖问题的关键,当所有对象都完成后,还要把对象放入到容器中,此时容器存在对象的几个状态:1已完成实例化但未初始化状态 2完整状态对象,所以使用不同的map容器来储存,一级缓存对应完整状态和二级缓存对应未初始化状态,

为什么需要三级缓存,三级缓存的类型是ObjectFactory是一个函数式接口,存在的意义是整个容器的运行过程中同名bean对象只能有一个,Spring在bean实例化后，将原始bean放入第三级缓存singletonFactories中，第三级缓存里实际存入的是ObjectFactory接口签名的回调实现。那么如果有动态代理的需求，里面可以埋点进行处理，提前曝光的是ObjectFactory对象，在被注入时才在ObjectFactory方式内实时生成代理对象，并将生成好的代理对象放入到二级缓存earlySingletonObjects中。将原始bean包装后返回。通过第三级缓存我们可以拿到可能经过包装的对象，解决对象代理封装的问题。

当一个对象需要被代理时,那么需要在使用之前使用代理对象覆盖掉容器中的原对象,所以当对象在调用时,传入lambda表达式来执行对对象的覆盖过程,因此,所有的bean对象都放在三级缓存中,在后续的使用过程中,如果需要被代理对象,如果不需要,则返回普通对象

在实际使用中，要获取一个bean，先从一级缓存一直查找到三级缓存，缓存bean的时候是从三级到一级的顺序保存，并且缓存bean的过程中，三个缓存都是互斥的，只会保持bean在一个缓存中，而且，最终都会在一级缓存中。



#### sping事务传播级别

```java
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>5.3.8</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>5.3.8</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.11</version>
    </dependency>
```

插入后抛出异常:

实例1:

```java
@Transactional
public void test() {
    jdbcTemplate.execute("insert into age value(1,2)");
    System.out.println("test");
    throw new NullPointerException();  //会导致上方事务回滚
}
```

实例2(重要):

```java
    @Transactional
    public void test() {
        jdbcTemplate.execute("insert into age value(1,2)");
        System.out.println("test");
        a();

    }

    // Propagation.NEVER 调用a方法前已存在一个事务 则抛出异常
    @Transactional(propagation = Propagation.NEVER)
    public void a() {
    }
```

测试代码:调用test方法:

```java
        Account account = applicationContext.getBean("account", Account.class);
        account.test();
```

在这里并没有意料的报错,a()方法上面的注解没有生效,并且a()方法上不管使用什么隔离级别都是失效的

原理分析:在从spring中获取account对象时,因为有事务,所以获取的是一个代理对象,所以执行test方法时,是代理对象去执行的,使用aop在test执行前创建了数据库连接等等,但是test的方法本身代理对象中的真实对象(target)执行的,a()方法同理,a()方法是真实对象去执行的, 不是代理对象执行的,自然没有aop,a()方法的注解都是无效的

这种问题的解决方案:

```java
@Component
public class Account {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    //从spring中将自己注入 但此时是spring创建bean之后的代理对象 代理对象调用a方法时,a()方法上面的注解就会生效了
    @Autowired
    private Account account;
    
    @Transactional
    public void test() {
        jdbcTemplate.execute("insert into age value(1,2)");
        System.out.println("test");
        //使用注入的代理对象调用
        account.a();

    }

    // Propagation.NEVER 调用a方法前已存在一个事务 则抛出异常
    @Transactional(propagation = Propagation.NEVER)
    public void a() {
    }
}
```

实例3(重要):

```java
public void test() {
    //使用自身调用
    a();
}

    @Transactional
    public void a() {
        jdbcTemplate.execute("insert into age value(2,2)");
        throw new NullPointerException();
    }
}
```

在没有事务注解的test方法中调用a()方法,a方法上面的事务是不生效的,同理.因为是target(真实对象)调用的test方法,而不是代理对象调用的该方发,此时的执行结果是该语句会被插入,然后后面抛出异常

实例4(重要):

```java
    @Transactional
    public void test() {
        jdbcTemplate.execute("insert into age value(1,2)");
        //使用代理对象调用 保证事务有效
        account.a();

    }

    // Propagation.NEVER 调用a方法前已存在一个事务 则重新创建一个事务
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void a() {
        jdbcTemplate.execute("insert into age value(2,2)");
//        System.out.println("a");
        throw new NullPointerException();
    }
```

在test方法中执行sql,调用a方法执行另外一个sql并抛出异常,最后谁会被执行?

都被回滚,因为a方法在test中调用时,就是test方法中报错且没有捕获,所以test的事务也是回滚的

实例4:(重要)

```java
@Transactional
public void test() {
    jdbcTemplate.execute("insert into age value(1,2)");
    System.out.println("test");
    account.a();
    throw new NullPointerException();
}

// Propagation.NEVER 调用a方法前已存在一个事务 则重新创建一个事务
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void a() {
    jdbcTemplate.execute("insert into age value(2,2)");
}
```

这种情况,a方法中的事务是可以正确执行的,因为a方法中是单独一个事务,并且没有任何异常,test方法中的异常并不会影响到a方法的事务

七种隔离级别:

PROPAGATION_REQUIRED – 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。
PROPAGATION_SUPPORTS – 支持当前事务，如果当前没有事务，就以非事务方式执行。
PROPAGATION_MANDATORY – 支持当前事务，如果当前没有事务，就抛出异常。
PROPAGATION_REQUIRES_NEW – 新建事务，如果当前存在事务，把当前事务挂起。
PROPAGATION_NOT_SUPPORTED – 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
PROPAGATION_NEVER – 以非事务方式执行，如果当前存在事务，则抛出异常。
PROPAGATION_NESTED – 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。

#### spring中的设计模式

- 单例模式:bean默认都是单例的

- 原型模式:指定作用域是原型模式

- 工厂模式:spring的BeanFactory

- 工厂方法:FactoryBean接口,实现了FactoryBean接口的bean是一类叫做factory的bean。其特点是，spring会在使用getBean()调用获得该bean时，会自动调用该bean的getObject()方法，所以返回的不是factory这个bean，而是这个bean.getOjbect()方法的返回值。

- 策略模式:在Spring中，我这里举的例子是Resource类，这是所有资源访问类所实现的接口。针对不同的访问资源的方式，Spring定义了不同的Resource类的实现类。

  **UrlResource**：访问网络资源的实现类。

  **ServletContextResource**：访问相对于 ServletContext 路径里的资源的实现类。

  **ByteArrayResource**：访问字节数组资源的实现类。

  **PathResource**：访问文件路径资源的实现类。

  **ClassPathResource**：访问类加载路径里资源的实现类。

- 代理模式:aop

- 观察者模式:Listener  事件驱动

- 适配器模式:Adapter
- 责任链模式:在aop拦截时,方法前,方法后,方法返回这些拦截集合,体现的是一种责任链模式



### springMvc

#### 执行流程

Servlet 容器首先接待了这个请求，并将该请求委托给 `DispatcherServlet` 进行处理。

`DispatcherServlet` 将请求传给处理器映射器,找到对应的该请求的处理器对象和HandlerExecutionChain 拦截器

但`DispatcherServlet` 并不是直接去掉该处理器处理请求,而是调用处理器适配器HandlerAdapter,让处理器适配器去执行具体的handler,并且处理器适配器需要进行一系列的操作包括表单数据的验证、数据类型的转换、将表单数据封装到 POJO 等，这一系列的操作后在具体handler执行业务方法之前

并将返回的模型和输入交给前端控制器,再次交给试图解析器处理获取解析后的页面数据,最后返回给客户端

#### 三大组件

处理映射器,处理器适配器,视图解析器



### springboot

三大注解:

```java
@Configuration:配置类 
@EnableAutoConfiguration:启用 SpringBoot 的自动配置机制
@ComponentScan:扫描被@Component (@Service,@Controller)注解的 bean，注解默认会扫描该类所在的包下所有的类。
```

EnableAutoConfiguration 主要有

```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
```

最核心的是里面导入的AutoConfigurationImportSelector,这个类有一个核心方法:

```java
/**
 * 核心方法，加载spring.factories文件中的 
 * org.springframework.boot.autoconfigure.EnableAutoConfiguration 配置类
 */
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
                                                  AnnotationAttributes attributes) {
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
        EnableAutoConfiguration.class, getBeanClassLoader());
    Assert.notEmpty(configurations,
           "No auto configuration classes found in META-INF/spring.factories. If you "
                    + "are using a custom packaging, make sure that file is correct.");
    return configurations;
}
```

spring-boot-autoconfigure.jar 包中的 META-INF/spring.factories 里面默认配置了很多aoto-configuration，如下

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
//省略其他
```

**总的来说，@EnableAutoConfiguration完成了一下功能：**

**从classpath中搜寻所有的 META-INF/spring.factories 配置文件，并将其中org.springframework.boot.autoconfigure.EnableutoConfiguration 对应的配置项通过反射实例化为对应的标注了@Configuration的JavaConfig形式的IoC容器配置类，然后汇总为一个并加载到IoC容器。**

