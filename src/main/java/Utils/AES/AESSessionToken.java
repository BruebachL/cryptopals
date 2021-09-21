package Utils.AES;

public class AESSessionToken {

	private byte[] IV;
	private byte[] message;

	public AESSessionToken(byte[] IV, byte[] message){
		this.IV = IV;
		this.message = message;
	}

	public byte[] getIV() {
		return IV;
	}

	public void setIV(byte[] IV) {
		this.IV = IV;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
}
