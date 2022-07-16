package com.myspring.design.decorator;

/**
 * 具体的装饰器 ,可派生多个,一个附加功能可附加生成一个装饰器
 *
 * @author jlz
 * @date 2022年07月16日 19:45
 */
public class BoldDecorator extends NodeDecorator {

    public BoldDecorator(TextNode textNode) {
        super(textNode);
    }

    @Override
    public void setText(String text) {
        this.textNode.setText(text);
    }

    @Override
    public String getText() {
        System.out.println("加粗增强");
        return "<br>" + this.textNode.getText() + "<br>";
    }
}
