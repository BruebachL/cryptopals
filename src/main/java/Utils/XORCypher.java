package Utils;

public class XORCypher {
    public static byte[] fixed(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) throw new RuntimeException("arrays must be same length");
        byte[] xord = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            xord[i] = (byte) (b1[i] ^ b2[i]);
        }
        return xord;
    }

    public static byte[] single(byte[] arr, byte key) {
        byte[] ret = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = (byte) (arr[i] ^ key);
        }
        return ret;
    }

    public static byte[] repeating(byte[] arr, byte[] key) {
        byte[] ret = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = (byte) (arr[i] ^ key[i % key.length]);
        }
        return ret;
    }

    public static byte[] breakSingle(byte[] input) {
        double bestScore = 0;
        byte[] bestGuess = new byte[0];
        for (int i = 0; i < 255; i++) {
            byte[] decoded = XORCypher.single(input, (byte) i);
            double score = StringUtils.scoreString(decoded);
            if (score > bestScore) {
                bestScore = score;
                bestGuess = decoded;
            }
        }
        return bestGuess;
    }
}
