package com.myspring.design.decorator;

/**
 * @author jlz
 * @date 2022年07月16日 19:49
 */
public class Client {

    public static void main(String[] args) {
        //包装加粗span
        BoldDecorator boldDecorator = new BoldDecorator((new Span()));
        boldDecorator.setText("hehe");

        System.out.println(boldDecorator.getText());

        //i标签包装加粗包装span
        ItalicNodeDecorator italicNodeDecorator = new ItalicNodeDecorator(new BoldDecorator((new Span())));
        italicNodeDecorator.setText("hello");
        System.out.println(italicNodeDecorator.getText());
    }
}
