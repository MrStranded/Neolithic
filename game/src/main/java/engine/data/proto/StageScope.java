package engine.data.proto;

import engine.data.variables.Variable;
import engine.parser.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            Logger.raw(prefix + key + ": " + get(key).map(Variable::toString).orElse("EMPTY"));
        });
    }

}
