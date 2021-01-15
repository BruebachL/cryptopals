package Sets;

import Utils.*;
import Utils.AES.AES;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static Utils.HexUtils.encodeHexString;


public class Set1 {
    public static void main(String[] args) throws Exception {
        challenge1();
        System.out.println();
        challenge2();
        System.out.println();
        challenge3();
        System.out.println();
        challenge4();
        System.out.println();
        challenge5();
        System.out.println();
        challenge6();
        System.out.println();
        challenge7();
        System.out.println();
        challenge8();
    }

    public static void challenge1() {
        String hexString = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        System.out.println("Hex -> Base64: " + hexString);
        System.out.println("Should get: SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t");
        System.out.println("Got: " + Base64Conversion.BytestoBase64(HexUtils.decodeHexString(hexString)));
    }

    public static void challenge2(){
        String hexString1 = "1c0111001f010100061a024b53535009181c";
        String hexString2 = "686974207468652062756c6c277320657965";
        String result = "746865206b696420646f6e277420706c6179";
        System.out.println(hexString1 + " XOR'd with " + hexString2 + " should get:");
        System.out.println(result);
        System.out.println("Got: " + encodeHexString(XORCypher.fixed(HexUtils.decodeHexString(hexString1), HexUtils.decodeHexString((hexString2)))));
    }

    public static void challenge3(){
        String hexString3 = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        for (int i = 0; i < 255; i++) {
            byte[] decoded = XORCypher.single(HexUtils.decodeHexString(hexString3), (byte) i);
            double score = StringUtils.scoreStrings(decoded);
            String line = new String(decoded, StandardCharsets.UTF_8);
            if (score > 1950)
                System.out.println(line + " - Score: " + score + " Key: " + (char) i);
        }
    }

    public static void challenge4() throws Exception {
        BufferedReader c4Input = new BufferedReader(new FileReader("src/main/resources/cyphertexts/4.txt"));
        String c4Line;
        System.out.println("possible decoded values for ciphertext: ");
        int c4LineNum = 1;
        while ((c4Line = c4Input.readLine()) != null) {
            for (int i = 0; i < 255; i++) {
                byte[] decoded = XORCypher.single(HexUtils.decodeHexString(c4Line), (byte) i);
                double score = StringUtils.scoreStrings(decoded);
                String line = new String(decoded, StandardCharsets.UTF_8);
                if (score > 1950)
                    System.out.println("Line " + c4LineNum + ": " + line + " - Score: " + score + " Key: " + (char) i);
            }
            c4LineNum++;
        }
        c4Input.close();
    }

    public static void challenge5() {
        byte[] c4Plaintext = ("Burning 'em, if you ain't quick and nimble I go crazy when I hear a cymbal").getBytes(StandardCharsets.UTF_8);
        byte[] c4Key = "ICE".getBytes(StandardCharsets.UTF_8);
        System.out.println("Should be: 0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f");
        System.out.println("We got:    " + encodeHexString(XORCypher.repeating(c4Plaintext, c4Key)));
    }

    public static void challenge6() {
        String test = "this is a test";
        String wokka = "wokka wokka!!!";
        System.out.println(HammingDist.Distance(test.getBytes(), wokka.getBytes()));

        byte[] ciphertext = FileUtils.readBase64("src/main/resources/cyphertexts/6.txt");
        ArrayList<KeyValuePair<Integer, Integer>> nrmlEditDistance = new ArrayList<>();

        // try different chunk sizes
        for (int KEYSIZE = 2; KEYSIZE < 40; KEYSIZE++) {
            byte[] data1 = Arrays.copyOfRange(ciphertext, 0, KEYSIZE);
            byte[] data2 = Arrays.copyOfRange(ciphertext, KEYSIZE, 2 * KEYSIZE);
            byte[] data3 = Arrays.copyOfRange(ciphertext, 2 * KEYSIZE, 3 * KEYSIZE);
            byte[] data4 = Arrays.copyOfRange(ciphertext, 3 * KEYSIZE, 4 * KEYSIZE);
            int totaldist = HammingDist.Distance(data1, data2);
            totaldist += HammingDist.Distance(data1, data3);
            totaldist += HammingDist.Distance(data1, data4);
            totaldist += HammingDist.Distance(data2, data3);
            totaldist += HammingDist.Distance(data2, data4);
            totaldist += HammingDist.Distance(data3, data4);

            nrmlEditDistance.add(new KeyValuePair<>(KEYSIZE, totaldist / (KEYSIZE)));
        }

        // see which chunk size has the lowest hamming distance

        nrmlEditDistance.sort(Map.Entry.comparingByValue());

        for (int i = 0; i < 1; i++) {
            int keysize = nrmlEditDistance.get(i).getKey();
            System.out.println("Trying key size " + keysize);
            byte[] key = new byte[keysize];

            // split the data into key sized blocks
            byte[][] data = new byte[ciphertext.length / keysize][keysize];
            int pos = 0;
            for (int j = 0; j < ciphertext.length / keysize; j++) {
                for (int l = 0; l < keysize; l++)
                    data[j][l] = ciphertext[pos++];
            }
            byte[][] transpose = new byte[keysize][ciphertext.length / keysize];
            for (int r = 0; r < data.length; r++) {
                for (int c = 0; c < data[0].length; c++) {
                    transpose[c][r] = data[r][c];
                }
            }

            // try each chunk against a single character and keep track of the "best" character

            for (int k = 0; k < keysize; k++) {
                for (int j = 32; j < 123; j++) {
                    byte[] decoded = XORCypher.single(transpose[k], (byte) j);
                    double score = StringUtils.stringMetric(decoded);
                    if (score > 0.86) {
                        key[k] = (byte) j;
                    }
                }
            }

            System.out.println("Guessed Key: " + StringUtils.toNormalStr(key));
            System.out.println("Decrypted message:");
            System.out.println(StringUtils.toNormalStr(XORCypher.repeating(ciphertext, key)));
        }
    }

    public static void challenge7() {
        byte[] cypherText = FileUtils.readBase64("src/main/resources/cyphertexts/7.txt");
        AES aes = new AES(128);
        cypherText = aes.ecbModeDecryption(cypherText, "YELLOW SUBMARINE".getBytes());
        for (byte plainChar : cypherText) {
            System.out.print((char) plainChar);
        }
    }

    public static void challenge8() throws Exception {
        String[] c8Lines = FileUtils.readLines("src/main/resources/cyphertexts/8.txt");
        for (int lineNumber = 0; lineNumber < c8Lines.length; lineNumber++) {
            String[] splitLine = HexUtils.splitStringToHexStringArray(c8Lines[lineNumber]);

            byte[][] splitBytes = HexUtils.hexStringArrayToSixteenByteChunkByteHyperArray(splitLine);

            if(FileUtils.detectECBblock(splitBytes)){
                System.out.println("Line " + (lineNumber + 1) + " could be ECB");
            }
        }
    }
}

