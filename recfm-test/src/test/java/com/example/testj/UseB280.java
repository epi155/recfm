package com.example.testj;

import com.example.sysj.cams.online.B280v2xReq;
import lombok.val;

public class UseB280 {
    void hello() {
        val b280 = new B280v2xReq();
        b280.transactionArea().setCdTransazione("IITB");
        b280.transactionArea().setEsitoAgg("0");
        val esitoComplTransaction = b280.transactionArea().getEsitoCompl();
    }
}
