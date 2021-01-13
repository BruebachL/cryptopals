package Utils;

import com.google.common.primitives.UnsignedBytes;

import java.nio.ByteBuffer;

public class ByteOperation {

    public static byte[] padPKCS7(byte[] messageToPad, int blockSize){
        int requiredPadSize;
        if(messageToPad.length<blockSize){
            requiredPadSize = blockSize-messageToPad.length;
        }else {
            requiredPadSize = messageToPad.length % blockSize;
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
}
