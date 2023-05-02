package com.ayoubinoss.dilight;

import com.ayoubinoss.dilight.annotations.Component;

/**
 * @author ayoubinoss
 */

@Component
public class Test {

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
