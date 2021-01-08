package Utils.AES;

import Utils.ByteOperation;

public class AES {

    GaloisField gf = new GaloisField(229);

    public AES(){

    }

    public static byte[] Encrypt(byte[] message, byte[] key){
        int numberOfRounds = 1;

        byte[] state = new byte[message.length];

        System.arraycopy(message, 0, state, 0, message.length);

        byte[] initialVector = new byte[16];

        for(byte iv : initialVector){
            iv = 0x00;
        }

        AES test = new AES();

        byte[] bytes = test.KeyExpansion128Bit(initialVector);
        int c = 0;
        for(byte byt : bytes){
            System.out.printf("0x%02X ", byt);
            c++;
            if(c > 15){
                System.out.println();
                c = 0;
            }
        }
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
