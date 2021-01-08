package Utils;

import java.util.BitSet;

public class HammingDist {
    public static int Distance(byte[] firstBytes, byte[] secondBytes) {
        int i = 0, count = 0;
        byte[] hammingBytes = firstBytes;
        BitSet firstBits = BitSet.valueOf(firstBytes);
        BitSet secondBits = BitSet.valueOf(secondBytes);
        firstBits.xor(secondBits);
        while (i < firstBits.size()) {
            if (firstBits.get(i) == true) {
                count++;
            }
            i++;
        }
        return count;
    }
}
