package engine.graphics.gui.statistics;

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
        frame.setSize(800,600);

        Insets inset = frame.getInsets();
        statistics = new StatisticsPanel(width - (inset.left + inset.right), height - (inset.top + inset.bottom));

        frame.add(statistics);
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
        StatisticsData.clear();
        statistics.tick();
    }

    public void register(Instance instance) {
        StatisticsData.add(instance.getId());

        if (instance.getId() == GameOptions.currentContainerId) {
            attributePlotter.setCurrentInstance(instance);
            BinaryTree<Attribute> tree = instance.getAttributes();
            if (tree != null) {
                tree.forEach(attributePlotter);
            }
        }
    }

}
