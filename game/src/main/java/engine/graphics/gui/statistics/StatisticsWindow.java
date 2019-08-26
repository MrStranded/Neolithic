package engine.graphics.gui.statistics;

import constants.GameConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.ProtoAttribute;
import engine.data.structures.Registrator;
import engine.data.structures.trees.binary.BinaryTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class StatisticsWindow {

    private int width, height;
    private String title;

    private JFrame frame;
    private JLabel currentType;
    private StatisticsPanel statistics;

    private AttributePlotter attributePlotter;

    public StatisticsWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(width+1,height+1));

        statistics = new StatisticsPanel(width, height);

        frame.add(statistics);
        frame.pack();
        frame.setVisible(true);

        attributePlotter = new AttributePlotter(statistics);
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void refresh() {
        statistics.repaint();
    }

    public void tick() {
        for (int id = 0; id < GameConstants.MAX_ATTRIBUTES; id++) {
            ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

            if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
                double value = StatisticsData.getAverage(id);
                statistics.fatMark(getRelativePosition(id, value), protoAttribute.getGuiColor(), 255);
            }
        }

        StatisticsData.clear();
        statistics.tick();
    }

    public void register(Instance instance) {
        StatisticsData.add(instance.getId());

        if (instance.getId() == GameOptions.currentContainerId) {
            /*attributePlotter.setCurrentInstance(instance);
            BinaryTree<Attribute> tree = instance.getAttributes();
            if (tree != null) {
                tree.forEach(attributePlotter);
            }*/
            for (int id = 0; id < GameConstants.MAX_ATTRIBUTES; id++) {
                ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

                if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
                    double value = instance.getAttributeValue(id);

                    statistics.mark(getRelativePosition(id, value), protoAttribute.getGuiColor(), 64);
                }
            }
        }
    }

    private double getRelativePosition(int id, double value) {
        ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

        if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
            StatisticsData.registerAttributeValue(id, (int) value);
            int lower, upper;
            if (protoAttribute.isHasLowerBound()) {
                lower = protoAttribute.getLowerBound();
            } else {
                lower = StatisticsData.getLowest(id);
            }
            if (protoAttribute.isHasUpperBound()) {
                upper = protoAttribute.getUpperBound();
            } else {
                upper = StatisticsData.getHighest(id);
            }
            double d = upper - lower;
            return value / d;
        }

        return -1;
    }

}
