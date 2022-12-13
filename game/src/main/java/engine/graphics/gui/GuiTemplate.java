package engine.graphics.gui;

import constants.PropertyKeys;
import engine.data.entities.GuiElement;
import engine.data.variables.Variable;
import engine.parser.utils.Logger;

public enum GuiTemplate {
    VERTICAL,
    HORIZONTAL
    ;

    public static GuiTemplate from(GuiElement element) {
        GuiTemplate template = GuiTemplate.VERTICAL;

        Variable templateString = element.getProperty(PropertyKeys.TEMPLATE);
        if (templateString.notNull()) {
            try {
                template = GuiTemplate.valueOf(templateString.getString().toUpperCase());
            } catch (IllegalArgumentException e) {
                Logger.error("Problem with updating " + element + ". Gui template with name '" + templateString.getString() + "' does not exist!");
            }
        }

        return template;
    }
}
