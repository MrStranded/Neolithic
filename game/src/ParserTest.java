import threads.ParserTestThread;
import threads.ParserThread;

/**
 * Created by Michael on 05.09.2017.
 */
public class ParserTest {

	public static void main (String[] args) {

		// ------------------- loading

		ParserThread parserThread = new ParserThread();
		ParserTestThread parserTestThread = new ParserTestThread();

		parserThread.start();
		parserTestThread.setDependant(parserThread);
		parserTestThread.start();
	}

}
