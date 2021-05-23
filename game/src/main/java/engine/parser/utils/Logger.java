package engine.parser.utils;

import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;

public class Logger {

	public static final int LOG_TRACE = 0;
	public static final int LOG_DEBUG = 1;
	public static final int LOG_INFO  = 2;
	public static final int LOG_ERROR = 3;

	public static int logLevel = LOG_DEBUG;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String FORMAT_TRACE = "\u001B[38;2;70;60;40;6;11m";
	public static final String FORMAT_DEBUG = "\u001B[38;2;200;180;140;5;1m";

	public static void nextLogLevel() {
		logLevel = (logLevel + 1) % (LOG_ERROR + 1);
		System.out.println(ANSI_CYAN + "LOG LEVEL SET TO " + ANSI_GREEN + logLevel + ANSI_RESET);
	}

	public static boolean hasLogLevel(int level) {
		return logLevel <= level;
	}

	public static void trace(String message) {
		if (hasLogLevel(LOG_TRACE)) {
			System.out.println(FORMAT_TRACE + "Trace " + ANSI_WHITE + System.currentTimeMillis() + ANSI_PURPLE + ": " + message + ANSI_RESET);
		}
	}

	public static void debug(String message) {
		if (hasLogLevel(LOG_DEBUG)) {
			System.out.println(FORMAT_DEBUG + "DEBUG " + ANSI_WHITE + System.currentTimeMillis() + ANSI_YELLOW + ": " + message + ANSI_RESET);
		}
	}

	public static void info(String message) {
		if (hasLogLevel(LOG_INFO)) {
			System.out.println(ANSI_GREEN + "LOG " + ANSI_WHITE + System.currentTimeMillis() + ANSI_GREEN + ": " + message + ANSI_RESET);
		}
	}

	public static void error(String message) {
		if (hasLogLevel(LOG_ERROR)) {
			System.out.println(ANSI_PURPLE + "ERROR " + ANSI_WHITE + System.currentTimeMillis() + ANSI_RED + ": " + message + ANSI_RESET);
		}
	}

	public static void parsingError(String message, Token token, Interpreter interpreter) {
		System.out.println(ANSI_BLUE + "PARSING ERROR " + ANSI_WHITE + System.currentTimeMillis() + ANSI_YELLOW + ": " + message + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   token: " + ANSI_YELLOW + ": " + token.getValue() + " ( " + token.getType().toString() + " )" + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   line: " + ANSI_YELLOW + ": " + token.getLine() + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   file: " + ANSI_YELLOW + ": " + interpreter.getCurrentFile() + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   mod: " + ANSI_YELLOW + ": " + interpreter.getCurrentMod() + ANSI_RESET);
	}

	public static void breakpoint(String message) {
		System.out.println(ANSI_BLACK + "BREAK " + ANSI_WHITE + System.currentTimeMillis() + ANSI_CYAN + ": " + message + ANSI_RESET);
	}
}
