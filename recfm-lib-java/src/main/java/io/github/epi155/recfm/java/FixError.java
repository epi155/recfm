package io.github.epi155.recfm.java;

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

        SetterException(int ic, int kp) {
            super();
            fillInStackTrace();
            List<StackTraceElement> stack = new ArrayList<>(Arrays.asList(getStackTrace()));
            StackTraceElement ste;
            String method;
            do {
                ste = stack.remove(0);
                method = ste.getMethodName();
            } while ((!method.startsWith("get")) && (!method.startsWith("set")));
            this.message = String.format("%s.%s, offending char U+%04x @+%d", ste.getClassName(), method, ic, kp);

            setStackTrace(stack.toArray(new StackTraceElement[0]));
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
}
