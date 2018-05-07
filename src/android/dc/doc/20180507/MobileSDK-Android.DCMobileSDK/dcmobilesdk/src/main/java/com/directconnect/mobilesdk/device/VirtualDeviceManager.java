package com.directconnect.mobilesdk.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Virtual device manager - simulates a hardware device by interacting with the user
 *
 * Created by fbergeon on 3/31/17.
 */
public class VirtualDeviceManager extends DeviceManager {
    private AlertDialog alertDialog;
    private static Device _device = new Device("VirtualDevice", VirtualDeviceManager.class, "static");

    public VirtualDeviceManager(Device device, Context context) {
        super(device, context);
    }

    @Override
    protected void _deviceConnect() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("connect()");
        alertDialog.setMessage("Connect device");

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                if (listener != null)
                    listener.onConnected();
            }
        };
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Connect", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", clickListener);
        alertDialog.show();
    }

    @Override
    protected void _deviceDisconnect() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("disconnect()");
        alertDialog.setMessage("Disconnect device");

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                if (listener != null)
                    listener.onDisconnected();
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Disconnect", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", clickListener);
        alertDialog.show();
    }

    @Override
    protected void _deviceDisplayMessage(String message) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("displayMessage()");
        alertDialog.setMessage(message);

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", clickListener);
    }

    @Override
    protected void _deviceAcceptCard(String message) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("acceptCard()");
        alertDialog.setMessage("Select card entry");

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // Track2
                    case DialogInterface.BUTTON_POSITIVE:
//                        cardData = new CardData("4788250000028291", "1220", "John Doe");
                        cardData = new CardData(";4788250000028291=2012101?");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //<P2PE>
                        //  <HSMDevice>Thales</HSMDevice>
                        //  <TerminalType>MUIRA</TerminalType>
                        //  <EncryptionType>DUKPT</EncryptionType>
                        //  <KSN>62994900720005E00002</KSN>
                        //  <DataBlock>069B1FAB80B010D52833D6289EFC75BAF09D8178D140287A6A5BAD58B9ACFC93E6B151DC3DA375C3</DataBlock>
                        //</P2PE>

                          // PAX
//                        EncryptionParameters encryptionParameters = new EncryptionParameters("Azure", "PAX", "RSA-OAEP");
//                        cardData = new CardData(";401211******1111=0420121?", "AO/UTTMQooYtqVvD264If8Qcvs6LRtgYP0zzq/JEEaHiPXXiQg+m2eRQMoj04aK7YzMl44is+HraZLXm1T7Mv6d1/ZGnTdZL8gMa9QNL26OieHA6lTO5fhQsUkKMV3zscOIoFFOWyYhYENy04FhXSWZtxduK+vwBQL4m6KEO40cK/nq6nP9UT6aAPrU379EmQ5IZaVsK60al66qci7Cs95xwb7iKGeH/7GbE4XCdMZvq+yZHqJoMKShm9NZ7IduAXPAK1r7VaV0jWuljq14oqffySiLN0A12guGvx6hT1Jivxf88gags8QqhlndoWozrh00mZgGqI34laNRENx2Bng==",
//                                null, encryptionParameters);

//                        //Miura
//                        EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "Miura", "DUKPT");
//                        cardData = new CardData(";478825******8291=2012101*************?",
//                                "FF5291BC56571738A40399B96C1FD595491AE67D05DCE20FC9ADB7CDE43E705AD702879F1B376B0EA5162C61B99BAEB01319B66A489028EF3AF80B95BDD716D8213052A694C2762C7E739f3780E86CB23712041C5E2C5D1B789797Af1631A7BBAE853BF5F3678555",
//                                "0000020000004FA00018", encryptionParameters);

                        //BTMag
                        // Track1: "0CCB020BE2DEF7311DF583F83F53638037A92650AB587C5F111BBD92ECC3923DB125CA7925E1855B517FCDEBE10DFF105528C7D1A456FB79"
                        EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "IDT", "DUKPT");
                        cardData = new CardData("%*4788********8291^ENGLISH/VISA^2012****************?*;478825******8291=2012101*************?",
                                "46C357C13AD004D95030339061705DFD4C7FADCDAF1A2D191CB522BD49C2A6A72AE04B7B0CB42543",
                                "6299490085001B800013", encryptionParameters);

//                        //UniMag
//                        EncryptionParameters encryptionParameters = new EncryptionParameters("Thales", "IDT", "DUKPT");
//                        cardData = new CardData(";478825******8291=2012101*************?",
//                                "295CA6ACAF9CA029E3F5F8082C622E9BB82F5C304DB78B22FAF11F79E12ECDD259845F46AE5D12CC09CF79FB0C9ED9052AE4512EFAEAD4E5",
//                                "6299490085001B60000A", encryptionParameters);


                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        cardData = new CardData();
                        break;
                }
                alertDialog.dismiss();
                if (listener != null && cardData != null)
                    listener.onCardSwiped(cardData);
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Bad swipe", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "P2PE swipe", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Clear swipe", clickListener);
        alertDialog.show();
    }

    @Override
    protected void _deviceAcceptPIN(String message, String amount, CardData cardData) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("acceptPIN()");
        alertDialog.setMessage(message);

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            // Cancel or timeout -> null PINData, bypassed entry -> null dataBlock & KSN in PINData object
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        pinData = new PINData("dataBlock", "KSN");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        pinData = new PINData(null, null);
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        pinData = null;
                        break;
                }
                alertDialog.dismiss();
                if (listener != null)
                    listener.onPINEntered(pinData);
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel/Timeout", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bypass", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PIN", clickListener);
        alertDialog.show();
    }

    @Override
    protected void _deviceAskYNQuestion(String message) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("askYNQuestion()");
        alertDialog.setMessage(message);

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // Track2
                    case DialogInterface.BUTTON_POSITIVE:
                        ynResponse.set(1);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        ynResponse.set(0);
                        break;

                    default:
                        break;
                }
                alertDialog.dismiss();
                if (listener != null && cardData != null)
                    listener.onYNAnswered(ynResponse.get());
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", clickListener);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", clickListener);
        alertDialog.show();
    }

    @Override
    public void _devicePresentMenu(final String[] messages) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("presentMenu()");

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= AlertDialog.BUTTON_NEUTRAL)
                    menuSelection.set(which - (AlertDialog.BUTTON_NEUTRAL));
                alertDialog.dismiss();
                if (listener != null)
                    listener.onMenuSelected(menuSelection.get());
            }
        };

        for (int i = 0; i < messages.length; i++)
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL+i, messages[i], clickListener);
        alertDialog.show();
    }

    /**
     * Return an array of devices
     * @return Device[] of devices
     */
    public static Device[] getAvailableDevices() {
        return new Device[] {
            _device
        };
    }
}
