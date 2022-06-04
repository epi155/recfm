package com.nexi.obi.cams.online;

import lombok.val;

public class UseB280 {
    void hello() {
        val b280 = new B280v2xReq();
        b280.transactionArea().setCdTransazione("IITB");
        b280.transactionArea().setEsitoAgg("0");
        val esitoComplTransaction = b280.transactionArea().getEsitoCompl();
    }
}
