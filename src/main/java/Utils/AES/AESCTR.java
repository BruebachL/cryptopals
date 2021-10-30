package Utils.AES;

import Utils.HexUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AESCTR extends AES {

	byte[] nonce;
	byte[] currentBlock;
	boolean useByteNonce = false;

	long streamCounter;
	int streamPosition;

	public AESCTR(AESKey key) {
		super(key);
		streamCounter = 0;
		streamPosition = 0;
		currentBlock = new byte[16];
		nonce = new byte[8];
		generateKeyBytes();
	}

	public void setNonce(byte[] n) {
		if (n.length != 8) {
			throw new IllegalArgumentException("Nonce must be 8 bytes.");
		}
		this.nonce = n;
	}

	public void setStreamCounter(byte[] byteCounter) {
		streamCounter = ByteBuffer.wrap(byteCounter).getLong();
	}

	public void generateKeyBytes() {
		System.arraycopy(nonce, 0, currentBlock, 0, nonce.length);

		byte[] streamCounterArray = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(streamCounter)
			.array();
		System.arraycopy(streamCounterArray, 0, currentBlock, nonce.length, streamCounterArray.length);
		streamCounter++;
		System.out.println("Input block keystream: " + HexUtils.encodeHexString(currentBlock));
		currentBlock = encrypt(currentBlock);
		System.out.println(HexUtils.encodeHexString(currentBlock));
	}

	// TODO: First block is gonna be all zeroes because we don't generate keybytes first... but challenge 18 breaks?
	// BECAUSE THE COUNTER IS INCREMENTED BEFORE IT SHOULD BE

	public byte[] process(byte[] message) {
		for (int i = 0; i <= message.length; i += 16) {
			for (int j = 0; j < 16; j++) {
				if (i + j == message.length)
					break;
				message[i + j] ^= currentBlock[streamPosition % 16];
				streamPosition++;
				if (streamPosition % 16 == 0)
					generateKeyBytes();
			}
		}
		return message;
	}
}
