package Utils.AES;

import Utils.ByteOperation;
import com.google.common.primitives.UnsignedBytes;

import java.util.ArrayList;
import java.util.Arrays;

/*
    WARNING: This source code may ONLY be used for academic purposes. It is vulnerable to a LOT of side-channel attacks.
    Never implement your own crypto. Only use tested open-source implementations.
 */

public class AES {

    GaloisField gf = new GaloisField(229);

    byte[] expandedKey;

    int expandedKeyCount = 0;

    int keyLengthInBits;

    int numberOfWordsInBlock = 4;

    public byte[][] state = new byte[4][numberOfWordsInBlock];

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

    public byte[] ecbModeEncryption(byte[] message, byte[] key){
        byte[] encryptedOut;
        if(message.length % 16 > 0){
            encryptedOut = new byte[(message.length/16 + 1)*(16)];
        }else{
            encryptedOut = new byte[message.length];
        }
        byte[] currentBlock = new byte[16];

        for(int i = 0; i < message.length/16; i++){
            System.arraycopy(message, i * 16, currentBlock, 0, 16);
            currentBlock = encrypt(currentBlock, key);
            System.arraycopy(currentBlock, 0, encryptedOut, i * 16, 16);
        }
        if(message.length % 16 > 0){
            currentBlock = new byte[message.length % 16];
            System.arraycopy(message, (message.length/16)*16, currentBlock, 0, message.length % 16);
            currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
            currentBlock = encrypt(currentBlock, key);
            System.arraycopy(currentBlock, 0, encryptedOut,(message.length/16)*16, 16);
        }

        return encryptedOut;
    }

    public byte[] ecbModeDecryption(byte[] message, byte[] key){
        byte[] decryptedOut;
        if(message.length % 16 > 0){
            decryptedOut = new byte[(message.length/16 + 1)*(16)];
        }else{
            decryptedOut = new byte[message.length];
        }
        byte[] currentBlock = new byte[16];

        for(int i = 0; i < message.length/16; i++){
            System.arraycopy(message, i * 16, currentBlock, 0, 16);
            currentBlock = decrypt(currentBlock, key);
            System.arraycopy(currentBlock, 0, decryptedOut, i * 16, 16);
        }
        if(message.length % 16 > 0){
            currentBlock = new byte[message.length % 16];
            System.arraycopy(message, (message.length/16)*16, currentBlock, 0, message.length % 16);
            currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
            currentBlock = decrypt(currentBlock, key);
            System.arraycopy(currentBlock, 0, decryptedOut,(message.length/16)*16, 16);
        }

        return decryptedOut;
    }

    public byte[] encrypt(byte[] message, byte[] key){
        state = ByteOperation.copyToColumnMajorOrderArray(message, numberOfWordsInBlock);

        expandedKey = keyExpansion128Bit(key);
        expandedKeyCount = 0;

        addRoundKey();

        for(int i = 1; i<numberOfRounds;i++){
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey();
        }

        // final round
        subBytes();
        shiftRows();
        addRoundKey();

        return ByteOperation.copyFromColumnMajorOrderArray(state);
    }

    public byte[] decrypt(byte[] cypher, byte[] key){
        state = ByteOperation.copyToColumnMajorOrderArray(cypher, numberOfWordsInBlock);

        expandedKey = keyExpansion128Bit(key);
        expandedKeyCount = 4 * numberOfWordsInBlock * (numberOfRounds + 1) - 1;

        invAddRoundKey();
        for(int round = numberOfRounds - 1; round >= 1; round--){
            invShiftRows();
            invSubBytes();
            invAddRoundKey();
            invMixColumns();
        }

        invShiftRows();
        invSubBytes();
        invAddRoundKey();

        return ByteOperation.copyFromColumnMajorOrderArray(state);
    }

