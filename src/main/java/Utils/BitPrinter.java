package Utils;

public class BitPrinter {

	public static final String ANSI_BLACK = "\033[30m";
	public static final String ANSI_LIGHT_RED = "\033[31m";
	public static final String ANSI_LIGHT_GREEN = "\033[32m";
	public static final String ANSI_LIGHT_YELLOW = "\033[33m";
	public static final String ANSI_LIGHT_BLUE = "\033[34m";
	public static final String ANSI_LIGHT_PURPLE = "\033[35m";
	public static final String ANSI_LIGHT_CYAN = "\033[36m";
	public static final String ANSI_LIGHT_GREY = "\033[37m";
	public static final String ANSI_BLACK_BACKGROUND = "\033[40m";
	public static final String ANSI_LIGHT_RED_BACKGROUND = "\033[41m";
	public static final String ANSI_LIGHT_GREEN_BACKGROUND = "\033[42m";
	public static final String ANSI_LIGHT_YELLOW_BACKGROUND = "\033[43m";
	public static final String ANSI_LIGHT_BLUE_BACKGROUND = "\033[44m";
	public static final String ANSI_LIGHT_PURPLE_BACKGROUND = "\033[45m";
	public static final String ANSI_LIGHT_CYAN_BACKGROUND = "\033[46m";
	public static final String ANSI_LIGHT_GREY_BACKGROUND = "\033[47m";
	public static final String ANSI_GREY = "\033[90m";
	public static final String ANSI_RED = "\033[91m";
	public static final String ANSI_GREEN = "\033[92m";
	public static final String ANSI_YELLOW = "\033[93m";
	public static final String ANSI_BLUE = "\033[94m";
	public static final String ANSI_PURPLE = "\033[95m";
	public static final String ANSI_CYAN = "\033[96m";
	public static final String ANSI_WHITE = "\033[97m";
	public static final String ANSI_GREY_BACKGROUND = "\033[100m";
	public static final String ANSI_RED_BACKGROUND = "\033[101m";
	public static final String ANSI_GREEN_BACKGROUND = "\033[102m";
	public static final String ANSI_YELLOW_BACKGROUND = "\033[103m";
	public static final String ANSI_BLUE_BACKGROUND = "\033[104m";
	public static final String ANSI_PURPLE_BACKGROUND = "\033[105m";
	public static final String ANSI_CYAN_BACKGROUND = "\033[106m";
	public static final String ANSI_RESET = "\u001B[0m";

	int state;

	public boolean verbose = false;

	public BitPrinter(int initialState, boolean verbose) {
		state = initialState;
		this.verbose = verbose;
	}

	public BitPrinter firstAnd(int andWith) {
		if (verbose) {
			System.out.println(state + " & " + andWith);
			formatPrintHighlighted(state, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_CYAN_BACKGROUND, ANSI_CYAN_BACKGROUND);
			System.out.println(" --> Decimal: " + state);
		}
		return thenAnd(andWith);
	}

	public BitPrinter thenAnd(int andWith) {
		if (verbose) {
			System.out.println(" & ");
			formatPrintHighlighted(andWith, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_RED_BACKGROUND, ANSI_RED_BACKGROUND);
			System.out.println(" --> Decimal: " + andWith);
			System.out.println("------------------------------------------");
			formatPrintHighlighted(state & andWith, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_PURPLE_BACKGROUND,
				ANSI_PURPLE_BACKGROUND);
			System.out.println(" --> Decimal: " + (state & andWith));
		}
		this.state = state & andWith;
		return this;
	}

	public BitPrinter firstXOR(int XORWith) {
		if (verbose) {
			System.out.println(state + " ^ " + XORWith);
			formatPrintHighlighted(state, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_BLUE_BACKGROUND, ANSI_BLUE_BACKGROUND);
			System.out.println(" --> Decimal: " + state);
		}
		return thenXOR(XORWith);
	}

	public BitPrinter thenXOR(int XORWith) {
		if (verbose) {
			System.out.println(" ^");
			formatPrintHighlighted(XORWith, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_RED_BACKGROUND, ANSI_RED_BACKGROUND);
			System.out.println(" --> Decimal: " + XORWith);
			System.out.println("------------------------------------------");
			formatPrintHighlighted(state ^ XORWith, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_PURPLE_BACKGROUND,
				ANSI_PURPLE_BACKGROUND);
			System.out.println(" --> Result: " + (state ^ XORWith));
		}
		this.state = state ^ XORWith;
		return this;
	}

