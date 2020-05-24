package engine.graphics.gui.statistics;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.ProtoAttribute;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatisticsPanel extends JPanel {

    private int currentPosition = 0;
    private int width, height;
    private final int border = 30;
    private BufferedImage img;
    private Graphics g;

    public StatisticsPanel(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height-border, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
        g = img.getGraphics();
    }

    public void register(Instance instance) {
        for (int id = 0; id < GameConstants.MAX_ATTRIBUTES; id++) {
            ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

            if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
                double value = instance.getAttributeValue(id);
                StatisticsData.registerAttributeValue(id, (int) value);

                mark(getAbsolutePosition(getRelativePosition(id, value)), protoAttribute.getGuiColor(), 64);
            }
        }
    }

    public void markAttribute(Instance instance, int id, int value) {
        StatisticsData.registerAttributeValue(id, value);
        ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

        if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
            int fullValue = instance.getAttributeValue(id);
            mark(getAbsolutePosition(getRelativePosition(id, fullValue)), protoAttribute.getGuiColor(), 64);
        }
    }

    public void mark(int y, Color color, int alpha) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y, 1, 1);
    }

    public void fatMark(int y, Color color, int alpha) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y-1, 1, 3);
    }

    private int getAbsolutePosition(double relativeHeight) {
        return (int) ((height - border - 1) * (1 - relativeHeight));
    }

    private double getRelativePosition(int id, double value) {
        ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

        if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
            StatisticsData.registerAttributeValue(id, (int) value);
            int lower, upper;
            if (protoAttribute.hasLowerBound()) {
                lower = protoAttribute.getLowerBound();
            } else {
                lower = StatisticsData.getLowest(id);
            }
            if (protoAttribute.hasUpperBound()) {
                upper = protoAttribute.getUpperBound();
            } else {
                upper = StatisticsData.getHighest(id);
            }
            double d = upper - lower;
            return value / d;
        }

        return -1;
    }

    public void tick() {
        g.setColor(new Color (255,255,255));
        g.fillRect(currentPosition + 3, 0, 150, height - border);

        for (int id = 0; id < GameConstants.MAX_ATTRIBUTES; id++) {
            ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

            if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
                double value = StatisticsData.getAverage(id);
                int yPos = getAbsolutePosition(getRelativePosition(id, value));
                fatMark(yPos, protoAttribute.getGuiColor(), 255);

                g.setColor(protoAttribute.getGuiColor().darker());
                g.drawString(((int) (value*100)/100d) + " : " + protoAttribute.getName(),currentPosition + 5, yPos + 5);
            }
        }

        currentPosition = (currentPosition + 1) % width;

        g.setColor(new Color(255,255,255));
        g.drawRect(currentPosition, 0, 1, height - border);

        g.setColor(new Color(255, 150, 100));
        g.drawRect((currentPosition + 1) % width, 0 , 1, height - border);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0,0,width,height);
        g.drawImage(img, 0,border, null);

        engine.data.proto.Container container = Data.getContainer(GameOptions.currentContainerId);
        if (container != null) {
            g.setColor(new Color(200, 200, 200));
            g.drawRect(0,0,width,border);
            g.setColor(new Color(0,0,0));
            g.drawString("Current selection: " + container.getName(), 10, 20);
            g.drawString("Count: " + StatisticsData.getCount(GameOptions.currentContainerId), width/2 + 10, 20);
        }
    }

}
