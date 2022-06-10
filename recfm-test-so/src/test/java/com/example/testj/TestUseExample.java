package com.example.testj;

import com.example.sysj.cams.online.B280v2xReq;
import com.example.sysj.file.FooAnag;
import com.example.sysj.file.FooResp;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestUseExample {
    @Test
    void test01() {
        Assertions.assertAll(() -> {
            val b280 = new B280v2xReq();
            b280.transactionArea().setCdTransazione("IITB");
            b280.transactionArea().setEsitoAgg("0");
            val esitoComplTransaction = b280.transactionArea().getEsitoCompl();
            System.out.println(b280);
        });
    }

    @Test
    void test02() {
        val resp = new FooResp();
        resp.errItem(1).setApplicationId("05");
        resp.errItem(1).setErrorCodeSource("91302");
        resp.errItem(2).setApplicationId("07");
        resp.errItem(2).setErrorCodeSource("38000");
    }

    @Test
    void test03() {
        val anag = new FooAnag();
        anag.setFirstName("Françisco");
        anag.setLastName("La Niña");
        anag.setBirdPlace("Los\uF3A2Agelos");
//        anag.setTaxCode("LNÑFRC50A01F501X");
//        anag.setWeightKg("cento");
        System.out.println(anag);
    }
}
