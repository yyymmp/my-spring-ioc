package com.myspring.design;

/**
 * @author jlz
 * @date 2022年06月24日 12:36
 */
public class Dcl {

    private Dcl() {
    }

    ;
    private static volatile Dcl dcl;

    public static Dcl getInstance() {
        if (dcl == null) {
            synchronized (Dcl.class) {
                if (dcl == null) {
                    dcl = new Dcl();
                }
            }
        }

        return dcl;
    }
}

