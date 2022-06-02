 ### spring core

#### spring bean 定义错误

##### 扫描不到bean

问题:在springboot项目中,当bean不在启动类所在包以及其子包所在目录时,该bean无法扫描到

分析:启动类上扫描注解默认basePackages即为当前类所在的包

解决1:  指定扫描路径```@ComponentScan("com.spring.puzzle.class1.example1.controller")```,这样会造成隐式扫描范围不会被添加

解决2: 添加扫描路径```@ComponentScans(value = { @ComponentScan(value = "com.spring.puzzle.class1.example1.controller") })```

##### 定义的bean缺少依赖

```java
@Service
public class ServiceImpl {

    private String serviceName;

    public ServiceImpl(String serviceName){
        this.serviceName = serviceName;
    }
}
```

问题: ```@Service```将```ServiceImpl```定义为了一个```bean```,并且显示指定了一个有参构造器,当spring通过反射去调用构造器构造参数时,无法获取到构造器的参数

解决: 将构造器所需参数进行装配

```java
@Bean
public String serviceName(){
    return "serviceName--";
}
```

##### 原型bean被固定

问题: 在spring中bean的类型,原型bean每次都是一个新的对象

```java
    @Autowired
    private PrototypeBeanImpl prototypeBean;

    @RequestMapping(value = "/a", method = RequestMethod.POST)
    public String a(@RequestBody User user) {
        System.out.println("接受请求");
        log.info("{}",prototypeBean);
        return user.toString();
    }
```

在每次请求中,prototypeBean都相同,与原型bean作用冲突

分析:当一个属性成员``` serviceImpl ```声明为 ```@Autowired ```后,通过后置处理器设置对应的属性,并且只会执行一次,后面就固定了,所以，**当一个单例的 Bean，使用 autowired 注解标记其属性时，你一定要注意这个属性值会被固定下来。**

解决: 不使用```@Autowired```注入,而是从容器中获取

```java
    @Autowired
    private PrototypeBeanImpl prototypeBean;

    public String b(@PathVariable("name") String name) {
        PrototypeBeanImpl bean = applicationContext.getBean(PrototypeBeanImpl.class);
        log.info("{}",bean);
        return "ok";
    }
```

#### spring bean 依赖注入错误

##### 过多赠予，无所适从

问题: 当一个注入有多个候选bean且无法区分

```java
public interface DataService {
    void delete(String id);
}

@Repository
class OracleDataServiceImpl implements DataService{

    @Override
    public void delete(String id) {
        System.out.println("OracleDataServiceImpl 删除 "+id);
    }
}

@Repository
class CassandraDataService implements DataService{

    @Override
    public void delete(String id) {
        System.out.println("CassandraDataService 删除 "+id);
    }
}


    @Autowired
    DataService dataService;


    @GetMapping(value = "/xx")
    public String delete(){
        dataService.delete("1111");
        return "ok";
    }
```

启动报错:

```Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed```

优先级的决策是先根据 @Primary 来决策，其次是 @Priority 决策，最后是根据 Bean 名字的严格匹配来决策。如果这些帮助决策优先级的注解都没有被使用，名字也不精确匹配，则返回 null，告知无法决策出哪种最合适

解决: 将注入的时候的名字匹配,明确匹配

```java
    @Autowired
    DataService oracleDataServiceImpl;
```

##### @Value 没有注入预期的值

说明: ```@value``` 并不只专属与注入字符串 如可以注入一个内置对象

```java
@Value("#{student}")
private Student student;


@Bean
public Student student(){
    Student student = createStudent(1, "xie");
    return student;
}
```

注入字符串:

```java

//注册正常字符串
@Value("我是字符串")
private String text; 

//注入系统参数、环境变量或者配置文件中的值
@Value("${ip}")
private String ip

//注入其他Bean属性，其中student为bean的ID，name为其属性
@Value("#{student.name}")
private String name;
```

问题: 配置文件中配置如下

```yaml
username=admin
password=pass
```

使用```@Value```注入

```java
@Value("${username}")
private String username;

@Value("${password}")
private String password;
```

结果:username并非配置文件中的值,而是当前这台计算机的用户名

分析:在```value```查找过程中,有多个源,并且一旦查找到就会返回,在````systemEnvironment```系统变量源中有一变量名与```username```重名

解决: 修改变量名,避免冲突

```
user.name1=admin
user.password=pass
```

#### 生命周期错误

##### 构造器内抛空指针异常

```java
@Service
public class LightMgrService {
    @Autowired
    private LightService  lightService ;

