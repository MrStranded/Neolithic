package engine.graphics.gui.statistics;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.proto.ProtoAttribute;
import engine.data.structures.Registrator;

public class AttributePlotter implements Registrator {

    private StatisticsPanel statisticsPanel;
    private Instance currentInstance;

    public AttributePlotter(StatisticsPanel statisticsPanel) {
        this.statisticsPanel = statisticsPanel;
    }

    public void setCurrentInstance(Instance instance) {
        currentInstance = instance;
    }

    @Override
    public void register(IDInterface idInterface) {
        if (currentInstance == null) {
            return;
        }

        Attribute attribute = (Attribute) idInterface;
        ProtoAttribute protoAttribute = Data.getProtoAttribute(attribute.getId());

        if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
            StatisticsData.registerAttributeValue(attribute.getId(), attribute.getValue());
            int lower, upper;
            if (protoAttribute.isHasLowerBound()) {
                lower = protoAttribute.getLowerBound();
            } else {
                lower = StatisticsData.getLowest(attribute.getId());
            }
            if (protoAttribute.isHasUpperBound()) {
                upper = protoAttribute.getUpperBound();
            } else {
                upper = StatisticsData.getHighest(attribute.getId());
            }
            double d = upper - lower;
            double value = currentInstance.getAttributeValue(attribute.getId());
            statisticsPanel.mark(value / d, protoAttribute.getGuiColor());
        }
    }

}
