package Sets;
import Utils.*;
import Utils.AES.GaloisField;
import com.sun.nio.sctp.AbstractNotificationHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Scanner;
import java.util.*;

import static Utils.Base64Conversion.byteToHex;
import static Utils.Base64Conversion.encodeHexString;


public class Set1 {
    public static void main(String[] args)throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Should get: SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t");
        System.out.print("Please enter the hex String: ");
        String hexString = scanner.nextLine();
        System.out.println("Got: " + Base64Conversion.BytestoBase64(Base64Conversion.decodeHexString(hexString)));

        System.out.println("Please supply the first Hex String:");
        String hexString1 = scanner.nextLine();
        System.out.println("Please supply the second Hex String:");
        String hexString2 = scanner.nextLine();
        System.out.println(encodeHexString(XORCypher.fixed(Base64Conversion.decodeHexString(hexString1),Base64Conversion.decodeHexString((hexString2)))));

        System.out.print("Please supply the Hex String:");
        String hexString3 = scanner.nextLine();
        //System.out.println(XORCypher.singleByte(hexString3));
        System.out.println(StringUtils.scoreStrings(Base64Conversion.decodeHexString(hexString3)));
        challenge5();

        byte[] test = "This is an example".getBytes(StandardCharsets.UTF_8);

        test = XORCypher.single(test,(byte)'A');

        for(byte b : test){
            System.out.println(Integer.toBinaryString(b));
        }

    }
    public static void challenge4() throws Exception
    {
        BufferedReader c4Input = new BufferedReader(new FileReader(new File("C:\\Users\\Ascor\\Documents\\4.txt")));
        String c4Line;
        System.out.println("possible decoded values for ciphertext: ");
        int c4LineNum = 1;
        while ((c4Line = c4Input.readLine()) != null)
        {
            for (int i = 0; i < 255; i++)
            {
                byte[] decoded = XORCypher.single(Base64Conversion.decodeHexString(c4Line), (byte) i);
                double score = StringUtils.scoreStrings(decoded);
                String line = new String(decoded, StandardCharsets.UTF_8);
                if (score > 1900)
                    System.out.println("Line " + c4LineNum + ": " + line + " - Score: " + score + " Key: " + (char) i);
            }
            c4LineNum++;
        }
        c4Input.close();
    }
    public static void challenge5() throws Exception{
        byte[] c4Plaintext = ("Burning 'em, if you ain't quick and nimble I go crazy when I hear a cymbal").getBytes(StandardCharsets.UTF_8);
        byte[] c4Key = "ICE".getBytes(StandardCharsets.UTF_8);
        System.out.println("Should be: 0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f");
        System.out.println("We got:    " + Base64Conversion.encodeHexString(XORCypher.repeating(c4Plaintext,c4Key)));
    }
    public static void challenge6() throws Exception {
        String test = "this is a test";
        String wokka = "wokka wokka!!!";
        System.out.println(HammingDist.Distance(test.getBytes(), wokka.getBytes()));

        byte[] ciphertext = FileUtils.readBase64("C:\\Users\\Ascor\\Documents\\6.txt");
        ArrayList<KeyValuePair<Integer, Integer>> nrmlEditDistance = new ArrayList<KeyValuePair<Integer, Integer>>();

        // try different chunk sizes
        for (int KEYSIZE = 2; KEYSIZE < 40; KEYSIZE++)
        {
            byte[] data1 = Arrays.copyOfRange(ciphertext, 0 * KEYSIZE, 1 * KEYSIZE);
            byte[] data2 = Arrays.copyOfRange(ciphertext, 1 * KEYSIZE, 2 * KEYSIZE);
            byte[] data3 = Arrays.copyOfRange(ciphertext, 2 * KEYSIZE, 3 * KEYSIZE);
            byte[] data4 = Arrays.copyOfRange(ciphertext, 3 * KEYSIZE, 4 * KEYSIZE);
            int totaldist = HammingDist.Distance(data1, data2);
            totaldist += HammingDist.Distance(data1, data3);
            totaldist += HammingDist.Distance(data1, data4);
            totaldist += HammingDist.Distance(data2, data3);
            totaldist += HammingDist.Distance(data2, data4);
            totaldist += HammingDist.Distance(data3, data4);

            nrmlEditDistance.add(new KeyValuePair<Integer, Integer>(KEYSIZE, totaldist / (KEYSIZE)));
        }

        // see which chunk size has the lowest hamming distance

        Collections.sort(nrmlEditDistance, new Comparator<KeyValuePair<Integer, Integer>>() {
            @Override
            public int compare(KeyValuePair<Integer, Integer> arg0, KeyValuePair<Integer, Integer> arg1)
            {
                return arg0.getValue().compareTo(arg1.getValue());
            }
        });

        for (int i = 0; i < 1; i++)
        {
            int keysize = nrmlEditDistance.get(i).getKey();
            System.out.println("trying keysize " + keysize);
            byte[] key = new byte[keysize];

            // split the data into keysize length blocks
            byte[][] data = new byte[ciphertext.length / keysize][keysize];
            int pos = 0;
            for (int j = 0; j < ciphertext.length / keysize; j++)
            {
                for (int l = 0; l < keysize; l++)
                    data[j][l] = ciphertext[pos++];
            }
            byte[][] transpose = new byte[keysize][ciphertext.length / keysize];
            for (int r = 0; r < data.length; r++)
            {
                for (int c = 0; c < data[0].length; c++)
                {
                    transpose[c][r] = data[r][c];
                }
            }

            // try each chunk against a single character and keep track of the "best" character

            for (int k = 0; k < keysize; k++)
            {
                for (int j = 32; j < 123; j++)
                {
                    byte[] decoded = XORCypher.single(transpose[k], (byte) j);
                    double score = StringUtils.stringMetric(decoded);
                    if (score > 0.86)
                    {
                        key[k] = (byte) j;
                    }
                }
            }

            System.out.println("Guessed Key: " + StringUtils.toNormalStr(key));
            System.out.println("Decrypted message:");
            System.out.println(StringUtils.toNormalStr(XORCypher.repeating(ciphertext, key)));


        }



    }
    }

