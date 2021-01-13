package Sets;

import Utils.AES.AES;
import Utils.ByteOperation;

public class Set2 {
    public static void main(String[] args){
        String message = "YELLOW SUBMARINE";
        byte[] messageBytes = message.getBytes();
        byte[] paddedMessage = ByteOperation.padPKCS7(messageBytes,20);
        for(byte messageByte : paddedMessage){
            System.out.printf("0x%02X ", messageByte);
        }
        System.out.println();
        System.out.println(paddedMessage.length);

        AES testCypher = new AES(128);
        byte[] plaintext = new byte[]{'T', 'w', 'o', ' ', 'O', 'n', 'e', ' ', 'N', 'i', 'n', 'e', ' ', 'T', 'w', 'o'};
        byte[] key = new byte[]{'T', 'h', 'a', 't', 's', ' ', 'm', 'y', ' ', 'K', 'u', 'n', 'g', ' ', 'F', 'u'};
        byte[] cypherText = testCypher.encrypt(plaintext, key);
        int c = 0;
        for(int i = 0; i < cypherText.length; i++){
            System.out.printf("0x%02X ",cypherText[i]);
            c++;
            if(c > 15){
                System.out.println();
                c = 0;
            }
        }

        System.out.println();
        byte[] decryptedPlaintext = testCypher.decrypt(cypherText, key);
        for(byte plainChar : decryptedPlaintext){
            System.out.print((char) plainChar);
        }
    }
}
