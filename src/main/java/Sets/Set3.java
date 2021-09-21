package Sets;

import Utils.AES.AES;
import Utils.AES.AESKey;
import Utils.AES.AESSessionToken;
import Utils.AES.CBCPaddingOracle;
import Utils.ByteOperation;
import Utils.HexUtils;
import com.google.common.primitives.UnsignedBytes;

import javax.crypto.BadPaddingException;

public class Set3 {

	public static void main(String[] args) throws BadPaddingException {
		challenge17();
	}

	/**
	 * This pair of functions approximates AES-CBC encryption as its deployed serverside in web applications;
	 * the second function models the server's consumption of an encrypted session token, as if it was a cookie.
	 */

	public static void challenge17() throws BadPaddingException {
		CBCPaddingOracle paddingOracle = new CBCPaddingOracle();
		paddingOracle.fullAttack();
	}
}


