package Utils.AES;

import Utils.ByteOperation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class AES {

    GaloisField gf = new GaloisField(229);

    static ArrayList<String> expectedKeyForAllZeroes = new ArrayList<>(Arrays.asList("0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00",
            "0x62", "0x63", "0x63", "0x63", "0x62", "0x63", "0x63", "0x63", "0x62", "0x63", "0x63", "0x63", "0x62", "0x63", "0x63", "0x63",
            "0x9B", "0x98", "0x98", "0xC9", "0xF9", "0xFB", "0xFB", "0xAA", "0x9B", "0x98", "0x98", "0xC9", "0xF9", "0xFB", "0xFB", "0xAA",
            "0x90", "0x97", "0x34", "0x50", "0x69", "0x6C", "0xCF", "0xFA", "0xF2", "0xF4", "0x57", "0x33", "0x0B", "0x0F", "0xAC", "0x99",
            "0xEE", "0x06", "0xDA", "0x7B", "0x87", "0x6A", "0x15", "0x81", "0x75", "0x9E", "0x42", "0xB2", "0x7E", "0x91", "0xEE", "0x2B",
            "0x7F", "0x2E", "0x2B", "0x88", "0xF8", "0x44", "0x3E", "0x09", "0x8D", "0xDA", "0x7C", "0xBB", "0xF3", "0x4B", "0x92", "0x90",
            "0xEC", "0x61", "0x4B", "0x85", "0x14", "0x25", "0x75", "0x8C", "0x99", "0xFF", "0x09", "0x37", "0x6A", "0xB4", "0x9B", "0xA7",
            "0x21", "0x75", "0x17", "0x87", "0x35", "0x50", "0x62", "0x0B", "0xAC", "0xAF", "0x6B", "0x3C", "0xC6", "0x1B", "0xF0", "0x9B",
            "0x0E", "0xF9", "0x03", "0x33", "0x3B", "0xA9", "0x61", "0x38", "0x97", "0x06", "0x0A", "0x04", "0x51", "0x1D", "0xFA", "0x9F",
            "0xB1", "0xD4", "0xD8", "0xE2", "0x8A", "0x7D", "0xB9", "0xDA", "0x1D", "0x7B", "0xB3", "0xDE", "0x4C", "0x66", "0x49", "0x41",
            "0xB4", "0xEF", "0x5B", "0xCB", "0x3E", "0x92", "0xE2", "0x11", "0x23", "0xE9", "0x51", "0xCF", "0x6F", "0x8F", "0x18", "0x8E" ));

    static ArrayList<String> expectedKeyForAllFs = new ArrayList<>(Arrays.asList("0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF", "0xFF",
            "0xE8", "0xE9", "0xE9", "0xE9", "0x17", "0x16", "0x16", "0x16", "0xE8", "0xE9", "0xE9", "0xE9", "0x17", "0x16", "0x16", "0x16",
            "0xAD", "0xAE", "0xAE", "0x19", "0xBA", "0xB8", "0xB8", "0x0F", "0x52", "0x51", "0x51", "0xE6", "0x45", "0x47", "0x47", "0xF0",
            "0x09", "0x0E", "0x22", "0x77", "0xB3", "0xB6", "0x9A", "0x78", "0xE1", "0xE7", "0xCB", "0x9E", "0xA4", "0xA0", "0x8C", "0x6E",
            "0xE1", "0x6A", "0xBD", "0x3E", "0x52", "0xDC", "0x27", "0x46", "0xB3", "0x3B", "0xEC", "0xD8", "0x17", "0x9B", "0x60", "0xB6",
            "0xE5", "0xBA", "0xF3", "0xCE", "0xB7", "0x66", "0xD4", "0x88", "0x04", "0x5D", "0x38", "0x50", "0x13", "0xC6", "0x58", "0xE6",
            "0x71", "0xD0", "0x7D", "0xB3", "0xC6", "0xB6", "0xA9", "0x3B", "0xC2", "0xEB", "0x91", "0x6B", "0xD1", "0x2D", "0xC9", "0x8D",
            "0xE9", "0x0D", "0x20", "0x8D", "0x2F", "0xBB", "0x89", "0xB6", "0xED", "0x50", "0x18", "0xDD", "0x3C", "0x7D", "0xD1", "0x50",
            "0x96", "0x33", "0x73", "0x66", "0xB9", "0x88", "0xFA", "0xD0", "0x54", "0xD8", "0xE2", "0x0D", "0x68", "0xA5", "0x33", "0x5D",
            "0x8B", "0xF0", "0x3F", "0x23", "0x32", "0x78", "0xC5", "0xF3", "0x66", "0xA0", "0x27", "0xFE", "0x0E", "0x05", "0x14", "0xA3",
            "0xD6", "0x0A", "0x35", "0x88", "0xE4", "0x72", "0xF0", "0x7B", "0x82", "0xD2", "0xD7", "0x85", "0x8C", "0xD7", "0xC3", "0x26"));

    byte[] expandedKey;

    int keyLengthInBits;

    int numberOfWordsInBlock = 4;

    byte[][] state = new byte[4][numberOfWordsInBlock];

    int numberOfRounds;

    public AES(int keyLengthInBits){
        this.keyLengthInBits = keyLengthInBits;
        switch(keyLengthInBits){
            case 128:
                numberOfRounds = 10;
                break;
                // Not implemented for now but included for completeness sake
            case 192:
                numberOfRounds = 12;
                break;
            case 256:
                numberOfRounds = 14;
                break;
        }
    }

    public static byte[] Encrypt(byte[] message, byte[] key){
        int numberOfRounds = 1;

        byte[] state = new byte[message.length];

        System.arraycopy(message, 0, state, 0, message.length);

        testKeyExpansion();

        AddRoundKey(state, key);

        for(int i = 0; i<numberOfRounds;i++){
            SubBytes(state);
            ShiftRows(state);
            MixColumns();
            AddRoundKey(state, key);
        }

        // final round
        SubBytes(state);
        ShiftRows(state);
        AddRoundKey(state, key);

        return state;
    }

    public byte[] KeyExpansion128Bit(byte[] initialVector){
        if(initialVector.length != 16){
            throw new IllegalArgumentException("Initial Vector should be 16 Bytes!");
        }
        byte[] expandedKey = new byte[176];
        for(int i = 0; i < 16; i++){
            expandedKey[i] = initialVector[i];
        }
        int rconValue = 1;
        int c = 16;
        byte[] t = new byte[4];
        while(c < 176){
            for(int a = 0; a < 4; a++){
                t[a] = expandedKey[a + c - 4];
            }
            if(c % 16 == 0){
                t = ByteOperation.fromIntToByteArray(scheduleCore(ByteOperation.fromByteArrayToInt(t), rconValue));
                rconValue++;
            }
            for(int a = 0; a < 4; a++){
                expandedKey[c] = (byte) (expandedKey[c - 16] ^ t[a]);
                c++;
            }
        }
        return expandedKey;
    }

    public static void testKeyExpansion(){
        byte[] initialVector = new byte[16];

        for(byte iv : initialVector){
            iv = 0x00;
        }

        AES test = new AES(128);

        byte[] bytes = test.KeyExpansion128Bit(initialVector);
        int c = 0;
        for(int i = 0; i < bytes.length; i++){
            System.out.printf("0x%02X ", bytes[i]);
            c++;
            if(c > 15){
                System.out.println();
                c = 0;
            }
        }
        for(int i = 0; i < bytes.length; i++){
            String expected = expectedKeyForAllZeroes.get(i);
            String actual = String.format("0x%02X", bytes[i]);
            if(!expected.equals(actual)){
                System.out.println("Found not matching at position " + i + " Expected: " + expected + " Actual: " + actual);
            }
        }

        System.out.println();

        initialVector = new byte[16];

        Arrays.fill(initialVector, (byte) 0xff);

        test = new AES(128);

        bytes = test.KeyExpansion128Bit(initialVector);
        c = 0;
        for(int i = 0; i < bytes.length; i++){
            System.out.printf("0x%02X ", bytes[i]);
            c++;
            if(c > 15){
                System.out.println();
                c = 0;
            }
        }
        for(int i = 0; i < bytes.length; i++){
            String expected = expectedKeyForAllFs.get(i);
            String actual = String.format("0x%02X", bytes[i]);
            if(!expected.equals(actual)){
                System.out.println("Found not matching at position " + i + " Expected: " + expected + " Actual: " + actual);
            }
        }
    }

    public int scheduleCore(int in, int i){
        byte[] inArray = ByteOperation.fromIntToByteArray(in);
        inArray = ByteOperation.rotate(inArray);
        for(int a = 0; a < 4; a++){
            inArray[a] = (byte) gf.sbox(inArray[a]);
        }
        inArray[0] ^= gf.rcon((byte) i);
        return ByteOperation.fromByteArrayToInt(inArray);
    }

    public static void SubBytes(byte[] state){

    }

    public static void ShiftRows(byte[] state){

    }

    public static void MixColumns(){

    }

    public static void AddRoundKey(byte[] state, byte[] key){

    }

}
