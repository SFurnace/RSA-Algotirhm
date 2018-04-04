package rsa;

import java.io.Serializable;

public class RSAEncryptedFile implements Serializable {
    private byte[] encryptedContent;
    private String fileName;

    public RSAEncryptedFile(String fileName, byte[] encryptedContent) {
        this.encryptedContent = encryptedContent;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getEncryptedContent() {
        return encryptedContent;
    }
}
