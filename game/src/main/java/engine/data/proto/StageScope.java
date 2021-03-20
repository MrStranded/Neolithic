package engine.data.proto;

import constants.ScriptConstants;
import engine.data.attributes.Attribute;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.scripts.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.graphics.renderer.color.RGBA;

import java.util.*;
import java.util.function.Function;

public class StageScope {

    // --------------------------------------------------------- Data

    private Map<String, Object> valueMap = new HashMap<>();

    // --------------------------------------------------------- Generic

    public void set(String key, Object value) {
        valueMap.put(key, value);
    }

    private Object get(String key) {
        return valueMap.get(key);
    }

    // --------------------------------------------------------- Specific

    public Optional<Boolean> getBoolean(String key) {
        Object value = get(key);
        return value instanceof Boolean ? Optional.of((Boolean) value) : Optional.empty();
    }

    public Optional<Integer> getInt(String key) {
        Object value = get(key);
        return value instanceof Integer ? Optional.of((Integer) value) : Optional.empty();
    }

    public Optional<Double> getDouble(String key) {
        Object value = get(key);
        return value instanceof Double ? Optional.of((Double) value) : Optional.empty();
    }

    public Optional<String> getString(String key) {
        Object value = get(key);
        return value instanceof String ? Optional.of((String) value) : Optional.empty();
    }

    public Optional<RGBA> getRGBA(String key) {
        Object value = get(key);
        return value instanceof RGBA ? Optional.of((RGBA) value) : Optional.empty();
    }

    public Optional<List<ContainerIdentifier>> getIdList(String key) {
        Object value = get(key);
        return value instanceof List ? Optional.of((List) value) : Optional.empty();
    }

    public BinaryTree<Attribute> getAttributes() {
        Object value = get(ScriptConstants.KEY_ATTRIBUTES);
        return value instanceof BinaryTree ? (BinaryTree) value : null;
    }

    public Attribute getAttribute(int id) {
        BinaryTree<Attribute> attributes = getAttributes();
        return attributes != null ? attributes.get(id) :null;
    }

    public BinaryTree<Script> getScripts() {
        Object value = get(ScriptConstants.KEY_SCIPTS);
        return value instanceof BinaryTree ? (BinaryTree) value : null;
    }

    public Script getScript(int id) {
        BinaryTree<Script> scripts = getScripts();
        return scripts != null ? scripts.get(id) : null;
    }

}
