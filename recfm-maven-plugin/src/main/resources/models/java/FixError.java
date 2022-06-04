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
}
