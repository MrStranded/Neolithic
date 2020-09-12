package engine.graphics.gui.statistics;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
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
        statisticsPanel.markAttribute(currentInstance, attribute.getId(), attribute.getValue());
    }

}