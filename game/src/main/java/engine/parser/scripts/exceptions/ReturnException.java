package engine.parser.scripts.exceptions;

import engine.data.variables.Variable;

public class ReturnException extends ScriptInterruptedException {
    Variable returnValue = new Variable();

    public ReturnException() {
        super();
    }
    public ReturnException(String message) {
        super(message);
    }
    public ReturnException(String message, Throwable error) {
        super(message, error);
    }

    public ReturnException(Variable returnValue) {
        this.returnValue = returnValue;
    }

    public Variable getReturnValue() {
        return returnValue;
    }
}