    public LightMgrService() {
        lightService.check();
    }
}
```

问题: 希望在```LightMgrService```初始化时调用```LightService```的```check```方法,```LightService```使用```@Autowired```注入方式

分析: 在spring生命周期中,类的构造器调用(newInstance)时间在属性填充(populateBean)之前,所以在构造器调用之前,属性还未被填充

解决1:作为构造器的参数传递.,利用构造器的隐式注入,**使用构造器参数来隐式注入是一种 Spring 最佳实践**

```java
    public LightMgrService(LightService  lightService) {
        lightService.check();
    }
```

解决2: 使用```PostConstruct```或者实现```InitializingBean```接口

在属性填充完之后执行

```java
    @Autowired
    LightService lightService;

    @PostConstruct
    public void init(){
        lightService.check();
    }
}
```

```java
public class LightMgrService implements InitializingBean {
    @Autowired
    LightService lightService;

    @Override
    public void afterPropertiesSet() throws Exception {
        lightService.check();
    }
}
```

#### spring aop 错误

##### this 调用的当前类方法无法被拦截

```java
@Service
public class ElectricService {
public void charge() throws Exception {
    System.out.println("Electric charging ...");
    //this  发起调用
    ElectricService.pay();
}

public void pay() throws Exception {
    System.out.println("Pay with alipay ...");
    Thread.sleep(1000);
}
}
```

切面配置:

```java
@Aspect
@Service
public class AopConfig {
    @Around("execution(* com.zhehe.aop.ElectricService.pay()) ")
    public void recordPayPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        joinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println("Pay method time cost（ms）: " + (end - start));
    }
}
```

问题: 在```charge```使用thi对```pay```进行调用,且```pay```做了切面配置,但是发现调用过程切面没有生效

分析:使用this调用时,该对象只是一个普通对象,并不是经过spring增强代理后的对象, **在需要使用 AOP 时，它会把创建的原始的 Bean 对象 wrap 成代理对象作为 Bean 返回**。所以我们从```spring```中获取的则是一个代理后对象,使用```this```则是一个普通对象

解决:

```java
public class ElectricService {
    @Autowired
    private ElectricService electricService;

    public void charge() throws Exception {
        System.out.println("Electric charging ...");
        electricService.pay();
    }
    public void pay() throws Exception {
        System.out.println("Pay with alipay ...");
        Thread.sleep(1000);
    }
}
```

##### 直接访问被拦截类的属性抛空指针异常

问题:

现在在支付前加一个登录接口login调用,并且为这个login做了切面处理

```java
public void pay() throws Exception {
    adminUserService.login();
    String payNum = adminUserService.adminUser.getPayNum();
    System.out.println("User pay num : " + payNum);
    System.out.println("Pay with alipay ...");
    Thread.sleep(1000);
}

@Service
public class AdminUserService {
    public final User adminUser = new User("202101166");
    public void login() {
        System.out.println("admin user login...");
    }
}

//切面:
@Before("execution(* com.zhehe.aop.AdminUserService.login(..)) ")
public void logAdminLogin(JoinPoint pjp) throws Throwable {
    System.out.println("! admin login ...");
}
```

调用时出现空指针,```adminUserService```的属性为```null```

分析:在动态代理创建过程中,这个类实际上是 ```AdminUserService``` 的一个子类。它会 ```overwrite ```所有``` public``` 和 ```protected ```方法，并在内部将调用委托给原始的 ```AdminUserService``` 实例,并且最终这个代理类使用过```jdk动态代理```创建的,这种方式并不会初始化成员变量

解决:我们在 AdminUserService 里加了个 getUser() 方法：

```java
    public User getAdminUser(){
        return adminUser;
    }
```

为什么方法可以,因为代理在拦截方法进入```intercept```时,在此方法中获取被代理对象进行方法调用

##### 错乱混合不同类型的增强(aop)

问题: 我们现在有一个```charge```方法,现在需要一个增强用于鉴权,一个用于统计时间

```java
    @Before("execution(* com.zhehe.aop.ElectricService.charge()) ")
    public void checkAuthority(JoinPoint pjp) throws Throwable {
        System.out.println("validating user authority");
        Thread.sleep(1000);
    }

    @Around("execution(* com.zhehe.aop.ElectricService.charge()) ")
    public void recordPerformance(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        pjp.proceed(); 
        long end = System.currentTimeMillis();
        System.out.println("charge method time cost: " + (end - start));
    }
```

在统计时间时,发现```Around```把```Before```的执行时间也进行了带入,这不是我们的目的

分析:原因是每个增加是有执行顺序的,同一个切面中，不同类型的增强方法被调用的顺序依次为 Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class。

解决:调正代码逻辑,将方法分开

```java

