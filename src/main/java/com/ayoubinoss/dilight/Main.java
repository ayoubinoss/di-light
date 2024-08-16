package com.ayoubinoss.dilight;

import com.ayoubinoss.dilight.core.Container;

/**
 * @author ayoubinoss
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Container.boot();
        Container.register(Test.class);
        Test a = (Test) Container.resolve(Test.class);
        a.setValue(12);
        a.setTestDependencyValue(10);
        System.out.println(a.getValue() + ", " + a.getTestDependencyValue());
    }
}
