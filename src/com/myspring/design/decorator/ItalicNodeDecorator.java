package com.myspring.design.decorator;

/**
 * @author jlz
 * @date 2022年07月16日 20:08
 */
public class ItalicNodeDecorator extends NodeDecorator{

    public ItalicNodeDecorator(TextNode textNode) {
        super(textNode);
    }

    @Override
    public void setText(String text) {
        this.textNode.setText(text);
    }

    @Override
    public String getText() {
        return "<i>"+textNode.getText()+"</i>";
    }
}
