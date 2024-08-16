package com.ayoubinoss.dilight;

import com.ayoubinoss.dilight.annotations.Component;
import com.ayoubinoss.dilight.annotations.Inject;

/**
 * @author ayoubinoss
 */

@Component
public class Test {

    private Integer value;

    @Inject
    private TestDependency testDependency;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setTestDependencyValue(Integer value) {
        this.testDependency.setValue(value);
    }

    public Integer getTestDependencyValue() {
        return this.testDependency.getValue();
    }
}
