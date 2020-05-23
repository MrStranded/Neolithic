package engine.parser.scripts.exceptions;

public class NotEnoughParametersException extends ScriptInterruptedException {
    public NotEnoughParametersException() {
        super();
    }
    public NotEnoughParametersException(String message) {
        super(message);
    }
    public NotEnoughParametersException(String message, Throwable error) {
        super(message, error);
    }
}
