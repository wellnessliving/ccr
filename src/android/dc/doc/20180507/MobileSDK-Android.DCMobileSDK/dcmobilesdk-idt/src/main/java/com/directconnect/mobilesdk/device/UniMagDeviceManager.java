package com.directconnect.mobilesdk.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;


/**
 * Created by fbergeon on 5/29/17.
 */
public class UniMagDeviceManager extends DeviceManager {
    private static final String TAG = "UniMagDeviceManager";
    private static final String PREFS_NAME = "DCMobileSDKIDTUniMagPrefs";

    private volatile StructConfigParameters configParams;
    private uniMagReader uniReader = null;
    private SharedPreferences settings;

    public UniMagDeviceManager(Device device, Context context) {
        super(device, context);
        Log.d(TAG, "constructor");
        uniReader = new uniMagReader(getReceiverListener(), context); //, uniMagReader.ReaderType.SHUTTLE);
        uniReader.setVerboseLoggingEnable(true);
        try {
            uniReader.unregisterListen();
            uniReader.registerListen();
        } catch (Exception e) {
        }
    }

    @Override
    protected void _deviceConnect() {
        bConnected.set(false);
        // Run in a separate thread because autoconfig will query network
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "loadConfigParams");
                configParams = loadConfigParams();
                if (configParams == null) {
                    // Try autoconfig
                    Log.d(TAG, "startAutoConfig");
                    String filename = getXMLFileFromRaw(); // May be null
                    boolean b = uniReader.startAutoConfig(filename, true);
                    if (!b) {
                        Log.e(TAG, "startAutoConfig failed");
                        signal();
                    }
                } else {
                    Log.d(TAG, "connectWithProfile");
                    uniReader.connectWithProfile(configParams);
                }
            }
        }).start();
    }

    @Override
    protected void _deviceDisconnect() {
        Log.d(TAG, "disconnect");
        uniReader.disconnect();
    }

    @Override
    protected void _deviceDisplayMessage(String message) {
        // Do nothing
    }

    @Override
    protected void _deviceAcceptCard(String message) {
        cardData = null;
        if (bConnected.get()) {
            Log.d(TAG, "startSwipeCard");
            uniReader.startSwipeCard();
        }
    }

    private uniMagReaderMsg getReceiverListener() {
        return new uniMagReaderMsg() {
            private final String TAG = "uniMagReaderMsg";

            @Override
            public void onReceiveMsgToConnect() {
                Log.d(TAG, "onReceiveMsgToConnect");
            }

            @Override
            public void onReceiveMsgConnected() {
                Log.d(TAG, "onReceiveMsgConnected");
                bConnected.set(true);
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void onReceiveMsgDisconnected() {
                Log.d(TAG, "onReceiveMsgDisconnected");
                bConnected.set(false);
                uniReader.stopSwipeCard();
                uniReader.stopAutoConfig();
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void onReceiveMsgTimeout(String s) {
                Log.d(TAG, "onReceiveMsgTimeout " + s);
                if (s.startsWith("Auto Config failed")) {
                    // AutoConfig failed
                    listener.onDisconnected();
                } else {
                    // Try again
                    _deviceAcceptCard(null);
                }
            }

            @Override
            public void onReceiveMsgToSwipeCard() {
                Log.d(TAG, "onReceiveMsgToSwipeCard");
            }

            @Override
            public void onReceiveMsgCommandResult(int i, byte[] bytes) {
                Log.d(TAG, "onReceiveMsgCommandResult " + i + " bytes:" + bytes.length);
            }

            @Override
            public void onReceiveMsgCardData(byte b, byte[] bytes) {
                Log.d(TAG, "onReceiveMsgCardData " + b + " bytes:" + bytes.length);
                UMCardData cd = new UMCardData(bytes);
                cardData = cd.buildCardData();
                if (cardData != null && listener != null)
                    listener.onCardSwiped(cardData);
            }

            @Override
            public void onReceiveMsgProcessingCardData() {
                Log.d(TAG, "onReceiveMsgProcessingCardData");
            }

            @Override
            public void onReceiveMsgToCalibrateReader() {
                Log.d(TAG, "onReceiveMsgToCalibrateReader");
            }

            @Override
            public void onReceiveMsgSDCardDFailed(String s) {
                Log.d(TAG, "onReceiveMsgSDCardDFailed " + s);
            }

            @Override
            public void onReceiveMsgFailureInfo(int i, String s) {
                Log.d(TAG, "onReceiveMsgFailureInfo " + i + ":" + s);
            }

            @Override
            public void onReceiveMsgAutoConfigProgress(int i) {
                Log.d(TAG, "onReceiveMsgAutoConfigProgress" + i);
            }

            @Override
            public void onReceiveMsgAutoConfigProgress(int i, double v, String s) {
                Log.d(TAG, "onReceiveMsgAutoConfigProgress" + i + ":" + s);
            }

            @Override
            public void onReceiveMsgAutoConfigCompleted(StructConfigParameters structConfigParameters) {
                Log.d(TAG, "onReceiveMsgAutoConfigCompleted");
                configParams = structConfigParameters;
                saveConfigParams(configParams);
                // <outDir=1,psamp=48000,baud=9600,rsamp=48000,vr=0>
                Log.d(TAG, "connectWithProfile");
                uniReader.connectWithProfile(configParams);
            }

            @Override
            public boolean getUserGrant(int i, String s) {
                Log.d(TAG, "getUserGrant " + i + ":" + s);
                // Allow everything
                return true;
            }
        };
    }

    private void saveConfigParams(StructConfigParameters configParams) {
        Log.d(TAG, "saveConfigParams");
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("_High", configParams.get__High());
        editor.putInt("_Low", configParams.get__Low());
        editor.putInt("High", configParams.get_High());
        editor.putInt("Low", configParams.get_Low());
        editor.putInt("BaudRate", configParams.getBaudRate());
        editor.putFloat("Device_Apm_Base", (float)configParams.getdevice_Apm_Base());
        editor.putInt("DirectionOutputWave", configParams.getDirectionOutputWave());
        editor.putInt("ForceHeadsetPlug", configParams.getForceHeadsetPlug());
        editor.putInt("FrequenceInput", configParams.getFrequenceInput());
        editor.putInt("FrequenceOutput", configParams.getFrequenceOutput());
        editor.putInt("highThreshold", configParams.gethighThreshold());
        editor.putInt("lowThreshold", configParams.getlowThreshold());
        editor.putInt("Max", configParams.getMax());
        editor.putInt("Min", configParams.getMin());
        editor.putInt("PowerupLastBeforeCMD", configParams.getPowerupLastBeforeCMD());
        editor.putInt("PowerupWhenSwipe", configParams.getPowerupWhenSwipe());
        editor.putInt("PreAmbleFactor", configParams.getPreAmbleFactor());
        editor.putInt("RecordBufferSize", configParams.getRecordBufferSize());
        editor.putInt("RecordReadBufferSize", configParams.getRecordReadBufferSize());
        editor.putInt("ShuttleChannel", configParams.getShuttleChannel());
        editor.putInt("UseVoiceRecognition", configParams.getUseVoiceRecognition());
        editor.putInt("VolueLevelAdjust", configParams.getVolumeLevelAdjust());
        editor.putInt("WaveDirection", configParams.getWaveDirection());
        editor.putBoolean("_valid", true);
        editor.commit();
    }

    private StructConfigParameters loadConfigParams() {
        Log.d(TAG, "loadConfigParams");
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean("_valid", false))
            return null;
        StructConfigParameters configParams = new StructConfigParameters();
        configParams.set__High((short)settings.getInt("_High", 0));
        configParams.set__Low((short)settings.getInt("_Low", 0));
        configParams.set_High((short)settings.getInt("High", 0));
        configParams.set_Low((short)settings.getInt("Low", 0));
        configParams.setBaudRate(settings.getInt("BaudRate", 0));
        configParams.setdevice_Apm_Base((double)settings.getFloat("Device_Apm_Base", 0));
        configParams.setDirectionOutputWave((short)settings.getInt("DirectionOutputWave", 0));
        configParams.setForceHeadsetPlug((short)settings.getInt("ForceHeadsetPlug", 0));
        configParams.setFrequenceInput(settings.getInt("FrequenceInput", 0));
        configParams.setFrequenceOutput(settings.getInt("FrequenceOutput", 0));
        configParams.sethighThreshold((short)settings.getInt("highThreshold", 0));
        configParams.setlowThreshold( (short)settings.getInt("lowThreshold", 0));
        configParams.setMax((short)settings.getInt("Max", 0));
        configParams.setMin((short)settings.getInt("Min", 0));
        configParams.setPowerupLastBeforeCMD((short)settings.getInt("PowerupLastBeforeCMD", 0));
        configParams.setPowerupWhenSwipe((short)settings.getInt("PowerupWhenSwipe", 0));
        configParams.setPreAmbleFactor((short)settings.getInt("PreAmbleFactor", 0));
        configParams.setRecordBufferSize(settings.getInt("RecordBufferSize", 0));
        configParams.setRecordReadBufferSize(settings.getInt("RecordReadBufferSize", 0));
        configParams.setShuttleChannel((byte)settings.getInt("ShuttleChannel", 0));
        configParams.setUseVoiceRecognition((short)settings.getInt("UseVoiceRecognition", 0));
        configParams.setVolumeLevelAdjust((short)settings.getInt("VolueLevelAdjust", 0));
        configParams.setWaveDirection(settings.getInt("WaveDirection", 0));
        return configParams;
    }

    /**
     * Return an array of devices
     * @return Device[] of devices
     */
    public static Device[] getAvailableDevices() {
        Log.d(TAG, "getAvailableDevices");
        return new Device[] {
                new Device("IDT UniMag Device", UniMagDeviceManager.class, "static")
        };
    }


    // Copy config file from resource
    private String getXMLFileFromRaw(){
        //the target filename in the application path
        Log.d(TAG, "Start getXMLFileFromRaw");
        String fileName = "idt_unimagcfg_default.xml";

        try{
            InputStream in = context.getResources().openRawResource(R.raw.idt_unimagcfg_default);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();

            // to refer to the application path
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists())
                file.delete();

            FileOutputStream fout = new FileOutputStream(file);
            fout.write(buffer);
            fout.close();

            // Check file was properly created
            if (!file.exists())
                fileName = null;
            else
                fileName = file.getAbsolutePath();

        } catch(Exception e){
            e.printStackTrace();
            fileName = null;
        }

        Log.d(TAG, "getXMLFileFromRaw successful file " + fileName);
        return fileName;
    }

}