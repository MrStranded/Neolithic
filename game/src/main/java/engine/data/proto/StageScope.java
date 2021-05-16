package engine.data.proto;

import constants.ScriptConstants;
import engine.data.attributes.Attribute;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.scripts.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.Variable;
import engine.graphics.renderer.color.RGBA;

import java.util.*;
import java.util.function.Function;

public class StageScope {

    // --------------------------------------------------------- Data

    private Map<String, Variable> valueMap = new HashMap<>();

    // --------------------------------------------------------- Generic

    public void set(String key, Variable value) {
        valueMap.put(key, value);
    }

    public Optional<Variable> get(String key) {
        return Optional.ofNullable(valueMap.get(key));
    }

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public void printProperties(String prefix) {
        valueMap.keySet().forEach(key -> {
            System.out.println(prefix + key + ": " + get(key).map(Variable::toString).orElse("EMPTY"));
        });
    }

}
