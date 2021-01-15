package Sets;

import Utils.AES.AES;
import Utils.ByteOperation;
import Utils.FileUtils;

public class Set2 {
    public static void main(String[] args){
        String message = "YELLOW SUBMARINE";
        byte[] messageBytes = message.getBytes();
        byte[] kungFuBytes = "Thats my Kung Fu".getBytes();
        byte[] paddedMessage = ByteOperation.padPKCS7(messageBytes,20);
        for(byte messageByte : paddedMessage){
            System.out.printf("0x%02X ", messageByte);
        }
        System.out.println();
        System.out.println(paddedMessage.length);
        byte[][] ecb = new byte[3][16];
        ecb[0] = messageBytes;
        ecb[1] = kungFuBytes;
        ecb[2] = new byte[16];
        System.out.println(FileUtils.detectECBblock(ecb));
    }
}
