package Sets;

import Utils.AES.AES;
import Utils.AES.GaloisField;
import Utils.ByteOperation;

import java.util.Collections;

public class Set2 {
    public static void main(String[] args){
        String message = "YELLOW SUBMARINE";
        byte[] messageBytes = message.getBytes();
        byte[] paddedMessage = ByteOperation.padPKCS7(messageBytes,20);
        for(byte messageByte : paddedMessage){
            System.out.printf("0x%02X ", messageByte);
            //System.out.print((char)messageByte);
        }
        System.out.println();
        System.out.println(paddedMessage.length);
        int generator = 0xE5;
        GaloisField gf = new GaloisField(generator);
        byte test = Byte.parseByte("00001111",2);
        System.out.println(Byte.toString(test));
        System.out.println(ByteOperation.checkBitSet(test,4));
        byte sevenAsBytes = Byte.parseByte("00000111",2);
        byte threeAsBytes = Byte.parseByte("00000011",2);
        System.out.print("Multiplying " + Byte.toString(sevenAsBytes) + " x " + Byte.toString(threeAsBytes) + " with Galois Multiplication: ");
        System.out.println(gf.galoisMultiplication(sevenAsBytes, threeAsBytes));

        gf.printExponentTable();
        gf.printLogTable();

        System.out.println(gf.fastGaloisMultiplication(3,7));
        System.out.println(gf.galoisMultiplicativeInverse(4));
       /* Collections.sort(gf.exponentiationTable);
        gf.printExponentTable();
        Collections.sort(gf.logarithmicTable);
        gf.printLogTable();
        System.out.println(gf.logarithmicTable.get(229));
        for(int i = 0; i < 256; i++){
            System.out.println(i + ":" + gf.galoisMultiplicativeInverse(i) + ", ");
        }*/

        System.out.println(gf.sbox(154));
        AES testCypher = new AES(128);
        byte[] key = new byte[]{'T', 'h', 'a', 't', 's', ' ', 'm', 'y', ' ', 'K', 'u', 'n', 'g', ' ', 'F', 'u'};
        byte[] plaintext = new byte[]{'T', 'w', 'o', ' ', 'O', 'n', 'e', ' ', 'N', 'i', 'n', 'e', ' ', 'T', 'w', 'o'};
        byte[] cypherText = testCypher.Encrypt(plaintext, key);
        int c = 0;
        for(int i = 0; i < cypherText.length; i++){
            System.out.printf("0x%02X ",cypherText[i]);
            c++;
            if(c > 15){
                System.out.println();
                c = 0;
            }
        }
    }
}
