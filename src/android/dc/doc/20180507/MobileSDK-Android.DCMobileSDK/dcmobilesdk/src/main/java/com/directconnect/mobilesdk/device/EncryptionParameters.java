package com.directconnect.mobilesdk.device;

/**
 * Created by fbergeon on 4/6/17.
 */
public class EncryptionParameters {
    private String hsmDevice;
    private String terminalType;
    private String encryptionType;

    EncryptionParameters(String hsmDevice, String terminalType, String encryptionType) {
        this.hsmDevice = hsmDevice;
        this.terminalType = terminalType;
        this.encryptionType = encryptionType;
    }

    // Property getters
    public String getHSMDevice() {
        return hsmDevice;
    }
    public String getTerminalType() {
        return terminalType;
    }
    public String getEncryptionType() {
        return encryptionType;
    }
}
