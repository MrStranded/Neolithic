package main.log;

/**
 * A convienience class for quicker and neater error descriptions
 *
 * Created by Michael on 12.09.2017.
 */
public class Log {

	private static String currentFile = "";
	private static int currentLine = 0;
	private static int numberOfErrors = 0;

	public static void error (String message) {
		System.out.println("**************************  ERROR!!  ***********************************");
		System.out.println("*** "+message);
		System.out.println("*** in file: '"+currentFile+"' in line "+currentLine);
		System.out.println("************************************************************************");
		numberOfErrors++;
	}

	public static void status () {
		System.out.println("**************************  STATUS  ************************************");
		System.out.println("*** number of encountered errors: "+numberOfErrors);
		System.out.println("************************************************************************");
		numberOfErrors++;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public static String getCurrentFile() {
		return currentFile;
	}
	public static void setCurrentFile(String currentFile) {
		Log.currentFile = currentFile;
	}

	public static int getCurrentLine() {
		return currentLine;
	}
	public static void setCurrentLine(int currentLine) {
		Log.currentLine = currentLine;
	}
}
