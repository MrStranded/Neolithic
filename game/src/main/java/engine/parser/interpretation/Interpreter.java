package engine.parser.interpretation;

import constants.ResourcePathConstants;
import engine.data.ContainerIdentifier;
import engine.data.attributes.PreAttribute;
import engine.data.proto.*;
import engine.data.structures.Script;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;
import engine.graphics.renderer.color.RGBA;
import engine.parser.utils.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.scripts.ASTBuilder;
import engine.parser.tokenization.Token;
import engine.parser.utils.TokenNumerifier;

import java.util.List;

public class Interpreter {

	private PeekingIterator<Token> tokenIterator;
	private String currentMod;

	public Interpreter(List<Token> tokens, String currentMod) {
		tokenIterator = new PeekingIterator<>(tokens);
		this.currentMod = currentMod;
	}

	// ###################################################################################
	// ################################ Token Consumption ################################
	// ###################################################################################

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
		// attributes { attOne, 10; attTwo, 7; }
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = consume())) {
			consume(TokenConstants.COMMA);
			Token token = consume();
			consume(TokenConstants.SEMICOLON);

			PreAttribute preAttribute = new PreAttribute(next.getValue(), TokenNumerifier.getInt(token));
			container.addPreAttribute(preAttribute);
		}
	}

	private void feedTextIDList(List<ContainerIdentifier> identifiers) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token next;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(next = consume())) {
			consume(TokenConstants.SEMICOLON);

			if (next.getValue() != null && next.getValue().length() > 0) {
				identifiers.add(new ContainerIdentifier(next.getValue()));
			}
		}
	}

	// ###################################################################################
	// ################################ Value Helper Functions ###########################
	// ###################################################################################

	// ################################################################################### General

	private void addName(Container container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token name = consume();
		container.setName(name.getValue());

		consume(TokenConstants.SEMICOLON);
	}

	private void addName(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token name = consume();
		protoAttribute.setName(name.getValue());

		consume(TokenConstants.SEMICOLON);
	}

	private void readMeshPath(Container container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token pathToken = consume();
		String path = ResourcePathConstants.MOD_FOLDER + currentMod + "/" + ResourcePathConstants.MESH_FOLDER + pathToken.getValue();
		MeshHub meshHub = Data.addMeshHub(path);
		container.setMeshHub(meshHub);

		consume(TokenConstants.SEMICOLON);
	}

	// ################################################################################### Tile

	private void readPreferredHeight(TileContainer container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token height = consume();
		container.setPreferredHeight(TokenNumerifier.getInt(height));

		Token nextSub = consume();
		if (TokenConstants.COMMA.equals(nextSub)) {
			Token blur = consume();
			container.setPreferredHeightBlur(TokenNumerifier.getInt(blur));

			consume(TokenConstants.SEMICOLON);

		} else if (TokenConstants.SEMICOLON.equals(nextSub)) {
			return;
		} else {
			Logger.error("Expected '" + TokenConstants.SEMICOLON.getValue() + "' but got '" + nextSub + "' on line " + nextSub.getLine());
		}
	}

	private void readPreferredHeightBlur(TileContainer container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token blur = consume();
		container.setPreferredHeightBlur(TokenNumerifier.getInt(blur));

		consume(TokenConstants.SEMICOLON);
	}

	private void readColor(TileContainer container) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token seperator;
		Token[][] values = new Token[3][2]; // x axis: r,g,b | y axis: color value, deviation

		for (int i=0; i<3; i++) {
			values[i][0] = consume();
			seperator = consume();
			if (TokenConstants.COMMA.equals(seperator)) {
				values[i][1] = consume();
				consume(TokenConstants.SEMICOLON);
			} else if (!TokenConstants.SEMICOLON.equals(seperator)) {
				String errorMessage = "Color values have to be followed by a '" + TokenConstants.SEMICOLON.getValue()
						+ "' or by a comma and a deviation value, which in turn have to be followed by a semicolon. Line: " + seperator.getLine();
				Logger.error(errorMessage);
				throw new Exception(errorMessage);
			}
		}

		container.setColor(new RGBA(
				TokenNumerifier.getDouble(values[0][0]) / 255d,
				TokenNumerifier.getDouble(values[1][0]) / 255d,
				TokenNumerifier.getDouble(values[2][0]) / 255d
		));
		container.setColorDeviation(new RGBA(
				TokenNumerifier.getDouble(values[0][1]) / 255d,
				TokenNumerifier.getDouble(values[1][1]) / 255d,
				TokenNumerifier.getDouble(values[2][1]) / 255d
		));

		consume(TokenConstants.CURLY_BRACKETS_CLOSE);
	}

	// ###################################################################################
	// ################################ Script ###########################################
	// ###################################################################################

	private void addScript(Container container) throws Exception {
		consume(TokenConstants.COLON);
		Token textId = consume();

		if (textId.getValue() != null && textId.getValue().length() > 0) {
			Script script = (new ASTBuilder(this)).buildScript(textId.getValue());
			container.addScript(script);
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
		// Attribute : attTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		ProtoAttribute protoAttribute = new ProtoAttribute(textId.getValue());
		Data.addProtoAttribute(protoAttribute);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoAttribute);

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
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Container container;

		// does such a container already exist?
		container = Data.getContainer(Data.getContainerID(textID.getValue()));
		if (container != null) {
			if (container.getType() != type) {
				String errorMessage = "Line " + textID.getLine()
						+ ": An object with textID '" + textID.getValue()
						+ "' already exists, but has type " + container.getType()
						+ " instead of " + type + ".";

				Logger.error(errorMessage);
				throw new Exception(errorMessage);
			}
		} else {
			// container creation
			switch (type) {
				case ENTITY:
					container = new Container(textID.getValue(), DataType.ENTITY);
					break;
				case TILE:
					container = new TileContainer(textID.getValue());
					break;
				case CREATURE:
					container = new CreatureContainer(textID.getValue());
					break;
				case DRIVE:
					container = new DriveContainer(textID.getValue());
					break;
				case PROCESS:
					container = new ProcessContainer(textID.getValue());
					break;
				case FORMATION:
					container = new Container(textID.getValue(), DataType.FORMATION);
					break;
				case WORLDGEN:
					container = new Container(textID.getValue(), DataType.WORLDGEN);
					break;
				default:
					Logger.error("Unknown entity constructor type '" + type + "' for '" + textID.getValue() + "' on line " + textID.getLine());
					return;
			}
			Data.addContainer(container);
		}

		// container filling
		while (true) {
			Token next = consume();
			// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% name
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(container);

			// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% type specific values
			} else if (TokenConstants.VALUE_PREFERREDHEIGHT.equals(next)) { // preferred height definition
				if (type == DataType.TILE) {
					readPreferredHeight((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUE_PREFERREDHEIGHTBLUR.equals(next)) { // preferred height blur definition
				if (type == DataType.TILE) {
					readPreferredHeightBlur((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUE_COLOR.equals(next)) { // color definition
				if (type == DataType.TILE) {
					readColor((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUE_MESH.equals(next)) { // mesh path definition
				if (type == DataType.ENTITY || type == DataType.CREATURE) {
					readMeshPath(container);
				} else { issueTypeError(next, type); }

			// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% value lists
			} else if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				feedAttributes(container);

			} else if (TokenConstants.VALUES_KNOWLEDGE.equals(next)) { // list of known processes
				if (type == DataType.CREATURE) {
					feedTextIDList(((CreatureContainer) container).getKnowledge());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_DRIVES.equals(next)) { // list of drives
				if (type == DataType.CREATURE) {
					feedTextIDList(((CreatureContainer) container).getDrives());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_SOLUTIONS.equals(next)) { // list of solutions
				if (type == DataType.DRIVE) {
					feedTextIDList(((DriveContainer) container).getSolutions());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_ALTERNATIVES.equals(next)) { // list of alternatives
				if (type == DataType.PROCESS) {
					feedTextIDList(((ProcessContainer) container).getAlternatives());
				} else {
					issueTypeError(next, type);
				}

			// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% general data
			} else if (TokenConstants.SCRIPT.equals(next)) { // Script
				addScript(container);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Entity definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
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
