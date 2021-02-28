package engine.parser.utils;

import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;

public class Logger {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static void log(String message) {
		System.out.println(ANSI_GREEN + "LOG " + ANSI_WHITE + System.currentTimeMillis() + ANSI_GREEN + ": " + message + ANSI_RESET);
	}

	public static void breakpoint(String message) {
		System.out.println(ANSI_BLACK + "BREAK " + ANSI_WHITE + System.currentTimeMillis() + ANSI_CYAN + ": " + message + ANSI_RESET);
	}

	public static void error(String message) {
		System.out.println(ANSI_PURPLE + "ERROR " + ANSI_WHITE + System.currentTimeMillis() + ANSI_RED + ": " + message + ANSI_RESET);
	}

	public static void parsingError(String message, Token token, Interpreter interpreter) {
		System.out.println(ANSI_BLUE + "PARSING ERROR " + ANSI_WHITE + System.currentTimeMillis() + ANSI_YELLOW + ": " + message + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   token: " + ANSI_YELLOW + ": " + token.getValue() + " ( " + token.getType().toString() + " )" + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   line: " + ANSI_YELLOW + ": " + token.getLine() + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   file: " + ANSI_YELLOW + ": " + interpreter.getCurrentFile() + ANSI_RESET);
		System.out.println(ANSI_CYAN + "   mod: " + ANSI_YELLOW + ": " + interpreter.getCurrentMod() + ANSI_RESET);
	}
}
