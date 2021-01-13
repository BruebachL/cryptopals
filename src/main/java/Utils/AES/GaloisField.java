package Utils.AES;

import Utils.ByteOperation;
import com.google.common.primitives.UnsignedBytes;
import jdk.jfr.Unsigned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringJoiner;

public class GaloisField {

    ArrayList<Integer> generators = new ArrayList<Integer>(Arrays.asList(3, 5, 6, 9, 11, 14, 17, 18, 19, 20, 23, 24, 25, 26, 28, 30, 31, 33, 34, 35, 39, 40, 42, 44, 48, 49, 60, 62, 63, 65, 69, 70, 71, 72,
                                                                        73, 75, 76, 78, 79, 82, 84, 86, 87, 88, 89, 90, 91, 95, 100, 101, 104, 105, 109, 110, 112, 113, 118, 119, 121, 122, 123, 126, 129, 132, 134, 135, 136, 138,
                                                                        142, 143, 144, 147, 149, 150, 152, 153, 155, 157, 160, 164, 165, 166, 167, 169, 170, 172, 173, 178, 180, 183, 184, 185, 186, 190, 191, 192, 193, 196, 200, 201, 206,
                                                                        207, 208, 214, 215, 218, 220, 221, 222, 226, 227, 229, 230, 231, 233, 234, 235, 238, 240, 241, 244, 245, 246, 248, 251, 253, 254, 255));
    int chosenGenerator;

    public ArrayList<Integer> exponentiationTable = new ArrayList<>();

    public ArrayList<Integer> logarithmicTable = new ArrayList<Integer>(Collections.nCopies(256, 0));

    public GaloisField(int generator){
        chosenGenerator = generator;
        createExponentTable();
        createLogTable();
    }

    public byte galoisAdd(byte a, byte b){
        return (byte) (a ^ b);
    }

    public int galoisMultiplication(int a, int b){
        a &= 0xFF;
        b &= 0xFF;
        int p = 0;
        for(int i = 0; i<9; i++) {
            if ((b & 1) == 1) {
                p ^= a;
            }
            int hiBitSet = (a & 0x80);
            boolean aHighBitSet = ByteOperation.checkBitSet(a, 8);
            a = ((a << 1)& 0xFF);
            if (aHighBitSet) {
                a ^= 0x1b;
            }
            b >>>= 1;
        }
        return p&0xFF;
    }

    public int fastGaloisMultiplication(int a, int b){
        a = ByteOperation.negativeByteToPositiveInt(a);
        b = ByteOperation.negativeByteToPositiveInt(b);
        a = logarithmicTable.get(a);
        b = logarithmicTable.get(b);
        return exponentiationTable.get((a+b)%255);
    }

    public int galoisDivision(int a, int b){
        if(a != 1) {
            a = logarithmicTable.get(a);
            b = logarithmicTable.get(b);
            return (a - b) % 255;
        } else {
            return 255 - logarithmicTable.get(b);
        }
    }

    public int galoisMultiplicativeInverse(int a) {
        if (a == 0) {
            return 0;
        } else {
            return exponentiationTable.get(255 - logarithmicTable.get(a));
        }
    }

    public byte rcon(byte in){
        byte c = 1;
        if(in == 0){
            return 0;
        } else {
            while(in != 1){
                c = (byte) galoisMultiplication(c, 2);
                in--;
            }
        }
        return c;
    }

    public int sbox(int in){
        int c, s, x;
        s = x = galoisMultiplicativeInverse(in);
        for(c = 0; c < 4; c++){
            s = (s << 1) | (s >> 7);
            s &= 0xff;
            x ^= s;
            x &= 0xff;
        }
        x ^= 99;
        x &= 0xff;
        return x;
    }

    public void createExponentTable(){
        int init = 0x01;
        exponentiationTable.add(init);
        for(int i = 0; i<255;i++){
            exponentiationTable.add(galoisMultiplication(exponentiationTable.get(exponentiationTable.size()-1),chosenGenerator));
        }
    }

    public void createLogTable(){
        for(int i = 0; i < exponentiationTable.size(); i++){
            Integer integer = exponentiationTable.get(i);
            logarithmicTable.set(integer, i);
        }
    }

    public void printLogTable(){
        System.out.println("Log table for " + chosenGenerator);
        StringJoiner sj = new StringJoiner(",");
        for(int i = 0 ; i < logarithmicTable.size(); i++){
            sj.add(String.format("0x%02X", logarithmicTable.get(i)));
            if((i+1)%16==0){
                System.out.println(sj.toString());
                sj = new StringJoiner(",");
            }
        }
    }

    public void printExponentTable(){
        System.out.println("Exponent table for " + chosenGenerator);
        StringJoiner sb = new StringJoiner(",");
        for(int i = 0; i< exponentiationTable.size(); i++){
            sb.add(String.format("0x%02X", exponentiationTable.get(i)));
            if((i+1)%16==0){
                System.out.println(sb.toString());
                sb = new StringJoiner(",");
            }
        }
    }

}
