package com.example.sys.file;

import lombok.val;

public class UseOcc {
    void test01() {
        val resp = new FooResp();
        resp.errItem(1).setApplicationId("05");
        resp.errItem(1).setErrorCodeSource("91302");
        resp.errItem(2).setApplicationId("07");
        resp.errItem(2).setErrorCodeSource("38000");
    }
}
