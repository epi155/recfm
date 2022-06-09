
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FixError {
    private FixError() {
    }

    public static class FieldOverFlowException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public FieldOverFlowException(String s) {
            super(s);
        }
    }

    public static class FieldUnderFlowException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public FieldUnderFlowException(String s) {
            super(s);
        }
    }

    public static class InvalidNumberException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InvalidNumberException(String s) {
            super(s);
        }
    }

    public static class RecordOverflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public RecordOverflowException(String s) {
            super(s);
        }
    }

    public static class RecordUnderflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public RecordUnderflowException(String s) {
            super(s);
        }
    }

    public static class NotAsciiException extends SetterException {
        public NotAsciiException(char c, int u) {
            super(String.format("Offending char: U+%04X @+%d", (int) c, u + 1));
        }
    }

    private static class SetterException extends RuntimeException {
        SetterException(String message) {
            super(message);
            fillInStackTrace();
            List<StackTraceElement> stack = new ArrayList<>(Arrays.asList(getStackTrace()));
            stack.remove(0);
            setStackTrace(stack.toArray(new StackTraceElement[0]));
        }
    }

    public static class NotLatinException extends SetterException {
        public NotLatinException(int c, int u) {
            super(String.format("Offending char: U+%04X @+%d", c, u + 1));
        }
    }

    public static class NotValidException extends SetterException {
        public NotValidException(char c, int u) {
            super(String.format("Offending char: U+%04X @+%d", (int) c, u + 1));
        }
    }
}
