package Utils.AES;

import Utils.ByteOperation;
import Utils.FileUtils;

public class BlockmodeDetectionOracle {

    public AESKey randomKey;
    byte[] encryptedMessage;
    int useEcb;
    int amountToPrepend;

    public BlockmodeDetectionOracle(byte[] message){
        randomKey = new AESKey();
        this.encryptedMessage = encryptUsingRandomMode(message);
        this.amountToPrepend = (int) (Math.random() * 100 + 1);
        System.out.println("Oracle is going to prepend " + amountToPrepend + " bytes.");
    }

    byte[] encryptUsingRandomMode(byte[] message){
        int appendBefore = (int) (Math.random() * 6 + 5);
        byte[] bytesToAppendBefore = ByteOperation.generateRandomByteArray(appendBefore);
        int appendAfter = (int) (Math.random() * 6 + 5);
        byte[] bytesToAppendAfter = ByteOperation.generateRandomByteArray(appendAfter);
        byte[] messageWithNonsensePadding = new byte[appendBefore + message.length + appendAfter];
        System.arraycopy(bytesToAppendBefore, 0, messageWithNonsensePadding, 0, appendBefore);
        System.arraycopy(message, 0, messageWithNonsensePadding, appendBefore, message.length);
        System.arraycopy(bytesToAppendAfter, 0, messageWithNonsensePadding, appendBefore + message.length, appendAfter);

        useEcb = (int) (Math.random() * 2);
        if(useEcb == 1){
            return new AESECB(randomKey).encrypt(messageWithNonsensePadding);
        } else {
            return new AESCBC(randomKey).encrypt(messageWithNonsensePadding, ByteOperation.generateRandomByteArray(16));
        }
    }

    public void appendThenEncryptUsingECBMode(byte[] message, byte[] toAppend){
        byte[] forcedWithUnknownPlainTextAppended = new byte[message.length + toAppend.length];
        System.arraycopy(message, 0, forcedWithUnknownPlainTextAppended, 0, message.length);
        System.arraycopy(toAppend, 0, forcedWithUnknownPlainTextAppended, message.length, toAppend.length);

        this.encryptedMessage = new AESECB(randomKey).encrypt(forcedWithUnknownPlainTextAppended);
    }

    public void prependThenAppendThenEncryptUsingECBMode(byte[] message, byte[] toAppend){
        byte[] messageWithRandomBytesPrepended = prependDeterminedAmountOfRandomBytes(message);
        byte[] forcedWithUnknownPlainTextAppended = new byte[messageWithRandomBytesPrepended.length + toAppend.length];
        System.arraycopy(messageWithRandomBytesPrepended, 0, forcedWithUnknownPlainTextAppended, 0, messageWithRandomBytesPrepended.length);
        System.arraycopy(toAppend, 0, forcedWithUnknownPlainTextAppended, messageWithRandomBytesPrepended.length, toAppend.length);

        this.encryptedMessage = new AESECB(randomKey).encrypt(forcedWithUnknownPlainTextAppended);
    }

    public byte[] prependDeterminedAmountOfRandomBytes(byte[] in){
        byte[] bytesToPrepend = ByteOperation.generateRandomByteArray(amountToPrepend);
        byte[] messageWithNonsensePrepended = new byte[amountToPrepend + in.length];
        System.arraycopy(bytesToPrepend, 0, messageWithNonsensePrepended, 0, amountToPrepend);
        System.arraycopy(in, 0, messageWithNonsensePrepended, amountToPrepend, in.length);
        return messageWithNonsensePrepended;
    }

    public byte[] prependRandomAmountOfRandomBytes(byte[] in){
        int amountToPrepend = (int) (Math.random() * 100 + 1);
        byte[] bytesToPrepend = ByteOperation.generateRandomByteArray(amountToPrepend);
        byte[] messageWithNonsensePrepended = new byte[amountToPrepend + in.length];
        System.arraycopy(bytesToPrepend, 0, messageWithNonsensePrepended, 0, amountToPrepend);
        System.arraycopy(in, 0, messageWithNonsensePrepended, amountToPrepend, in.length);
        return messageWithNonsensePrepended;
    }

    public byte[] prependRandomAmountOfRandomBytes(byte[] in, int upperBound){
        if(upperBound < 0){
            throw new IllegalArgumentException("Upper bound can't be negative");
        }
        int amountToPrepend = (int) (Math.random() * upperBound + 1);
        byte[] bytesToPrepend = ByteOperation.generateRandomByteArray(amountToPrepend);
        byte[] messageWithNonsensePrepended = new byte[amountToPrepend + in.length];
        System.arraycopy(bytesToPrepend, 0, messageWithNonsensePrepended, 0, amountToPrepend);
        System.arraycopy(in, 0, messageWithNonsensePrepended, amountToPrepend, in.length);
        return messageWithNonsensePrepended;
    }

    public byte[] getEncryptedMessage(){
        return encryptedMessage;
    }

    public boolean guessThatItIsECB(){
        return FileUtils.detectECBblock(ByteOperation.splitByteArrayInto16ByteChunkHyperArray(encryptedMessage));
    }

    public void printMode(){
        switch (useEcb){
            case 0:
                System.out.println("Oracle used CBC.");
                break;
            case 1:
                System.out.println("Oracle used ECB.");
                break;
            default:
                System.out.println("Oracle had no mode defined.");
                break;
        }
    }

}
