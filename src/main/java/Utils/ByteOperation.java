package Utils;

import com.google.common.primitives.UnsignedBytes;

import javax.crypto.BadPaddingException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;

public class ByteOperation {

    public static byte[] padPKCS7(byte[] messageToPad, int blockSize){
        int requiredPadSize;
        if(messageToPad.length<blockSize){
            requiredPadSize = blockSize-messageToPad.length;
        }else {
            requiredPadSize = blockSize - (messageToPad.length % blockSize);
        }
        byte[] padding = new byte[requiredPadSize];

        for(int i = 0; i<padding.length;i++){
            padding[i] = (byte) requiredPadSize;
        }

        byte[] buffer = new byte[messageToPad.length+padding.length];

        for(int i = 0; i<messageToPad.length;i++){
            buffer[i] = messageToPad[i];
        }

        for(int i = messageToPad.length; i<messageToPad.length+padding.length;i++){
            buffer[i] = padding[i-messageToPad.length];
        }

        return buffer;
    }

    public static byte[] stripAndValidatePKCS7padding(byte[] messageToPad, int blockSize) throws BadPaddingException {
        int bytesOfPadding = UnsignedBytes.toInt(messageToPad[messageToPad.length-1]);
        if(bytesOfPadding > blockSize || bytesOfPadding <= 0){
            throw new BadPaddingException();
        }
        if(bytesOfPadding > 0) {
            for (int i = bytesOfPadding; i > 0; i--) {
                if (messageToPad[messageToPad.length - i] != bytesOfPadding) {
                    throw new BadPaddingException();
                }
            }
            byte[] buffer = new byte[messageToPad.length - bytesOfPadding];
            System.arraycopy(messageToPad, 0, buffer, 0, buffer.length);
            return buffer;
        }
        return messageToPad;
    }

    public static boolean checkBitSet(int checkOn, int position){
        int shifted = (checkOn >> position-1);
        return (shifted & 1) == 1;
    }

    public static byte[] fromIntToByteArray(int in){
        return ByteBuffer.allocate(4).putInt(in).array();
    }

    public static int fromByteArrayToInt(byte[] in){
        return ByteBuffer.wrap(in).getInt();
    }

    public static byte[] rotate(byte[] in){
        byte a = in[0];
        for(int i = 0; i < 3; i++){
            in[i] = in[i + 1];
        }
        in[3] = a;
        return in;
    }

    public static int negativeByteToPositiveInt(int signedByte){
       if(signedByte > 0){
           return signedByte;
       } else {
           return signedByte + 255;
       }
    }

    public static byte[][] copyToColumnMajorOrderArray(byte[] in, int wordsInBlock){
        byte[][] out = new byte[4][wordsInBlock];
        int inPosition = 0;
        for(int i = 0; i < 4; i++){
            for(int c = 0; c < wordsInBlock; c++){
                out[c][i] = in[inPosition++];
            }
        }
        return out;
    }

    public static byte[] copyFromColumnMajorOrderArray(byte[][] in){
        byte[] out = new byte[in[0].length * in.length];
        int outPosition = 0;
        for(int i = 0; i < in.length; i++){
            for(int c = 0; c < in[0].length; c++){
                out[outPosition++] = in[c][i];
            }
        }
        return out;
    }

    public static byte[] getBlock(byte[] in, int blockNumber) {
        byte[][] splitIn = splitByteArrayInto16ByteChunkHyperArray(in);
        return splitIn[blockNumber];
    }

