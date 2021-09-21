package Utils.AES;

import Utils.Base64Conversion;
import Utils.ByteOperation;
import Utils.HexUtils;
import Utils.StringUtils;

import javax.crypto.BadPaddingException;

public class CBCPaddingOracle {

	AES aes;
	AESKey key;
	/**
	 * Select random String, generate a random AES key (which it should save for all future encryptions), pad the string
	 * out to the 16-byte AES block size and CBC-encrypt it under that key, providing the caller the ciphertext and IV.
	 * <p>
	 * The second function should consume the ciphertext produced by the first function, decrypt it, check its padding,
	 * and return true or false depending on whether the padding is valid.
	 */

	String[] sessionTokens = new String[] { "MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc=",
		"MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic=",
		"MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw==",
		"MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg==",
		"MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl",
		"MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA==",
		"MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw==",
		"MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8=", "MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g=",
		"MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93" };

	public CBCPaddingOracle() {
		this.aes = new AES(128);
		this.key = new AESKey();
	}

	public AESSessionToken chooseAndEncryptSessionToken() {
		String sessionToken = sessionTokens[(int) (Math.random() * sessionTokens.length)];
		byte[] paddedPlain = ByteOperation.padPKCS7(sessionToken.getBytes(), 16);
		return aes.cbcModeSessionTokenEncryption(paddedPlain, key, ByteOperation.generateRandomByteArray(16));
	}

	public boolean consumeEncryptedSessionToken(AESSessionToken encryptedSessionToken) throws BadPaddingException {
		AESSessionToken decryptedSessionToken = aes.cbcModeSessionTokenDecryption(encryptedSessionToken.getMessage(),
			key, encryptedSessionToken.getIV());
		ByteOperation.stripAndValidatePKCS7padding(decryptedSessionToken.getMessage(), 16);
		return true;
	}

	public void fullAttack() throws BadPaddingException {
		AESSessionToken aesSessionToken = chooseAndEncryptSessionToken();
		byte[] plainMessage = new byte[aesSessionToken.getMessage().length];

		for(int blockPos = (aesSessionToken.getMessage().length / 16) - 1; blockPos >= 0; blockPos--){
			byte[] plainBlock = new byte[16];

			for(int pos = plainBlock.length - 1; pos >= 0; pos--){
				int padding = plainBlock.length - pos;

				byte[] previousBlock = blockPos == 0 ? aesSessionToken.getIV() : ByteOperation.getBlock(aesSessionToken.getMessage(), blockPos - 1);
				byte[] currentBlock = ByteOperation.getBlock(aesSessionToken.getMessage(), blockPos);
				byte[] forceBlock = new byte[currentBlock.length];

				for(int i = currentBlock.length - 1; i > pos; i--) {
					forceBlock[i] = (byte) (plainBlock[i] ^ padding ^ previousBlock[i]);
				}

				for (int i = 0; i < 256; i++) {
					forceBlock[pos] = (byte) i;
					AESSessionToken craftedBlockToken = new AESSessionToken(forceBlock, currentBlock);
					try {
						consumeEncryptedSessionToken(craftedBlockToken);
						plainBlock[pos] = (byte) (padding ^ i ^ previousBlock[pos]);
						System.out.println(
							"Found valid padding with fake IV: " + HexUtils.encodeHexString(craftedBlockToken.getIV(), 1));
						break;
					}
					catch (BadPaddingException e) {

					}
				}
				System.arraycopy(plainBlock, 0, plainMessage, blockPos * 16, 16);
			}
		}
		System.out.println("Final Plain " + StringUtils.toNormalStr(
			Base64Conversion.Base64toBytes(StringUtils.toNormalStr(ByteOperation.stripAndValidatePKCS7padding(plainMessage,16)))));
	}
}