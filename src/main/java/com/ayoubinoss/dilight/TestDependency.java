package com.ayoubinoss.dilight;

import com.ayoubinoss.dilight.annotations.Component;

@Component
public class TestDependency {

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
