package engine.parser.scripts.execution.commands.math;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.constants.TokenType;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.nodes.AbstractScriptNode;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.scripts.nodes.LiteralNode;
import engine.parser.tokenization.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractCommandTest {

    protected ScriptContext createContext(int numberOfParameters) {
        Instance self = new Instance(0);
        Script script = new Script("scriptId", "testFile", null, Collections.emptyList());
        return new ScriptContext(self, script, createNode(numberOfParameters));
    }

    private CommandExpressionNode createNode(int numberOfParameters) {
        List<AbstractScriptNode> parameterList = new ArrayList<>(numberOfParameters);
        for (int i = 0; i < numberOfParameters; i++) {
            parameterList.add(new LiteralNode(new Token(TokenType.LITERAL, "parameter" + i, 0)));
        }

        return new CommandExpressionNode(null, parameterList);
    }

    protected Variable[] createParameters(Object... values) {
        Variable[] parameters = new Variable[values.length];

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];

            if (value instanceof Integer) {
                parameters[i] = new Variable((Integer) value);
            } else if (value instanceof Double) {
                parameters[i] = new Variable((Double) value);
            } else {
                parameters[i] = new Variable();
            }
        }

        return parameters;
    }

}
