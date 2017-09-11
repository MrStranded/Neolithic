import threads.TestThread;
import threads.ParserThread;

/**
 * Created by Michael on 05.09.2017.
 */
public class ParserTest {

	public static void main (String[] args) {

		// ------------------- loading

		new ParserThread().start();

		new TestThread().start();
	}

}
