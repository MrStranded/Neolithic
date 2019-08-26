package engine.graphics.gui.statistics;

import engine.data.Data;
import engine.data.options.GameOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatisticsPanel extends JPanel {

    private int currentPosition = 0;
    private int width, height;
    private BufferedImage img;

    public StatisticsPanel(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void mark(double relativeHeight) {
        int y = (int) (height * (1 - relativeHeight));
        Graphics g = img.getGraphics();
        g.setColor(new Color(0,0,0));
        g.drawRect(currentPosition, y, 1, 1);
    }

    public void tick() {
        currentPosition = (currentPosition + 1) % width;

        Graphics g = img.getGraphics();
        g.setColor(new Color(255,255,255));
        g.drawRect(currentPosition, 0, 1, height);

        g.setColor(new Color(255, 150, 100));
        g.drawRect((currentPosition + 1) % width, 0 , 1, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0,0, null);

        engine.data.proto.Container container = Data.getContainer(GameOptions.currentContainerId);
        if (container != null) {
            g.setColor(new Color(0,0,0));
            g.drawString("Current selection: " + container.getName(), 10, 20);
            g.drawString("Count: " + StatisticsData.getCount(GameOptions.currentContainerId), width/2 + 10, 20);
        }
    }

}
