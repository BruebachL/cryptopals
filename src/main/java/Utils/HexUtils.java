package Utils;

import org.jetbrains.annotations.NotNull;

public class HexUtils {

    //TODO this is a duplicate of toByteArray
    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    public static String encodeHexString(byte[] byteArray, int spacing) {
        if(spacing < 1){
            throw new IllegalArgumentException("Spacing can't be smaller than 1.");
        }
        StringBuilder hexStringBuffer = new StringBuilder();
        int i = 0;
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
            if(i == spacing - 1){
                hexStringBuffer.append(" ");
                i = 0;
            } else {
                i++;
            }
        }
        return hexStringBuffer.toString();
    }

    //TODO this is a duplicate of decodeHexString
    public static byte[] toByteArray(String str)
    {
        if (str.length() % 2 != 0)
            return null;
        byte[] ret = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2)
        {
            ret[i / 2] = (hexToByte(str.charAt(i) + String.valueOf(str.charAt(i + 1))));
        }
        return ret;
    }

    @NotNull
    public static String[] splitStringToHexStringArray(String line) {
        if(line.length() % 2 != 0){
            throw new IllegalArgumentException("Line length wasn't divisible by 2");
        }
        String[] splitLine = new String[line.length() / 2];
        for(int l = 0; l < line.length(); l += 2){
            splitLine[l/2] = line.substring(l, l+2);
        }
        return splitLine;
    }

    @NotNull
    public static byte[][] hexStringArrayToSixteenByteChunkByteHyperArray(String[] splitLine) {
        if(splitLine.length % 16 != 0){
            throw new IllegalArgumentException("Could not split input line into 16 hex bytes");
        }

        byte[][] splitBytes = new byte[splitLine.length / 16][16];

        for(int b = 0; b < splitBytes.length; b++){
            for(int chunk = 0; chunk < 16; chunk++){
                splitBytes[b][chunk] = hexToByte(splitLine[(b * 16) + chunk]);
            }
        }
        return splitBytes;
    }
}
