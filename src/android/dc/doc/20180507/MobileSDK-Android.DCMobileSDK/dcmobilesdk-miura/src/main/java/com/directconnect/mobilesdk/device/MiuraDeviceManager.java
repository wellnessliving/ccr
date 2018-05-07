package com.directconnect.mobilesdk.device;

import android.content.Context;
import android.util.Log;

import com.miurasystems.miuralibrary.BaseBluetooth;
import com.miurasystems.miuralibrary.BluetoothService;
import com.miurasystems.miuralibrary.MPIConnectionDelegate;
import com.miurasystems.miuralibrary.api.executor.MiuraManager;
import com.miurasystems.miuralibrary.api.listener.ApiOnlinePinListener;
import com.miurasystems.miuralibrary.api.listener.MiuraDefaultListener;
import com.miurasystems.miuralibrary.enums.BacklightSettings;
import com.miurasystems.miuralibrary.enums.BatteryData;
import com.miurasystems.miuralibrary.enums.DeviceStatus;
import com.miurasystems.miuralibrary.enums.M012Printer;
import com.miurasystems.miuralibrary.enums.OnlinePINError;
import com.miurasystems.miuralibrary.enums.StatusSettings;
import com.miurasystems.miuralibrary.tlv.CardStatus;
import com.miurasystems.miuralibrary.tlv.HexUtil;
import com.miurasystems.miuralibrary.tlv.Track2Data;

/**
 * Miura device manager - extends generic DeviceManager
 *
 * Created by fbergeon on 3/10/17.
 */
