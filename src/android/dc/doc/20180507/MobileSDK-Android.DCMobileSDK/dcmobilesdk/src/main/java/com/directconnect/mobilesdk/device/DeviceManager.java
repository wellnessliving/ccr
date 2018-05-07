package com.directconnect.mobilesdk.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DeviceManager - base class to interface with interaction device
 *
 * Created by Francois Bergeon on 3/10/17.
 */
public abstract class DeviceManager {
    private static final String TAG = "DeviceManager";
    private DeviceManager.Listener synchronousListener = getSynchronousListener();

    // Internal variables
    protected DeviceManager.Listener listener;
    protected Device device;
    protected Context context;

    // State variables
    protected volatile AtomicBoolean bConnected = new AtomicBoolean(false);
    protected volatile AtomicBoolean bCardInserted = new AtomicBoolean(false);

    // Result variables
    protected volatile AtomicInteger ynResponse = new AtomicInteger(-1);
    protected volatile AtomicInteger menuSelection = new AtomicInteger(-1);
    protected volatile CardData cardData = null;
    protected volatile PINData pinData = null;

    // Multithreading variables
    protected Lock l = new ReentrantLock();
    protected Condition change = l.newCondition();

    /**
     * Protected constructor called by derived classes
     *
     * @param device Device object
     * @param context Android context
     */
    protected DeviceManager(Device device, Context context) {
        this.device = device;
        this.context = context;
    }

    /**
     * Listener - event listener subclass
     *
     * Created by Francois Bergeon on 3/10/17.
     */
    public interface Listener {
        void onConnected();
        void onDisconnected();
        void onCardSwiped(CardData cardData);
        void onCardInserted(CardData cardData);
        void onCardRemoved();
        void onPINEntered(PINData pinData);
        void onYNAnswered(int response); // 1 -> yes, 0 -> no, -1 -> cancel
        void onMenuSelected(int selection);
 //       public abstract void onUserInput(String input);
    }

    /**
     * Synchronously connect to device
     *
     * @return true if onConnected, false otherwise
     */
    public boolean connect() {
        Log.d(TAG, "connect");
        connect(synchronousListener);
        return bConnected.get();
    }

    /**
     * Asynchronously connect to device
     *
     * @param listener DeviceManager.Listener delegate
     */
    public void connect(DeviceManager.Listener listener) {
        Log.d(TAG, "connect(listener)");
        l.lock();
        bConnected.set(false);
        this.listener = listener;

        _deviceConnect();

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
    }

    /**
     * Disconnect from device
     */
    public void disconnect() {
        Log.d(TAG, "disconnect");
        l.lock();

        _deviceDisconnect();

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
        listener = null;
    }

    /**
     * Display message
     * @param message Message to be displayed
     */
    public void displayMessage(String message) {
        Log.d(TAG, "displayMessage " + message);
        // Nothing fancy here
        _deviceDisplayMessage(message);
    }

    /**
     * Display messages
     * @param messages Messages to be displayed
     */
    public void displayMessages(String[] messages) {
        Log.d(TAG, "displayMessage " + messages);
        // Nothing fancy here
        _deviceDisplayMessages(messages);
    }

    /**
     * Accept card swipe or insert
     * @param message
     * @return CardData object or null if asynchronous
     */
    public CardData acceptCard(String message) {
        Log.d(TAG, "acceptCard " + message);
        l.lock();
        cardData = null;

        _deviceAcceptCard(message);

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
        return cardData;
    }

    /**
     * Accept PIN
     *
     * @param message Message to be displayed
     * @param amount Transaction amount
     * @param cardData CardData object containing track data
     * @return PINData object or null if asynchronous
     */
    public PINData acceptPIN(String message, String amount, CardData cardData) {
        Log.d(TAG, "acceptPIN " + message + " " + amount + " " + cardData);
        pinData = null;
        l.lock();

        _deviceAcceptPIN(message, amount, cardData);

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
        return pinData;
    }

    /**
     * Ask Y/N Question
     *
     * @param message Message to be displayed
     * @return -1 if cancel or asynchronous, 0 if no, 1 if yes
     */
    public int askYNQuestion(String message) {
        Log.d(TAG, "askYNQuestion " + message);
        l.lock();
        ynResponse.set(-1);

        _deviceAskYNQuestion(message);

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
        return ynResponse.get();
    }

    /**
     * Present menu
     *
     * @param messages Messages to be displayed
     * @return -1 if cancel or asynchronous, menu selection otherwise
     */
    public int presentMenu(String[] messages) {
        Log.d(TAG, "presentMenu " + messages);
        l.lock();
        menuSelection.set(-1);

        _devicePresentMenu(messages);

        if (listener == synchronousListener)
            await();
        else
            l.unlock();
        return menuSelection.get();
    }



