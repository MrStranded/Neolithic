package engine.parser.scripts.exceptions;

public class ScriptInterruptedException extends Exception {
    public ScriptInterruptedException() {
        super();
    }
    public ScriptInterruptedException(String message) {
        super(message);
    }
    public ScriptInterruptedException(String message, Throwable error) {
        super(message, error);
    }
}
