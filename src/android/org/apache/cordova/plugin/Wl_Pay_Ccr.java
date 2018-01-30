package org.apache.cordova.plugin;

import android.content.pm.PackageManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Interface for Android SDKs of NMI and Direct Connect.
 *
 * @see https://secure.networkmerchants.com/gw/merchants/resources/integration/integration_portal.php#mobile_methodology
 */
public class Wl_Pay_Ccr extends CordovaPlugin
{
  /**
   * Whether plugin was initialized successfully.
   */
  private ArrayList<String> a_log=new ArrayList<String>();

  /**
   * Whether plugin was initialized successfully.
   */
  private boolean is_active=false;

  /**
   * Callback context for sending events.
   */
  private CallbackContext o_context_event=null;

  /**
   * Callback context for permission handling.
   */
  private CallbackContext o_context_permission=null;

  /**
   * Payment processor object.
   */
  private Wl_Pay_Ccr_Abstract o_processor=null;

  /**
   * Returns debug information.
   *
   * @param callbackContext Callback context.
   */
  private void doDebugInfo(CallbackContext callbackContext) throws JSONException
  {
    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("is_active",this.is_active);

    JSONObject a_result_permission=new JSONObject();

    String[] a_permission=this.o_processor.permissionList();
    for (String s_permission : a_permission)
      a_result_permission.put(s_permission,cordova.hasPermission(s_permission));
    a_result.put("a_permission",a_result_permission);
    callbackContext.success(a_result);
  }

  /**
   * Requests permissions that are not granted.
   *
   * @param callbackContext Callback context.
   */
  private void doPermissionRequest(CallbackContext callbackContext) throws JSONException
  {
    // Plugin is deactivated.
    if(!this.is_active)
    {
      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Plugin is not activated.");

      callbackContext.error(a_result);
      return;
    }

    // See
    // https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html#android-permissions
    this.o_context_permission=callbackContext;
    String[] a_permission=this.o_processor.permissionList();
    this.log("Require permissions: "+a_permission.length);
    cordova.requestPermissions(this, 1, a_permission);
  }

  /**
   * Initializes plugin.
   *
   * @param a_config Configuration array.
   * @param callbackContext Callback context.
   */
  private void doStartup(JSONObject a_config, CallbackContext callbackContext) throws JSONException
  {
    int id_pay_processor=a_config.getInt("id_pay_processor");
    Wl_Pay_Ccr_Abstract o_processor=Wl_Pay_Ccr_Abstract.create(id_pay_processor);
    if(o_processor==null)
    {
      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("id_pay_processor",id_pay_processor);
      a_result.put("s_message","Interface for this payment processor is not implemented.");

      callbackContext.error(a_result);
      return;
    }

    this.is_active=true;
    this.o_context_event=callbackContext;
    this.o_processor=o_processor;

    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("has_permissions",this.permissionHas());

    PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_result);
    o_result.setKeepCallback(true);

    callbackContext.sendPluginResult(o_result);
  }

  /**
   * Stops plugin.
   *
   * @param callbackContext Callback context.
   */
  private void doTearDown(CallbackContext callbackContext) throws JSONException
  {
    if(!this.is_active)
    {
      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Not initialized.");

      callbackContext.error(a_result);
      return;
    }

    JSONObject a_result=new JSONObject();
    a_result.put("event","tearDown");

    PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_result);
    o_result.setKeepCallback(true);

    this.o_context_event.sendPluginResult(o_result);

    a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("s_message","Complete.");
    callbackContext.success(a_result);

    this.is_active=false;
    this.o_context_event=null;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
  {
    if(action.equals("debugInfo"))
    {
      this.doDebugInfo(callbackContext);
      return true;
    }
    else if(action.equals("permissionRequest"))
    {
      this.doPermissionRequest(callbackContext);
      return true;
    }
    else if(action.equals("startup"))
    {
      this.doStartup(args.getJSONObject(0), callbackContext);
      return true;
    }
    else if(action.equals("tearDown"))
    {
      this.doTearDown(callbackContext);
      return true;
    }
    return false;
  }

  /**
   * Writes a message to debug log.
   *
   * @param s_message Message to write to log.
   */
  private void log(String s_message)
  {
    this.a_log.add(s_message);
  }

  /**
   * Prepares logs for returning in result.
   *
   * @return Logs prepared for returning in result.
   */
  private JSONArray logResult()
  {
    JSONArray a_log=new JSONArray();
    for (String s_log : this.a_log)
      a_log.put(s_log);
    this.a_log=new ArrayList<String>();

    return a_log;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
    // Plugin was deactivated during request of permissions.
    if(!this.is_active||this.o_context_permission==null)
    {
      this.o_context_permission=null;
      return;
    }

    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("requestCode",requestCode);

    JSONArray a_result_permission=new JSONArray();
    for (String s_permission : permissions)
      a_result_permission.put(s_permission);
    a_result.put("a_permission",a_result_permission);
    a_result.put("a_permission.length",permissions.length);

    JSONArray a_result_grant=new JSONArray();
    for (int i_result : grantResults)
      a_result_grant.put(i_result);
    a_result.put("a_grant",a_result_grant);
    a_result.put("a_grant.length",grantResults.length);

    for(int r:grantResults)
    {
      if(r == PackageManager.PERMISSION_DENIED)
      {
        a_result.put("s_message","Permission denied.");
        this.o_context_permission.error(a_result);
        this.o_context_permission=null;
        return;
      }
    }
    a_result.put("s_message","Permissions allowed.");
    this.o_context_permission.success(a_result);
    this.o_context_permission=null;
  }

  /**
   * Verifies if all permissions are granted.
   *
   * @return Whether all permissions are granted.
   */
  private boolean permissionHas()
  {
    String[] a_permission=this.o_processor.permissionList();
    for (String s_permission : a_permission)
    {
      if(!cordova.hasPermission(s_permission))
        return false;
    }
    return true;
  }
}
