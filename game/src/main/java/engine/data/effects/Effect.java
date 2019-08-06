package engine.data.effects;

import engine.data.attributes.Attribute;
import engine.data.structures.trees.binary.BinaryTree;

public class Effect {

    private String name;
    private int remainingTicks;
    private BinaryTree<Attribute> attributes;

    public Effect (String name) {
        this.name = name;
        this.remainingTicks = -1; // eternal effect

        attributes = new BinaryTree<>();
    }

    public Effect (String name, int remainingTicks) {
        this.name = name;
        this.remainingTicks = remainingTicks;

        attributes = new BinaryTree<>();
    }

    // ###################################################################################
    // ################################ Handling the Effect ##############################
    // ###################################################################################

    public void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public boolean shouldBeRemoved() {
        return (remainingTicks == 0);
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    public int getAttributeValue(int attributeID) {
        Attribute attribute = attributes.get(attributeID);
        return attribute != null ? attribute.getValue() : 0;
    }

    public void addAttrubte(Attribute attribute) {
        attributes.insert(attribute);
    }

}