public class MiuraDeviceManager extends DeviceManager {
    private static final String TAG = "MiuraDeviceManager";
    // Static encryption parameters specific to the Miura devices
    private static final EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "PAX", "DUKPT");

    private BluetoothService bluetoothService = BluetoothService.getInstance();
    private BaseBluetooth baseBluetooth = BaseBluetooth.getInstance();
    private MPIConnectionDelegate connectionDelegate = getConnectionDelegate();


    // Constructor
    public MiuraDeviceManager(Device device, Context context) {
        super(device, context);
    }


    /**
      * Connect to Miura Bluetooth device
      *
      */
    @Override
    protected void _deviceConnect() {
        // Open session with the Miura device and wait for an onConnected event
        Log.d(TAG, "openSession");
        baseBluetooth.openSession(bluetoothService, device.getAddress(), connectionDelegate, null);
    }

    /**
     * Disconnect from Miura Bluetooth device
     */
    @Override
    protected void _deviceDisconnect() {
        Log.d(TAG, "closeSession");
        bluetoothService.closeSession();
    }

    /**
     * Display message
     * @param message Message to be displayed
     */
    @Override
    public void _deviceDisplayMessage(String message) {
        Log.d(TAG, String.format("displayText(%s)", message));
        MiuraManager.getInstance().displayText(message, null);
    }

    /**
     * Accept card asynchronously
     *
     * @param message Message to be displayed
     */
    @Override
    protected void _deviceAcceptCard(String message) {
        Log.d(TAG, "cardStatus");
        MiuraManager.getInstance().cardStatus(true, null);
        displayMessage(message);
    }

    /**
     * Accept PIN
     *
     * @param message Message to be displayed
     */
    @Override
    protected void _deviceAcceptPIN(String message, String amount, CardData cardData) {
        if (cardData == null)
            return;

        com.miurasystems.miuralibrary.tlv.CardData miuraCardData = (com.miurasystems.miuralibrary.tlv.CardData)cardData.source;
        if (miuraCardData == null)
            throw new IllegalArgumentException();

        int nAmount = stringAmountToInt(amount);
        Log.d(TAG, "onlinePin");
        MiuraManager.getInstance().onlinePin(nAmount, 840, miuraCardData.getTrack2Data(), message, getApiOnlinePinListener());
    }

    @Override
    protected void _deviceAskYNQuestion(String message) {
        MiuraManager.getInstance().displayText(String.format("%s\nYes/No", message), getMiuraDefaultListener());
        Log.d(TAG, "keyboardStatus");
        MiuraManager.getInstance().keyboardStatus(StatusSettings.Enable, BacklightSettings.Enable, getMiuraDefaultListener());
    }


    /**
     * Get Miura connection delegate
     *
     * @return connection delegate singleton (not static)
     */
    private MPIConnectionDelegate getConnectionDelegate() {
        return new MPIConnectionDelegate() {
            private final String TAG = "MPIConnectionDelegate";

            /**
             * Connected event: change state of bConencted and signal waiting thread(s)
             */
            @Override
            public void connected() {
                Log.d(TAG, "connected");
                bConnected.set(true);
                MiuraManager.getInstance().setConnectionDelegate(connectionDelegate);
                MiuraManager.getInstance().setDeviceType(MiuraManager.DeviceType.PED);
                if (listener != null)
                    listener.onConnected();
            }

            /**
             * Disonnected event: change state of bConencted and signal waiting thread(s)
             */
            @Override
            public void disconnected() {
                Log.d(TAG, "disconnected");
                bConnected.set(false);
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void connectionState(boolean flg) {
                Log.d(TAG, "connectionState " + flg);
            }

            @Override
            public void onKeyPressed(int keyCode) {
                Log.d(TAG, "onKeyPressed " + keyCode);
                if (listener != null) {
                    switch (keyCode) {
                        // Yes
                        case 0x0d:
                            listener.onYNAnswered(1);
                            break;
                        // No
                        case 0x1b:
                            listener.onYNAnswered(0);
                            break;
                        // Cancel
                        case 0x7f:
                            listener.onYNAnswered(-1);
                            break;
                    }
                }
            }

            /**
             * Card status change event: convert Miura CardData object to DeviceManager.CardData object and signal waiting thread(s)
             */
            @Override
            public void onCardStatusChange(com.miurasystems.miuralibrary.tlv.CardData miuraCardData) {
                Log.d(TAG, "onCardStatusChange " + miuraCardData);
                if (miuraCardData != null) {
                    // Get status
                    CardStatus cardStatus = miuraCardData.getCardStatus();

                    // Update card inserted flag
                    bCardInserted.set(cardStatus.isCardPresent());

                    // Parse Miura CardData object
                    CardData cardData = makeCardData(miuraCardData);

                    // Fire corresponding event if appropriate
                    if (cardData != null && listener != null) {
                        if (cardStatus.isMSRDataAvailable())
                            listener.onCardSwiped(cardData);
                        else if (cardStatus.isEMVCompatible())
                            listener.onCardInserted(cardData);
                    }
                }
            }

            @Override
            public void onDeviceStatusChange(DeviceStatus deviceStatus, String statusText) {
                Log.d(TAG, "onDeviceStatusChange " + deviceStatus.name() + ", msg " + statusText);
            }

            @Override
            public void onBatteryStatusChange(BatteryData batteryData) {
                Log.d(TAG, "onBatteryStatusChange " + batteryData.name());
            }

            @Override
            public void onBarcodeScan(String scanned) {
                Log.d(TAG, "onBarcodeScanned " + scanned);
            }

            public void onPrintSledStatus(M012Printer m012PrinterStatus) {
                Log.d(TAG, "onPrintSledStatus " + m012PrinterStatus);
            }
        };
    }

    /**
     * Get Miura ApiOnlinePinListener delegate
     *
     * @return connection delegate singleton (not static)
     */
    private ApiOnlinePinListener getApiOnlinePinListener() {
        return new ApiOnlinePinListener() {
            private final String TAG = "ApiOnlinePinListener";

            @Override
            public void onCancelOrTimeout() {
                Log.d(TAG, "onCancelOrTimeout");
                if (listener != null)
                    listener.onPINEntered(null);
            }


            @Override
            public void onBypassedPINEntry() {
                Log.d(TAG, "onBypassedPINEntry");
                if (listener != null)
                    listener.onPINEntered(new PINData(null, null));
            }

            @Override
            public void onOnlinePIN(byte[] encryptedOnlinePIN, byte[] onlinePINKeySerialNumber) {
                Log.d(TAG, "onOnlinePIN");
                String dataBlock = HexUtil.bytesToHexStrings(encryptedOnlinePIN);
                String KSN = HexUtil.bytesToHexStrings(onlinePINKeySerialNumber);
                PINData pinData = new PINData(dataBlock, KSN);
                if (listener != null)
                    listener.onPINEntered(pinData);
            }

            @Override
            public void onError(OnlinePINError error) {
                Log.d(TAG, "OnlinePINError " + error);
                if (OnlinePINError.NO_PIN_KEY == error) {
                    // TODO
                } else if (OnlinePINError.INVALID_PARAM == error) {
                    // TODO
                }
            }
        };
    }

    /**
     * Get Miura default listener
     */
    private MiuraDefaultListener getMiuraDefaultListener() {
        return new MiuraDefaultListener() {
            private final String TAG = "MiuraDefaultListener";

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError");
            }
        };
    }

    /**
     * Convert Miura CardData object to DeviceManager.CardData object
     * @param miuraCardData
     * @return DeviceManager.CardData
     */
    private static CardData makeCardData(com.miurasystems.miuralibrary.tlv.CardData miuraCardData) {
        CardData cardData = null;
        if (miuraCardData.getCardStatus().isMSRDataAvailable()) {
            Track2Data track2Data = miuraCardData.getTrack2Data();
            if (track2Data != null) {
                String trackData = new String(track2Data.getRaw());
                cardData = new CardData(trackData, miuraCardData.getSredData().toUpperCase(), miuraCardData.getSredKSN().toUpperCase(), encryptionParameters);
                cardData.source = miuraCardData;
            }
        } else if (miuraCardData.getCardStatus().isEMVCompatible()) {
            // Extract tlv tags
        }
        return cardData;
    }

    /**
     * Return an array of paired Bluetooth devices
     * @return Device[] of paired Bluetooth devices
     */
    public static Device[] getAvailableDevices() {
        Log.d(TAG, "getAvailableDevices");
        //devices.add(new Device("Miura M020", "5C:F3:70:7B:C4:E6"));
        return DeviceManager.getMatchingDevices("Miura", MiuraDeviceManager.class);
    }
}