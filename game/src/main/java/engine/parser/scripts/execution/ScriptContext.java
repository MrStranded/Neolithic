package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.parser.scripts.nodes.CommandExpressionNode;

public class ScriptContext {

    private final Instance self;
    private final Script script;
    private final CommandExpressionNode commandNode;

    public ScriptContext(Instance self, Script script, CommandExpressionNode commandNode) {
        this.self = self;
        this.script = script;
        this.commandNode = commandNode;
    }

    public Instance getSelf() {
        return self;
    }

    public Script getScript() {
        return script;
    }

    public CommandExpressionNode getCommandNode() {
        return commandNode;
    }
}
