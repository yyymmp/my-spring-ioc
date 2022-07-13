package com.myspring.design.factory.ab;

/**
 * @author jlz
 * @date 2022年07月12日 21:04
 */
public class Toc {

    public static void main(String[] args) {
        SendClient sendClient = new SendClient(new DingDingMsgFactory());

        MsgTemplate msgTemplate = sendClient.getAbstractFactory().msgTemplate();

        msgTemplate.send();
    }
}
