数据类型应用场景

string: 点赞数,阅读量,喜欢

hash: map(key,map(key,val)):  购物车

list:有序可重复,关注列表,排行榜(每次计算一次) 

set: 抽奖小程序(随机抽取set)     立即参与抽奖(加入set)   点赞(加入点赞)  关注列表(交集 并集)

zset: 排行榜



分布式锁:redlock -> redission

数据类型应用场景

string: 点赞数,阅读量,喜欢

hash: map(key,map(key,val)):  购物车

list:有序可重复,关注列表,排行榜(每次计算一次) 

set: 抽奖小程序(随机抽取set)     立即参与抽奖(加入set)   点赞(加入点赞)  关注列表(交集 并集)

zset: 排行榜



分布式锁:redlock -> redission



lru(Least Recently Used)算法: 最近最少使用  页面置换算法

1 投机取消方法,使用```LinkedHashMap```,具备lru功能

```java
        Map<String, String> map = new LinkedHashMap<String, String>(1,0.75f,true);
        map.put("apple", "苹果");
        map.put("watermelon", "西瓜");
        map.put("banana", "香蕉");
        map.put("peach", "桃子");
        System.out.println(map.keySet()); //[apple, watermelon, banana, peach]
        //get之后 将watermelon提到最后
        String watermelon = map.get("watermelon");
        System.out.println(watermelon);
        System.out.println("+++++++++++++++++++++++");
        System.out.println(map.keySet()); //[apple, banana, peach, watermelon]

public class LRUCache extends LinkedHashMap
{
    public LRUCache(int maxSize)
    {
        super(maxSize, 0.75F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest)
    {
        //逻辑很简单，当大小超出了Map的容量，就移除掉双向队列头部的元素，给其他元素腾出点地来。
        return size() > maxElements;
    }

    private static final long serialVersionUID = 1L;
    protected int maxElements;
}
```

2 手写:

![image-20211025163755968](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20211025163755968.png)
```
public class LRUCacheDemo {
    static class Node<K, V> {
        K key;
        V value;
        Node prev;
        Node next;

        public Node() {
            this.prev = this.next = null;
        }

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.prev = this.next = null;
        }
    }

    /**
     * 双向链表 里面就是我们的node
     * @param <K>
     * @param <V>
     */
    static class DoubleLinkList<K,V>{
        Node<K,V> head;
        Node<K,V> tail;

        public DoubleLinkList(){
            head = new Node<>();
            tail = new Node<>();
            head.next = tail;
            tail.prev = head;
        }

        /**
         * 双向链表添加到头
         */
        public void addHead(Node node){
            node.next = head.next;
            head.next = node;
            node.prev = head;
            head.next.prev = node;
        }

        /**
         * 删除节点
         * @param node
         */
        public void remove(Node node){
            node.next.prev = node.prev;
            node.prev.next = node.next;
            //将当前节点设置前后设置为null
            node.prev = null;
            node.next = null;
        }

        /**
         * 获取最后一个节点
         * @return
         */
        public Node getLast(){
            return tail.prev;
        }
    }

    public int cacheSize;
    /**
     * hash配合Linklist
     */
    Map<Integer, Node<Integer,Integer>> map;
    DoubleLinkList<Integer,Integer> doubleLinkList;


    public LRUCacheDemo(int cacheSize){
        this.cacheSize = cacheSize;
        map = new HashMap<>(cacheSize);
        doubleLinkList = new DoubleLinkList<>();
    }

    public int get(int key){
        if (!map.containsKey(key)) {
            return -1;
        }
        Node<Integer, Integer> node = map.get(key);
        //移除然后增加到队头
        doubleLinkList.remove(node);
        doubleLinkList.addHead(node);
        return node.value;
    }

    public void put(Node node){

    }
}
```

