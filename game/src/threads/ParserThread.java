package threads;

import parser.Parser;

/**
 * This class handles the Parser activities in a seperate thread.
 *
 * Created by Michael on 14.08.2017.
 */
public class ParserThread extends Thread {

	public void run() {
		Parser.loadMods(Parser.getModList());
	}
}
