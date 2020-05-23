package engine.parser.scripts.execution.commands;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.proto.Container;
import engine.data.scripts.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityManipulationCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& effect addEffect (Instance target, Container effectContainer)
                new Command(TokenConstants.ADD_EFFECT.getValue(), 2, (self, parameters) -> {
                    if (parameters.length >= 4) { // addEffect (Instance target, String effectName, int duration, List attributes)
                        Instance target = parameters[0].getInstance();
                        CommandUtils.checkValueExists(target, "target instance");

                        String name = parameters[1].getString();
                        int duration = parameters[2].getInt();
                        List<Variable> attributes = parameters[3].getList();

                        Effect effect = new Effect(-1);
                        effect.setName(name);
                        effect.setRemainingTicks(duration);
                        effect.setSuperInstance(target);

                        readAttributeList(attributes).forEach(attribute -> effect.setAttribute(attribute.getId(), attribute.getValue()));

                        effect.run(ScriptConstants.EVENT_CREATE, new Variable[] {new Variable(target)});
                        target.addEffect(effect);

                        return new Variable(effect);
                    } else { // addEffect (Instance target, Container effectContainer)
                        Instance target = parameters[0].getInstance();
                        CommandUtils.checkValueExists(target, "target instance");

                        int containerId = CommandUtils.getAndCheckContainerID(parameters[1]);

                        Effect effect = new Effect(containerId);
                        effect.setSuperInstance(target);
                        effect.run(ScriptConstants.EVENT_CREATE, new Variable[]{new Variable(target)});
                        target.addEffect(effect);

                        return new Variable(effect);
                    }
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int addPersonalAtt (Instance target, String attributeTextID, int amount)
                new Command(TokenConstants.ADD_PERSONAL_ATTRIBUTE.getValue(), 3, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");

                    String attributeTextID = parameters[1].getString();
                    int attributeValue = parameters[2].getInt();

                    int attributeID = Data.getProtoAttributeID(attributeTextID);
                    CommandUtils.checkAttribute(attributeID, attributeTextID);

                    target.addAttribute(attributeID, attributeValue);

                    return new Variable(attributeValue);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& void addOccupation (Instance instance, int duration[, String callBackScript])
                new Command(TokenConstants.ADD_OCCUPATION.getValue(), 2, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");

                    int duration = parameters[1].getInt();
                    Script callBackScript = null;

                    if (parameters.length >= 3) {
                        callBackScript = parameters[2].getScript();

                        if (callBackScript == null) { // alternatively try to retrieve script from target's container
                            Container container = Data.getContainer(target.getId());
                            if (container != null) {
                                callBackScript = container.getScript(parameters[2].getString());
                            }
                        }
                    }

                    target.addOccupation(duration, callBackScript);

                    return new Variable();
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& Instance change (Instance target, Container / String container)
                new Command(TokenConstants.CHANGE.getValue(), 2, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");

                    int containerId = CommandUtils.getAndCheckContainerID(parameters[1]);

                    target.change(containerId);

                    return new Variable(target);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance create (Container container, Instance holder)
                new Command(TokenConstants.CREATE.getValue(), 2, (self, parameters) -> {
                    Instance holder = parameters[1].getInstance();
                    CommandUtils.checkValueExists(holder, "holder instance");

                    int containerId = CommandUtils.getAndCheckContainerID(parameters[0]);

                    Instance instance = new Instance(containerId);
                    instance.placeInto(holder);

                    instance.run(ScriptConstants.EVENT_PLACE, new Variable[] {parameters[1]});
                    Data.addInstanceToQueue(instance);

                    return new Variable(instance);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& void deleteEffects (Instance target, Container effectContainer)
                new Command(TokenConstants.DELETE_EFFECTS.getValue(), 1, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");

                    int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

                    target.deleteEffects(containerID);

                    return new Variable();
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int destroy (Instance instance)
                new Command(TokenConstants.DESTROY.getValue(), 1, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");

                    instance.destroy();

                    return new Variable();
                })


        );
    }

    private List<Attribute> readAttributeList(List<Variable> attributeValues) {
        List<Attribute> attributes = new ArrayList<>();

        boolean getID = true;
        String attributeTextID = null;

        for (Variable variable : attributeValues) {
            if (getID) {
                if (variable.getType() == DataType.ATTRIBUTE) {
                    attributes.add(variable.getAttribute());
                } else {
                    attributeTextID = variable.getString();
                    getID = false;
                }
            } else {
                int id = Data.getProtoAttributeID(attributeTextID);
                if (id >= 0) {
                    attributes.add(new Attribute(id, variable.getInt()));
                } else {
                    Logger.error("Attribute with textID '" + attributeTextID + "' does not exist!");
                    // no strong exception handling here!
                    // the reason being, that we still try to extract the remaining attributes
                }
                getID = true;
            }
        }

        return attributes;
    }

}
