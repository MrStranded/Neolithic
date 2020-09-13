package engine.graphics.gui.statistics;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.ProtoAttribute;
import engine.parser.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class StatisticsWindow {

    private int width, height;
    private String title;

    private JFrame frame;
    private StatisticsPanel statistics;
    private InstanceDetailPanel detailPanel;

    private AttributePlotter attributePlotter;

    public StatisticsWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statistics = new StatisticsPanel(width, height);
        detailPanel = new InstanceDetailPanel(width, height);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Statistics", statistics);
        tabbedPane.addTab("Instance Details", detailPanel);

        frame.add(tabbedPane);

        frame.pack();
        frame.setVisible(true);

        attributePlotter = new AttributePlotter(statistics);
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void refresh() {
        if (statistics.isVisible()) {
            statistics.repaint();
        }

        if (detailPanel.isVisible()) {
            detailPanel.repaint();
        }
    }

    public void tick() {
        statistics.tick();

        StatisticsData.clear();
    }

    public void register(Instance instance) {
        StatisticsData.add(instance.getId());

        if (instance.getId() == GameOptions.currentContainerId) {
            statistics.register(instance);
        }
    }

}
