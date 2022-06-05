package com.example.tests

import com.example.syss.cams.online.B280v2xReq
import com.example.syss.file.FooResp
import org.scalatest.funsuite.AnyFunSuite

class UseExampleTest extends AnyFunSuite {
  def test01(): Unit = {
    val b280 = new B280v2xReq
    b280.transactionArea.cdTransazione = "IITB"
    b280.transactionArea.esitoAgg = "0"
    val esitoComplTransaction = b280.transactionArea.esitoCompl
    val s = b280.encode
  }

  test("dump") {
    val b280 = B280v2xReq.decode("123")
    b280.transactionArea.cdTransazione = "IITB"
    b280.transactionArea.esitoAgg = "0"
    val esitoComplTransaction = b280.transactionArea.esitoCompl
    val s = b280.encode
    println(b280.toString)
  }

  def test11(): Unit = {
    val resp = new FooResp
    resp.errItem(1).applicationId = "05"
    resp.errItem(1).errorCodeSource = "91302"
    resp.errItem(2).applicationId = "07"
    resp.errItem(2).errorCodeSource = "38000"
  }
}
