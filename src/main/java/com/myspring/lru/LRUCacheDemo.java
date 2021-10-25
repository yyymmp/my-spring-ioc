package com.myspring.lru;

import java.util.HashMap;
import java.util.Map;

/**
 * @author clearlove
 * @ClassName LRUCacheDemo.java
 * @Description
 * @createTime 2021年10月25日 22:49:00
 */
public class LRUCacheDemo {

    //定义结点
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
     *
     * @param <K>
     * @param <V>
     */
    static class DoubleLinkList<K, V> {

        Node<K, V> head;
        Node<K, V> tail;

        public DoubleLinkList() {
            head = new Node<>();
            tail = new Node<>();
            head.next = tail;
            tail.prev = head;
        }

        /**
         * 双向链表添加到头
         */
        public void addHead(Node node) {
            node.next = head.next;
            head.next = node;
            node.prev = head;
            head.next.prev = node;
        }

        /**
         * 删除节点
         *
         * @param node
         */
        public void remove(Node node) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            //将当前节点设置前后设置为null
            node.prev = null;
            node.next = null;
        }

        /**
         * 获取最后一个节点
         *
         * @return
         */
        public Node getLast() {
            return tail.prev;
        }
    }

    public int cacheSize;
    /**
     * hash配合Linklist
     */
    Map<Integer, Node<Integer, Integer>> map;
    DoubleLinkList<Integer, Integer> doubleLinkList;


    public LRUCacheDemo(int cacheSize) {
        this.cacheSize = cacheSize;
        map = new HashMap<>(cacheSize);
        doubleLinkList = new DoubleLinkList<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node<Integer, Integer> node = map.get(key);
        //移除然后增加到队头
        doubleLinkList.remove(node);
        doubleLinkList.addHead(node);
        return node.value;
    }

    public void put(int key, int val) {
        //是否存在
        if (map.containsKey(key)) {
            //更新map
            Node<Integer, Integer> node = map.get(key);
            node.value = val;
            //更新list
            doubleLinkList.remove(node);
            doubleLinkList.addHead(node);
        } else {
            //坑位满了
            if (map.size() == cacheSize) {
                //删除最后一个
                Node<Integer, Integer> last = doubleLinkList.getLast();
                map.remove(last);
                map.remove(last);
            }
            //加到map中 加入doubleLinkList中
            final Node<Integer, Integer> newNode = new Node<>(key, val);
            map.put(key, newNode);
            doubleLinkList.addHead(newNode);
        }
    }
}