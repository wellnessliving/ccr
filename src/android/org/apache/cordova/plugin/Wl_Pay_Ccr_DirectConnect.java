package org.apache.cordova.plugin;

import android.Manifest;

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
  public void startup()
  {

  }

  @Override
  public void tearDown()
  {

  }
}
