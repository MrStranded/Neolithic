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

    private Map<String, Object> valueMap = new HashMap<>();

    public void set(String key, Object value) {
        valueMap.put(key, value);
    }

    private Object get(String key) {
        return valueMap.get(key);
    }

    private Object getOrCreate(String key, Function<String, Object> create) {
        return valueMap.computeIfAbsent(key, create);
    }

    public int getInt(String key) {
        Object value = get(key);
        return value instanceof Integer ? (Integer) value : 0;
    }

    public double getDouble(String key) {
        Object value = get(key);
        return value instanceof Double ? (Double) value : 0;
    }

    public String getString(String key) {
        Object value = get(key);
        return value instanceof String ? (String) value : "";
    }

    public RGBA getRGBA(String key) {
        Object value = get(key);
        return value instanceof RGBA ? (RGBA) value : new RGBA();
    }

    public List<ContainerIdentifier> getIdList(String key) {
        Object value = getOrCreate(key, k -> new ArrayList<ContainerIdentifier>());
        return value instanceof List ? (List) value : Collections.emptyList();
    }

    public BinaryTree<Attribute> getAttributes() {
        Object value = getOrCreate(ScriptConstants.KEY_ATTRIBUTES, k -> new BinaryTree<Attribute>());
        return value instanceof BinaryTree ? (BinaryTree) value : new BinaryTree<>();
    }

    public Attribute getAttribute(int id) {
        BinaryTree<Attribute> attributes = getAttributes();
        return attributes.get(id);
    }

    public BinaryTree<Script> getScripts() {
        Object value = getOrCreate(ScriptConstants.KEY_SCIPTS, k -> new BinaryTree<Script>());
        return value instanceof BinaryTree ? (BinaryTree) value : new BinaryTree<>();
    }

    public Script getScript(int id) {
        BinaryTree<Script> scripts = getScripts();
        return scripts.get(id);
    }

}
