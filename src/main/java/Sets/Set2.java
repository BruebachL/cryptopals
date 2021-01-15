package Sets;

import Utils.AES.AES;
import Utils.ByteOperation;
import Utils.FileUtils;

import java.nio.charset.StandardCharsets;

public class Set2 {
    public static void main(String[] args){
        challenge9();
        System.out.println();
        challenge10();
    }

    public static void challenge9(){
        byte[] paddedMessage = ByteOperation.padPKCS7("YELLOW SUBMARINE".getBytes(),20);
        for(byte messageByte : paddedMessage){
            System.out.printf("0x%02X ", messageByte);
        }
        System.out.println();
        System.out.println(paddedMessage.length);
    }

    public static void challenge10(){
        byte[] cypherText = FileUtils.readBase64("src/main/resources/cyphertexts/10.txt");
        AES aes = new AES(128);
        cypherText = aes.cbcModeDecryption(cypherText, "YELLOW SUBMARINE".getBytes(), new byte[16]);
        for (byte plainChar : cypherText) {
            System.out.print((char) plainChar);
        }
    }
}
