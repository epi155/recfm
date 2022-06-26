package io.github.epi155.recfm.java;

import java.text.NumberFormat;
import java.util.Arrays;

abstract class FixEngine {
    private static final String FIELD_AT = "Field @";
    private static final String EXPECTED = " expected ";
    private static final String CHARS_FOUND = " chars , found ";
    private static final String RECORD_LENGTH = "Record length ";
    protected final char[] rawData;

    protected FixEngine(int length) {
        this.rawData = new char[length];
    }

    protected FixEngine(char[] c, int lrec, boolean overflowError, boolean underflowError) {
        if (c.length == lrec) {
            rawData = c;
        } else if (c.length > lrec) {
            if (overflowError)
                throw new FixError.RecordOverflowException(RECORD_LENGTH + c.length + EXPECTED + lrec);
            rawData = Arrays.copyOfRange(c, 0, lrec);
        } else {
            if (underflowError)
                throw new FixError.RecordUnderflowException(RECORD_LENGTH + c.length + EXPECTED + lrec);
            this.rawData = new char[lrec];
            initialize();
            System.arraycopy(c, 0, rawData, 0, c.length);
        }
    }

    private static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected abstract void initialize();

    protected String getAbc(int offset, int count) {
        return new String(rawData, offset, count);
    }

