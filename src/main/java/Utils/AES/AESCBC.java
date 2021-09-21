package Utils.AES;

import Utils.ByteOperation;
import Utils.XORCypher;

public class AESCBC extends AES {

	public AESCBC(int keyLengthInBits) {
		super(keyLengthInBits);
	}

	public byte[] encrypt(byte[] message, AESKey key, byte[] iv) {
		byte[] encryptedOut;
		if (iv.length != 16) {
			throw new IllegalArgumentException();
		}
		byte[] chainBlock = iv;

		if (message.length % 16 > 0) {
			encryptedOut = new byte[(message.length / 16 + 1) * (16)];
		}
		else {
			encryptedOut = new byte[message.length];
		}
		byte[] currentBlock = new byte[16];

		for (int i = 0; i < message.length / 16; i++) {
			System.arraycopy(message, i * 16, currentBlock, 0, 16);
			currentBlock = XORCypher.fixed(currentBlock, chainBlock);
			currentBlock = encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, chainBlock, 0, 16);
			System.arraycopy(currentBlock, 0, encryptedOut, i * 16, 16);
		}
		if (message.length % 16 > 0) {
			currentBlock = new byte[message.length % 16];
			System.arraycopy(message, (message.length / 16) * 16, currentBlock, 0, message.length % 16);
			currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
			currentBlock = encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, encryptedOut, (message.length / 16) * 16, 16);
		}

		return encryptedOut;
	}

	public byte[] decrypt(byte[] message, AESKey key, byte[] iv) {
		byte[] decryptedOut;
		if (iv.length != 16) {
			throw new IllegalArgumentException();
		}
		byte[] chainBlock = iv;
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
			currentBlock = XORCypher.fixed(currentBlock, chainBlock);
			System.arraycopy(currentBlock, 0, decryptedOut, i * 16, 16);
			System.arraycopy(message, i * 16, chainBlock, 0, 16);
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

	public AESSessionToken encryptSessionToken(byte[] message, AESKey key, byte[] iv) {
		byte[] encryptedOut;
		if (iv.length != 16) {
			throw new IllegalArgumentException();
		}
		byte[] chainBlock = iv.clone();

		if (message.length % 16 > 0) {
			encryptedOut = new byte[(message.length / 16 + 1) * (16)];
		}
		else {
			encryptedOut = new byte[message.length];
		}
		byte[] currentBlock = new byte[16];

		for (int i = 0; i < message.length / 16; i++) {
			System.arraycopy(message, i * 16, currentBlock, 0, 16);
			currentBlock = XORCypher.fixed(currentBlock, chainBlock);
			currentBlock = super.encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, chainBlock, 0, 16);
			System.arraycopy(currentBlock, 0, encryptedOut, i * 16, 16);
		}
		if (message.length % 16 > 0) {
			currentBlock = new byte[message.length % 16];
			System.arraycopy(message, (message.length / 16) * 16, currentBlock, 0, message.length % 16);
			currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
			currentBlock = super.encrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, encryptedOut, (message.length / 16) * 16, 16);
		}

		return new AESSessionToken(iv, encryptedOut);
	}

	public AESSessionToken decryptSessionToken(byte[] message, AESKey key, byte[] iv) {
		byte[] decryptedOut;
		if (iv.length != 16) {
			throw new IllegalArgumentException();
		}
		byte[] chainBlock = iv.clone();
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
			currentBlock = XORCypher.fixed(currentBlock, chainBlock);
			System.arraycopy(currentBlock, 0, decryptedOut, i * 16, 16);
			System.arraycopy(message, i * 16, chainBlock, 0, 16);
		}
		if (message.length % 16 > 0) {
			currentBlock = new byte[message.length % 16];
			System.arraycopy(message, (message.length / 16) * 16, currentBlock, 0, message.length % 16);
			currentBlock = ByteOperation.padPKCS7(currentBlock, 16);
			currentBlock = super.decrypt(currentBlock, key);
			System.arraycopy(currentBlock, 0, decryptedOut, (message.length / 16) * 16, 16);
		}

		return new AESSessionToken(iv, decryptedOut);
	}
}
