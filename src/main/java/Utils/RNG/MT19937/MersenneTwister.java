package Utils.RNG.MT19937;

import Utils.BitPrinter;

import java.util.ArrayList;
import java.util.BitSet;

public class MersenneTwister {

	// w
	int wordSizeInBits = 32;
	// n
	int degreeOfRecurrence = 624;
	// m: middle word, an offset used in the recurrence relation defining the series x, 1 ≤ m < n
	int middleWord = 397;
	// r: separation point of one word, or the number of bits of the lower bitmask, 0 ≤ r ≤ w − 1
	int separationPoint = 31;
	// a: coefficients of the rational normal form twist matrix
	int twistMatrixCoefficient = 0x9908b0df;
	// b, c: TGFSR(R) tempering bitmasks
	static int b = 0x9D2C5680;
	static int c = 0xEFC60000;
	// s, t: TGFSR(R) tempering bit shifts
	static int s = 7;
	static int t = 15;
	// u, d, l: additional Mersenne Twister tempering bit shifts/masks
	static int u = 11;
	static int d = 0xFFFFFFFF;
	static int l = 18;
	static int f = 1812433253;

	private int[] state;
	private int index;
	private final int lowerMask = (1 << separationPoint) - 1;
	private final int upperMask = ~lowerMask >>> wordSizeInBits;

	public MersenneTwister() {
		setSeed(System.currentTimeMillis());
	}

	private void setSeed(int seed) {
		if (state == null) {
			state = new int[degreeOfRecurrence];
		}
		state[0] = seed;
		for (index = 1; index < degreeOfRecurrence; index++) {
			state[index] = f * (state[index - 1] ^ (state[index - 1] >> (wordSizeInBits - 2))) + index;
		}
	}

	public void setSeed(long seed) {
		setSeed(new int[] { (int) (seed >>> 32), (int) (seed & 0xffffffffL) });
	}

	public void setSeed(int[] seed) {
		if (seed == null) {
			setSeed(System.currentTimeMillis());
			return;
		}

		//Happy birthday, Matsumoto.
		setSeed(19650218);
		int i = 1;
		int j = 0;

		for (int k = Math.max(degreeOfRecurrence, seed.length); k != 0; k--) {
			long l0 = (state[i] & 0x7fffffffL) | ((state[i] < 0) ? 0x80000000L : 0x0L);
			long l1 = (state[i - 1] & 0x7fffffffL) | ((state[i - 1] < 0) ? 0x80000000L : 0x0L);
			long l = (l0 ^ ((l1 ^ (l1 >> 30)) * 1664525L)) + seed[j] + j; // non linear
			state[i] = (int) (l & 0xffffffffL);
			i++;
			j++;
			if (i >= degreeOfRecurrence) {
				state[0] = state[degreeOfRecurrence - 1];
				i = 1;
			}
			if (j >= seed.length) {
				j = 0;
			}
		}

		for (int k = degreeOfRecurrence - 1; k != 0; k--) {
			long l0 = (state[i] & 0x7fffffffL) | ((state[i] < 0) ? 0x80000000L : 0x0L);
			long l1 = (state[i - 1] & 0x7fffffffL) | ((state[i - 1] < 0) ? 0x80000000L : 0x0L);
			long l = (l0 ^ ((l1 ^ (l1 >> 30)) * 1566083941L)) - i; // non linear
			state[i] = (int) (l & 0xffffffffL);
			i++;
			if (i >= degreeOfRecurrence) {
				state[0] = state[degreeOfRecurrence - 1];
				i = 1;
			}
		}

		state[0] = 0x80000000; // MSB is 1; assuring non-zero initial array
	}

	public int extractNumber() {
		if (index >= degreeOfRecurrence) {
			twist();
		}

		int y = state[index];

		System.out.println("The real number is " + y);

		BitPrinter printer = new BitPrinter(y);
		System.out.println("--------------- Y0 -----------------");
		System.out.println("Shifting by " + u + " and masking with " + BitPrinter.intToString(d, 4));
		y = printer.firstRightShift(u).thenAnd(d).thenXOR(y).finish();
		printer = new BitPrinter(y);
		System.out.println("--------------- Y1 -----------------");
		System.out.println("Shifting by " + s + " and masking with " + BitPrinter.intToString(b, 4));
		y = printer.firstLeftShift(s).thenAnd(b).thenXOR(y).finish();
		printer = new BitPrinter(y);
		System.out.println("--------------- Y2 -----------------");
		System.out.println("Shifting by " + t + " and masking with " + BitPrinter.intToString(c, 4));
		y = printer.firstLeftShift(t).thenAnd(c).thenXOR(y).finish();
		printer = new BitPrinter(y);
		System.out.println("--------------- Y3 -----------------");
		System.out.println("Shifting by " + l);
		y = printer.firstRightShift(l).thenXOR(y).finish();

		index += 1;
		System.out.println("This is o.");
		printer = new BitPrinter(y);
		return printer.firstRightShift(wordSizeInBits).finish();
	}

