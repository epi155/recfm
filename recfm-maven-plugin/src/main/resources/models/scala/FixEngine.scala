import java.text.NumberFormat
import java.util

abstract class FixEngine(
                          length: Int,
                          s: String,
                          r: FixRecord,
                          overflowError: Boolean, underflowError: Boolean
                        ) {
  final protected var rawData: Array[Char] = _

  if (s != null)
    buildFromString(length, s, overflowError, underflowError)
  else if (r != null)
    buildFromRecord(length, r, overflowError, underflowError)
  else
    buildEmpty(length)

  def encode = new String(rawData)

  def validate(handler: FieldValidateHandler): Boolean = validateFields(handler)

  def audit(handler: FieldValidateHandler): Boolean = auditFields(handler)

  protected def initialize(): Unit

  protected def abc(offset: Int, count: Int) = new String(rawData, offset, count)

  protected def abc(s: String, offset: Int, count: Int, overflowAction: OverflowAction.OverflowAction, underflowAction: UnderflowAction.UnderflowAction, pad: Char, init: Char): Unit = {
    if (s == null) {
      if (underflowAction eq UnderflowAction.Error) throw new FixError.FieldUnderFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + " null")
      fillChar(offset, count, init)
    }
    else if (s.length == count) setAsIs(s, offset)
    else if (s.length < count) underflowAction match {
      case UnderflowAction.PadRight =>
        padToRight(s, offset, count, pad)

      case UnderflowAction.PadLeft =>
        padToLeft(s, offset, count, pad)

      case UnderflowAction.Error =>
        throw new FixError.FieldUnderFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
    }
    else overflowAction match {
      case OverflowAction.TruncRight =>
        truncRight(s, offset, count)

      case OverflowAction.TruncLeft =>
        truncLeft(s, offset, count)

      case OverflowAction.Error =>
        throw new FixError.FieldOverFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
    }
  }

  private def truncLeft(s: String, offset: Int, count: Int): Unit = {
    var u = s.length - 1
    var v = offset + count - 1
    while ( {
      v >= offset
    }) {
      rawData(v) = s.charAt(u)
      u -= 1
      v -= 1
    }
  }

  private def truncRight(s: String, offset: Int, count: Int): Unit = {
    var u = 0
    var v = offset
    while ( {
      u < count
    }) {
      rawData(v) = s.charAt(u)
      u += 1
      v += 1
    }
  }

  private def padToLeft(s: String, offset: Int, count: Int, c: Char): Unit = {
    var u = s.length - 1
    var v = offset + count - 1

    while ( {
      u >= 0
    }) {
      rawData(v) = s.charAt(u)

      u -= 1
      v -= 1
    }

    while ( {
      v >= offset
    }) {
      rawData(v) = c

      v -= 1
    }
  }

  private def padToRight(s: String, offset: Int, count: Int, c: Char): Unit = {
    var u = 0
    var v = offset

    while ( {
      u < s.length
    }) {
      rawData(v) = s.charAt(u)

      u += 1
      v += 1
    }

    while ( {
      u < count
    }) {
      rawData(v) = c

      u += 1
      v += 1
    }
  }

  protected def testAscii(offset: Int, count: Int): Unit = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (!(32 <= c && c <= 127)) throw new FixError.NotAsciiException(c, u)
      u += 1
      v += 1
    }
  }

  protected def num(s: String, offset: Int, count: Int, ovfl: OverflowAction.OverflowAction, unfl: UnderflowAction.UnderflowAction): Unit = {
    if (s == null) {
      if (unfl eq UnderflowAction.Error) throw new FixError.FieldUnderFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + " null")
      fillChar(offset, count, '0')
    }
    else if (s.length == count) setAsIs(s, offset)
    else if (s.length < count) unfl match {
      case UnderflowAction.PadRight =>
        padToRight(s, offset, count, '0')

      case UnderflowAction.PadLeft =>
        padToLeft(s, offset, count, '0')

      case UnderflowAction.Error =>
        throw new FixError.FieldUnderFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
    }
    else ovfl match {
      case OverflowAction.TruncRight =>
        truncRight(s, offset, count)

      case OverflowAction.TruncLeft =>
        truncLeft(s, offset, count)

      case OverflowAction.Error =>
        throw new FixError.FieldOverFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
    }
  }

  private def fillChar(offset: Int, count: Int, fill: Char): Unit = {
    var u = 0
    var v = offset
    while ( {
      u < count
    }) {
      rawData(v) = fill

      u += 1
      v += 1
    }
  }

  protected def fill(offset: Int, count: Int, c: Char): Unit = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      rawData(u) = c

      u += 1
      v += 1
    }
  }

  protected def pic9(digits: Int): NumberFormat = {
    val nf = NumberFormat.getInstance
    nf.setMinimumIntegerDigits(digits)
    nf.setGroupingUsed(false)
    nf
  }

  protected def validateFields(handler: FieldValidateHandler): Boolean

  protected def auditFields(handler: FieldValidateHandler): Boolean

  protected def checkDigit(name: String, offset: Int, count: Int, handler: FieldValidateHandler): Boolean = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (!('0' <= c && c <= '9')) {
        handler.error(name, offset, count, u + 1, ValidateError.NotNumber)
        return true
      }

      u += 1
      v += 1
    }
    false
  }

  protected def checkAscii(name: String, offset: Int, count: Int, handler: FieldValidateHandler): Boolean = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (!(32 <= c && c <= 127)) {
        handler.error(name, offset, count, u + 1, ValidateError.NotAscii)
        return true
      }

      u += 1
      v += 1
    }
    false
  }

  protected def testDigit(offset: Int, count: Int): Unit = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (!('0' <= c && c <= '9')) throw new FixError.NotDigitException(c, u)
      u += 1
      v += 1
    }
  }

  protected def testAscii(value: String): Unit = {
    if (value == null) return
    val raw = value.toCharArray
    for (u <- 0 until raw.length) {
      val c = raw(u)
      if (!(32 <= c && c <= 127)) throw new FixError.NotAsciiException(c, u)
    }
  }

  protected def fill(offset: Int, count: Int, s: String): Unit = {
    if (s.length == count) setAsIs(s, offset)
    else if (s.length < count) throw new FixError.FieldUnderFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
    else throw new FixError.FieldOverFlowException(FixEngine.FIELD_AT + offset + FixEngine.EXPECTED + count + FixEngine.CHARS_FOUND + s.length)
  }

  protected def testDigit(value: String): Unit = {
    if (value == null) return
    val raw = value.toCharArray
    for (u <- 0 until raw.length) {
      val c = raw(u)
      if (!('0' <= c && c <= '9')) throw new FixError.NotDigitException(c, u)
    }
  }

  protected def checkEqual(name: String, offset: Int, count: Int, handler: FieldValidateHandler, value: String): Boolean = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      if (rawData(u) != value.charAt(v)) {
        handler.error(name, offset, count, u + 1, ValidateError.Mismatch)
        return true
      }

      u += 1
      v += 1
    }
    false
  }

  private def setAsIs(s: String, offset: Int): Unit = {
    var u = 0
    var v = offset
    while ( {
      u < s.length
    }) {
      rawData(v) = s.charAt(u)

      u += 1
      v += 1
    }
  }

  protected def checkLatin(name: String, offset: Int, count: Int, handler: FieldValidateHandler): Boolean = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u) & 0xff7f
      if (!(32 <= c && c <= 127)) {
        handler.error(name, offset, count, u + 1, ValidateError.NotLatin)
        return true
      }

      u += 1
      v += 1
    }
    false
  }

  protected def testLatin(offset: Int, count: Int): Unit = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u) & 0xff7f
      if (!(32 <= c && c <= 127)) throw new FixError.NotLatinException(c, u)
      u += 1
      v += 1
    }
  }

  protected def testLatin(value: String): Unit = {
    if (value == null) return
    val raw = value.toCharArray
    for (u <- 0 until raw.length) {
      val c = raw(u) & 0xff7f
      if (!(32 <= c && c <= 127)) throw new FixError.NotLatinException(c, u)
    }
  }

  protected def checkValid(name: String, offset: Int, count: Int, handler: FieldValidateHandler): Boolean = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (Character.isISOControl(c) || !Character.isDefined(c)) {
        handler.error(name, offset, count, u + 1, ValidateError.NotValid)
        return true
      }

      u += 1
      v += 1
    }
    false
  }

  protected def testValid(offset: Int, count: Int): Unit = {
    var u = offset
    var v = 0
    while ( {
      v < count
    }) {
      val c = rawData(u)
      if (Character.isISOControl(c) || !Character.isDefined(c)) throw new FixError.NotValidException(c, u)
      u += 1
      v += 1
    }
  }

  protected def testValid(value: String): Unit = {
    if (value == null) return
    val raw = value.toCharArray
    for (u <- 0 until raw.length) {
      val c = raw(u)
      if (Character.isISOControl(c) || !Character.isDefined(c)) throw new FixError.NotValidException(c, u)
    }
  }

  protected def dump(offset: Int, count: Int): String = {
    val sb = new scala.collection.mutable.StringBuilder
    for (k <- 0 until count) {
      var c = rawData(offset + k)
      if (c <= 32) c = (0x2400 + c).toChar
      else if (c == 127) c = '\u2421' // delete
      sb.append(c)
    }
    sb.toString
  }

  private def buildEmpty(length: Int): Unit = {
    this.rawData = new Array[Char](length)
    initialize()
  }

  private def buildFromString(length: Int, s: String, overflowError: Boolean, underflowError: Boolean): Unit = {
    if (s.length == length) rawData = s.toCharArray
    else if (s.length > length) {
      if (overflowError) throw new FixError.RecordOverflowException(FixEngine.RECORD_LENGTH + s.length + FixEngine.EXPECTED + length)
      rawData = util.Arrays.copyOfRange(s.toCharArray, 0, length)
    }
    else {
      if (underflowError) throw new FixError.RecordUnderflowException(FixEngine.RECORD_LENGTH + s.length + FixEngine.EXPECTED + length)
      this.rawData = new Array[Char](length)
      initialize()
      System.arraycopy(s.toCharArray, 0, rawData, 0, s.length)
    }
  }

  private def buildFromRecord(lrec: Int, r: FixRecord, overflowError: Boolean, underflowError: Boolean): Unit = {
    if (r.rawData.length == lrec) rawData = r.rawData
    else if (r.rawData.length > lrec) {
      if (overflowError) throw new FixError.RecordOverflowException(FixEngine.RECORD_LENGTH + r.rawData.length + FixEngine.EXPECTED + lrec)
      rawData = util.Arrays.copyOfRange(r.rawData, 0, lrec)
    }
    else {
      if (underflowError) throw new FixError.RecordUnderflowException(FixEngine.RECORD_LENGTH + r.rawData.length + FixEngine.EXPECTED + lrec)
      this.rawData = new Array[Char](lrec)
      initialize()
      System.arraycopy(r.rawData, 0, rawData, 0, r.rawData.length)
    }
  }

}

object FixEngine {
  private val FIELD_AT = "Field @"
  private val EXPECTED = " expected "
  private val CHARS_FOUND = " chars , found "
  private val FOR_FIELD_AT = "> for field @"
  private val INVALID_NUMERIC = "Invalid numeric value <"
  private val RECORD_LENGTH = "Record length "

}
