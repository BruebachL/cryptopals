package Utils;

import java.util.Base64;

public class Base64Conversion {
    public static byte[] Base64toBytes (String Cyphertext){
        return Base64.getDecoder().decode(Cyphertext.getBytes());
    }
    public static String BytestoBase64 (byte[] Cypherbytes){
        return Base64.getEncoder().encodeToString(Cypherbytes);
    }

}
