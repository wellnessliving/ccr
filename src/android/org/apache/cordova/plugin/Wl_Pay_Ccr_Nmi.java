package org.apache.cordova.plugin;

import android.Manifest;

/**
 * Connector for mobile SDK of NMI.
 */
public class Wl_Pay_Ccr_Nmi extends Wl_Pay_Ccr_Abstract
{
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
}
