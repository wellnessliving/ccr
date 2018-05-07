package com.directconnect.mobilesdk.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.directconnect.mobilesdk.device.Device;
import com.directconnect.mobilesdk.device.DeviceManager;
import com.directconnect.mobilesdk.device.EncryptionParameters;
import com.magtek.mobile.android.mtlib.IMTCardData;
import com.magtek.mobile.android.mtlib.MTCardDataState;
import com.magtek.mobile.android.mtlib.MTConnectionState;
import com.magtek.mobile.android.mtlib.MTConnectionType;
import com.magtek.mobile.android.mtlib.MTSCRA;
import com.magtek.mobile.android.mtlib.MTSCRAEvent;
import com.magtek.mobile.android.mtlib.MTDeviceConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.LogRecord;


public class MagtekDeviceManager extends DeviceManager {
    /**
     * Protected constructor called by derived classes
     *
     * @param device  Device object
     * @param context Android context
     */
    private static final String TAG = "MagtekDeviceManager";

    private String connectionType;
    private MTConnectionType m_connectionType;
    private android.os.Handler  m_SCRAHandler;
    private MTSCRA m_SCRA;
    private long m_ConnectStartTime=0;
    private long m_InterruptWaitTime=10000;
    private Boolean connectionRetry = false;
    private String deviceAddress;
    private  BluetoothAdapter bluetoothAdapter;
    private MTConnectionState m_connectionState = MTConnectionState.Disconnected;
    private Handler m_scraHandler = new Handler(new SCRAHandlerCallback()) {
    };
    private static final EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "MTEK", "DUKPT");
    /*
     * Flag to track when user initiates a disconnect. This is used for BLE connections only
     */
    private boolean m_DisconnectRequest = false;


    public MagtekDeviceManager(Device device, Context context, String connectionType) {
        super(device, context);
        m_SCRA = new MTSCRA(context, m_scraHandler);
        this.connectionType = connectionType;
        // m_SCRA = new MTSCRA(device);
    }

    //okay so use m_SRA.getMaskedTracked2();
    private class SCRAHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MTSCRAEvent.OnDeviceConnectionStateChanged:
                    OnDeviceStateChanged((MTConnectionState) msg.obj);
                    break;
                case MTSCRAEvent.OnCardDataStateChanged:
                    OnCardDataStateChanged((MTCardDataState) msg.obj);
                    break;
                case MTSCRAEvent.OnDataReceived:
                    OnCardDataReceived();
                    break;
                case MTSCRAEvent.OnDeviceResponse:
                    OnDeviceResponse((String) msg.obj);
                    break;
            }
            return true;
        }
    }

    protected void OnDeviceResponse(String data)
    {
        Log.d(data, "OnDeviceResponse: ");
    }
    private void OnDeviceStateChanged(MTConnectionState deviceState)
    {
        MTConnectionState tPrevState = m_connectionState;
        switch (deviceState)
        {
            case Disconnected:
                //Toast.makeText(MainActivity.this, R.string.cr_state_disconnect, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Card reader device is disconnecting...");
                if (listener != null)
                    listener.onDisconnected();
                break;
            case Connected:
               m_connectionState = MTConnectionState.Connected;
               connectionRetry = false;
                if (listener != null)
                    listener.onConnected();
                //Toast.makeText(MainActivity.this, R.string.cr_state_connected, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Card reader device is connected");
                break;
            case Error:
                break;
            case Connecting:
                Log.d(TAG, "Card reader device is connecting...");

                Log.d(m_SCRA.getDeviceName(), "OnDeviceStateChanged: ");
                Log.d(m_SCRA.getDeviceSerial(), "OnDeviceStateChanged: ");
                m_SCRA.setConnectionRetry(true);

                break;
            case Disconnecting:
                if (m_connectionType == MTConnectionType.BLEEMV)
                {
                    if(tPrevState==MTConnectionState.Connecting)
                        m_ConnectStartTime = System.currentTimeMillis();
                }
                break;
        }
    }

    private void OnCardDataStateChanged(MTCardDataState cardDataState)
    {
        switch (cardDataState) {
            case DataNotReady:
                break;
            case DataReady:
                break;
            case DataError:
                //Toast.makeText(MainActivity.this, R.string.cr_data_error, Toast.LENGTH_LONG).show();
                Log.d(TAG, "There was was an error reading the card data");
                break;
        }

    }
    public void waitOnClose()
    {
        if(m_connectionType == MTConnectionType.BLEEMV)
        {
            if (m_connectionState != MTConnectionState.Connected)
            {
                long tTime = System.currentTimeMillis();
                if(m_ConnectStartTime > 0)
                {
                    if((tTime -m_ConnectStartTime) < m_InterruptWaitTime)
                    {
                        try
                        {
                            long tDiffTime = tTime - m_ConnectStartTime;
                            Log.i(TAG, "SCRADevice closeDevice:Diff:" + tDiffTime);
                            long tWaitTime = m_InterruptWaitTime-tDiffTime;
                            Log.i(TAG, "SCRADevice closeDevice:Waiting:" + tWaitTime);
                            for(;;)
                            {
                                tTime = System.currentTimeMillis();
                                if((tTime -m_ConnectStartTime) >= m_InterruptWaitTime)
                                {
                                    break;
                                }
                                if (m_connectionState == MTConnectionState.Connected)
                                {
                                    break;
                                }
                                Thread.sleep((100));
                            }
                        }
                        catch(Exception ex)
                        {

                        }
                    }
                    m_ConnectStartTime=0;
                }
            }
        }

    }
    private void OnCardDataReceived()
    {
        String trackdata = "";
        String datablock = "";
        if(m_SCRA.getTrack1().length() > 0 || m_SCRA.getTrack2().length() > 0 || m_SCRA.getTrack3().length() > 0)
        {
            if(m_SCRA.getTrack1().length() > 0)
            {
                String trackdata2 = "";
                String c1 = "";
                for (int i = 0; i < m_SCRA.getTrack1Masked().length(); i++)
                {

                    if(i >5 && i < 14)
                    {
                        c1 = c1 + "*";
                    }
                    else {
                        c1 = c1 + m_SCRA.getTrack1Masked().charAt(i);
                    }
                    //Process char
                }

                trackdata2 += c1;
                trackdata += trackdata2;
                datablock = m_SCRA.getTrack1();
            }
            else if(m_SCRA.getTrack2().length() > 0)
            {
                String trackdata2 = "";
                String c1 = "";
                for (int i = 0; i < m_SCRA.getTrack1Masked().length(); i++)
                {

                    if(i >5 && i < 14)
                    {
                        c1 = c1 + "*";
                    }
                    else {
                        c1 = c1 + m_SCRA.getTrack1Masked().charAt(i);
                    }
                    //Process char
                }

                trackdata2 += c1;
                trackdata += trackdata;
                datablock = m_SCRA.getTrack2();
            }
            else if(m_SCRA.getTrack3().length() > 0)
            {
                String trackdata2 = "";
                String c1 = "";
                for (int i = 0; i < m_SCRA.getTrack1Masked().length(); i++)
                {

                    if(i >5 && i < 14)
                    {
                        c1 = c1 + "*";
                    }
                    else {
                        c1 = c1 + m_SCRA.getTrack1Masked().charAt(i);
                    }
                    //Process char
                }

                trackdata2 += c1;
                trackdata += m_SCRA.getTrack3Masked();
                datablock = m_SCRA.getTrack3();
            }
        }

        //clearDisplay();

        if (trackdata.length() > 0) {
            Log.d("Track data is valid", "OnCardDataReceived: ");
            this.cardData = new CardData(trackdata, datablock, m_SCRA.getKSN(), encryptionParameters);
            if (listener != null) {
                listener.onCardSwiped(this.cardData);
            }

        }
    }
    @Override
    protected void _deviceConnect() {
        Log.d(TAG, "connect");

            if(this.connectionType.compareTo("BLEEMV") == 0)

        {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            List<String> s = new ArrayList<String>();
            for(BluetoothDevice bt : pairedDevices) {
                s.add(bt.getAddress());
                if(bt.getName().startsWith("eDynamo") || bt.getName().startsWith("DynaMAX") || bt.getName().startsWith("BulleT"))
                {

                    deviceAddress = bt.getAddress();
                }

            }


        }
        if (! m_SCRA.isDeviceConnected()) {
            if (openDevice() != 0) {
                Log.d(TAG, "Failed to open card reader device");
            } else {
                Log.d(TAG, "Card reader device is now open");
            }
        }
        //mIDTechHelper.connect(device.getAddress());
    }

    @Override
    protected void _deviceDisplayMessage(String message)
    {
        // Do nothing
    }
    @Override
    protected void _deviceDisconnect()
    {
        closeDevice();

    }
    @Override
    protected void _deviceAcceptCard(String message)
    {

        Log.d(TAG, "Accepting swipe");

    }
    public long closeDevice() {
        long result = -1;
        if (m_SCRA != null)
        {
            m_DisconnectRequest =false;
            waitOnClose();
            m_SCRA.closeDevice();

            result = 0;
        }

        return result;
    }
    public long openDevice()
    {
        long result = -1;

        if (m_SCRA != null)
        {
            m_ConnectStartTime = System.currentTimeMillis();

            if(connectionType.equalsIgnoreCase("Audio"))
            {
                m_SCRA.setConnectionType(MTConnectionType.Audio);
                m_SCRA.setAddress("uDynamo or aDynamo");
                m_SCRA.openDevice();
                result = 0;
            }
            else if(connectionType.equalsIgnoreCase("BLE"))
            {
                m_SCRA.setConnectionType(MTConnectionType.BLE);
                m_SCRA.setAddress(deviceAddress);
                m_SCRA.openDevice();
                result = 0;
            }
            else if(connectionType.equalsIgnoreCase("BLEEMV"))
            {
                m_SCRA.setConnectionType(MTConnectionType.BLEEMV);
                m_SCRA.setAddress(deviceAddress);
                m_SCRA.openDevice();

                result = 0;;
            }
            else if(connectionType.equalsIgnoreCase("Bluetooth"))
            {
                m_SCRA.setConnectionType(MTConnectionType.Bluetooth);
                m_SCRA.setAddress(deviceAddress);
                m_SCRA.openDevice();
                result = 0;
            }
            else if(connectionType.equalsIgnoreCase("USB"))
            {
                m_SCRA.setConnectionType(MTConnectionType.USB);
                m_SCRA.setAddress("USB Magtek device");
                m_SCRA.openDevice();
                result = 0;
            }
            if(m_connectionType == MTConnectionType.BLEEMV)
            {
                if(m_connectionState!=MTConnectionState.Disconnected)
                {
                    Log.i(TAG, "SCRADevice openDevice:Device Not Disconnected");
                    return 0;
                }
            }

            //m_ConnectStartTime = System.currentTimeMillis();


        }

        return result;
    }
    /**
     * Return an array of paired Bluetooth devices
     *
     * @return Device[] of paired Bluetooth devices
     */

    public static Device[] getAvailableDevices() {
        Log.d(TAG, "getAvailableDevices");
        //devices.add(new Device("Miura M020", "5C:F3:70:7B:C4:E6"));
        //return DeviceManager.getMatchingDevices("uDynamo", MagtekDeviceManager.class);
        return new Device[] {
                new Device("eDynamo", MagtekDeviceManager.class, "static")
        };

    }

}
