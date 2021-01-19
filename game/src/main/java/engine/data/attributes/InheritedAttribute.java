package engine.data.attributes;

import engine.data.Data;

public class InheritedAttribute extends Attribute {

    private double variation = 0;
    private double variationProbability = 0;

    public InheritedAttribute(int id) {
        super(id);
    }

    public InheritedAttribute(int id, int value) {
        super(id, value);
    }

    public InheritedAttribute(int id, int value, double variation, double variationProbability) {
        super(id, value);

        this.variation = variation;
        this.variationProbability = variationProbability;
    }

    public int getVariedValue() {
        if (variation == 0) {
            return 0;
        }
        double delta = Math.random() * variation * 2d;
        return (int) Math.round(-variation + delta);
    }

    public double getVariationProbability() { return variationProbability; }

    @Override
    public String toString() {
        return "InheritedAttribute (id = " + Data.getProtoAttribute(getId()).getTextID() + ": " + getValue()
                + ", variation: " + variation
                + ", v. probability: " + variationProbability + ")";
    }
}
