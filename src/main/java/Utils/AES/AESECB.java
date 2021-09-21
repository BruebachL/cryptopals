package Utils.AES;

import Utils.ByteOperation;

public class AESECB extends AES {

	public AESECB(int keyLengthInBits) {
		super(keyLengthInBits);
	}

	@Override public byte[] encrypt(byte[] message, AESKey key) {
		byte[] encryptedOut;
		if (message.length % 16 > 0) {
			encryptedOut = new byte[(message.length / 16 + 1) * (16)];
		}
		else {
			encryptedOut = new byte[message.length];
		}
		byte[] currentBlock = new byte[16];

		for (int i = 0; i < message.length / 16; i++) {
			System.arraycopy(message, i * 16, currentBlock, 0, 16);
			currentBlock = super.encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, encryptedOut, i * 16, 16);
		}
		if (message.length % 16 > 0) {
			currentBlock = new byte[message.length % 16];
			System.arraycopy(message, (message.length / 16) * 16, currentBlock, 0, message.length % 16);
			currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
			currentBlock = super.encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, encryptedOut, (message.length / 16) * 16, 16);
		}

		return encryptedOut;
	}

	@Override public byte[] decrypt(byte[] message, AESKey key) {
		byte[] decryptedOut;
		if (message.length % 16 > 0) {
			decryptedOut = new byte[(message.length / 16 + 1) * (16)];
		}
		else {
			decryptedOut = new byte[message.length];
		}
		byte[] currentBlock = new byte[16];

		for (int i = 0; i < message.length / 16; i++) {
			System.arraycopy(message, i * 16, currentBlock, 0, 16);
			currentBlock = super.decrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, decryptedOut, i * 16, 16);
		}
		if (message.length % 16 > 0) {
			currentBlock = new byte[message.length % 16];
			System.arraycopy(message, (message.length / 16) * 16, currentBlock, 0, message.length % 16);
			currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
			currentBlock = super.decrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, decryptedOut, (message.length / 16) * 16, 16);
		}

		return decryptedOut;
	}

}