    protected static void testDigit(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            char c = raw[u];
            if (!('0' <= c && c <= '9')) {
                throw new FixError.NotDigitException(c, u + 1);
            }
        }
    }

    protected String spaceNull(String s) {
        return isBlank(s) ? null : s;
    }

    protected static void testAscii(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            char c = raw[u];
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotAsciiException(c, u + 1);
            }
        }
    }

    protected static void testLatin(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            int c = (raw[u] & 0xff7f);
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotLatinException(c, u + 1);
            }
        }
    }

    protected static void testValid(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            char c = raw[u];
            if (Character.isISOControl(c) || !Character.isDefined(c)) {
                throw new FixError.NotValidException(c, u + 1);
            }
        }
    }

    private void fillChar(int offset, int count, char fill) {
        for (int u = 0, v = offset; u < count; u++, v++) {
            rawData[v] = fill;
        }
    }

    protected void setAbc(String s, int offset, int count, OverflowAction overflowAction, UnderflowAction underflowAction, char pad, char init) {
        if (s == null) {
            if (underflowAction == UnderflowAction.Error)
                throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + " null");
            fillChar(offset, count, init);
        } else if (s.length() == count)
            setAsIs(s, offset);
        else if (s.length() < count) {
            switch (underflowAction) {
                case PadRight:
                    padToRight(s, offset, count, pad);
                    break;
                case PadLeft:
                    padToLeft(s, offset, count, pad);
                    break;
                case Error:
                    throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
            }
        } else switch (overflowAction) {
            case TruncRight:
                truncRight(s, offset, count);
                break;
            case TruncLeft:
                truncLeft(s, offset, count);
                break;
            case Error:
                throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        }
    }

    protected void fill(int offset, int count, char c) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            rawData[u] = c;
        }
    }

    private void truncLeft(String s, int offset, int count) {
        for (int u = s.length() - 1, v = offset + count - 1; v >= offset; u--, v--) {
            rawData[v] = s.charAt(u);
        }
    }

    private void truncRight(String s, int offset, int count) {
        for (int u = 0, v = offset; u < count; u++, v++) {
            rawData[v] = s.charAt(u);
        }
    }

    protected NumberFormat pic9(int digits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(digits);
        nf.setGroupingUsed(false);
        return nf;
    }

    public String encode() {
        return new String(rawData);
    }

    /**
     * Valida tutti i campi
     *
     * @param handler gestore errore
     * @return <b>true</b> in caso di errore, <b>false</b> in assenza di errori
     */
    public boolean validate(FieldValidateHandler handler) {
        return validateFields(handler);
    }

    /**
     * Valida i campi marcati con <i>audit</i>: <b>true</b>
     *
     * @param handler gestore errore
     * @return <b>true</b> in caso di errore, <b>false</b> in assenza di errori
     */
    public boolean audit(FieldValidateHandler handler) {
        return auditFields(handler);
    }

    protected abstract boolean validateFields(FieldValidateHandler handler);

    protected abstract boolean auditFields(FieldValidateHandler handler);

    protected boolean checkDigit(String name, int offset, int count, FieldValidateHandler handler) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (!('0' <= c && c <= '9')) {
                handler.error(name, offset, count, u + 1, ValidateError.NotNumber);
                return true;
            }
        }
        return false;
    }

    protected boolean checkDigitBlank(String name, int offset, int count, FieldValidateHandler handler) {
        char c = rawData[offset];
        if (c == ' ') {
            for (int u = offset + 1, v = 1; v < count; u++, v++) {
                if (rawData[u] != ' ') {
                    handler.error(name, offset, count, u + 1, ValidateError.NotNumber);
                    return true;
                }
            }
        } else if ('0' <= c && c <= '9') {
            for (int u = offset + 1, v = 1; v < count; u++, v++) {
                c = rawData[u];
                if (!('0' <= c && c <= '9')) {
                    handler.error(name, offset, count, u + 1, ValidateError.NotNumber);
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    protected void setNum(String s, int offset, int count, OverflowAction ovfl, UnderflowAction unfl, char fill) {
        if (s == null) {
            if (unfl == UnderflowAction.Error)
                throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + " null");
            fillChar(offset, count, fill);
        } else if (s.length() == count)
            setAsIs(s, offset);
        else if (s.length() < count) {
            switch (unfl) {
                case PadRight:
                    padToRight(s, offset, count, '0');
                    break;
                case PadLeft:
                    padToLeft(s, offset, count, '0');
                    break;
                case Error:
                    throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
            }
        } else switch (ovfl) {
            case TruncRight:
                truncRight(s, offset, count);
                break;
            case TruncLeft:
                truncLeft(s, offset, count);
                break;
            case Error:
                throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        }
    }

    private void padToLeft(String s, int offset, int count, char c) {
        int u = s.length() - 1;
        int v = offset + count - 1;
        for (; u >= 0; u--, v--) {
            rawData[v] = s.charAt(u);
        }
        for (; v >= offset; v--) {
            rawData[v] = c;
        }
    }

    protected boolean checkAscii(String name, int offset, int count, FieldValidateHandler handler) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (!(32 <= c && c <= 127)) {
                handler.error(name, offset, count, u + 1, ValidateError.NotAscii);
                return true;
            }
        }
        return false;
    }

    private void padToRight(String s, int offset, int count, char c) {
        int u = 0;
        int v = offset;
        for (; u < s.length(); u++, v++) {
            rawData[v] = s.charAt(u);
        }
        for (; u < count; u++, v++) {
            rawData[v] = c;
        }
    }

    private void setAsIs(String s, int offset) {
        for (int u = 0, v = offset; u < s.length(); u++, v++) {
            rawData[v] = s.charAt(u);
        }
    }

    protected boolean checkEqual(int offset, int count, FieldValidateHandler handler, String value) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            if (rawData[u] != value.charAt(v)) {
                char cu = rawData[u];
                char cv = value.charAt(v);
                String name = (32 <= cu && cu < 127) ? String.format("[%c:=%c]", cv, cu) : String.format("[%c:=\\u%04x]", cv, (int) cu);
                handler.error(name, offset, count, u + 1, ValidateError.Mismatch);
                return true;
            }
        }
        return false;
    }

    protected void testDigit(int offset, int count) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (!('0' <= c && c <= '9')) {
                throw new FixError.NotDigitException(c, u + 1);
            }
        }
    }

    protected boolean checkLatin(String name, int offset, int count, FieldValidateHandler handler) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            int c = (rawData[u] & 0xff7f);
            if (!(32 <= c && c <= 127)) {
                handler.error(name, offset, count, u + 1, ValidateError.NotLatin);
                return true;
            }
        }
        return false;
    }

    protected void testAscii(int offset, int count) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotAsciiException(c, u + 1);
            }
        }
    }

    protected void fill(int offset, int count, String s) {
        if (s.length() == count)
            setAsIs(s, offset);
        else if (s.length() < count) {
            throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        } else {
            throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        }
    }

    protected void testLatin(int offset, int count) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            int c = (rawData[u] & 0xff7f);
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotLatinException(c, u + 1);
            }
        }
    }

    protected void testValid(int offset, int count) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (Character.isISOControl(c) || !Character.isDefined(c)) {
                throw new FixError.NotValidException(c, u + 1);
            }
        }
    }

    protected boolean checkValid(String name, int offset, int count, FieldValidateHandler handler) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            char c = rawData[u];
            if (!Character.isDefined(c) || Character.isISOControl(c)) {
                handler.error(name, offset, count, u + 1, ValidateError.NotValid);
                return true;
            }
        }
        return false;
    }

    protected static void testDigitBlank(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        if (raw[0] == ' ') {
            for (int u = 1; u < raw.length; u++) {
                char c = raw[u];
                if (c != ' ') {
                    throw new FixError.NotBlankException(c, u + 1);
                }
            }
        } else {
            for (int u = 0; u < raw.length; u++) {
                char c = raw[u];
                if (!('0' <= c && c <= '9')) {
                    throw new FixError.NotDigitException(c, u + 1);
                }
            }
        }
    }

    protected void testDigitBlank(int offset, int count) {
        char c = rawData[offset];
        if (c == ' ') {
            for (int u = offset + 1, v = 1; v < count; u++, v++) {
                if (rawData[u] != ' ') {
                    throw new FixError.NotBlankException(c, u + 1);
                }
            }
        } else {
            for (int u = offset, v = 0; v < count; u++, v++) {
                c = rawData[u];
                if (!('0' <= c && c <= '9')) {
                    throw new FixError.NotDigitException(c, u + 1);
                }
            }
        }
    }


    protected String dump(int offset, int count) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < count; k++) {
            char c = rawData[offset + k];
            if (c <= 32) {
                c = (char) (0x2400 + c);
            } else if (c == 127) {
                c = '\u2421'; // delete
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
