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
            super(c, u);
        }
    }

    private static class SetterException extends RuntimeException {
        private final String message;

        SetterException(String value) {
            super();
            Info info = arrangeStack();
            this.message = String.format("%s.%s, offending value %s", info.name, info.method, value);

        }

        public SetterException(String value, int offset) {
            Info info = arrangeStack();
            this.message = String.format("%s.%s, offending value %s @%d", info.name, info.method, value, offset);
        }

        SetterException(int ic, int kp) {
            super();
            Info info = arrangeStack();
            this.message = String.format("%s.%s, offending char U+%04x @+%d", info.name, info.method, ic, kp);
        }

        private Info arrangeStack() {
            fillInStackTrace();
            List<StackTraceElement> stack = new ArrayList<>(Arrays.asList(getStackTrace()));
            StackTraceElement ste;
            String method;
            do {
                ste = stack.remove(0);
                method = ste.getMethodName();
            } while ((!method.startsWith("get")) && (!method.startsWith("set")));
            setStackTrace(stack.toArray(new StackTraceElement[0]));
            return new Info(ste.getClassName(), method);
        }

        @Override
        public String getMessage() {
            return message;
        }

    }

    public static class NotLatinException extends SetterException {
        public NotLatinException(int c, int u) {
            super(c, u);
        }
    }

    public static class NotValidException extends SetterException {
        public NotValidException(char c, int u) {
            super(c, u);
        }
    }

    public static class NotDigitException extends SetterException {
        public NotDigitException(char c, int u) {
            super(c, u);
        }
    }

    public static class NotBlankException extends SetterException {
        public NotBlankException(char c, int u) {
            super(c, u);
        }
    }

    public static class NotDomainException extends SetterException {
        public NotDomainException(String value) {
            super(value);
        }

        public NotDomainException(int offset, String value) {
            super(value, offset);
        }
    }
    public static class NotMatchesException extends SetterException {
        public NotMatchesException(String value) {
            super(value);
        }

        public NotMatchesException(int offset, String value) {
            super(value, offset);
        }
    }
    private static class Info {
        private final String method;
        private final String name;

        public Info(String className, String method) {
            this.name = className;
            this.method = method;
        }
    }

}
