#### 并发安全工具类可以保证线程安全吗

##### 线程重用造成用户信息错乱

在一个springboot工程中,使用ThreadLocal存储当前用户信息

```java
@GetMapping("wrong")
public Map wrong(@RequestParam("userId") Integer userId) {
    //设置用户信息之前先查询一次ThreadLocal中的用户信息
    String before = Thread.currentThread().getName() + ":" + currentUser.get();
    //设置用户信息到ThreadLocal
    currentUser.set(userId);
    // 设置用户信息之后再查询一次ThreadLocal中的用户信息
    String after = Thread.currentThread().getName() + ":" + currentUser.get();
    //汇总输出两次查询结果
    Map result = new HashMap();
    result.put("before", before);
    result.put("after", after);
    return result;
}
```

因为tomcat使用线程池处理请求,当一个请求响应用户1之后,会在该线程的ThreadLocal存储userId,当该线程再次被响应其他用户请求时,此时取出来的用户请求就是上一位用户的

#### 正确加锁

##### 加锁要理清业务逻辑和锁之间的关系

```java
static public class Interesting {
    volatile int a = 1;
    volatile int b = 1;

    public void add() {
        log.info("add start");
        for (int i = 0; i < 10000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    public void compare() {
        log.info("compare start");
        for (int i = 0; i < 10000; i++) {
            //a始终等于b吗？
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
                //最后的a>b应该始终是false吗？
            }
        }
        log.info("compare done");
    }
}
```

启动两个线程分别执行该方法,

```java
      Interesting interesting = new Interesting();
        new Thread(() -> interesting.add()).start();
        new Thread(() -> interesting.compare()).start();
```

期望在比较大小过程中,始终相等而不应该出现不等于的情况,因为add中的a++和b++会穿插着compare执行,造成了在compare中a与b的值并不相等

在add方法加锁并没有用,本身也只有一个方法在执行add方法,根本原因在于a++和b++在compare的前后穿插执行,确保add方法进行累加时,compare不能访问a和b,所以两个方法都需要加上锁

##### 加锁前要清楚锁和被保护的对象是不是一个层面的

```java
public class Data {
    @Getter
    private static int counter = 0;
    public static int reset() {
        counter = 0;
        return counter;
    }
    //对同一实例的操作有加锁作用
    public synchronized void wrong() {
        //共享变量对所有实例共享
        counter++;
    }
}
```

```java
 //多线程循环一定次数调用Data类不同实例的wrong方法  累计调用
 IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().wrong());
```

线程不安全,修改,对所有实例 共用一把锁

```java

    private static final Object lock = new Object(); 
    public synchronized void wrong() {
        synchronized (lock) {
            counter++;
        }
    }
```