    public byte[] keyExpansion128Bit(byte[] initialVector){
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

    public int scheduleCore(int in, int i){
        byte[] inArray = ByteOperation.fromIntToByteArray(in);
        inArray = ByteOperation.rotate(inArray);
        for(int a = 0; a < 4; a++){
            inArray[a] = (byte) gf.calculateSBoxEntry(UnsignedBytes.toInt(inArray[a]));
        }
        inArray[0] ^= gf.rcon((byte) i);
        return ByteOperation.fromByteArrayToInt(inArray);
    }

    public void subBytes(){
        for(int r = 0; r < 4; r++){
            for(int c = 0; c < numberOfWordsInBlock; c++){
                state[r][c] = gf.sBox.get(UnsignedBytes.toInt(state[r][c])).byteValue();
            }
        }
    }

    public void invSubBytes(){
        for(int r = 0; r < 4; r++){
            for(int c = 0; c < numberOfWordsInBlock; c++){
                state[r][c] = gf.invSbox.get(UnsignedBytes.toInt(state[r][c])).byteValue();
            }
        }
    }

    public void shiftRows(){
        byte[] t = new byte[4];
        for(int i = 1; i < 4; i++){
            for(int c = 0; c < numberOfWordsInBlock; c++){
                t[c] = state[i][(i+c)%numberOfWordsInBlock];
            }
            if (numberOfWordsInBlock >= 0) System.arraycopy(t, 0, state[i], 0, numberOfWordsInBlock);
        }
    }

    public void invShiftRows(){
        byte[] t = new byte[4];
        for(int i = 1; i < 4; i++){
            for(int c = 0; c < numberOfWordsInBlock; c++){
                t[(i + c) % numberOfWordsInBlock] = state[i][c];
            }
            for(int c = 0; c < numberOfWordsInBlock; c++){
                state[i][c] = t[c];
            }
        }
    }

    public void mixColumns(){
        byte[] sp = new byte[4];
        for(int c = 0; c < 4; c++){
            sp[0] = (byte) ((gf.galoisMultiplication(0x02, state[0][c])) ^ (gf.galoisMultiplication(0x03, state[1][c])) ^
                        state[2][c] ^ state[3][c]);
            sp[1] = (byte) (state[0][c] ^ (gf.galoisMultiplication(0x02, state[1][c])) ^
                    (gf.galoisMultiplication(0x03, state[2][c])) ^ state[3][c]);
            sp[2] = (byte) (state[0][c] ^ state[1][c] ^
                    gf.galoisMultiplication(0x02, state[2][c]) ^ gf.galoisMultiplication(0x03, state[3][c]));
            sp[3] = (byte) (gf.galoisMultiplication(0x03, state[0][c]) ^ state[1][c] ^
                    state[2][c] ^ gf.galoisMultiplication(0x02, state[3][c]));
            for(int i = 0; i < 4; i++){
                state[i][c] = sp[i];
            }
        }
    }

    public void invMixColumns(){
        byte[] sp = new byte[4];
        for(int c = 0; c < 4; c++){
            sp[0] = (byte) ((gf.galoisMultiplication(0x0e, state[0][c])) ^ (gf.galoisMultiplication(0x0b, state[1][c])) ^
                    (gf.galoisMultiplication(0x0d, state[2][c])) ^ (gf.galoisMultiplication(0x09, state[3][c])));
            sp[1] = (byte) ((gf.galoisMultiplication(0x09, state[0][c])) ^ (gf.galoisMultiplication(0x0e, state[1][c])) ^
                    (gf.galoisMultiplication(0x0b, state[2][c])) ^ (gf.galoisMultiplication(0x0d, state[3][c])));
            sp[2] = (byte) ((gf.galoisMultiplication(0x0d, state[0][c])) ^ (gf.galoisMultiplication(0x09, state[1][c])) ^
                    (gf.galoisMultiplication(0x0e, state[2][c])) ^ (gf.galoisMultiplication(0x0b, state[3][c])));
            sp[3] = (byte) ((gf.galoisMultiplication(0x0b, state[0][c])) ^ (gf.galoisMultiplication(0x0d, state[1][c])) ^
                    (gf.galoisMultiplication(0x09, state[2][c])) ^ (gf.galoisMultiplication(0x0e, state[3][c])));
            for(int i = 0; i < 4; i++){
                state[i][c] = sp[i];
            }
        }
    }

    public void addRoundKey(){
        for(int i = 0; i < numberOfWordsInBlock; i++){
            for(int c = 0; c < 4; c++){
                state[c][i] = (byte) (state[c][i] ^ expandedKey[expandedKeyCount++]);
            }
        }
    }

    public void invAddRoundKey(){
        for(int i = numberOfWordsInBlock - 1; i >= 0; i--){
            for(int c = 3; c >= 0; c--){
                state[c][i] = (byte) (state[c][i] ^ expandedKey[expandedKeyCount--]);
            }
        }
    }

}
