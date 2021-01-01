package engine.graphics.gui.statistics;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.behaviour.Occupation;
import engine.data.entities.Instance;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.interaction.SelectedInstance;
import engine.data.options.GameOptions;
import engine.data.proto.Container;
import engine.data.proto.CreatureContainer;
import engine.data.proto.ProtoAttribute;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.data.variables.Variable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class InstanceDetailPanel extends JPanel implements MouseListener {

    private int width, height;
    private BufferedImage img;

    private int currentYPosition = 0;

    private List<Button> buttons;


    public InstanceDetailPanel(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));

        addMouseListener(this);
    }

    public void update() {
        Graphics g = img.getGraphics();

        g.setColor(new Color (255,255,255));
        g.fillRect(0, 0, width, height);

        if (buttons != null) { buttons.clear(); }
        buttons = new ArrayList<>(8);

        g.setColor(new Color (65, 167, 215));
        g.fillRect(10 -3, 10 -3, 15, 20);
        buttons.add(new BackButton(10 -3, 10 -3, 15, 20));

        drawInstance(g, GameOptions.selectedInstance, 30, 10);
        drawAttributes(g, GameOptions.selectedInstance, height / 2);
        drawVariables(g, GameOptions.selectedInstance, 310, height / 2);
        drawEffects(g, GameOptions.selectedInstance, 310, height * 3 / 4);
        drawInstanceSpecificInfo(g, GameOptions.selectedInstance, 710, height / 2);
    }

    private int drawInstance(Graphics g, Instance instance, int xPos, int yPos) {
        if (instance != null) {
            g.setColor(new Color (65, 167, 215));
            g.fillRect(xPos - 3, yPos - 3, 145, 20);
            buttons.add(new InstanceButton(instance, xPos -3, yPos -3, 145, 20));

            g.setColor(new Color (0,0,0));
            g.drawString(instance.getName(), xPos, yPos + 12);

            List<Instance> subInstances = instance.getSubInstances();
            if (subInstances != null) {
                boolean first = true;

                for (Instance sub : subInstances) {
                    if (! first) { yPos += 25; }
                    first = false;

                    yPos = drawInstance(g, sub, xPos + 150, yPos);
                }
            }
        }

        return yPos;
    }

    private void drawAttributes(Graphics g, Instance instance, int yPos) {
        if (instance == null) { return; }

        setCurrentYPosition(yPos);
        BinaryTree<Attribute> tree = instance.getAttributes();

        if (tree != null) {
            tree.forEach(attribute -> {
                ProtoAttribute protoAttribute = Data.getProtoAttribute(attribute.getId());

                if (protoAttribute != null) {
                    Color attributeColor = protoAttribute.getGuiColor();
                    if (attributeColor == null) { attributeColor = new Color(0,0,0); }

                    g.setColor(getInverted(attributeColor));
                    g.fillRect(10 - 3, getCurrentYPosition() - 3, 290, 18);

                    g.setColor(attributeColor);
                    g.drawString(protoAttribute.getName(), 10, getCurrentYPosition() + 12);
                    g.drawString(String.valueOf(instance.getAttributeValue(attribute.getId())), 10 + 200, getCurrentYPosition() + 12);
                    
                    setCurrentYPosition(getCurrentYPosition() + 20);
                }
            });
        }
    }

    private void drawVariables(Graphics g, Instance instance, int xPos, int yPos) {
        if (instance == null) { return; }

        g.setColor(new Color(0,0,0));
        setCurrentYPosition(yPos);
        BinaryTree<Variable> tree = instance.getVariables();

        if (tree != null) {
            tree.forEach(variable -> {
                g.drawString(String.valueOf(variable.toString()), xPos, getCurrentYPosition() + 12);
                setCurrentYPosition(getCurrentYPosition() + 20);
            });
        }
    }

    private void drawEffects(Graphics g, Instance instance, int xPos, int yPos) {
        if (instance == null) { return; }

        g.setColor(new Color(0,0,0));
        setCurrentYPosition(yPos);
        instance.getEffects().forEach(effect -> {
            g.drawString("Effect: " + effect.toString(), xPos, getCurrentYPosition() + 12);
            setCurrentYPosition(getCurrentYPosition() + 20);

            g.drawString("-> should be removed: " + effect.shouldBeRemoved(instance), xPos, getCurrentYPosition() + 12);
            setCurrentYPosition(getCurrentYPosition() + 20);
        });
    }

    private void drawInstanceSpecificInfo(Graphics g, Instance instance, int xPos, int yPos) {
        if (instance == null) { return; }

        Optional<engine.data.proto.Container> container = instance.getContainer();
        if (container.isEmpty()) { return; }

        g.setColor(new Color(0,0,0));

        g.drawString(container.get().getName() + " - " + container.get().getType(), xPos, yPos + 12);
        if (container.get().isRunTickScripts()) {
            g.drawString("(" + instance.getDelayUntilNextTick() + ")", xPos + 200, yPos + 12);
        }
        yPos += 20;

        g.drawString("Mesh: " + container.get().getMeshPath(instance.getStage()), xPos, yPos + 12);
        yPos += 20;

        if (instance.getStage() != null && !ScriptConstants.DEFAULT_STAGE.equals(instance.getStage())) {
            g.drawString("Stage: " + instance.getStage(), xPos + 50, yPos + 12);
            yPos += 20;
        }

        g.drawString(instance.getMemoryAddress(), xPos + 50, yPos + 12);
        yPos += 20;

        if (container.get().getType() == DataType.TILE) {
            g.drawString("Tile height: ", xPos, yPos + 12);
            g.drawString(String.valueOf(instance.getPosition().getHeight()), xPos + 150, yPos + 12);
            yPos += 20;

            g.drawString("Water height: ", xPos, yPos + 12);
            g.drawString(String.valueOf(instance.getPosition().getWaterHeight()), xPos + 150, yPos + 12);
            yPos += 20;
        }

        if (container.get().getType() == DataType.CREATURE) {
            g.drawString("Cur. task: " + SelectedInstance.instance().getCurrentTaskName(), xPos, yPos + 12);
            yPos += 20;

            if (instance.getOccupations() != null) {
                g.drawString("Occupations:", xPos, yPos + 12);
                yPos += 20;

                for (Occupation occupation : instance.getOccupations()) {
                    g.drawString(occupation.toString(), xPos + 10, yPos + 12);
                    yPos += 20;
                }
            }

            CreatureContainer creatureContainer = (CreatureContainer) container.get();
            if (creatureContainer.getDrives(instance.getStage()) != null) {
                g.drawString("Drives:", xPos, yPos + 12);
                yPos += 20;

                for (ContainerIdentifier identifier : creatureContainer.getDrives(instance.getStage())) {
                    Container drive = identifier.retrieve();

                    if (drive != null) {
                        g.drawString(drive.getName(), xPos + 10, yPos + 12);

                        Variable condition = SelectedInstance.instance().getDriveCondition(drive.getTextID());

                        if (condition.notNull()) { // condition is fulfilled
                            g.drawString("triggered", xPos + 110, yPos + 12);

                            Variable weight = SelectedInstance.instance().getDriveWeight(drive.getTextID());
                            g.drawString(weight.getString(), xPos + 170, yPos + 12);
                        }

                        yPos += 20;
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        update();

        g.clearRect(0,0,width,height);
        g.drawImage(img, 0, 0, null);
    }

    private Color getInverted(Color c) {
        return new Color(
                (c.getRed() + 128) % 255,
                (c.getGreen() + 128) % 255,
                (c.getBlue() + 128) % 255
        );
    }

    public int getCurrentYPosition() {
        return currentYPosition;
    }
    public void setCurrentYPosition(int currentYPosition) {
        this.currentYPosition = currentYPosition;
    }

    static class InstanceButton extends Button {
        private Instance instance;

        public InstanceButton(Instance instance, int xPos, int yPos, int width, int height) {
            this.instance = instance;
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = width;
            this.height = height;
        }

        public void click() {
            GameOptions.selectedInstance = instance;
        }
    }

    static abstract class Button {
        int xPos, yPos, width, height;

        public Button() {}
        public Button(int xPos, int yPos, int width, int height) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = width;
            this.height = height;
        }

        public boolean isClicked(int x, int y) {
            return (x >= xPos && x < xPos + width)
                    && (y >= yPos && y < yPos + height);
        }

        abstract void click();
    }

    static class BackButton extends Button {
        public BackButton(int xPos, int yPos, int width, int height) {
            super(xPos, yPos, width, height);
        }

        public void click() {
            if (GameOptions.selectedInstance != null) {
                GameOptions.selectedInstance = GameOptions.selectedInstance.getSuperInstance();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (buttons != null) {
            buttons.forEach(button -> {
                if (button.isClicked(e.getX(), e.getY())) {
                    button.click();
                }
            });
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
