package engine.graphics.gui.statistics;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.ProtoAttribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class StatisticsWindow {

    private int width, height;
    private String title;

    private JFrame frame;
    private JLabel currentType;
    private StatisticsPanel statistics;
    private JPanel options;

    private AttributePlotter attributePlotter;

    public StatisticsWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statistics = new StatisticsPanel(width, height);
        currentType = new JLabel("Current type: ");
        currentType.setPreferredSize(new Dimension(width, 30));
        options = new JPanel();
        options.setPreferredSize(new Dimension(width, height/2));

        /*frame.getContentPane().add(currentType);
        frame.getContentPane().add(statistics);
        frame.getContentPane().add(options);*/
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
        statistics.tick();
        StatisticsData.clear();
    }

    public void register(Instance instance) {
        StatisticsData.add(instance.getId());

        if (instance.getId() == GameOptions.currentContainerId) {
            /*attributePlotter.setCurrentInstance(instance);
            BinaryTree<Attribute> tree = instance.getAttributes();
            if (tree != null) {
                tree.forEach(attributePlotter);
            }*/
            statistics.register(instance);
        }
    }

}
