package Utils;

import java.util.Base64;

public class Base64Conversion {
    public static byte[] Base64toBytes (String Cyphertext){
        byte[] Bytes = Base64.getDecoder().decode(Cyphertext.getBytes());
        return Bytes;
    }
    public static String BytestoBase64 (byte[] Cypherbytes){
        String Cyphertext = Base64.getEncoder().encodeToString(Cypherbytes);
        return Cyphertext;
    }

}
