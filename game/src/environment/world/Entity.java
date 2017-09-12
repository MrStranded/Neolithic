package environment.world;

import parser.ScriptBlock;
import parser.Variable;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Entities cover almost everything from items to units to objects.
 * Thus, they are rather abstract and should be kept flexible.
 *
 * Created by Michael on 27.07.2017.
 */
public class Entity {

	private String textID = "";
	private int id = 0;
	private char thumbnail = 'X';
	private Tile tile = null;

	private ConcurrentLinkedDeque<Entity> entities = new ConcurrentLinkedDeque<Entity>(); // contained entities
	private ConcurrentLinkedDeque<ScriptBlock> scriptBlocks = null;
	private ConcurrentLinkedDeque<Variable> variables = null;

	public Entity (Tile tile) {
		this.tile = tile;
	}
	public Entity (Tile tile, char thumbnail) {
		this.tile = tile;
		this.thumbnail = thumbnail;
	}
	public Entity (String textID) {
		this.textID = textID;
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void addEntity (Entity entity) {
		entities.add(entity);
	}

	public void addScriptBlock(ScriptBlock scriptBlock) {
		if (scriptBlocks == null) scriptBlocks = new ConcurrentLinkedDeque<ScriptBlock>();
		scriptBlocks.add(scriptBlock);
	}

	public void addVariable (Variable variable) {
		variables.add(variable);
	}

	public Variable tryToGetVariable(String name) {
		if (name == null) return null;
		if (variables != null) {
			for (Variable variable : variables) {
				if (name.equals(variable.getName())) return variable;
			}
		}
		return null;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public ConcurrentLinkedDeque<Entity> getEntities() {
		return entities;
	}

	public ConcurrentLinkedDeque<ScriptBlock> getScriptBlocks() {
		return scriptBlocks;
	}

	public String getTextID() {
		return textID;
	}
	public void setTextID(String textID) {
		this.textID = textID;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public char getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(char thumbnail) {
		this.thumbnail = thumbnail;
	}
}
