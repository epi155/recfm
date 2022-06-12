package com.example.tests

import com.example.syss.cams.online.B280v2xReq
import com.example.syss.file.{FooAnag, FooResp}
import io.github.epi155.recfm.scala.FixError
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
    println("dump")
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

  test("testAnag-Ascii") {
    println("ascii")
    val anag = new FooAnag
    assertThrows[FixError.NotAsciiException] { // Result type: Assertion
      anag.taxCode = "LNÑFRC50A01F501X"
    }
    println(anag)
  }
  test("testAnag-Latin") {
    println("latin")
    val anag = new FooAnag
    assertThrows[FixError.NotLatinException] { // Result type: Assertion
      anag.firstName = "Franç€sco"
    }
    println(anag)
  }
  test("testAnag-Valid") {
    println("valid")
    val anag = new FooAnag
    assertThrows[FixError.NotValidException] { // Result type: Assertion
      anag.birdPlace = "Los\u2fe0Agelos"
    }
    println(anag)
  }

}
