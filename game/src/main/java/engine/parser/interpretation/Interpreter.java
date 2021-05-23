package engine.parser.interpretation;

import constants.PropertyKeys;
import constants.ResourcePathConstants;
import constants.ScriptConstants;
import engine.data.Data;
import engine.data.proto.Container;
import engine.data.scripts.Script;
import engine.data.attributes.PreAttribute;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.proto.*;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.renderer.color.RGBA;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.scripts.ASTBuilder;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;
import engine.parser.utils.TokenNumerifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter {

	private PeekingIterator<Token> tokenIterator;
	private String currentMod;
	private String currentFile;
	private String currentStage;

	public Interpreter(List<Token> tokens, String currentMod, String currentFile) {
		tokenIterator = new PeekingIterator<>(tokens);
		this.currentMod = currentMod;
		this.currentFile = currentFile;
	}

	// ###################################################################################
	// ################################ Token Consumption ################################
	// ###################################################################################

	/**
	 * If the next token equals the given target, it is consumed and true is returned.
	 * Otherwise, it is not consumed and false is returned.
	 * @param tokenConstant target
	 * @return true, if target was consumed
	 */
	public boolean voluntaryConsume(TokenConstants tokenConstant) {
		try {
			if (tokenConstant != null && tokenConstant.equals(peek())) {
				consume(tokenConstant);
				return true;
			}

		} catch (Exception e) {
			// it's okay. we don't worry because the consume was voluntary to begin with
		}
		return false;
	}

	public Token consume(TokenConstants tokenConstant) throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		Token next = tokenIterator.next();
		if (tokenConstant.equals(next)) {
			return next;

		} else { // error reporting
			String errorMessage = "Wrong Token! Expected " + tokenConstant.getValue()
					+ " but found " + next.getValue()
					+ " on line " + next.getLine();

			Logger.error(errorMessage);
			throw new Exception(errorMessage);

		}
	}

	public Token consume() throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		return tokenIterator.next();
	}

	public Token peek() throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		return tokenIterator.peek();
	}

	// ###################################################################################
	// ################################ Top Level  #######################################
	// ###################################################################################

	public void interpret() throws Exception {
		// outer most level of a script
		while (tokenIterator.hasNext()) {
			Token next = consume();

			if (TokenConstants.ATTRIBUTE.equals(next)) {        // Attribute
				createAttribute();
			} else if (TokenConstants.TILE.equals(next)) {      // Tile
				createEntity(DataType.TILE);
			} else if (TokenConstants.CREATURE.equals(next)) {  // Creature
				createEntity(DataType.CREATURE);
			} else if (TokenConstants.EFFECT.equals(next)) {    // Effect
				createEntity(DataType.EFFECT);
			} else if (TokenConstants.ENTITY.equals(next)) {    // Entity
				createEntity(DataType.ENTITY);
			} else if (TokenConstants.DRIVE.equals(next)) {     // Drive
				createEntity(DataType.DRIVE);
			} else if (TokenConstants.PROCESS.equals(next)) {   // Process
				createEntity(DataType.PROCESS);
			} else if (TokenConstants.FORMATION.equals(next)) { // Formation
				createEntity(DataType.FORMATION);
			} else if (TokenConstants.WORLDGEN.equals(next)) {  // WorldGen
                createEntity(DataType.WORLDGEN);
            } else if (TokenConstants.SCRIPT.equals(next)) { // Script
			    addScript(Data.getMainContainer());
			}
		}
	}

	// ###################################################################################
	// ################################ Lists ############################################
	// ###################################################################################

	/**
	 * A TokenConstants.VALUES_ATTRIBUTES has been encountered. So let's add some Attributes to the container, shall we?
	 * @param container to add the attributes to
	 * @throws Exception
	 */
	private void feedAttributes(Container container) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = consume())) {
			consume(TokenConstants.COMMA);
			int value = TokenNumerifier.getInt(consume());
			int variation = 0;
			double variationProbability = 0;

			if (TokenConstants.COMMA.equals(peek())) {
				consume(TokenConstants.COMMA);
				variation = TokenNumerifier.getInt(consume());
				variationProbability = 1d;
			}

			if (TokenConstants.COMMA.equals(peek())) {
				consume(TokenConstants.COMMA);
				variationProbability = TokenNumerifier.getDouble(consume());

				if (TokenConstants.MODULO.equals(peek())) {
					variationProbability /= 100d;
					consume(TokenConstants.MODULO);
				}
			}

			voluntaryConsume(TokenConstants.SEMICOLON);

            PreAttribute preAttribute = new PreAttribute(next.getValue(), currentStage,
					value, variation, variationProbability);
            container.addPreAttribute(preAttribute);
		}
	}

	private List<ContainerIdentifier> readTextIDList() throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		List<ContainerIdentifier> result = new ArrayList<>(4);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = consume())) {
			voluntaryConsume(TokenConstants.SEMICOLON);

			if (next.getType() != TokenType.LITERAL && next.getType() != TokenType.IDENTIFIER) {
				Logger.parsingError("Disallowed token type", next, this);
				continue;
			}

			if (next.getValue() != null && next.getValue().length() > 0) {
				result.add(new ContainerIdentifier(next.getValue()));
			}
		}

		return result;
	}

	private void feedPrecursors(List<ContainerIdentifier> identifiers) throws Exception {
		consume(TokenConstants.INHERITS);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_OPEN.equals(peek())) {
			voluntaryConsume(TokenConstants.COMMA);

			next = consume();
			if (next.getValue() != null && next.getValue().length() > 0) {
				identifiers.add(new ContainerIdentifier(next.getValue()));
			}
		}
	}

	// ###################################################################################
	// ################################ Value Helper Functions ###########################
	// ###################################################################################

	// ################################################################################### General

	private Variable readProperty() throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token value = peek();
		Variable result;

		if (TokenConstants.CURLY_BRACKETS_OPEN.equals(value)) {
			result = new Variable(readTextIDList().stream().map(Variable::new).collect(Collectors.toList()));

		} else if (TokenConstants.RGB.equals(value) || TokenConstants.RGBA.equals(value)) {
			consume();
			double r = TokenNumerifier.getDouble(consume());
			consume(TokenConstants.COMMA);
			double g = TokenNumerifier.getDouble(consume());
			consume(TokenConstants.COMMA);
			double b = TokenNumerifier.getDouble(consume());

			double a = 1;
			if (TokenConstants.RGBA.equals(value)) {
				consume(TokenConstants.COMMA);
				a = TokenNumerifier.getDouble(consume());
			}

			result = new Variable(new RGBA(r, g, b, a));

		} else if (TokenConstants.COLOR.equals(value)) {
			consume();
			double r = TokenNumerifier.getDouble(consume());
			consume(TokenConstants.COMMA);
			double g = TokenNumerifier.getDouble(consume());
			consume(TokenConstants.COMMA);
			double b = TokenNumerifier.getDouble(consume());

			result = new Variable(new RGBA(r / 255d, g / 255d, b / 255d));

		} else if (TokenConstants.MESH_PATH.equals(value)) {
			consume();
			Token pathToken = consume();
			result = new Variable(
					ResourcePathConstants.MOD_FOLDER + currentMod + "/" + ResourcePathConstants.MESH_FOLDER + pathToken.getValue()
			);

		} else if (TokenNumerifier.isNumber(value, true)) {
			result = new Variable(TokenNumerifier.getDouble(consume()));

		} else if (TokenNumerifier.isNumber(value, false)) {
			result = new Variable(TokenNumerifier.getInt(consume()));

		} else if (TokenConstants.TRUE.equals(value)) {
			consume();
			result = new Variable(true);

		} else if (TokenConstants.FALSE.equals(value)) {
			consume();
			result = new Variable(false);

		} else {
			result = new Variable(consume().getValue());
		}

		voluntaryConsume(TokenConstants.SEMICOLON);
		return result;
	}

	// ################################################################################### Attribute

    private void readName(ProtoAttribute protoAttribute) throws Exception {
        consume(TokenConstants.ASSIGNMENT);

        Token name = consume();
        protoAttribute.setName(name.getValue());

		voluntaryConsume(TokenConstants.SEMICOLON);
    }

    private void addInherited(ProtoAttribute protoAttribute) {
        protoAttribute.setInherited(true);

		voluntaryConsume(TokenConstants.SEMICOLON);
    }

	private void addLowerBound(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token value = consume();
		protoAttribute.setLowerBound(TokenNumerifier.getInt(value));

		voluntaryConsume(TokenConstants.SEMICOLON);
	}

	private void addUpperBound(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token value = consume();
		protoAttribute.setUpperBound(TokenNumerifier.getInt(value));

		voluntaryConsume(TokenConstants.SEMICOLON);
	}

	private void addBounds(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token lower = consume();
		protoAttribute.setLowerBound(TokenNumerifier.getInt(lower));

		consume(TokenConstants.COMMA);

		Token upper = consume();
		protoAttribute.setUpperBound(TokenNumerifier.getInt(upper));

		voluntaryConsume(TokenConstants.SEMICOLON);
	}

	private void addGuiColor(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);
		Token r = consume();
		consume(TokenConstants.COMMA);
		Token g = consume();
		consume(TokenConstants.COMMA);
		Token b = consume();

		protoAttribute.setGuiColor(new Color(TokenNumerifier.getInt(r),TokenNumerifier.getInt(g),TokenNumerifier.getInt(b)));

		voluntaryConsume(TokenConstants.SEMICOLON);
	}

	// ###################################################################################
	// ################################ Script ###########################################
	// ###################################################################################

	private void addScript(Container container) throws Exception {
		consume(TokenConstants.COLON);
		Token textId = consume();

		if (textId.getValue() != null && textId.getValue().length() > 0) {
			Script script = (new ASTBuilder(this)).buildScript(textId.getValue());
			container.addScript(currentStage, script);
		} else {
			String errorMessage = "Script has no textID (Script : textID { ...) on line " + textId.getLine();

			Logger.error(errorMessage);
			throw new Exception(errorMessage);
		}
	}

	// ###################################################################################
	// ################################ Attribute ########################################
	// ###################################################################################

	private void createAttribute() throws Exception {
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		ProtoAttribute protoAttribute = new ProtoAttribute(textId.getValue());
		Data.addProtoAttribute(protoAttribute);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				readName(protoAttribute);

			} else if (TokenConstants.VALUE_INHERITED.equals(next)) { // attribute is inherited
				addInherited(protoAttribute);

			} else if (TokenConstants.VALUE_LOWER_BOUND.equals(next)) { // lower bound
				addLowerBound(protoAttribute);
			} else if (TokenConstants.VALUE_UPPER_BOUND.equals(next)) { // upper bound
				addUpperBound(protoAttribute);
			} else if (TokenConstants.VALUE_BOUNDS.equals(next)) { // lower bound, upper bound
				addBounds(protoAttribute);

			} else if (TokenConstants.VALUE_GUI_COLOR.equals(next)) { // gui color
				addGuiColor(protoAttribute);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Attribute definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	// ###################################################################################
	// ################################ Entity ###########################################
	// ###################################################################################

	private void createEntity(final DataType type) throws Exception {
		consume(TokenConstants.COLON);
		Token textID = consume();
		currentStage = ScriptConstants.DEFAULT_STAGE;

		List<ContainerIdentifier> precursors = null;
		if (TokenConstants.INHERITS.equals(peek())) {
			precursors = new ArrayList<>(0);
			feedPrecursors(precursors);
		}

		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		// does such a container already exist?
		Container setupContainer = Data.getContainer(Data.getContainerID(textID.getValue())).orElse(null);
		if (setupContainer != null) {
			if (setupContainer.getType() != type) {
				String errorMessage = "Line " + textID.getLine()
						+ ": An object with textID '" + textID.getValue()
						+ "' already exists, but has type " + setupContainer.getType()
						+ " instead of " + type + ".";

				Logger.error(errorMessage);
				throw new Exception(errorMessage);
			}

		} else {
			// container creation
			switch (type) {
				case ENTITY:
					setupContainer = new Container(textID.getValue(), DataType.ENTITY);
					break;
				case EFFECT:
					setupContainer = new Container(textID.getValue(), DataType.EFFECT);
					break;
				case TILE:
					setupContainer = new TileContainer(textID.getValue());
					break;
				case CREATURE:
					setupContainer = new CreatureContainer(textID.getValue());
					break;
				case DRIVE:
					setupContainer = new DriveContainer(textID.getValue());
					break;
				case PROCESS:
					setupContainer = new ProcessContainer(textID.getValue());
					break;
				case FORMATION:
					setupContainer = new Container(textID.getValue(), DataType.FORMATION);
					break;
				case WORLDGEN:
					setupContainer = new Container(textID.getValue(), DataType.WORLDGEN);
					break;
				default:
					Logger.error("Unknown entity constructor type '" + type + "' for '" + textID.getValue() + "' on line " + textID.getLine());
					return;
			}

			if (precursors != null) {
				setupContainer.setInheritedContainers(precursors);
			}
			Data.addContainer(setupContainer);
		}

		// container filling
		readStage(setupContainer, type);
//		setupContainer.printProperties();
	}

	private void readStage(Container container, DataType type) throws Exception {
		while (true) {
			Token next = consume();

				// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% attribute lists
			if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				feedAttributes(container);

				// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% scripts
			} else if (TokenConstants.SCRIPT.equals(next)) { // Script
				addScript(container);

				// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% stage scopes
			} else if (TokenConstants.STAGE.equals(next)) { // new Stage scope
				String previousStage = currentStage;

				consume(TokenConstants.COLON);
				currentStage = consume().getValue();
				consume(TokenConstants.CURLY_BRACKETS_OPEN);

				readStage(container, type);
				currentStage = previousStage;

				// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end of stage scope
			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition || stage scope
				return;

				// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% properties
			} else {
				Token operator = peek();

				if (TokenConstants.ASSIGNMENT.equals(operator)) {
					String property = next.getValue();
					container.setProperty(currentStage, property, readProperty());

					if (Arrays.stream(PropertyKeys.values())
							.noneMatch(propertyKey -> propertyKey.key().equals(property))
					) {
						Logger.debug("Property that does not exist in PropertyKeys was set on " + container.getTextID() + ": " + property);
					}

				} else {
					Logger.error("Unknown Entity definition command '" + next.getValue() + "' on line " + next.getLine());
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String getCurrentMod() {
		return currentMod;
	}

	public String getCurrentFile() {
		return currentFile;
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	private void issueTypeError(Token command, DataType type) {
		Logger.error("The definition command '" + command.getValue() + "' is not applicable to the type " + type + " (line " + command.getLine() + ")");
	}

	/**
	 * This is for debugging only! Using this during a real parsing run will disrupt the process!
	 * This method dumps the remainder of the iterator on the Console. The iterator is left empty!
	 */
	public void printRemainder() {
		while (tokenIterator.hasNext()) {
			System.out.println("--->   " + tokenIterator.next());
		}
	}

}
