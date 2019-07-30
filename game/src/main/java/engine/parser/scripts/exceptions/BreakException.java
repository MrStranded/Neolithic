package engine.parser.scripts.exceptions;

public class BreakException extends ScriptInterruptedException {
    public BreakException() {
        super();
    }
    public BreakException(String message) {
        super(message);
    }
    public BreakException(String message, Throwable error) {
        super(message, error);
    }
}
