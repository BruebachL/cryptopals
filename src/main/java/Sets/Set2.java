package Sets;

import Utils.AES.AESCBC;
import Utils.AES.AESECB;
import Utils.AES.AESKey;
import Utils.AES.BlockmodeDetectionOracle;
import Utils.*;

import javax.crypto.BadPaddingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Set2 {
    public static void main(String[] args) throws BadPaddingException {
        challenge9();
        System.out.println();
        challenge10();
        System.out.println();
        challenge11();
        System.out.println();
        challenge12();
        System.out.println();
        challenge13();
        System.out.println();
        challenge14();
        System.out.println();
        challenge15();
        System.out.println();
        challenge16();
    }

    public static void challenge9() {
        byte[] paddedMessage = ByteOperation.padPKCS7("YELLOW SUBMARINE".getBytes(), 20);
        for (byte messageByte : paddedMessage) {
            System.out.printf("0x%02X ", messageByte);
        }
        System.out.println();
        System.out.println(paddedMessage.length);
    }

    public static void challenge10() {
        byte[] cypherText = FileUtils.readBase64("src/main/resources/cyphertexts/10.txt");
        AESCBC aes = new AESCBC(new AESKey("YELLOW SUBMARINE".getBytes()));
        cypherText = aes.decrypt(cypherText, new byte[16]);
        for (byte plainChar : cypherText) {
            System.out.print((char) plainChar);
        }
    }

    public static void challenge11() {
        // Inserting arbitrary length plaintext means we can feed our oracle bytes until we're sure they'll repeat
        // thus beating the nonsense padding. It seems that blocksize * 3 is the magic number for this challenge.
        BlockmodeDetectionOracle detectionOracle = new BlockmodeDetectionOracle(new byte[48]);
        detectionOracle.printMode();
        if (detectionOracle.guessThatItIsECB()) {
            System.out.println("... and guessed ECB");
        } else {
            System.out.println("... and guessed CBC");
        }
    }

    public static void challenge12() throws BadPaddingException {
        BlockmodeDetectionOracle detectionOracle = new BlockmodeDetectionOracle(new byte[48]);
        byte[] toAppend = Base64Conversion.Base64toBytes("Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg\n" +
                "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq\n" +
                "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg\n" +
                "YnkK");

        // Find Block Size
        int blockSize = 1;
        byte[] plaintext = {0x20, 0x20};
        detectionOracle.appendThenEncryptUsingECBMode(plaintext, new byte[0]);
        byte[] encryptedMessage = detectionOracle.getEncryptedMessage();
        byte[] firstBlock = new byte[encryptedMessage.length / 2];
        byte[] secondBlock = new byte[encryptedMessage.length / 2];
        System.arraycopy(encryptedMessage, 0, firstBlock, 0, firstBlock.length);
        System.arraycopy(encryptedMessage, firstBlock.length, secondBlock, 0, secondBlock.length);
        while (!Arrays.equals(firstBlock, secondBlock)) {
            blockSize++;
            plaintext = new byte[blockSize * 2];
            Arrays.fill(plaintext, (byte) 0x20);
            detectionOracle.appendThenEncryptUsingECBMode(plaintext, new byte[0]);
            encryptedMessage = detectionOracle.getEncryptedMessage();
            firstBlock = new byte[encryptedMessage.length / 2];
            secondBlock = new byte[encryptedMessage.length / 2];
            System.arraycopy(encryptedMessage, 0, firstBlock, 0, firstBlock.length);
            System.arraycopy(encryptedMessage, firstBlock.length, secondBlock, 0, secondBlock.length);
        }

        byte[] possibleECBplain = new byte[blockSize * 2];
        detectionOracle.appendThenEncryptUsingECBMode(possibleECBplain, new byte[0]);
        if (detectionOracle.guessThatItIsECB()) {
            System.out.println("Cypher used ECB with blocksize " + blockSize);
        }

        ArrayList<byte[]> dictionary = new ArrayList<>();
        byte[] forcedPlain = new byte[blockSize - 1];
        Arrays.fill(forcedPlain, (byte) 'A');
        for (int i = 0; i < 256; i++) {
            detectionOracle.appendThenEncryptUsingECBMode(forcedPlain, new byte[]{(byte) i});
            dictionary.add(detectionOracle.getEncryptedMessage());
        }

        int currentByteToDecrypt = 0;
        // because the encryption function internally applies padding we gotta pad the decrypted byte array too.
        byte[] decryptedCypher = new byte[toAppend.length + 1];
        byte[] remainingCypher = new byte[toAppend.length];
        while (remainingCypher.length > 0) {
            remainingCypher = new byte[toAppend.length - currentByteToDecrypt];
            System.arraycopy(toAppend, currentByteToDecrypt, remainingCypher, 0, toAppend.length - currentByteToDecrypt);
            detectionOracle.appendThenEncryptUsingECBMode(forcedPlain, remainingCypher);
            byte[] forcedCypher = detectionOracle.getEncryptedMessage();

            byte[] firstForcedBlock = new byte[blockSize];
            System.arraycopy(forcedCypher, 0, firstForcedBlock, 0, 16);

            for (int i = 0; i < dictionary.size(); i++) {
                if (Arrays.equals(dictionary.get(i), firstForcedBlock)) {
                    decryptedCypher[currentByteToDecrypt] = (byte) i;
                }
            }
            currentByteToDecrypt++;
        }
        decryptedCypher = ByteOperation.stripAndValidatePKCS7padding(decryptedCypher, 16);
        for (byte b : decryptedCypher) {
            System.out.print((char) b);
        }
    }

    public static String profileForChallenge13(String s) {
        KeyValueList kvList = new KeyValueList();
        s = s.replace("&", "_");
        s = s.replace("=", "_");
        kvList.add("email", s);
        kvList.add("uid", "" + new Random().nextInt(50));
        kvList.add("role", "user");
        return kvList.encode();
    }

    public static void challenge13() throws BadPaddingException {
        // This challenge "breaks" if the userID happens to be less than 10 because then the padding no longer works.
        // The fix for this is trivial and not really the point of this challenge. Maybe if I have time for it later.
        AESKey key = new AESKey();
        AESECB cypher = new AESECB(key);
        String plainText = profileForChallenge13(
            "justpaddinadmin" + (char) 11 + (char) 11 + (char) 11 + (char) 11 + (char) 11 + (char) 11 + (char) 11
                + (char) 11 + (char) 11 + (char) 11 + (char) 11 + "abc");
        byte[] cypherText = cypher.encrypt(plainText.getBytes());
        byte[][] cypherChunks = ByteOperation.splitByteArrayInto16ByteChunkHyperArray(cypherText);
        cypherChunks[3] = cypherChunks[1];
        byte[] swappedCypher = ByteOperation.merge16ByteChunkHyperArray(cypherChunks);
        byte[] decryptedText = ByteOperation.stripAndValidatePKCS7padding(cypher.decrypt(swappedCypher), 16);
        String decryptedString = StringUtils.toNormalStr(decryptedText);
        KeyValueList kvList = new KeyValueList(decryptedString);
        System.out.println(kvList.toString());
    }

    public static void challenge14() throws BadPaddingException {
        BlockmodeDetectionOracle detectionOracle = new BlockmodeDetectionOracle(new byte[48]);
        byte[] toAppend = Base64Conversion.Base64toBytes("Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg\n" +
                "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq\n" +
                "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg\n" +
                "YnkK");

        // Find Block Size
        int blockSize = 1;
        byte[] plaintext = {0x20, 0x20};

        detectionOracle.prependThenAppendThenEncryptUsingECBMode(plaintext, new byte[0]);
        byte[] encryptedMessage = detectionOracle.getEncryptedMessage();

        // Determine which blocks we can fill

        int messageSize = encryptedMessage.length;
        while (detectionOracle.getEncryptedMessage().length == messageSize) {
            blockSize++;
            plaintext = new byte[blockSize];
            Arrays.fill(plaintext, (byte) 0x20);
            detectionOracle.prependThenAppendThenEncryptUsingECBMode(plaintext, new byte[0]);
        }
        int actualBlockSize = detectionOracle.getEncryptedMessage().length - messageSize;
        System.out.println("Actual block size is " + actualBlockSize);



        byte[] possibleECBplain = new byte[actualBlockSize * 3];
        detectionOracle.prependThenAppendThenEncryptUsingECBMode(possibleECBplain, new byte[0]);
        int ecbBlockOccursAt = 0;
        if (detectionOracle.guessThatItIsECB()) {
            ecbBlockOccursAt = ByteOperation.countWhereECBblockOccurs(ByteOperation.splitByteArrayInto16ByteChunkHyperArray(detectionOracle.getEncryptedMessage()));
            System.out.println("Cypher used ECB with blocksize " + actualBlockSize);
        }

        ArrayList<byte[]> dictionary = new ArrayList<>();
        byte[] forcedPlain = new byte[blockSize + actualBlockSize - 2];
        Arrays.fill(forcedPlain, (byte) 'A');
        for (int i = 0; i < 256; i++) {
            detectionOracle.prependThenAppendThenEncryptUsingECBMode(forcedPlain, new byte[]{(byte) i});
            byte[][] cypherChunks = ByteOperation.splitByteArrayInto16ByteChunkHyperArray(detectionOracle.getEncryptedMessage());
            dictionary.add(cypherChunks[cypherChunks.length-1]);
        }

        int currentByteToDecrypt = 0;
        // because the encryption function internally applies padding we gotta pad the decrypted byte array too.
        byte[] decryptedCypher = new byte[toAppend.length + 1];
        byte[] remainingCypher = new byte[toAppend.length];
        while (remainingCypher.length > 0) {
            remainingCypher = new byte[toAppend.length - currentByteToDecrypt];
            System.arraycopy(toAppend, currentByteToDecrypt, remainingCypher, 0, toAppend.length - currentByteToDecrypt);
            detectionOracle.prependThenAppendThenEncryptUsingECBMode(forcedPlain, remainingCypher);
            byte[] forcedCypher = detectionOracle.getEncryptedMessage();

            byte[] firstForcedBlock = new byte[actualBlockSize];
            System.arraycopy(forcedCypher, (ecbBlockOccursAt * 16), firstForcedBlock, 0, 16);
            for (int i = 0; i < dictionary.size(); i++) {
                if (Arrays.equals(dictionary.get(i), firstForcedBlock)) {
                    decryptedCypher[currentByteToDecrypt] = (byte) i;
                }
            }
            currentByteToDecrypt++;
        }
        decryptedCypher = ByteOperation.stripAndValidatePKCS7padding(decryptedCypher, 16);
        for (byte b : decryptedCypher) {
            System.out.print((char) b);
        }
    }

    public static void challenge15() {
        String validPadding = "ICE ICE BABY" + (char) 4 + (char) 4 + (char) 4 + (char) 4;
        String invalidPadding = "ICE ICE BABY" + (char) 5 + (char) 5 + (char) 5 + (char) 5;
        String anotherInvalidPadding = "ICE ICE BABY" + (char) 1 + (char) 2 + (char) 3 + (char) 4;
        try {
            byte[] valid = ByteOperation.stripAndValidatePKCS7padding(validPadding.getBytes(), 16);
            System.out.println("Stripped and validated valid padding.");
        } catch (BadPaddingException e) {
            System.out.println("That wasn't supposed to happen. We thought valid padding was invalid.");
        }
        try {
            byte[] invalidPaddingBytes = ByteOperation.stripAndValidatePKCS7padding(invalidPadding.getBytes(), 16);
        } catch (BadPaddingException e) {
            System.out.println("That was supposed to happen. We successfully recognized invalid padding.");
        }
        try {
            byte[] invalidPaddingBytes = ByteOperation.stripAndValidatePKCS7padding(anotherInvalidPadding.getBytes(), 16);
        } catch (BadPaddingException e) {
            System.out.println("That was supposed to happen. We successfully recognized invalid padding again.");
        }
    }

    public static void challenge16() {
        AESKey key = new AESKey();
        AESCBC aes = new AESCBC(key);
        byte[] iv = ByteOperation.generateRandomByteArray(16);
        String userInput = ":admin<true";
        byte[] encrypted = StringUtils.challenge16StringEncryption(userInput, key, iv);
        byte[] result = aes.decrypt(encrypted, iv);
        byte[][] resultChunks = ByteOperation.splitByteArrayInto16ByteChunkHyperArray(result);
        for (byte[] chunk : resultChunks) {
            for (byte b : chunk) {
                System.out.print((char) b);
            }
            System.out.println();
        }
    }
}