	public BitPrinter firstRightShift(int shiftBy) {
		if (verbose) {
			System.out.println("Right shifting " + state + " by " + shiftBy);
			formatPrintHighlighted(state, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_RED_BACKGROUND, ANSI_RED_BACKGROUND);
			System.out.println(" --> Decimal: " + state);
		}
		return thenRightShift(shiftBy);
	}

	public BitPrinter thenRightShift(int shiftBy) {
		if (verbose) {
			System.out.println(" >>> " + shiftBy);
			for (int i = 0; i < 32; i++) {
				if (i + 1 != shiftBy) {
					System.out.print("-");
				}
				else {
					System.out.print("|");
				}
				if ((i + 1) % 4 == 0) {
					System.out.print(((i + 1)) % 10);
				}
			}
			System.out.println();
			formatPrintRightShiftHighlighted(state >>> shiftBy, shiftBy);
			System.out.println(" --> Decimal:  " + (state >>> shiftBy));
		}
		this.state = state >>> shiftBy;
		return this;
	}

	public BitPrinter firstLeftShift(int shiftBy) {
		if (verbose) {
			System.out.println("Left shifting " + state + " by " + shiftBy);
			formatPrintHighlighted(state, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_BLUE_BACKGROUND, ANSI_BLUE_BACKGROUND);
			System.out.println(" --> Decimal: " + state);
		}
		return thenLeftShift(shiftBy);
	}

	public BitPrinter thenLeftShift(int shiftBy) {
		if (verbose) {
			System.out.println(" << " + shiftBy);
			for (int i = 0; i < 32; i++) {
				if (32 - (i + 1) != shiftBy) {
					System.out.print("-");
				}
				else {
					System.out.print("|");
				}
				if ((i + 1) % 4 == 0) {
					System.out.print((32 - (i + 1)) % 10);
				}
			}
			System.out.println();
			formatPrintHighlighted(state << shiftBy, ANSI_BLACK, ANSI_WHITE, ANSI_LIGHT_BLUE_BACKGROUND,
				ANSI_BLUE_BACKGROUND);
			System.out.println(" --> Decimal: " + (state << shiftBy));
		}
		this.state = state << shiftBy;
		return this;
	}

	public int finish() {
		if (verbose) {
			System.out.print(ANSI_RED);
			System.out.println("================================");
			System.out.println(ANSI_RESET);
		}
		return state;
	}

	public int getState() {
		return state;
	}

	private void formatPrintHighlighted(int highlight, String foregroundColorZeroes, String foregroundColorOnes,
		String backgroundColorZeroes, String backgroundColorOnes) {
		String andWithAsBits = intToString(highlight, 4);
		int i = 0;
		for (char c : andWithAsBits.toCharArray()) {
			if (i % 2 == 0) {
				System.out.print(backgroundColorZeroes);
			}
			else {
				System.out.print(backgroundColorOnes);
			}
			i++;
			if (c == '0') {
				System.out.print(foregroundColorZeroes);
			}
			else if (c == '1') {
				System.out.print(foregroundColorOnes);
			}
			else if (c == ' ') {
				System.out.print(ANSI_RESET);
			}
			System.out.print(c);
		}
		System.out.print(ANSI_RESET);
	}

	private void formatPrintRightShiftHighlighted(int highlight, int shiftBy) {
		String andWithAsBits = intToString(highlight, 4);
		int i = 0;
		for (char c : andWithAsBits.toCharArray()) {
			if ((i + 1) - (i / 4) < shiftBy) {
				System.out.print(ANSI_BLACK);
				System.out.print(ANSI_YELLOW_BACKGROUND);
			}
			else {
				if (c == '0') {
					System.out.print(ANSI_BLACK);
					System.out.print(ANSI_LIGHT_RED_BACKGROUND);
				}
				else if (c == '1') {
					System.out.print(ANSI_RESET);
					System.out.print(ANSI_RED_BACKGROUND);
				}
			}
			System.out.print(c);
			i++;
		}
		System.out.print(ANSI_RESET);
	}

	/**
	 * Converts an integer to a 32-bit binary string
	 *
	 * @param number    The number to convert
	 * @param groupSize The number of bits in a group
	 * @return The 32-bit long bit string
	 */
	public static String intToString(int number, int groupSize) {
		StringBuilder result = new StringBuilder();

		for (int i = 31; i >= 0; i--) {
			int mask = 1 << i;
			result.append((number & mask) != 0 ? "1" : "0");

			if (i % groupSize == 0)
				result.append(" ");
		}
		result.replace(result.length() - 1, result.length(), "");

		return result.toString();
	}

}
