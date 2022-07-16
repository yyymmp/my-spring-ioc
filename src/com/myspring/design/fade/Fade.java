package com.myspring.design.fade;

/**
 * @author jlz
 * @date 2022年07月17日 0:06
 */
public class Fade {

    private SubSystem1 system1 = new SubSystem1();
    private SubSystem2 system2 = new SubSystem2();
    private SubSystem3 system3 = new SubSystem3();

    public void process(){
        system1.open();
        system2.open();
        system3.open();
    }
}
