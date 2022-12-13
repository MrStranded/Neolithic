package engine.parser.utils;

import engine.data.scripts.Script;
import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;

import static engine.parser.utils.LoggerFormatting.*;

public class Logger {

	private static final long startMillis = System.currentTimeMillis();

	public static final int LOG_TRACE = 0;
	public static final int LOG_DEBUG = 1;
	public static final int LOG_INFO  = 2;
	public static final int LOG_ERROR = 3;

	public static int logLevel;

	public static void setLogLevel(int level) {
		logLevel = Math.max(LOG_TRACE, Math.min(LOG_ERROR, level));
	}

	public static void nextLogLevel() {
		logLevel = logLevel > 0 ? logLevel - 1 : LOG_ERROR;
		System.out.println(CYAN + "LOG LEVEL SET TO " + GREEN + getLogLevelName() + RESET);
	}

	public static String getLogLevelName() {
		switch (logLevel) {
			case LOG_TRACE: return "Trace";
			case LOG_DEBUG: return "Debug";
			case LOG_INFO:  return "Info";
			case LOG_ERROR: return "Error";
			default: return "Invalid Log Level: " + logLevel;
		}
	}

	public static boolean hasLogLevel(int level) {
		return logLevel <= level;
	}

	public static void raw(Object text) {
		System.out.println(text);
	}

	public static void trace(String message) {
		if (hasLogLevel(LOG_TRACE)) {
			System.out.println(PURPLE_BOLD + "Trace " + WHITE_BOLD + getTimestamp() + PURPLE + ": " + message + RESET);
		}
	}

	public static void debug(String message) {
		if (hasLogLevel(LOG_DEBUG)) {
			System.out.println(YELLOW_BOLD + "DEBUG " + WHITE_BOLD + getTimestamp() + YELLOW + ": " + message + RESET);
		}
	}

	public static void info(String message) {
		if (hasLogLevel(LOG_INFO)) {
			System.out.println(GREEN_BOLD + "LOG " + WHITE_BOLD + getTimestamp() + GREEN + ": " + message + RESET);
		}
	}

	public static void error(String message) {
		if (hasLogLevel(LOG_ERROR)) {
			System.out.println(RED_BOLD + "ERROR " + WHITE_BOLD + getTimestamp() + RED_BOLD + ": " + message + RESET);
		}
	}

	public static void error(Throwable throwable) {
		if (hasLogLevel(LOG_ERROR)) {
			throwable.printStackTrace();
		}
	}

	public static void parsingError(String message, Token token, Interpreter interpreter) {
		if (hasLogLevel(LOG_ERROR)) {
			System.out.println(BLUE_BOLD + "PARSING ERROR " + WHITE_BOLD + getTimestamp() + YELLOW_BOLD + ": " + message + RESET);
			System.out.println(CYAN + "   file:  " + YELLOW + ": " + interpreter.getCurrentFile() + RESET);
			System.out.println(CYAN + "   line:  " + YELLOW + ": " + token.getLine() + RESET);
			System.out.println(CYAN + "   token: " + YELLOW + ": " + token.getValue() + " ( " + token.getType().toString() + " )" + RESET);
			System.out.println(CYAN + "   mod:   " + YELLOW + ": " + interpreter.getCurrentMod() + RESET);
		}
	}

	public static void executionError(String message, Token token, Script script) {
		if (hasLogLevel(LOG_ERROR)) {
			System.out.println(BLUE_BOLD + "EXECUTION ERROR " + WHITE_BOLD + getTimestamp() + YELLOW_BOLD + ": " + message + RESET);
			System.out.println(CYAN + "   file:   " + YELLOW + ": " + script.getFileName() + RESET);
			System.out.println(CYAN + "   script: " + YELLOW + ": " + script.getTextId() + RESET);
			System.out.println(CYAN + "   line:   " + YELLOW + ": " + token.getLine() + RESET);
			System.out.println(CYAN + "   token:  " + YELLOW + ": " + token.getValue() + " ( " + token.getType().toString() + " )" + RESET);
		}
	}
	public static void executionError(String message, Token token) {
		if (hasLogLevel(LOG_ERROR)) {
			System.out.println(BLUE_BOLD + "EXECUTION ERROR " + WHITE_BOLD + getTimestamp() + YELLOW_BOLD + ": " + message + RESET);
			System.out.println(CYAN + "   line:  " + YELLOW + ": " + token.getLine() + RESET);
			System.out.println(CYAN + "   token: " + YELLOW + ": " + token.getValue() + " ( " + token.getType().toString() + " )" + RESET);
		}
	}

	public static void breakpoint(String message) {
		System.out.println(BLACK_BOLD + "BREAK " + WHITE_BOLD + getTimestamp() + CYAN + ": " + message + RESET);
	}

	private static String getTimestamp() {
		long millis = System.currentTimeMillis() - startMillis;

		long seconds = millis / 1000;
		millis -= seconds * 1000;

		long minutes = seconds / 60;
		seconds -= minutes * 60;

		return minutes + ":"
				+ normalizeNumberLength(seconds, 2) + "."
				+ normalizeNumberLength(millis, 3);
	}
	private static String normalizeNumberLength(long number, int desiredLength) {
		String normalized = String.valueOf(number);
		while (normalized.length() < desiredLength) { normalized = "0" + normalized; }
		return normalized;
	}
}
