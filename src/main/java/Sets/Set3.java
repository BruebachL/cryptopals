package Sets;

import Utils.AES.AESCTR;
import Utils.AES.AESKey;
import Utils.AES.CBCPaddingOracle;
import Utils.*;
import Utils.RNG.MT19937.MersenneTwister;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Set3 {

	public static void main(String[] args) throws BadPaddingException, IOException, InterruptedException {
		/*challenge17();
		System.out.println();
		challenge18();
		System.out.println();
		challenge19();
		System.out.println();
		challenge20();
		System.out.println();*/
		//		StringUtils.printANSIColors();
		//challenge21();
		//System.out.println();
		//challenge22();
		//System.out.println();
		//challenge23();
		//System.out.println();
		challenge24();
	}

	/**
	 * This pair of functions approximates AES-CBC encryption as its deployed serverside in web applications;
	 * the second function models the server's consumption of an encrypted session token, as if it was a cookie.
	 */

	public static void challenge17() throws BadPaddingException {
		CBCPaddingOracle paddingOracle = new CBCPaddingOracle();
		paddingOracle.fullAttack();
	}

	/*
	AES CTR Mode
	 */

	public static void challenge18() {
		AESKey key = new AESKey("YELLOW SUBMARINE".getBytes());
		AESCTR aes = new AESCTR(key);
		byte[] decrypted = aes.process(
			Base64Conversion.Base64toBytes("L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ=="));
		System.out.println(StringUtils.toNormalStr(decrypted));
	}

	public static void challenge19() throws IOException {
		AESKey key = new AESKey();
		String[] lines = FileUtils.readLines("src/main/resources/cyphertexts/19.txt");
		byte[][] cyphertexts = new byte[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			byte[] plaintext = Base64Conversion.Base64toBytes(lines[i]);
			AESCTR aes = new AESCTR(key);
			cyphertexts[i] = aes.process(plaintext);
		}

		int maxLength = Arrays.stream(cyphertexts).max(Comparator.comparingInt(a -> a.length)).get().length;
		byte[] predicedKeystream = new byte[maxLength];
		predicedKeystream[0] = (byte) (cyphertexts[25][0] ^ (byte) 't');
		predicedKeystream[1] = (byte) (cyphertexts[25][1] ^ (byte) 'h');
		predicedKeystream[2] = (byte) (cyphertexts[5][2] ^ (byte) 0x20);
		predicedKeystream[3] = (byte) (cyphertexts[9][3] ^ (byte) 'a');
		predicedKeystream[4] = (byte) (cyphertexts[0][4] ^ (byte) 'v');
		predicedKeystream[5] = (byte) (cyphertexts[0][5] ^ (byte) 'e');
		predicedKeystream[6] = (byte) (cyphertexts[3][6] ^ (byte) 'e');
		predicedKeystream[7] = (byte) (cyphertexts[3][7] ^ (byte) 'n');
		predicedKeystream[8] = (byte) (cyphertexts[10][8] ^ (byte) 'e');
		predicedKeystream[9] = (byte) (cyphertexts[1][9] ^ (byte) 't');
		predicedKeystream[10] = (byte) (cyphertexts[0][10] ^ (byte) 0x20);
		predicedKeystream[11] = (byte) (cyphertexts[9][11] ^ (byte) 'g');
		predicedKeystream[12] = (byte) (cyphertexts[5][12] ^ (byte) 'a');
		predicedKeystream[13] = (byte) (cyphertexts[0][13] ^ (byte) 'e');
		predicedKeystream[14] = (byte) (cyphertexts[6][14] ^ (byte) 'e');
		predicedKeystream[15] = (byte) (cyphertexts[3][15] ^ (byte) 'u');
		predicedKeystream[16] = (byte) (cyphertexts[3][16] ^ (byte) 'r');
		predicedKeystream[17] = (byte) (cyphertexts[3][17] ^ (byte) 'y');
		predicedKeystream[18] = (byte) (cyphertexts[2][18] ^ (byte) 's');
		predicedKeystream[19] = (byte) (cyphertexts[2][19] ^ (byte) 'k');
		predicedKeystream[20] = (byte) (cyphertexts[10][20] ^ (byte) 'n');
		predicedKeystream[21] = (byte) (cyphertexts[11][21] ^ (byte) 'e');
		predicedKeystream[22] = (byte) (cyphertexts[1][22] ^ (byte) 's');
		predicedKeystream[23] = (byte) (cyphertexts[0][23] ^ (byte) 'e');
		predicedKeystream[24] = (byte) (cyphertexts[3][24] ^ (byte) 's');
		predicedKeystream[25] = (byte) (cyphertexts[3][25] ^ (byte) '.');
		predicedKeystream[26] = (byte) (cyphertexts[5][26] ^ (byte) 's');
		predicedKeystream[27] = (byte) (cyphertexts[0][27] ^ (byte) ' ');
		predicedKeystream[28] = (byte) (cyphertexts[0][28] ^ (byte) 'd');
		predicedKeystream[29] = (byte) (cyphertexts[0][29] ^ (byte) 'a');
		predicedKeystream[30] = (byte) (cyphertexts[0][30] ^ (byte) 'y');
		predicedKeystream[31] = (byte) (cyphertexts[6][31] ^ (byte) 'd');
		predicedKeystream[32] = (byte) (cyphertexts[4][32] ^ (byte) 'h');
		predicedKeystream[33] = (byte) (cyphertexts[4][33] ^ (byte) 'e');
		predicedKeystream[34] = (byte) (cyphertexts[4][34] ^ (byte) 'a');
		predicedKeystream[35] = (byte) (cyphertexts[4][35] ^ (byte) 'd');
		predicedKeystream[36] = (byte) (cyphertexts[37][36] ^ (byte) 'n');
		predicedKeystream[37] = (byte) (cyphertexts[37][37] ^ (byte) ',');

		System.out.println();

		StringUtils.printSolvedColumns(cyphertexts, predicedKeystream,
			new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
				26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37 });
		//ArrayList<HashMap<ArrayList<Byte>, Integer>> kgrams = StringUtils.countKGrams(cyphertexts, 3);
		//StringUtils.printFrequentKGrams(kgrams);
	}

	public static void challenge20() throws IOException {
		AESKey key = new AESKey();
		String[] lines = FileUtils.readLines("src/main/resources/cyphertexts/20.txt");
		byte[][] cyphertexts = new byte[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			byte[] plaintext = Base64Conversion.Base64toBytes(lines[i]);
			AESCTR aes = new AESCTR(key);
			cyphertexts[i] = aes.process(plaintext);
		}

		byte[][] transposedCyphers = ByteOperation.transposeByteMatrix(ByteOperation.truncateByteMatrix(cyphertexts));
		byte[][] decodedCyphers = new byte[transposedCyphers.length][transposedCyphers[0].length];
		for (int i = 0; i < transposedCyphers.length; i++) {
			decodedCyphers[i] = XORCypher.breakSingle(transposedCyphers[i]);
		}
		byte[][] transposedPlain = ByteOperation.transposeByteMatrix(decodedCyphers);
		for (byte[] plain : transposedPlain) {
			System.out.println(StringUtils.toNormalStr(plain));
		}
	}

	public static void challenge21() {
		MersenneTwister mersenneTwister = new MersenneTwister();
		mersenneTwister.setSeed(6);
		for (int i = 0; i < 20; i++) {
			System.out.println(mersenneTwister.extractNumber());
		}
	}

	public static void challenge22() throws InterruptedException {
		int lookingFor = MersenneTwister.setSeedAndWait();
		System.out.println(lookingFor);
		long searching = MersenneTwister.checkIfTwisterWasSeededWithinTimeFrame(lookingFor, System.currentTimeMillis(),
			60 * 10);
		System.out.println("Found: " + lookingFor + " generated with seed: " + searching);
	}

	// Mostly verbose validation code to check if untemper and setState work

	public static void challenge23() {
		MersenneTwister mersenneTwister = new MersenneTwister();
		mersenneTwister.setSeed(69);
		int[] extractedNumbers = new int[624];
		int[] untemperedNumbers = new int[624];
		int[] clonedNumbers = new int[624];
		for (int i = 0; i < 624; i++) {
			extractedNumbers[i] = mersenneTwister.extractNumber();
			untemperedNumbers[i] = MersenneTwister.untemper(extractedNumbers[i]);
			System.out.println("Extracted: " + extractedNumbers[i] + " and Untempered to: " + untemperedNumbers[i]);
		}
		MersenneTwister clonedTwister = new MersenneTwister();
		clonedTwister.setState(untemperedNumbers);
		for (int i = 0; i < 624; i++) {
			clonedNumbers[i] = clonedTwister.extractNumber();
			System.out.println("Extracted cloned: " + clonedNumbers[i]);
		}
	}

	/*
	  You can create a trivial stream cipher out of any PRNG; use it to generate a sequence of 8 bit outputs and call those outputs a keystream. XOR each byte of plaintext with each successive byte of keystream.
	  Write the function that does this for MT19937 using a 16-bit seed. Verify that you can encrypt and decrypt properly. This code should look similar to your CTR code.
	  Use your function to encrypt a known plaintext (say, 14 consecutive 'A' characters) prefixed by a random number of random characters.
	  From the ciphertext, recover the "key" (the 16 bit seed).
	  Use the same idea to generate a random "password reset token" using MT19937 seeded from the current time.
	  Write a function to check if any given password token is actually the product of an MT19937 PRNG seeded with the current time.
	 */

	public static void challenge24() throws InterruptedException {
		Random random = new Random();
		int randomSeed = random.nextInt((int) Math.pow(2, 16));
		MersenneTwister encryptTwister = new MersenneTwister(randomSeed);
		byte[] plaintext = "AAAAAAAAAAAAAA".getBytes(StandardCharsets.UTF_8);
		System.out.println("Random seed was :" + randomSeed + " and we bruteforced: " + MersenneTwister.bruteforceSeed(
			encryptTwister.encrypt(ByteOperation.prefixByteArrayWithRandomByteAmount(plaintext)), plaintext, true));
		long startTime = System.currentTimeMillis();
		String passwordResetToken = new MersenneTwister(startTime).getPasswordResetToken(8);
		System.out.println("Created Password Reset Token " + passwordResetToken + " at time: " + startTime);
		long sleepTime = (long) ((Math.random() * 12 * 60)
			* 1000); // Set this a bit higher intentionally to test failure.
		System.out.println("Sleeping for: " + sleepTime / 1000 / 60 + " minutes");
		Thread.sleep(sleepTime);
		System.out.println("Resuming...");
		long possibleSeed = MersenneTwister.checkIfPasswordResetTokenCameFromTwisterSeededWithinTimeFrame(
			passwordResetToken, System.currentTimeMillis(), 60 * 10);
		if (possibleSeed == -1) {
			System.out.println(
				"Password reset token did not come from a Mersenne Twister seeded within the last " + 10 + " Minutes.");
		}
		else {
			System.out.println("Found " + passwordResetToken + " seeded with time: " + possibleSeed);
		}
	}

}


