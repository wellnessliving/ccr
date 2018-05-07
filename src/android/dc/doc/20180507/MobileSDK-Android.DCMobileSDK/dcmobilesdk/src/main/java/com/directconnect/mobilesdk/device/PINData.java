package com.directconnect.mobilesdk.device;

/**
 * PINdata
 *
 * Created by Francois Bergeon on 3/10/17.
 */
public class PINData {
    private String dataBlock;
    private String KSN;

    PINData(String dataBlock, String KSN) {
        this.dataBlock = dataBlock;
        this.KSN = KSN;
    }

    // Property getters
    public String getDataBlock() { return dataBlock; }
    public String getKSN() { return KSN; }
}
