package com.directconnect.mobilesdk.device;

import android.content.Context;
import android.util.Log;

import com.idtech.bluetooth.IDTechOpenHelper;
import com.idtech.bluetooth.IDTechReader;
import com.idtech.bluetooth.OnReceiveListener;

/**
 * Created by fbergeon on 5/22/17.
 */

public class BTMagDeviceManager extends DeviceManager {
    private static final String TAG = "BTMagDeviceManager";
    private IDTechOpenHelper mIDTechHelper;


    public BTMagDeviceManager(Device device, Context context) {
        super(device, context);
        Log.d(TAG, "constructor");
        mIDTechHelper = new IDTechOpenHelper(context);
        mIDTechHelper.setOnReceiveListener(getReceiverListener());
    }


    @Override
    protected void _deviceConnect() {
        bConnected.set(false);
        Log.d(TAG, "connect");
        mIDTechHelper.connect(device.getAddress());
    }

    @Override
    protected void _deviceDisplayMessage(String message) {
        // Do nothing

    }
    @Override
    protected void _deviceDisconnect()
    {
        mIDTechHelper.close();

    }
    @Override
    protected void _deviceAcceptCard(String message) {
        Log.d(TAG, "setMSRReading");
        mIDTechHelper.setDecodingMethod(IDTechReader.DECODING_BOTH_DIRECTIONS);
        mIDTechHelper.setMSRReading(IDTechReader.MSR_READING_ENABLE);
    }

    private OnReceiveListener getReceiverListener() {
        return new OnReceiveListener() {
            private final String TAG = "OnReceiveListener";
//            private int STX = 0x02, ETX = 0x03;
//            private byte isTrack1Present = 0x01;
//            private byte isTrack2Present = 0x02;
//            private byte isTrack3Present = 0x04;

            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected");
                bConnected.set(true);
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void onConnecting() {
                Log.d(TAG, "onConnecting");
            }

            @Override
            public void onConnectedError(int i, String s) {
                Log.d(TAG, "onConnecting " + i + ":" + s);
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "onDisconnected");
                bConnected.set(false);
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void onReceivedData(int funcType, byte[] data) {
                Log.d(TAG, "onReceivedData " + funcType + "bytes:" + data.length);
//                if (data.length < 10)
//                    return;
//
//                if (data[0] !=  STX || data[data.length-1] != ETX) {
//                    // Malformed
//                    return;
//                }
//
//                if (data[3] != (byte)0 && data[3] != (byte)0x80) {
//                    // Not ISO/ABA format
//                    return;
//                }
//
//                int nTrack1Len = data[5];
//                int nTrack2Len = data[6];
//                int nTrack3Len = data[7];
//                String trackData = new String(data, 10, nTrack1Len+ nTrack2Len);
//
//                // Need to calculate encrypted track length based on block size (8 for 3DES, 16 for AES)
//                int r = ((encTrackLen % encryptionBlockSize == 0) ? 0 : 1);
//                encTrackLen = ((trackLen / encryptionBlockSize) + r) * encryptionBlockSize;
//
//                byte[] encryptedBytes = Arrays.copyOfRange(data, 10 + nEncTrack1Len + nEncTrack2Len + nEncTrack3Len, data.length - 53);
//                String dataBlock = CardData.byteArrayToHexString(encryptedBytes);
//                byte[] ksnBytes = Arrays.copyOfRange(data, data.length - 13, data.length - 3);
//                String ksn = CardData.byteArrayToHexString(ksnBytes);
//                cardData = new CardData(trackData, dataBlock, ksn, encryptionParameters);
//                if (listener != null)
//                    listener.onCardSwiped(cardData);
                UMCardData cd = new UMCardData(data);
                cardData = cd.buildCardData();
                if (cardData != null && listener != null)
                    listener.onCardSwiped(cardData);
            }

            @Override
            public void onReceivedFailed(int i) {
                Log.d(TAG, "onReceivedFailed " + i);
            }

            @Override
            public void onReceivedSuccess(int i) {
                Log.d(TAG, "onReceivedSuccess  " + i);
            }
        };
    }

    /**
     * Return an array of paired Bluetooth devices
     *
     * @return Device[] of paired Bluetooth devices
     */
    public static Device[] getAvailableDevices() {
        Log.d(TAG, "getAvailableDevices");
        //devices.add(new Device("Miura M020", "5C:F3:70:7B:C4:E6"));
        return DeviceManager.getMatchingDevices("BT MAG", BTMagDeviceManager.class);
    }

}