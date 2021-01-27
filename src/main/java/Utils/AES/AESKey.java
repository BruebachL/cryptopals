package Utils.AES;

import Utils.ByteOperation;
import com.google.common.primitives.UnsignedBytes;

import java.security.SecureRandom;

public class AESKey {

    GaloisField gf = new GaloisField(229);

    byte[] initialKey;
    byte[] expandedKey;

    public AESKey(){
        initialKey = ByteOperation.generateRandomByteArray(16);
        this.expandedKey = keyExpansion128Bit(initialKey);
    }

    public AESKey(byte[] initialKey){
        this.initialKey = initialKey;
        this.expandedKey = keyExpansion128Bit(initialKey);
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

}
