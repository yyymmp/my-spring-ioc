package com.myspring.design.decorator;

/**
 * @author jlz
 * @date 2022年07月16日 19:40
 */
public class Span implements TextNode {

    private String text;

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return "<span> " + text + "</span>";

    }
}