	public void twist() {
		for (int i = 0; i < degreeOfRecurrence; i++) {
			int x = (state[i] & upperMask) + (state[(i + 1) % degreeOfRecurrence] & lowerMask);
			int xA = x >> 1;
			if ((x % 2) != 0) {
				xA ^= twistMatrixCoefficient;
			}
			state[i] = state[(i + middleWord) % degreeOfRecurrence] ^ xA;
		}
		index = 0;
	}

	public static int setSeedAndWait() throws InterruptedException {
		for (int i = 0; i < (Math.random() * 960) + 40; i++) {
			Thread.sleep(1000);
		}
		MersenneTwister mersenneTwister = new MersenneTwister();
		for (int i = 0; i < (Math.random() * 960) + 40; i++) {
			Thread.sleep(1000);
		}
		return mersenneTwister.extractNumber();
	}

	public static int setSeedAndWait(long seed) throws InterruptedException {
		MersenneTwister mersenneTwister = new MersenneTwister();
		mersenneTwister.setSeed(seed);
		return mersenneTwister.extractNumber();
	}

	public static int untemper(int o) {
		ArrayList<Integer> bits = new ArrayList<>();
		BitPrinter printer = new BitPrinter(o);
		System.out.println("=================UNTEMPERING Y3===================");
		int y3 = printer.firstRightShift(l).thenXOR(o).finish();
		printer = new BitPrinter(y3);
		System.out.println("=================UNTEMPERING Y2===================");
		int y2print = printer.firstLeftShift(t).thenAnd(c).thenXOR(y3).finish();
		int y2 = leftShiftUntemper(y3, t, c);
		System.out.println(y2);
		printer = new BitPrinter(y2);
		System.out.println("=================UNTEMPERING Y1===================");
		int y1printer = printer.firstLeftShift(s).thenAnd(b).thenXOR(y2).finish();
		int y1 = leftShiftUntemper(y2, s, b);
		System.out.println(y1);
		printer = new BitPrinter(y1);
		System.out.println("=================UNTEMPERING Y0===================");
		int y0 = printer.firstRightShift(u).thenAnd(d).thenXOR(y1).finish();
		return y0;
	}

	/*
	The essential insight here is that our shift amount matches the 0's on the masks. Because shifting results in 0's and
	the lsb of the mask are also 0's, the [shift]-lsb's of y' are equal to [shift]-lsb of y. y' & 0 = 0. y' ^ 0 = y;
	Now that we have [shift]-lsb's, we can calculate the [shift * 2] lsb's of y, by taking the [shift * 2]-lsb's of y'
	 & with the [shift * 2]-lsb's of the known mask and XOR with the [shift]-lsb's we just discovered.
	 */

	public static int leftShiftUntemper(int number, int shift, int mask) {
		// WDYM Java is verbose?
		BitSet reversedBitSet = BitSet.valueOf(new long[] {
			Integer.parseUnsignedInt(new StringBuilder(BitPrinter.intToString(number, 32)).reverse().toString(), 2) });
		BitSet reversedMask = BitSet.valueOf(new long[] {
			Integer.parseUnsignedInt(new StringBuilder(BitPrinter.intToString(mask, 32)).reverse().toString(), 2) });
		BitSet recoveredBits = new BitSet(32);
		for (int i = 0; i < 32; i++) {
			if (i < shift) {
				recoveredBits.set(i, reversedBitSet.get(i));
			}
			else {
				recoveredBits.set(i, reversedBitSet.get(i) ^ (reversedMask.get(i) & recoveredBits.get(i - shift)));
			}
		}
		return Integer.parseUnsignedInt(
			new StringBuilder(BitPrinter.intToString((int) recoveredBits.toLongArray()[0], 32)).reverse().toString(),
			2);
	}
}
