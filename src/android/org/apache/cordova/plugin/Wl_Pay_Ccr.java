package org.apache.cordova.plugin;

import android.content.pm.PackageManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for Android SDKs of NMI and Direct Connect.
 *
 * @see <a href="https://secure.networkmerchants.com/gw/merchants/resources/integration/integration_portal.php#mobile_methodology">NMI Integration Documentation</a>
 */
public class Wl_Pay_Ccr extends CordovaPlugin
{
  /**
   * Whether plugin was initialized successfully.
   */
  private JSONArray a_log=new JSONArray();

  /**
   * Whether plugin was initialized successfully.
   */
  private boolean is_active=false;

  /**
   * Whether a method call is being processed now.
   *
   * <tt>true</tt> if
   * {@link Wl_Pay_Ccr#execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext)}
   * is being executed now.
   *
   * <tt>false</tt> if a background process is being executed now.
   */
  private boolean is_method=false;

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

    if(this.o_processor!=null)
    {
      JSONObject a_result_permission=new JSONObject();

      String[] a_permission=this.o_processor.permissionList();
      for (String s_permission : a_permission)
        a_result_permission.put(s_permission,cordova.hasPermission(s_permission));
      a_result.put("a_permission",a_result_permission);
    }

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
      this.logError("[Wl_Pay_Ccr.doPermissionRequest] Permissions may not be requested when plugin is not initialized.");

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
    this.logInfo("Require permissions: "+a_permission.length);
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
    if(this.is_active)
    {
      this.logError("[Wl_Pay_Ccr.doStartup] It is not allowed to initialize plugin when it is initialized already.");

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Plugin is initialized already.");
      a_result.put("s_error","already");

      callbackContext.error(a_result);
      return;
    }

    int id_pay_processor=a_config.getInt("id_pay_processor");
    Wl_Pay_Ccr_Abstract o_processor=Wl_Pay_Ccr_Abstract.create(id_pay_processor,this);
    if(o_processor==null)
    {
      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("id_pay_processor",id_pay_processor);
      a_result.put("s_message","Interface for this payment processor is not implemented.");
      a_result.put("s_error","not-implemented");

      callbackContext.error(a_result);
      return;
    }

    this.is_active=true;
    this.o_context_event=callbackContext;
    this.o_processor=o_processor;

    o_processor.startup();

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
      this.logError("[Wl_Pay_Ccr.doTearDown] It is not allowed to tear down plugin that is not initialized.");

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Not initialized.");

      callbackContext.error(a_result);
      return;
    }

    this.o_processor.tearDown();

    // **** BE ATTENTIVE ***
    // All tear down actions should be performed before the code the follows.
    // The following code sends final event.
    // No events can be sent after that.
    JSONObject a_result=new JSONObject();
    a_result.put("event","tearDown");

    PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_result);
    o_result.setKeepCallback(false);

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
    this.is_method=true;

    try
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

      this.logError("[Wl_Pay_Ccr.execute] Method not found: "+action);

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Method not found.");
      callbackContext.error(a_result);
      return true;
    }
    finally
    {
      this.is_method=false;
    }
  }

  /**
   * Fires an event.
   *
   * @param a_event Event data to send.
   */
  private void fire(String s_event,JSONObject a_event) throws JSONException
  {
    if(this.o_context_event==null)
      return;

    a_event.put("event",s_event);

    PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_event);
    o_result.setKeepCallback(true);
    this.o_context_event.sendPluginResult(o_result);
  }

  /**
   * Fires log event.
   */
  private void fireLog() throws JSONException
  {
    if(this.a_log.length()==0)
      return;

    JSONObject a_event=new JSONObject();
    a_event.put("a_log",this.logResult());

    this.fire("log",a_event);
  }

  /**
   * Writes a message to debug logInfo.
   *
   * @param s_message Message to write to logInfo.
   */
  private void logError(String s_message) throws JSONException
  {
    JSONObject a_item=new JSONObject();
    a_item.put("is_error",true);
    a_item.put("s_message",s_message);
    this.logPut(a_item);
  }

  /**
   * Writes a message to debug logInfo.
   *
   * @param s_message Message to write to logInfo.
   */
  void logInfo(String s_message) throws JSONException
  {
    JSONObject a_item=new JSONObject();
    a_item.put("is_error",false);
    a_item.put("s_message",s_message);
    this.logPut(a_item);
  }

  /**
   * Puts a message to the log.
   *
   * @param o_message Message to put to log.
   */
  private void logPut(JSONObject o_message) throws JSONException
  {
    this.a_log.put(o_message);
    if(!this.is_method)
      this.fireLog();
  }

  /**
   * Prepares logs for returning in result.
   *
   * @return Logs prepared for returning in result.
   */
  private JSONArray logResult()
  {
    JSONArray a_log=this.a_log;
    this.a_log=new JSONArray();
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

    boolean has_permissions=this.permissionHas();

    if(has_permissions)
    {
      for(int r:grantResults)
      {
        if(r == PackageManager.PERMISSION_DENIED)
        {
          has_permissions=false;
          break;
        }
      }
    }

    a_result.put("has_permissions",has_permissions);

    if(has_permissions)
    {
      a_result.put("s_message","Permissions allowed.");
      this.o_context_permission.success(a_result);
    }
    else
    {
      a_result.put("s_message","Permission denied.");
      this.o_context_permission.error(a_result);
    }

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
