package com.myspring.design.decorator;

/**
 * 定义装饰器角色 持有核心功能的引用
 * @author jlz
 * @date 2022年07月16日 19:42
 */
public abstract class NodeDecorator implements TextNode{
    protected TextNode textNode;

    public NodeDecorator(TextNode textNode) {
        this.textNode = textNode;
    }
}
