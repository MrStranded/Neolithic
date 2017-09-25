package threads;

import data.Data;
import environment.world.Entity;
import parser.Parser;
import parser.ScriptBlock;

/**
 * This class handles the Parser activities in a seperate thread.
 *
 * Created by Michael on 14.08.2017.
 */
public class ParserTestThread extends Thread {

	public void run() {

		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦ parser test ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦

		double lastProgress = -1;
		while (!Parser.isFinished()) {
			if (lastProgress != Parser.getProgress()) {
				lastProgress = Parser.getProgress();
				System.out.println("Parser progress: "+lastProgress);
			}

			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("");
		System.out.println("----------- Loaded Entities:");

	}

	private void writeScriptBlocks (ScriptBlock scriptBlock, String prefix) {
		System.out.println("ScriptBlock: "+prefix+" "+scriptBlock);
		if (scriptBlock.getScriptBlocks() != null) {
			for (ScriptBlock sB : scriptBlock.getScriptBlocks()) {
				writeScriptBlocks(sB,prefix+"-->");
			}
		}
	}
}