@Service
public class ElectricService {
    @Autowired
    ElectricService electricService;
    //切面对charge进行鉴权
    public void charge() {
        electricService.doCharge();
    }
    //切面对doCharge进行性能统计
    public void doCharge() {
        System.out.println("Electric charging ...");
    }
}
```

##### 错乱混合同类型增强

问题: 现在仅仅有一个```charge```,但是多个```before```增强

分析: 对同类型的执行顺序只与方法名有关,比较方法名首字母

解决: 通过调整方法名来调整顺序

#### 事件常见错误

##### 部分监听器失效

问题: 当一个事件含有多个监听器时,其中一个监听器发生异常,后面的监听器无法执行,可以使用```@Order(1)```定义顺序

分析: **在事件执行过程中,是由一个线程去顺序执行的,一旦报错,则后面的无法执行了**

解决:保证在事件处理过程中不抛出异常

### spring web

#### 参数校验

##### 对象参数校验无效

问题: 对参数对象进行校验,但未生效

```java
@RestController
@Slf4j
@Validated
public class HelloController {
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public void student(@RequestBody    Student student){
        System.out.println(student.toString());
    }
}

@Data
public class Student {
    @Size(max = 5)
    private String name;
    private short age;
}
```

分析:要对参数student进行校验,必须匹配下面两个条件之一:

标记了``` org.springframework.validation.annotation.Validated ```注解；

标记了其他类型的注解，且注解名称以 Valid 关键字开头。

但是我们的参数都未满足

解决: 在参数上加上```@Validated```即可

```java
@RequestMapping(value = "/student", method = RequestMethod.POST)
public void student(@RequestBody @Validated Student student) {
    System.out.println(student.toString());
}
```

天坑,引包必须是```spring-boot-starter-validation```,否则校验无效

```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

##### 嵌套参数校验无效

问题:

```java
@Data
public class Student {
    @Size(max = 5)
    private String name;
    private short age;
    private Phone phone;
}

@Data
public class Phone {
    @Size(max = 10)
    private String number;
}
```

此时对number的校验无效

分析:在当前案例代码中，phone 字段并没有被 @Valid 标记，所以关于这个字段信息的 cascading 属性肯定是 false，因此在校验 Student 时并不会级联校验它。

解决:

```java
@Valid
private Phone phone;
```

#### 过滤器常见错误

##### @WebFilter 过滤器无法被自动注入

问题:添加一个过滤器,使用```@WebFilter```注解修饰,统计接口耗时

```java
@WebFilter
@Slf4j
public class MyFilter implements Filter {
    public MyFilter(){ System.out.println("construct"); }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("开始计算接口耗时");
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("执行时间(ms)：" + time);
    }
}
```

为了让过滤器生效,在启动程序中,我们需要加上扫描注解（即 ```@ServletComponentScan```）让其生效，启动程序如下

```java
@SpringBootApplication
@ServletComponentScan
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}    
```

当我们想在某个地方,统计接口耗时时间,将```MyFilter```注入,就会写出以下代码:

```java
    @Autowired
    MyFilter myFilter;
```

此时,项目无法启动```Field myFilter in com.zhehe.controller.HelloController required a bean of type 'com.zhehe.filter.MyFilter' that could not be found.```

分析:因为```MyFilter```并没有作为一个```bean```对象注册到容器中,本质上，过滤器被 ```@WebFilter``` 修饰后，```MyFilter```只会被包装为 ```FilterRegistrationBean```，而 ```TimeCostFilter ```自身，只会作为一个 ```InnerBean ```被实例化，这意味着 ```TimeCostFilter ```实例并不会作为 ```Bean ```注册到容器

解决: 将注入的类型改为```FilterRegistrationBean```,并且指定```bean```名称,便于多个过滤器共存

```java
    @Autowired
    @Qualifier("com.zhehe.filter.MyFilter")
    FilterRegistrationBean myFilter;
```

##### doFilter 执行多次

问题:添加一个过滤器,使用```component```搭配```fiter```方式添加过滤器

```java
@Component
public class DemoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try { //模拟异常
            System.out.println("Filter 处理中时发生异常");
            throw new RuntimeException();
        } catch (Exception e) {
            chain.doFilter(request, response);
        }
        chain.doFilter(request, response);
    }
}
```

分析:当抛出异常时,```doFilter```被执行了两次

解决: 删掉```catch```中的调用或者权衡保留一处调用

#### exception常见错误

##### 小心过滤器异常

问题:在过滤器中抛出异常

```java
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        if (!"1".equals(token)) {
            System.out.println("throw NotAllowException");
            throw new NotAllException("not allow");
        }
        chain.doFilter(request, response);
    }
```

全局异常捕获:

```java
@RestControllerAdvice
public class NotAllowExceptionHandler {
    @ExceptionHandler(NotAllException.class)
    @ResponseBody
    public String handle() {
        System.out.println("403");
        return "{\"resultCode\": 403}";
    }
}
```

