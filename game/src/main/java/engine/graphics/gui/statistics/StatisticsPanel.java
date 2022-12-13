package engine.graphics.gui.statistics;

import constants.GameConstants;
import constants.PropertyKeys;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.variables.Variable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class StatisticsPanel extends JPanel {

    private int currentPosition = 0;
    private int width, height;
    private double verticalPointRange;
    private static final int TOP_BAR_HEIGHT = 30;
    private static final int VERTICAL_MARGIN = 6;
    private BufferedImage img;

    public StatisticsPanel(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        verticalPointRange = (height - TOP_BAR_HEIGHT - VERTICAL_MARGIN*2 - 1.0);
        img = new BufferedImage(width, height - TOP_BAR_HEIGHT, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
    }

    public void register(Instance instance) {
        if (! GameOptions.plotOnlySelectedEntity && instance.getId() != GameOptions.currentContainerId) { return; }
        if (GameOptions.plotOnlySelectedEntity && GameOptions.selectedInstance != instance) { return; }

        StatisticsData.registerStage(instance.getStage());

        // if plot is false, we do not plot the attributes
        boolean plot = instance.getContainer()
                .flatMap(c -> c.getProperty(instance.getStage(), PropertyKeys.PLOT.key()))
                .map(Variable::getBoolean)
                .orElse(Boolean.TRUE);

        if (! plot) { return; }

        // plot attributes
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
        Graphics g = img.getGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y, 1, 1);
    }

    public void fatMark(int y, Color color, int alpha) {
        Graphics g = img.getGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawRect(currentPosition, y-1, 1, 3);
    }

    private int getAbsolutePosition(double relativeHeight) {
        return (int) (verticalPointRange * (1.0 - relativeHeight)) + VERTICAL_MARGIN;
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
            return (value - lower) / d;
        }

        return 0;
    }

    private Color[] stageColors = {
            new Color (255,255,255),
            new Color (255,230,230),
            new Color (230,255,230),
            new Color (230,230,255)
    };

    public void tick() {
        Graphics g = img.getGraphics();

        g.setColor(new Color (255,255,255));
        g.fillRect(currentPosition + 3, 0, 150, height - TOP_BAR_HEIGHT);

        double all = StatisticsData.getStagesCountsSum();
        if (all > 0) {
            int index = 0;
            double behind = 0;
            double h = height - TOP_BAR_HEIGHT;

            for (String stage : StatisticsData.getStages()) {
                double count = StatisticsData.getStageCount(stage);

                g.setColor(stageColors[index++ % stageColors.length]);
                g.fillRect(currentPosition + 3, (int) (h * behind / all), 150, (int) (h * count / all));
                g.setColor(new Color(200,200,200));
                g.drawString(stage + " (" + ((int) count) + ")", currentPosition + 12,  (int) (h * behind / all) + 15);
                behind += count;
            }
        }

        for (int id = 0; id < GameConstants.MAX_ATTRIBUTES; id++) {
            ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

            if (protoAttribute != null && protoAttribute.getGuiColor() != null) {
                double value = StatisticsData.getAverage(id);
                if (value == 0) { continue; }

                int yPos = getAbsolutePosition(getRelativePosition(id, value));
                fatMark(yPos, protoAttribute.getGuiColor(), 255);

                g.setColor(protoAttribute.getGuiColor().darker());
                g.drawString(((int) (value*100)/100d) + " : " + protoAttribute.getName(),currentPosition + 5, yPos + 5);
            }
        }

        currentPosition = (currentPosition + 1) % width;

        g.setColor(new Color(255,255,255));
        g.drawRect(currentPosition, 0, 1, height - TOP_BAR_HEIGHT);

        g.setColor(new Color(255, 150, 100));
        g.drawRect((currentPosition + 1) % width, 0 , 1, height - TOP_BAR_HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0,0,width,height);
        g.drawImage(img, 0, TOP_BAR_HEIGHT, null);

        drawTopBar(g);
    }

    private void drawTopBar(Graphics g) {
        Optional<Container> container = Data.getContainer(GameOptions.currentContainerId);
        if (container.isPresent()) {
            g.setColor(new Color(200, 200, 200));
            g.drawRect(0,0,width, TOP_BAR_HEIGHT);
            g.setColor(new Color(0,0,0));
            String selectedInstance = GameOptions.selectedInstance != null ? " [ " + GameOptions.selectedInstance.getName() + "]" : "";
            g.drawString("Current selection: " + container.get().getName(null) + selectedInstance, 10, 20);
            g.drawString("Count: " + StatisticsData.getCount(GameOptions.currentContainerId), width/2 + 10, 20);
        }
    }

}
