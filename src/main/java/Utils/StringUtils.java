package Utils;

import Utils.AES.AESCBC;
import Utils.AES.AESKey;

import java.util.*;

public class StringUtils {

    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static int[] scoreStringColumns(byte[][] hexDecodedStrings) {
        int length = Arrays.stream(hexDecodedStrings).max(Comparator.comparingInt(a -> a.length)).get().length;
        int[] columnScores = new int[length];
        byte[][] transposed = new byte[length][hexDecodedStrings.length];
        for (int i = 0; i < hexDecodedStrings.length; i++) {
            for (int j = 0; j < hexDecodedStrings[i].length; j++) {
                transposed[j][i] = hexDecodedStrings[i][j];
            }
        }
        for (int i = 0; i < transposed.length; i++) {
            columnScores[i] = scoreString(transposed[i]);
        }
        return columnScores;
    }

    public static int scoreString(byte[] hexDecodedString) {
        String ETAOIN = "etaoin shrdlcuETAOINSHRDLCU";
        int encodedStringHighScore = 0;
        int Stringscore;
        String encodedBytes = new String(hexDecodedString);
        Stringscore = 0;
        for (int i = 0; i < encodedBytes.length(); i++) {
            for (int cnt = 0; cnt < ETAOIN.length(); cnt++) {
                if (encodedBytes.charAt(i) == ETAOIN.charAt(cnt)) {
                    Stringscore = Stringscore + (100 - cnt);
                }
            }
            if (Stringscore > encodedStringHighScore) {
                encodedStringHighScore = Stringscore;
            }
        }
        return encodedStringHighScore;
    }
    public static String toNormalStr(byte[] arr)
    {
        try
        {
            return new String(arr, "UTF-8");
        } catch (Exception e)
        {
            return "";
        }
    }
    public static double stringMetric(byte[] arr)
    {
        int count = 0;
        for (byte b : arr)
        {
            if ((b >= 'a' && b <= 'z') || b == ' ')
                count += 4;
            if ((b >= 'A' && b <= 'Z') || b == '\'' || b == '.' || b == '!' || b == '?')
                count += 2;
            if ((b >= '0' && b <= '9') || b == '\n' || b == '\t' || b == '\r')
                count++;
        }
        return (double)count / (arr.length * 4);
    }
    public static String toNormalStr(ArrayList<Byte> arr)
    {
        String s = "";
        for (Byte b : arr)
        {
            s += (char)b.byteValue();
        }
        return s;
    }
    public static String decodeSingle(byte[] hexDecodedString) {
        char letter = 'a';
        String ETAOIN = "etaoin shrdlcuETAOINSHRDLCU";
        char[] frequency = ETAOIN.toCharArray();
        String bestGuess = "";
        char key = 'x';
        int encodedStringHighScore = 0;
        int Stringscore = 0;
        while (letter != 'Y') {

            byte[] cAsBytes = new byte[hexDecodedString.length];
            for (int i = 0; i < hexDecodedString.length; i++) {
                cAsBytes[i] = (byte) (hexDecodedString[i] ^ letter);
            }

            String encodedBytes = new String(cAsBytes);
            Stringscore = 0;
            for (int i = 0; i < encodedBytes.length(); i++) {

                for (int cnt = 0; cnt < ETAOIN.length(); cnt++) {
                    if (encodedBytes.charAt(i) == ETAOIN.charAt(cnt)) {

                        Stringscore = Stringscore + (100 - cnt);
                    }

                }
                if (Stringscore > encodedStringHighScore) {
                    encodedStringHighScore = Stringscore;
                    bestGuess = encodedBytes;
                    key = letter;
                }
            }
            letter++;
        }
        return bestGuess;
    }

    public static byte[] challenge16StringEncryption(String input, AESKey key, byte[] iv) {
        if (iv.length != 16) {
            throw new IllegalArgumentException("IV length should be 16");
        }
        input = input.replace('=', '.');
        input = input.replace(';', '.');
        input = "comment1=cooking%20MCs;userdata=" + input + ";comment2=%20like%20a%20pound%20of%20bacon";

        byte[] toEncrypt = ByteOperation.padPKCS7(input.getBytes(), 16);
        AESCBC aes = new AESCBC(key);
        return aes.encrypt(toEncrypt, iv);
    }

