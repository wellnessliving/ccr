package org.apache.cordova.plugin;

import android.Manifest;
import com.SafeWebServices.PaymentGateway.PGSwipeController;
import com.SafeWebServices.PaymentGateway.PGSwipeController.SwipeListener;
import com.SafeWebServices.PaymentGateway.PGSwipeDevice;
import com.SafeWebServices.PaymentGateway.PGSwipedCard;

import org.apache.cordova.R;
import org.json.JSONException;

/**
 * Connector for mobile SDK of NMI.
 */
public class Wl_Pay_Ccr_Nmi extends Wl_Pay_Ccr_Abstract implements SwipeListener
{
  private PGSwipeController swipeController;


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
    this.swipeController = new PGSwipeController(this, PGSwipeDevice.SwipeDevice.ENTERPRISE);
    //this.swipeController = new PGSwipeController(this, PGSwipeDevice.SwipeDevice.IPS);

    /*
     * The default values can be changed here.
     *
     * For example:
     * this.swipeController.getDevice().setSwipeTimeout(30);
     * this.swipeController.getDevice().setAlwaysAcceptSwipe(false);
     * this.swipeController.getDevice().setActivateReaderOnConnect(false);
     */

    this.swipeController.getDevice().requestSwipe();
  }

  @Override
  public void tearDown()
  {
    this.swipeController.getDevice().stopSwipeController();
  }
}
