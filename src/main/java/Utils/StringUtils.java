package Utils;

import java.util.ArrayList;

public class StringUtils {
    public static int scoreStrings(byte[] hexDecodedString) {
        String ETAOIN = "etaoin shrdlcuETAOINSHRDLCU";
        int encodedStringHighScore = 0;
        int Stringscore = 0;
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
            // stuff is weighted how I felt like it
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
        String bestGuess = new String();
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
}