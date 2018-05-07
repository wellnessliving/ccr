package com.directconnect.mobilesdk.device;

import java.util.Arrays;

/**
 * Created by fbergeon on 5/31/17.
 */

class UMCardData {
    boolean isValid;
    boolean isEncrypted;
    private boolean isAesEncrypted;

    byte[] track1;
    byte[] track2;
    byte[] track3;
    byte[] track1_encrypted;
    byte[] track2_encrypted;
    byte[] track3_encrypted;

    byte[] serialNumber;
    byte[] KSN;

    static boolean isBitSet(byte b, int bitIndex) {
        byte mask = (byte)(1 << bitIndex);
        return ((b & mask) != 0);
    }

    UMCardData(byte[] cardData) {
        //check validity and parse
        if (cardData.length >= 1) {
            //is valid encrypted swipe?
            if (cardData[0] == 0x02) {
                isEncrypted = true;
                verifyAndParse_encrypted(cardData);
            } else {
                //is valid unencrypted swipe?
                isEncrypted = false;
                verifyAndParse_unencrypted(cardData);
            }
        }
    }

    private void verifyAndParse_encrypted(byte[] bytes) {
    /*
    Encrypted swipe format: STX LenL LenH <payload> CheckXor CheckSum ETX
    The rest... there's a document.
    */

        //things to fill out
        byte[][] tracks = new byte[3][];
        byte[][] tracks_enc = new byte[3][];

        //shorthand
        int len  = bytes.length;

        //Verify
        if (len < 6)
            return;

        // STX ETX
        if (bytes[0] != 0x02 || bytes[len-1] != 0x03)
            return;

        // Length
        int payloadLen = ((bytes[2] & 0xff) << 8) + (bytes[1] & 0xff);
        if (payloadLen+6 != len)
            return;

        // CheckXor and CheckSum
        byte cksum=0, ckxor=0;
        for (int i=3; i<len-3; i++) {
            ckxor ^= bytes[i];
            cksum += bytes[i];
        }
        if (bytes[len-2]!=cksum || bytes[len-3]!=ckxor)
            return;

        parse(bytes, tracks, tracks_enc);

        //save result
        track1 = tracks[0];
        track2 = tracks[1];
        track3 = tracks[2];
        track1_encrypted = tracks_enc[0];
        track2_encrypted = tracks_enc[1];
        track3_encrypted = tracks_enc[2];
    }

    //parse
    private void parse(byte[] bytes, byte[][] tracks, byte[][] tracks_enc) {
        int idx = 0;
        int len = bytes.length - 3;

        //get track1, track2, track3 length
        int trackLens[] = new int[3];
        idx = 5;
        if (idx+3 > len)
            return;
        for (int i = 0; i < 3; i++)
            trackLens[i] = bytes[idx + i] & 0xff;

        //get masked track
        int trackLensSum = 0;
        idx = 10;
        for (int i = 0; i < 3; i++) {
            //skip if len is 0 or presence flag not set
            if (trackLens[i] == 0 || !isBitSet(bytes[8], i))
                continue;
            if (idx + trackLens[i] > len)
                return;
            tracks[i] = Arrays.copyOfRange(bytes, idx, idx+trackLens[i]);
            idx += trackLens[i];
            trackLensSum += trackLens[i];
        }

        //determine encryption type (TDES or AES)
        idx = 8;
        if (idx + 1 > len)
            return;

        byte encType = (byte)((bytes[idx] >> 4) & 0x03);
        if (encType == 0x00)
            isAesEncrypted = false;
        else if (encType == 0x01)
            isAesEncrypted = true;
        else
            return;

        //get encrypted section
        int trackLens_enc[] = new int[3];
        int encryptionBlockSize = (isAesEncrypted ? 16 : 8);
        for (int i = 0; i < 3; i++) {
            // Stupid!
            // trackLens_enc[i] = (int)Math.ceil((double)trackLens[i] / (double) encryptionBlockSize) * encryptionBlockSize;
            int r = ((trackLens[i] % encryptionBlockSize == 0) ? 0 : 1);
            trackLens_enc[i] = ((trackLens[i] / encryptionBlockSize) + r) * encryptionBlockSize;
        }
        int trackLensSum_enc = 0;
        idx = 10 + trackLensSum;
        for (int i = 0; i < 3; i++) {
            //skip if len is 0 or presence flag not set
            if (trackLens_enc[i] == 0 || !isBitSet(bytes[9], i))
                continue;

            if (idx + trackLens_enc[i] > len)
                return;

            tracks_enc[i] = Arrays.copyOfRange(bytes, idx, idx+trackLens_enc[i]);
            idx += trackLens_enc[i];
            trackLensSum_enc += trackLens_enc[i];
        }

        //get KSN
        if ((isBitSet(bytes[9], 7)) &&
                (isBitSet(bytes[9], 0) ||
                        isBitSet(bytes[9], 1) ||
                        isBitSet(bytes[9], 2))) {
            idx = len - 10;
            if (idx < 10 + trackLensSum + trackLensSum_enc)
                return;
            KSN = Arrays.copyOfRange(bytes, idx, idx+10);
        }

        //get serial number
        if (isBitSet(bytes[8], 7)) {
            idx = len - 10 - (KSN != null ? 10 : 0);
            if (idx < 10 + trackLensSum + trackLensSum_enc)
                return;
            serialNumber = Arrays.copyOfRange(bytes, idx, idx+10);
        }

        //all checks and parsing succeeded
        isValid = true;
    }

    private void verifyAndParse_unencrypted(byte[] cardData) {
    /*
    Unencrypted swipe is the concatenation of one or more tracks (as listed below),
    then ended by a "\x0D".
    Track formats (in regular expression):
    ISO_1: "[\x25][^\x3F]+[\x3F]"
    ISO_2: "[\x3B][^\x3F]+[\x3F]"
    JIS  : "[\x7F][^\x7F]+[\x7F]"
    */
        if (cardData[cardData.length-1] == 0x0D)
            isValid = true;
    }

    private static final EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "IDT", "DUKPT");

    // Build com.directconnect.mobilesdk.device.CardData from this UMCardData
    CardData buildCardData() {
        CardData cardData = null;
        if (isValid) {
            StringBuilder sb = new StringBuilder();
            if (track1 != null)
                sb.append(new String(track1));
            if (track2 != null)
                sb.append(new String(track2));
            String trackData = sb.toString();
            if (isEncrypted) {
                String dataBlock = null;
                String ksn = null;
                if (track2_encrypted != null)
                    dataBlock = CardData.byteArrayToHexString(track2_encrypted);
                else if (track1_encrypted != null)
                    dataBlock = CardData.byteArrayToHexString(track1_encrypted);
                if (KSN != null)
                    ksn = CardData.byteArrayToHexString(KSN);
                cardData = new CardData(trackData, dataBlock, ksn, encryptionParameters);
            } else {
                cardData = new CardData(trackData);
            }
        }
        return cardData;
    }
}
