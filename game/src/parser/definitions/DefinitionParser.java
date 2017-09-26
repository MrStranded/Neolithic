package parser.definitions;

import data.Data;
import data.proto.Container;
import data.proto.Value;
import enums.script.Sign;
import log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 26.09.2017.
 *
 * this class creates Containers from .def files.
 */
public class DefinitionParser {

	/**
	 * This method goes through a definition file char by char.
	 * In the process it builds Containers and fills them with Values.
	 */
	public static void parseFile(File file) {
		try {
			FileReader fileReader = new FileReader(file);

			try {

				String stringBuilder = "";
				char currentChar = 0;

				boolean inString = false;
				boolean inComment = false;

				String valueName = "";
				List<String> valueData = new ArrayList<>();

				int blockCounter = 0;
				Container currentContainer = null;

				while (fileReader.ready()) {
					currentChar = (char) fileReader.read();

					if (inString) { // //////////////// in string
						if (currentChar == Sign.QUOTATION_MARK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% exit string
							inString = !inString;
						} else {
							stringBuilder = addChar(stringBuilder,currentChar,true);
						}
					} else {
						if (inComment) { // //////////////// in comment
							if (currentChar == Sign.COMMENT.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% exit comment
								inComment = !inComment;
							} else {
								if ((currentChar == 10)||(currentChar == 13)) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% exit comment because of line break
									inComment = false;
								}
							}
						} else {
							if (currentChar == Sign.OPEN_BLOCK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% start of new block
								if (blockCounter==1) Log.error("Opening Bracket encountered without closing of previous object definition!");
								blockCounter++;
								if (currentContainer != null) {
									currentContainer.setTextId(stringBuilder);
								} else {
									Log.error("Object "+stringBuilder+" has no type defined!");
								}
								stringBuilder = "";

							} else if (currentChar == Sign.CLOSE_BLOCK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end of a block
								blockCounter--;
								stringBuilder = "";
								Data.addContainer(currentContainer);
								currentContainer = null;

							} else if (currentChar == Sign.QUOTATION_MARK.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% enter string
								inString = !inString;

							} else if (currentChar == Sign.COMMENT.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% enter comment
								inComment = !inComment;

							} else if (currentChar == Sign.SEMICOLON.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end of value
								valueData.add(stringBuilder);
								Value value = new Value(valueName,valueData);
								currentContainer.addValue(value);
								stringBuilder = "";
								valueData.clear();
								valueName = "";

							} else if (currentChar == Sign.COLON.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% start of value of of textId
								if (currentContainer == null) {
									currentContainer = new Container(stringBuilder);
								} else {
									valueName = stringBuilder;
								}
								stringBuilder = "";

							} else if (currentChar == Sign.COMMA.getChar()) { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% next data point for value
								valueData.add(stringBuilder);
								stringBuilder = "";

							} else { // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% a "normal" char
								stringBuilder = addChar(stringBuilder, currentChar,false);
							}
						}
					}
				}

				if (blockCounter != 0) {
					Log.error("Block brackets "+ Sign.OPEN_BLOCK.getChar()+" and "+ Sign.CLOSE_BLOCK.getChar()+" do not match up");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not load file '"+file.getName()+"'");
			e.printStackTrace();
		}
	}

	private static String addChar(String str, char c, boolean full) {
		if (c >= 32) { // no special chars please
			if (full) {
				str = str + c;
			} else {
				if (
						((c>=48)&&(c<=57)) // numbers
						||((c>=65)&&(c<=90)) // upper case letters
						||((c>=97)&&(c<=122)) // lower case letters
						) {
					str = str + c;
				}
			}
		}
		if (c == 10) Log.setCurrentLine(Log.getCurrentLine() + 1);
		return str;
	}

}
