package com.directconnect.mobilesdk.device;

import java.io.StringWriter;

/**
 * CardData - Card parsing and properties
 *
 * Created by Francois Bergeon on 3/10/17.
 */
public class CardData {
    public enum DataType {
        nil,
        P2PE,
        track1,
        track2,
        PAN
    }

    protected Object source;

    private String track1;
    private String track2;
    private String track3;
    private String PAN;
    private String expDate;
    private String serviceCode;
    private String cardholderName;
    private String dataBlock;
    private String KSN;
    private EncryptionParameters encryptionParameters;
    private DataType dataType = DataType.nil;

    // Constructor - Bad swipe
    CardData() {} // DataType.nil

    // Constructor - Track data
    CardData(String trackData) {
        parseTrackData(trackData);
    }

    // Constructor - PAN/Expiration date/Cardholder Name
    CardData(String PAN, String expDate, String cardholderName) {
        this.PAN = PAN;
        this.expDate = expDate;
        this.cardholderName = cardholderName;
        dataType = DataType.PAN;
    }

    // Constructor - Track data, P2PE data
    CardData(String trackData, String dataBlock, String KSN, EncryptionParameters encryptionParameters) {
        parseTrackData(trackData);

        if (dataBlock != null && dataBlock.length() > 0) {
            this.dataBlock = dataBlock;
            this.KSN = KSN;
            this.encryptionParameters = encryptionParameters;
            dataType = DataType.P2PE;
        }
    }

    // Parse track data
    private boolean parseTrackData(String trackData) {
        boolean success = false;
        if (trackData != null && trackData.length() != 0) {
            byte[] bytes = trackData.getBytes();

            // Try Track1
            if (parseTrack1(bytes)) {
                dataType = DataType.track1;
                success = true;
            }

            // Try Track2
            if (parseTrack2(bytes)) {
                dataType = DataType.track2;
                success = true;
            }
        }
        return success;
    }

    /**
     * Try to parse track1
     * Sentinels required
     * @param bytes track data
     */
    private boolean parseTrack1(byte[] bytes) {
        // Sentinels
        int start = charLookup(bytes, 0, '%');
        if (start < 0 || (bytes[start+1] != 'B' && bytes[start+1] != '*'))
            return false;

        int next = start + 2;
        int delim = charLookup(bytes, next, '^');
        if (delim < 0)
            return  false;

        PAN = new String(bytes, next, delim-next);
        next = delim+1;
        delim = charLookup(bytes, next, '^');
        if (delim < 0)
            return false;

        cardholderName = new String(bytes, next, delim-next).trim();
        cardholderName.replace('/', ' ');

        next = delim+1;
        expDate = makeExpDate(bytes, next);

        next += 4;
        serviceCode = new String(bytes, next, 3);

        delim = charLookup(bytes, next, '?') + 1;
        if (delim == 0)
            delim = bytes.length;
        track1 = new String(bytes, start, delim-start).trim();

        return true;
    }

    /**
     * Try to parse track2
     * Sentinels optional unless Track1 is present
     * @param bytes track data
     */
    private boolean parseTrack2(byte[] bytes) {
        // Sentinel or start of array
        int start = charLookup(bytes, 0, ';');
        if (start < 0) {
            // Sentinel optional if track1 not found, otherwise we don't really need track2
            if (track1 == null)
                start = 0;
            else
                return false;
        }

        int next = start + 1;
        int delim = charLookup(bytes, next, '=');
        if (delim < 0)
            return false;

        PAN = new String(bytes, next, delim-next);

        next = delim+1;
        expDate = makeExpDate(bytes, next);

        next += 4;
        serviceCode = new String(bytes, next, 3);

        delim = charLookup(bytes, next, '?') + 1;
        if (delim == 0)
            delim = bytes.length;
        track2 = new String(bytes, start, delim-start).trim();

        return true;
    }

    private static int charLookup(byte[] bytes, int start, char c) {
        for (int i = start; i < bytes.length; i++)
            if (bytes[i] == c)
                return i;
        return -1;
    }

    private static String makeExpDate(byte[] bytes, int start) {
        if (bytes.length < start+4)
            return null;
        StringBuilder sb = new StringBuilder(4);
        sb.append((char)bytes[start+2]);
        sb.append((char)bytes[start+3]);
        sb.append((char)bytes[start+0]);
        sb.append((char)bytes[start+1]);
        return sb.toString();
    }

    // Property getters
    public String getTrack1() { return track1; }
    public String getTrack2() { return track2; }
    public String getTrack3() { return track3; }
    public String getPAN() { return PAN; }
    public String getExpDate() { return expDate; }
    public String getServiceCode() { return serviceCode; }
    public String getCardholderName() { return cardholderName; }
    public String getDataBlock() { return dataBlock; }
    public String getKSN() { return KSN; }
    public EncryptionParameters getEncryptionParameters() { return encryptionParameters; }
    public DataType getDataType() { return dataType; }
    
    // Convert byte array to String
    public static String byteArrayToHexString(byte[] bytes) {
	   StringBuilder sb = new StringBuilder(bytes.length * 2);
	   for(byte b: bytes)
	      sb.append(String.format("%02X", b));
	   return sb.toString();
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        sw.append("CardData: ");
        switch (dataType)
        {
            case PAN:
                sw.append("PAN");
                break;
            case track1:
                sw.append("track1");
                break;
            case track2:
                sw.append("track2");
                break;
            case P2PE:
                sw.append("P2PE");
                break;
        }
        return sw.toString();
    }
}
