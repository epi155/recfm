import java.text.NumberFormat;
import java.util.Arrays;

abstract class FixEngine {
    private static final String FIELD_AT = "Field @";
    private static final String EXPECTED = " expected ";
    private static final String CHARS_FOUND = " chars , found ";
    private static final String FOR_FIELD_AT = "> for field @";
    private static final String INVALID_NUMERIC = "Invalid numeric value <";
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

    protected void setAbc(String s, int offset, int count, OverflowAction overflowAction, UnderflowAction underflowAction, char pad) {
        if (s == null) {
            if (underflowAction == UnderflowAction.Error)
                throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + " null");
            fillChar(offset, count, ' ');
        } else if (s.length() == count)
            setAbcAsIs(s, offset);
        else if (s.length() < count) {
            switch (underflowAction) {
                case PadRight:
                    padAbcToRight(s, offset, count, pad);
                    break;
                case PadLeft:
                    padAbcToLeft(s, offset, count, pad);
                    break;
                case Error:
                    throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
            }
        } else switch (overflowAction) {
            case TruncRight:
                truncAbcRight(s, offset, count);
                break;
            case TruncLeft:
                truncAbcLeft(s, offset, count);
                break;
            case Error:
                throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        }
    }

    protected String spaceNull(String s) {
        return isBlank(s) ? null : s;
    }

    private void truncAbcLeft(String s, int offset, int count) {
        for (int u = s.length() - 1, v = offset + count - 1; v >= offset; u--, v--) {
            rawData[v] = s.charAt(u);
        }
    }

    private void truncAbcRight(String s, int offset, int count) {
        for (int u = 0, v = offset; u < count; u++, v++) {
            rawData[v] = s.charAt(u);
        }
    }

    protected void setNum(String s, int offset, int count, OverflowAction ovfl, UnderflowAction unfl, char fill) {
        if (s == null) {
            if (unfl == UnderflowAction.Error)
                throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + " null");
            fillChar(offset, count, fill);
        } else if (s.length() == count)
            setNumAsIs(s, offset);
        else if (s.length() < count) {
            switch (unfl) {
                case PadRight:
                    padNumToRight(s, offset, count);
                    break;
                case PadLeft:
                    padNumToLeft(s, offset, count);
                    break;
                case Error:
                    throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
            }
        } else switch (ovfl) {
            case TruncRight:
                truncNumRight(s, offset, count);
                break;
            case TruncLeft:
                truncNumLeft(s, offset, count);
                break;
            case Error:
                throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        }
    }

    private void fillChar(int offset, int count, char fill) {
        for (int u = 0, v = offset; u < count; u++, v++) {
            rawData[v] = fill;
        }
    }

    private void truncNumLeft(String s, int offset, int count) {
        for (int u = s.length() - 1, v = offset + count - 1; v >= offset; u--, v--) {
            if (moveNum(s.charAt(u), v))
                throw new FixError.InvalidNumberException(INVALID_NUMERIC + s + FOR_FIELD_AT + offset);
        }
    }

    private void truncNumRight(String s, int offset, int count) {
        for (int u = 0, v = offset; u < count; u++, v++) {
            if (moveNum(s.charAt(u), v))
                throw new FixError.InvalidNumberException(INVALID_NUMERIC + s + FOR_FIELD_AT + offset);
        }
    }

    private void padNumToLeft(String s, int offset, int count) {
        int u = s.length() - 1;
        int v = offset + count - 1;
        for (; u >= 0; u--, v--) {
            if (moveNum(s.charAt(u), v))
                throw new FixError.InvalidNumberException(INVALID_NUMERIC + s + FOR_FIELD_AT + offset);
        }
        for (; v >= offset; v--) {
            rawData[v] = '0';
        }
    }

    private void padNumToRight(String s, int offset, int count) {
        int u = 0;
        int v = offset;
        for (; u < s.length(); u++, v++) {
            if (moveNum(s.charAt(u), v))
                throw new FixError.InvalidNumberException(INVALID_NUMERIC + s + FOR_FIELD_AT + offset);
        }
        for (; u < count; u++, v++) {
            rawData[v] = '0';
        }
    }

    private void setNumAsIs(String s, int offset) {
        for (int u = 0, v = offset; u < s.length(); u++, v++) {
            if (moveNum(s.charAt(u), v))
                throw new FixError.InvalidNumberException(INVALID_NUMERIC + s + FOR_FIELD_AT + offset);
        }
    }

    private boolean moveNum(char c, int v) {
        if ('0' <= c && c <= '9') {
            rawData[v] = c;
            return false;
        } else
            return true;
    }

    private void padAbcToLeft(String s, int offset, int count, char c) {
        int u = s.length() - 1;
        int v = offset + count - 1;
        for (; u >= 0; u--, v--) {
            rawData[v] = s.charAt(u);
        }
        for (; v >= offset; v--) {
            rawData[v] = c;
        }
    }

    protected void fill(int offset, int count, char c) {
        for (int u = offset, v = 0; v < count; u++, v++) {
            rawData[u] = c;
        }
    }

    private void padAbcToRight(String s, int offset, int count, char c) {
        int u = 0;
        int v = offset;
        for (; u < s.length(); u++, v++) {
            rawData[v] = s.charAt(u);
        }
        for (; u < count; u++, v++) {
            rawData[v] = c;
        }
    }

    private void setAbcAsIs(String s, int offset) {
        for (int u = 0, v = offset; u < s.length(); u++, v++) {
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

    protected boolean checkDigSp(String name, int offset, int count, FieldValidateHandler handler) {
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

    protected static void testAscii(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            char c = raw[u];
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotAsciiException(c, u);
            }
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

    protected void fill(int offset, int count, String s) {
        if (s.length() == count)
            setAbcAsIs(s, offset);
        else if (s.length() < count) {
            throw new FixError.FieldUnderFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
        } else {
            throw new FixError.FieldOverFlowException(FIELD_AT + offset + EXPECTED + count + CHARS_FOUND + s.length());
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

    protected static void testLatin(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            int c = (raw[u] & 0xff7f);
            if (!(32 <= c && c <= 127)) {
                throw new FixError.NotLatinException(c, u);
            }
        }
    }

    protected static void testValid(String value) {
        if (value == null) return;
        char[] raw = value.toCharArray();
        for (int u = 0; u < raw.length; u++) {
            char c = raw[u];
            if (Character.isISOControl(c) || !Character.isDefined(c)) {
                throw new FixError.NotValidException(c, u);
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
