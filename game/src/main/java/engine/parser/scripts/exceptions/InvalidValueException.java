package engine.parser.scripts.exceptions;

public class InvalidValueException extends ScriptInterruptedException {
    public InvalidValueException() {
        super();
    }
    public InvalidValueException(String message) {
        super(message);
    }
    public InvalidValueException(String message, Throwable error) {
        super(message, error);
    }
}