    // Methods to be overridden
    // Default methods are either do nothing or unsupported until overriden
    protected void _deviceConnect() {
        // Do nothing
        bConnected.set(true);
    }

    protected void _deviceDisconnect() {
        // Do nothing
        bConnected.set(false);
    }

    protected void _deviceDisplayMessage(String message) {
        throw new UnsupportedOperationException();
    }

    protected void _deviceAcceptCard(String message) {
        throw new UnsupportedOperationException();
    }

    protected void _deviceAcceptPIN(String message, String amount, CardData cardData) {
        throw new UnsupportedOperationException();
    }

    protected void _deviceAskYNQuestion(String message) {
        throw new UnsupportedOperationException();
    }

    protected void _devicePresentMenu(String[] messages) {
        throw new UnsupportedOperationException();
    }

    /**
     * Display messages - default implementation - maybe overridden if necessary
     * @param messages Messages to be displayed
     */
    protected void _deviceDisplayMessages(String[] messages) {
        StringBuilder sw = new StringBuilder();
        for (String msg : messages) {
            if (sw.length() > 0)
                sw.append('\n');
            sw.append(msg);
        }
        _deviceDisplayMessage(sw.toString());
    }

    /**
     * Return connection state
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() { return bConnected.get(); }

    /**
     * Return card present state
     *
     * @return true if inserted, false otherwise
     */
    public boolean isCardInserted() { return bCardInserted.get(); }


    /**
     * Get private listener delegate used by synchronous methods
     *
     * @return listener delegate singleton (non static)
     */
    protected DeviceManager.Listener getSynchronousListener() {
        return new DeviceManager.Listener() {
            @Override
            public void onConnected() { signal(); }

            @Override
            public void onCardSwiped(CardData cD) {
                cardData = cD;
                signal();
            }

            @Override
            public void onCardInserted(CardData cD) {
                cardData = cD;
                signal();
            }

            @Override
            public void onCardRemoved() {}
            @Override
            public void onPINEntered(PINData pinData) {}

            @Override
            public void onYNAnswered(int response) {
                ynResponse.set(response);
                signal();
            }

            @Override
            public void onMenuSelected(int selection) {}
            @Override
            public void onDisconnected() {}
        };
    }

    /**
     * Await for thread to be signalled
     */
    protected void await() {
        l.lock();
        try {
            change.await();
        } catch (InterruptedException e) {
        } finally {
            l.unlock();
        }
    }

    /**
     * Signall waiting thread(s)
     */
    protected void signal() {
        l.lock();
        try {
            change.signalAll();
        } catch (Exception e) {
        } finally {
            l.unlock();
        }
    }

    /**
     * Convert String amount to integer in cents
     *
     * @param amount String amount
     * @return amount in cents as integer
     */
    protected static int stringAmountToInt(String amount) {
        return (int)(Double.valueOf(amount) * 100);
    }

    /**
     * Return an array of available devices matching the specified device manager types
     *
     * @param types Array of DeviceManager derived class types
     * @return Device[] of paired available devices
     */
     public static Device[] getAvailableDevices(Class[] types) {
        Device[] devices = new Device[0];
        for (Class type : types) {
            Device[] typeDevices = null;
            try {
                typeDevices = (Device[])type.getDeclaredMethod("getAvailableDevices").invoke(null);
            } catch (Exception e) {
                continue;
            }
            if (typeDevices != null)
                devices = concatenate(devices, typeDevices);
        }
        return devices;
    }

    /**
     * Instantiate derived DeviceManager based on device's type
     *
     * @param device Device object
     * @param context Android context
     * @return DeviceManager object
     * @throws InstantiationException
     */
    public static DeviceManager createInstance(Device device, Context context) throws InstantiationException {
        DeviceManager deviceManager = null;
        try {
            Class[] cArg = new Class[2];
            cArg[0] = Device.class;
            cArg[1] = Context.class;
            return (DeviceManager) device.getType().getDeclaredConstructor(cArg).newInstance(device, context);
        } catch (Exception e) {
            throw new InstantiationException();
        }
    }

    /**
     * Return an array of paired Bluetooth devices matching the specified name
     * @return Device[] of paired Bluetooth devices
     */
    protected static Device[] getMatchingDevices(String match, Class type) {
        Log.d(TAG, "getMatchingDevices " + match);
        List<Device> devices = new ArrayList<>();
        //devices.add(new Device("Miura M020", "5C:F3:70:7B:C4:E6"));
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices) {
            String deviceName = bt.getName();
            if (deviceName.contains(match)) {
                Device device = new Device(deviceName, type, bt.getAddress());
                devices.add(device);
            }
        }

        Device[] deviceArray = new Device[devices.size()];
        return devices.toArray(deviceArray);
    }

   static  <T> T[] concatenate (T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