    public static byte[][] transposeByteMatrix(byte[][] input) {
        int length = Arrays.stream(input).max(Comparator.comparingInt(a -> a.length)).get().length;
        byte[][] transposed = new byte[length][input.length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                transposed[j][i] = input[i][j];
            }
        }
        return transposed;
    }

    public static byte[][] truncateByteMatrix(byte[][] input) {
        int length = Arrays.stream(input).min(Comparator.comparingInt(a -> a.length)).get().length;
        byte[][] truncated = new byte[input.length][length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < length; j++) {
                truncated[i][j] = input[i][j];
            }
        }
        return truncated;
    }

    public static byte[][] splitByteArrayInto16ByteChunkHyperArray(byte[] in) {
        if (in.length % 16 != 0) {
            throw new IllegalArgumentException(
                "Can't split byte array of length " + in.length + " into 16 byte chunks. Leftover bytes: "
                    + in.length % 16);
        }
        byte[][] out = new byte[in.length / 16][16];
        for (int i = 0; i < in.length / 16; i++) {
            for (int c = 0; c < 16; c++) {
                out[i][c] = in[(i * 16) + c];
            }
        }
        return out;
    }

    public static byte[] merge16ByteChunkHyperArray(byte[][] in){
        for(byte[] chunk : in){
            if(chunk.length != 16){
                throw new IllegalArgumentException("Can't merge hyper array chunks of size " + chunk.length + "! Only 16 Byte chunks are supported");
            }
        }
        byte[] out = new byte[in.length * 16];
        for(int i = 0; i < in.length; i++){
            System.arraycopy(in[i], 0, out, i * 16, 16);
        }
        return out;
    }

    public static int countWhereECBblockOccurs(byte[][] in){
        for(int b = 0; b < in.length; b++){
            for(int bAgain = 0; bAgain < in.length; bAgain++){
                if(Arrays.equals(in[b], in[bAgain]) && b != bAgain){
                    return b;
                }
            }
        }
        return -1;
    }

    public static byte[] generateRandomByteArray(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public static byte[] prefixByteArrayWithRandomByteAmount(byte[] toPrefix) {
        int randomPrefixAmount = (int) (Math.random() * 100);
        byte[] prefixed = new byte[randomPrefixAmount + toPrefix.length];
        for (int i = 0; i < randomPrefixAmount; i++) {
            prefixed[i] = (byte) (Math.random() * 256);
        }
        System.arraycopy(toPrefix, 0, prefixed, randomPrefixAmount, toPrefix.length);
        return prefixed;
    }

    public static boolean compareByteArrays(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            throw new IllegalArgumentException(
                "Bytearrays to compare must match in length. B1 Length:" + b1.length + " B2 Length: " + b2.length);
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] splitToEnd(byte[] toSplit, int splitPointFromEnd) {
        byte[] output = new byte[toSplit.length - splitPointFromEnd];
        System.arraycopy(toSplit, toSplit.length - splitPointFromEnd, output, 0, splitPointFromEnd);
        return output;
    }

    public static byte[] forgePlaintext(int plainTextTotalLength, byte[] knownPlaintext, byte padWith,
        boolean isPrefixedByUnknown) {
        return isPrefixedByUnknown ?
            ByteOperation.prefixPadKnownPlaintext(knownPlaintext, plainTextTotalLength, padWith) :
            ByteOperation.appendPadKnownPlaintext(knownPlaintext, plainTextTotalLength, padWith);

    }

    public static byte[] prefixPadKnownPlaintext(byte[] knownPlaintext, int lengthToPadTo, byte fillWith) {
        byte[] forgedPlaintext = new byte[lengthToPadTo];
        Arrays.fill(forgedPlaintext, fillWith);
        System.arraycopy(knownPlaintext, 0, forgedPlaintext, lengthToPadTo - knownPlaintext.length,
            knownPlaintext.length);
        return forgedPlaintext;
    }

    public static byte[] appendPadKnownPlaintext(byte[] knownPlaintext, int lengthToPadTo, byte fillWith) {
        byte[] forgedPlaintext = new byte[lengthToPadTo];
        Arrays.fill(forgedPlaintext, fillWith);
        System.arraycopy(knownPlaintext, 0, forgedPlaintext, 0, knownPlaintext.length);
        return forgedPlaintext;
    }

}
