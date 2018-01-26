package org.apache.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for Android SDKs of NMI and Direct Connect.
 */
public class Wl_Pay_Ccr extends CordovaPlugin
{
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
  {
    if(action.equals("mul"))
    {
      String message = args.getString(0);
      this.mul(message, callbackContext);
      return true;
    }
    return false;
  }

  private void mul(String message, CallbackContext callbackContext)
  {
    if(message!=null&&message.length()>0)
    {
      if(message.charAt(0)=='0')
        callbackContext.error(message+"-mul-error");
      else
        callbackContext.success(message+"-mul-ok");
    }
    else
    {
      callbackContext.error("Expected one non-empty string argument.");
    }
  }
}
