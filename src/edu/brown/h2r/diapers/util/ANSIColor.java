package edu.brown.h2r.diapers.util;

public class ANSIColor {
	public static final String RESET  = "\u001B[0m";
	public static final String BLACK  = "\u001B[30m";
	public static final String RED    = "\u001B[31m";
	public static final String GREEN  = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN   = "\u001B[36m";
	public static final String WHITE  = "\u001B[37m";

	public static void reset() {
		System.out.print(RESET);
	}

	public static void black() {
		System.out.print(BLACK);
	}

	public static void red() {
		System.out.print(RED);
	}

	public static void green() {
		System.out.print(GREEN);
	}

	public static void yellow() {
		System.out.print(YELLOW);
	}

	public static void blue() {
		System.out.print(BLUE);
	}

	public static void purple() {
		System.out.print(PURPLE);
	}	

	public static void cyan() {
		System.out.print(CYAN);
	}

	public static void white() {
		System.out.print(WHITE);
	}

	public static void black(String text) {
		black();
		System.out.print(text);
		reset();
	}

	public static void red(String text) {
		red();
		System.out.print(text);
		reset();
	}

	public static void green(String text) {
		green();
		System.out.print(text);
		reset();
	}

	public static void yellow(String text) {
		yellow();
		System.out.print(text);
		reset();
	}

	public static void blue(String text) {
		blue();
		System.out.print(text);
		reset();
	}

	public static void purple(String text) {
		purple();
		System.out.print(text);
		reset();
	}

	public static void cyan(String text) {
		cyan();
		System.out.print(text);
		reset();
	}

	public static void white(String text) {
		white();
		System.out.print(text);
		reset();
	}
}
