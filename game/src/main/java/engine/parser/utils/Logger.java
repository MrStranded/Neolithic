package engine.parser.utils;

public class Logger {

	public static void log(String message) {
		System.out.println("LOG " + System.currentTimeMillis() + ": " + message);
	}

	public static void error(String message) {
		System.out.println("ERROR " + System.currentTimeMillis() + ": " + message);
	}
}
