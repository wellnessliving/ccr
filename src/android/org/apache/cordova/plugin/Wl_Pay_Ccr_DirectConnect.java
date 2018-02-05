package org.apache.cordova.plugin;

import android.Manifest;

import com.directconnect.mobilesdk.device.Device;
import com.directconnect.mobilesdk.device.DeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connector for mobile SDK of NMI.
 */
public class Wl_Pay_Ccr_DirectConnect extends Wl_Pay_Ccr_Abstract
{
  @Override
  JSONObject debugGet() throws JSONException
  {
    JSONObject a_debug=new JSONObject();

    a_debug.put("s_class","Wl_Pay_Ccr_DirectConnect");

    return a_debug;
  }

  @Override
  protected String[] permissionList()
  {
    // See
    // https://secure.networkmerchants.com/gw/merchants/resources/integration/integration_portal.php#android_swipe
    // for a list of required permissions.

    return new String[]{
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.INTERNET
    };
  }

  @Override
  public void startup() throws JSONException
  {
//    JSONObject a_config=this.controller().config().getJSONObject("a_processor");
//
//    int id_device=a_config.getInt("id_device");
//    Device[] devices;
//    DeviceManager deviceManager;
//
//    switch (id_device)
//    {
//      case Wl_DeviceSid.
//    }
//
//
//    Device[] devices = DeviceManagerXYZ.getAvailableDevices();
//    DeviceManagerXYZ deviceManager = new DeviceManagerXYZ(devices[0], this.getApplicationContext());
//    if (deviceManager.connect())
//    {
//      deviceManager.acceptCard("Swipe Card");
//      CardData cardData = deviceManager.acceptCard();
//      String pan = cardData.getPAN();
//      String expDate = cardData.getExpDate());
//    }
//
//    this.swipeController = new PGSwipeController(this,this.getApplicationContext(), deviceType);
//
//    // Sets the time between requestSwipe and when swipes will no longer be accepted.
//    // Default and maximum are 20 seconds. The minimum is 3 seconds.
//    // This still applies even if alwaysAcceptSwipe is true, but the swipe request will be
//    // automatically renewed in that case.
//    //
//    // The swipeTimeout only matters if you have enabled always accept swipes.
//    //
//    // this.swipeController.getDevice().setSwipeTimeout(30);
//
//    // The Shuttle does not accept swipes from the user unless a swipe has been requested. If
//    // alwaysAcceptSwipe is true, the SDK will immediately request a swipe and renew the request any
//    // time the old swipe request times out or ends. You will still receive periodic
//    // didBecomeUnreadyForSwipe: messages, but the reason will be
//    // SwipeReasonUnreadyForSwipeRefreshing to indicate that you should be receiving a
//    // didBecomeReadyForSwipe: message immediately after without any interaction.
//    //
//    // The mobile device's battery may deplete faster if the swipe reader is always awaiting a
//    // swipe. If battery life is a concern, consider setting this to false and using requestSwipe
//    // when a swipe is expected, or only setting alwaysAcceptSwipe to true when a swipe is expected.
//    //
//    // If alwaysAcceptSwipe is true, you should not use requestSwipe or cancelSwipeRequest.
//    // By default, alwaysAcceptSwipe is true.
//    this.swipeController.getDevice().setAlwaysAcceptSwipe(false);
//
//    // If this is true, the SDK will attempt to power-up the reader when attachment is detected.
//    // There are 3 things to be aware of:
//    // 1. If the user attaches headphones to the mobile device, it will be treated as a swipe reader
//    //    and an attempt to power it up will be made.
//    // 2. Before the attempt to activate the reader, if
//    //    messageOptions.activateReaderWithoutPromptingUser
//    //    is set to false (it is false by default), the user will receive a prompt asking to confirm
//    //    activation. If they decline, no activation will be attempted.
//    // 3. If you call powerDown to deactivate the device, leaving activateReaderOnAttach set to true
//    //    will cause the device to immediately power back up.
//    this.swipeController.getDevice().setActivateReaderOnConnect(true);
//
//    this.swipeController.getDevice().requestSwipe();
  }

  @Override
  public void tearDown()
  {

  }

  @Override
  public void testSwipe(JSONObject a_card) throws JSONException
  {

  }
}