过滤器中的异常没有被捕获

分析:过滤器被执行完毕后,才会进入```servlet```相关处理,因此过滤器中的异常无法被统一处理

处理：在过滤器中处理异常，

```java
@Autowired
@Qualifier("handlerExceptionResolver")
private HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader("token");
        if (!"1".equals(token)) {
            System.out.println("throw NotAllowException");
            resolver.resolveException(httpServletRequest, httpServletResponse, null, new NotAllException("not allow"));
            //throw new NotAllException("not allow");
        }
        chain.doFilter(request, response);
    }
```

但这种处理方式,接口还是会被调用一次,但是返回是403

### spring补充

#### Spring Data常见错误 

##### 数据读取与存储的一致性,如redis存取使用的序列化方法保持一致

#### spring 事务常见错误

##### unchecked 异常与事务回滚

问题:

```java
@Transactional
public void save() {
        Student student = new Student();
        student.setRealname(realname);
        studentMapper.saveStudent(student);
        if (student.getRealname().equals("小明")) {
            throw new Exception("该学生已存在");
        }
    }
}
```

在事务中抛出```Exception```但是数据依旧被插入

分析:Spring 处理事务的时候，如果没有在 @Transactional 中配置 rollback 属性，那么只有捕获到 RuntimeException 或者 Error 的时候才会触发回滚操作。而我们案例抛出的异常是 Exception，又没有指定与之匹配的回滚规则，所以我们不能触发回滚。

修正:改变异常类型

```java
    @Transactional
    public void save() {
            Student student = new Student();
            student.setRealname(realname);
            studentMapper.saveStudent(student);
            if (student.getRealname().equals("小明")) {
                throw new RuntimeException("该学生已存在");
            }
        }
    }
```

##### 试图给 private 方法添加事务

问题:private方法的事务未生效

分析:只有当注解为事务的方法被声明为 public 的时候，才会被 Spring 处理。

```@Transactional(rollbackFor = Exception.class, noRollbackFor = RuntimeException.class)```

##### 嵌套事务回滚失败

问题: 在一个外部事务中,嵌套调用内部事务,并对该事务进行异常捕获,期望内部事务异常回滚,但外部事务正常执行

```java

  // 外层事务
  @Transactional(rollbackFor = Exception.class)
  public void saveStudent(String realname) throws Exception {
      //......省略逻辑代码.....
      studentService.doSaveStudent(student);
      try {
        // 嵌套的内层事务
        @Transactional(rollbackFor = Exception.class)
        public void regCourse(int studentId) throws Exception {
          //......省略逻辑代码.....
        }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
```

regCourse中抛出异常,事务回滚,但saveStudent也一起回滚了

分析:spring默认的传播机制当前有事务,加入当前事务,没有事务则新建事务,所以在当前外部事务和内部事务共用一个事务,所以一起回滚

解决:修改内部事务的传播级别,新开一个事务

```java

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public void regCourse(int studentId) throws Exception {
    studentCourseMapper.saveStudentCourse(studentId, 1);
    courseMapper.addCourseNumber(1);
    throw new Exception("注册失败");
}
```

#### restTemplate常见错误

##### 参数类型是 MultiValueMap

问题:

```java
    @RequestMapping(path = "/rec", method = RequestMethod.POST)
    public String rec(@RequestParam("p1") String p1, @RequestParam("p2") String p2) {
        return "hi" + p1 + " " + p2;
    }
    
    @RequestMapping(path = "/send",method = RequestMethod.GET)
    public void send(){
        RestTemplate template = new RestTemplate();
        Map paramMap = new HashMap();
        paramMap.put("para1", "001");
        paramMap.put("para2", "002");
        String url = "http://localhost:8082/rec";
        String result = template.postForObject(url, paramMap, String.class);
        System.out.println(result);
    }
```

使用```RestTemplate``` 调用接口,使用```HashMap```传递参数,但是调用```rec```时发现接口 400

分析:在使用```map```进行参数组装时,```RestTemplate```发送会以```json```请求体的方式提交参数,但是```rec```接口接受参数的形式并不是以请求体形式接受参数

解决: 使用```MultiValueMap```代替```map```

```java
@RequestMapping(path = "/send",method = RequestMethod.GET)
public void send(){
    RestTemplate template = new RestTemplate();
    MultiValueMap<String,String> paramMap = new LinkedMultiValueMap();
    paramMap.add("p1", "001");
    paramMap.add("p2", "002");
    String url = "http://localhost:8082/rec";
    String result = template.postForObject(url, paramMap, String.class);
    System.out.println(result);
}
```

小坑: ```url```参数中含有```#```等特殊字符时,需要```urlencode```,但注意小心多次```encode```



