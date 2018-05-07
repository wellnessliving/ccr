package com.directconnect.mobilesdk.device;

import android.content.Context;

import com.idtechproducts.device.Common;
import com.idtechproducts.device.IDTEMVData;
import com.idtechproducts.device.IDTMSRData;
import com.idtechproducts.device.IDT_Augusta;
import com.idtechproducts.device.IDT_Device;
import com.idtechproducts.device.OnReceiverListener;
import com.idtechproducts.device.StructConfigParameters;

/**
 * Created by fbergeon on 3/16/17.
 */

public class AugustaDeviceManager extends DeviceManager {
    private static EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "IDT", "DUKPT");
    IDT_Augusta augusta;


    public AugustaDeviceManager(Device device, Context context) {
        super(device, context);
        augusta = new IDT_Augusta(getReceiverListener(), context);
    }


    @Override
    protected void _deviceConnect() {
        bConnected.set(false);
        if (augusta != null) {
            augusta.registerListen();
            bConnected.set(augusta.device_isConnected());
            if (bConnected.get())
                getReceiverListener().deviceConnected();
        }
    }

    @Override
    protected void _deviceDisplayMessage(String message) {
        // Do nothing
    }

    @Override
    protected void _deviceAcceptCard(String message) {
        if (augusta != null)
            augusta.msr_startMSRSwipe();
    }

    private OnReceiverListener getReceiverListener() {
        return new OnReceiverListener() {
            @Override
            public void ICCNotifyInfo(byte[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void LoadXMLConfigFailureInfo(int arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void autoConfigCompleted(StructConfigParameters arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void autoConfigProgress(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void dataInOutMonitor(byte[] data, boolean isIncoming) {
                // TODO Auto-generated method stub
            }

            @Override
            public void deviceConnected() {
                bConnected.set(true);
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void deviceDisconnected() {
                bConnected.set(false);
                if (augusta != null)
                    augusta.unregisterListen();
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void emvTransactionData(IDTEMVData arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void lcdDisplay(int arg0, String[] arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void msgAudioVolumeAjustFailed() {
                // TODO Auto-generated method stub
            }

            @Override
            public void msgBatteryLow() {
                // TODO Auto-generated method stub
            }

            @Override
            public void msgRKICompleted(String arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void msgToConnectDevice() {
                // TODO Auto-generated method stub
            }

            @Override
            /**
             * IDRMSRData:
             * cardData = 0x383239315e454e474c4953482f564953415e32303132313031353433323131323334353630313f3b343738383235303030303032383239313d32303132313031353433323131323334353630313f0d559903
             * 02 67 00 06 00 00 00 35 27 00 00 00 5d 25 42 34 37 38 38 32 35 30 30 30 30 30 32 38 32 39 31 5e 45 4e 47 4c 49 53 48 2f 56 49 53 41 5e 32 30 31 32 31 30 31 35 34 33 32 31 31 32 33 34 35 36 30 31 3f 3b 34 37 38 38 32 35 30 30 30 30 30 32 38 32 39 31 3d 32 30 31 32 31 30 31 35 34 33 32 31 31 32 33 34 35 36 30 31 3f 0d 55 99 03
             *
             * KSN = null
             * encTrack1 = encTrack2 = null
             * captureEncryptType = CAPTURE_ENCRYPT_TYPE_TDES
             * captureEncodeStatus = CAPTURE_ENCODE_TYPE_ISOABA
             */
            public void swipeMSRData(IDTMSRData arg0) {
                if (arg0 == null)
                    cardData = new CardData();
                else if (arg0.track2 != null && arg0.track2.length() > 0)
                    cardData = new com.directconnect.mobilesdk.device.CardData(arg0.track2);
                else if (arg0.track1 != null && arg0.track1.length() > 0)
                    cardData = new com.directconnect.mobilesdk.device.CardData(arg0.track1);
                else if (arg0.cardData != null && arg0.cardData.length > 0) {
                    String datablock = Common.base16Encode(arg0.cardData);
                    String ksn = null;
                    if (arg0.KSN != null)
                        ksn = Common.base16Encode(arg0.KSN);
                    cardData = new com.directconnect.mobilesdk.device.CardData(arg0.track2, datablock, ksn, encryptionParameters);
                }
            }

            @Override
            public void timeout(int arg0) {
                // TODO Auto-generated method stub
            }
        };
    }

    /**
     * Return an array of devices
     *
     * @return Device[] of devices
     */
    public static Device[] getAvailableDevices() {
        IDT_Device idtDevice = IDT_Augusta.getIDT_Device();
        if (idtDevice == null)
            return null;

        StringBuilder sn = new StringBuilder();
        idtDevice.config_getSerialNumber(sn);
        return new Device[]{
                new Device("IDT Device", AugustaDeviceManager.class, sn.toString())
        };
    }

}