    public static void printSolvedColumns(byte[][] cypherTexts, byte[] predictedKeyStream, int[] solvedColumns) {
        int maxLength = Arrays.stream(cypherTexts).max(Comparator.comparingInt(a -> a.length)).get().length;
        for (int i = 0; i < maxLength; i++) {
            System.out.printf("%-2s\t", i);
        }
        System.out.println();
        for (int i = 0; i < cypherTexts.length; i++) {
            for (int j = 0; j < cypherTexts[i].length; j++) {
                int finalJ = j;
                if (Arrays.stream(solvedColumns).anyMatch(n -> n == finalJ)) {
                    System.out.print(ANSI_RED);
                }
                if ((char) (cypherTexts[i][j] ^ predictedKeyStream[j]) != '\n') {
                    System.out.printf("%-2c", (char) (cypherTexts[i][j] ^ predictedKeyStream[j]));
                }
                else {
                    System.out.printf("%-2c", ' ');
                }

                if (Arrays.stream(solvedColumns).anyMatch(n -> n == finalJ)) {
                    System.out.print(ANSI_RESET);
                }
            }
            System.out.println();
        }
    }

    public static void printWithPredictedKeystream(byte[][] cypherTexts, byte[] predictedKeyStream) {
        int maxLength = Arrays.stream(cypherTexts).max(Comparator.comparingInt(a -> a.length)).get().length;
        for (int i = 0; i < maxLength; i++) {
            System.out.printf("| %-2s\t", i);
        }
        System.out.println();
        for (int i = 0; i < cypherTexts.length; i++) {
            for (int j = 0; j < cypherTexts[i].length; j++) {
                System.out.printf("| %-2c\t", (char) (cypherTexts[i][j] ^ predictedKeyStream[j]));
            }
            System.out.println();
        }
    }

    public static ArrayList<HashMap<ArrayList<Byte>, Integer>> countKGrams(byte[][] input, int k) {
        int minLength = Arrays.stream(input).min(Comparator.comparingInt(a -> a.length)).get().length;
        ArrayList<HashMap<ArrayList<Byte>, Integer>> kgrams = new ArrayList<>();
        // for every entry in the subarray up to the minimum length
        for (int i = 0; i < minLength; i++) {
            // for every subarray
            HashMap<ArrayList<Byte>, Integer> map = new HashMap<>();
            for (int j = 0; j < input.length; j++) {
                ArrayList<Byte> kgram = new ArrayList<>();
                // for the current position in the subarray
                for (int pos = i; pos < i + k; pos++) {
                    if (pos < minLength) {
                        kgram.add(input[j][pos]);
                    }
                }
                map.put(kgram, map.get(kgram) == null ? 1 : map.get(kgram) + 1);
            }
            kgrams.add(map);
        }
        return kgrams;
    }

    public static void printFrequentKGrams(ArrayList<HashMap<ArrayList<Byte>, Integer>> kgrams) {
        int i = 0;
        for (HashMap<ArrayList<Byte>, Integer> map : kgrams) {
            Integer max = Collections.max(map.values());
            for (Map.Entry<ArrayList<Byte>, Integer> entry : map.entrySet()) {
                if (entry.getValue().equals(max)) {
                    ArrayList<Byte> key = entry.getKey();
                    System.out.println(
                        "Frequency: " + entry.getValue() + " at position " + i + " for " + StringUtils.toNormalStr(
                            key));
                }
            }
            i++;
        }
    }

    public static void printANSIColors() {
        for (int i = 0; i < 300; i++) {
            System.out.print("\033[" + i + "m" + "\\033[" + i + "m");
            if (i % 25 == 0) {
                System.out.println();
            }
        }
        System.out.println(ANSI_RESET);
    }
}