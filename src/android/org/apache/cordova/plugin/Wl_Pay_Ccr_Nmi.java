package org.apache.cordova.plugin;

import android.Manifest;
import com.SafeWebServices.PaymentGateway.PGSwipeController;
import com.SafeWebServices.PaymentGateway.PGSwipeController.SwipeListener;
import com.SafeWebServices.PaymentGateway.PGSwipeDevice;
import com.SafeWebServices.PaymentGateway.PGSwipedCard;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connector for mobile SDK of NMI.
 */
public class Wl_Pay_Ccr_Nmi extends Wl_Pay_Ccr_Abstract implements SwipeListener
{
  /**
   * Swipe controller object.
   */
  private PGSwipeController swipeController=null;

  @Override
  JSONObject debugGet() throws JSONException
  {
    JSONObject a_debug=new JSONObject();

    a_debug.put("s_class","Wl_Pay_Ccr_Nmi");

    if(this.swipeController==null)
    {
      a_debug.put("s_message","swipeController is null.");
    }
    else
    {
      PGSwipeDevice o_device=this.swipeController.getDevice();

      a_debug.put("getDefaultMsg",o_device.getDefaultMsg());
      a_debug.put("getDeviceType",o_device.getDeviceType());
      a_debug.put("getIsActivated",o_device.getIsActivated());
      a_debug.put("getIsConnected",o_device.getIsConnected());
      a_debug.put("getIsReadyForSwipe",o_device.getIsReadyForSwipe());
    }

    return a_debug;
  }

  @Override
  public void onSwipedCard(PGSwipedCard pgSwipedCard, PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onSwipedCard]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceReadyForSwipe(PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceReadyForSwipe]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceUnreadyForSwipe(PGSwipeDevice pgSwipeDevice, PGSwipeDevice.ReasonUnreadyForSwipe reasonUnreadyForSwipe)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceUnreadyForSwipe]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceConnected(PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceConnected]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceDisconnected(PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceDisconnected]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceActivationFinished(PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceActivationFinished]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDeviceDeactivated(PGSwipeDevice pgSwipeDevice)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_Nmi.onDeviceDeactivated]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  protected String[] permissionList()
  {
    // See
    // https://secure.networkmerchants.com/gw/merchants/resources/integration/integration_portal.php#android_swipe
    // for a list of required permissions.

    return new String[]{
      Manifest.permission.RECORD_AUDIO,

      // This permission can not be granted.
      //Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,

      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.INTERNET
    };
  }

  @Override
  public void startup()
  {
    /*
     * Create swipe controller for device to be used.
     * The device type being used should be un-commented here.
     */
    //this.swipeController = new PGSwipeController(this, PGSwipeDevice.SwipeDevice.UNIMAG);
    this.swipeController = new PGSwipeController(this,this.getApplicationContext(), PGSwipeDevice.SwipeDevice.ENTERPRISE);
    //this.swipeController = new PGSwipeController(this, PGSwipeDevice.SwipeDevice.IPS);

    // Sets the time between requestSwipe and when swipes will no longer be accepted.
    // Default and maximum are 20 seconds. The minimum is 3 seconds.
    // This still applies even if alwaysAcceptSwipe is true, but the swipe request will be
    // automatically renewed in that case.
    //
    // The swipeTimeout only matters if you have enabled always accept swipes.
    //
    // this.swipeController.getDevice().setSwipeTimeout(30);

    // The Shuttle does not accept swipes from the user unless a swipe has been requested. If
    // alwaysAcceptSwipe is true, the SDK will immediately request a swipe and renew the request any
    // time the old swipe request times out or ends. You will still receive periodic
    // didBecomeUnreadyForSwipe: messages, but the reason will be
    // SwipeReasonUnreadyForSwipeRefreshing to indicate that you should be receiving a
    // didBecomeReadyForSwipe: message immediately after without any interaction.
    //
    // The mobile device's battery may deplete faster if the swipe reader is always awaiting a
    // swipe. If battery life is a concern, consider setting this to false and using requestSwipe
    // when a swipe is expected, or only setting alwaysAcceptSwipe to true when a swipe is expected.
    //
    // If alwaysAcceptSwipe is true, you should not use requestSwipe or cancelSwipeRequest.
    // By default, alwaysAcceptSwipe is true.
    this.swipeController.getDevice().setAlwaysAcceptSwipe(false);

    // If this is true, the SDK will attempt to power-up the reader when attachment is detected.
    // There are 3 things to be aware of:
    // 1. If the user attaches headphones to the mobile device, it will be treated as a swipe reader
    //    and an attempt to power it up will be made.
    // 2. Before the attempt to activate the reader, if
    //    messageOptions.activateReaderWithoutPromptingUser
    //    is set to false (it is false by default), the user will receive a prompt asking to confirm
    //    activation. If they decline, no activation will be attempted.
    // 3. If you call powerDown to deactivate the device, leaving activateReaderOnAttach set to true
    //    will cause the device to immediately power back up.
    this.swipeController.getDevice().setActivateReaderOnConnect(true);

    this.swipeController.getDevice().requestSwipe();
  }

  @Override
  public void tearDown()
  {
    this.swipeController.getDevice().stopSwipeController();
    this.swipeController=null;
  }
}
