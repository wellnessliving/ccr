package org.apache.cordova.plugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
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
   * Plugin configuration object.
   */
  private JSONObject a_config=null;

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
   *
   * This field may be set to <tt>false</tt> in <tt>do*</tt> methods if result is already sent, but
   * there are other calls that may invoke exception. Doing so leads to that exception is sent as
   * a log event, and not as a erroneous result which can not be returned (due to that result is
   * already sent).
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
   * Processes exception.
   *
   * Serializes exception object, and sends it to application in the browser.
   *
   * @param callbackContext Callback context.
   * @param e Exception object.
   */
  void _exception(CallbackContext callbackContext,Exception e)
  {
    try
    {
      JSONObject a_result=new JSONObject();
      if(this.is_method)
        a_result.put("a_log",this.logResult());

      if(e instanceof Wl_UserException)
      {
        a_result.put("s_message",((Wl_UserException) e).messageGet());
        a_result.put("s_error",((Wl_UserException) e).errorGet());
      }
      else
      {
        a_result.put("s_class",e.getClass());
        a_result.put("s_error","internal");
        a_result.put("s_message",e.getMessage());
        a_result.put("s_message_local",e.getLocalizedMessage());

        StackTraceElement[] a_stack=e.getStackTrace();
        StringBuilder s_stack= new StringBuilder();
        for(StackTraceElement a_element:a_stack)
        {
          if(a_element.getClassName().equals(CordovaPlugin.class.getName()))
            break;
          if(s_stack.length()>0)
            s_stack.append(" - ");
          s_stack.append(a_element.getClassName()).append(':').append(a_element.getLineNumber());
        }

        a_result.put("s_stack",s_stack);
      }


      if(this.is_method&&callbackContext!=null)
        callbackContext.error(a_result);
      else
        this.logError(a_result);
    }
    catch (JSONException ignored)
    {
    }
  }

  /**
   * Returns configuration array.
   *
   * @return Configuration array.
   */
  JSONObject config()
  {
    return this.a_config;
  }

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
      a_result.put("a_processor",this.o_processor.debugGet());
    }

    a_result.put("dc",Wl_Pay_Ccr_DirectConnect.debugGlobal());

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

    if(this.permissionHas())
    {
      this.logError("[Wl_Pay_Ccr.doPermissionRequest] It is not allowed to require permissions if they are already granted.");

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Permissions are already granted.");

      callbackContext.error(a_result);
      return;
    }

    // See
    // https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html#android-permissions
    this.o_context_permission=callbackContext;
    cordova.requestPermissions(this, 1, this.o_processor.permissionList());
  }

  /**
   * Initializes plugin.
   *
   * @param a_config Configuration array.
   * @param callbackContext Callback context.
   */
  private void doStartup(JSONObject a_config, CallbackContext callbackContext) throws Exception
  {
    if(this.is_active)
    {
      this.logInfo("[Wl_Pay_Ccr.doStartup] Plugin is marked as active. Deactivating before activation.");
      this.tearDown();
    }

    // a_config is used in DirectConnect version of create().
    this.a_config=a_config;

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

    AudioManager manager = (AudioManager)this.cordova.getActivity().getSystemService(Context.AUDIO_SERVICE);

    manager.setStreamVolume(AudioManager.STREAM_MUSIC,100,AudioManager.FLAG_SHOW_UI);

    this.is_active=true;
    this.o_context_event=callbackContext;
    this.o_processor=o_processor;

    boolean has_permissions=this.permissionHas();

    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("has_permissions",has_permissions);

    PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_result);
    o_result.setKeepCallback(true);

    callbackContext.sendPluginResult(o_result);

    // startup() may issue events.
    // These events should be issued AFTER plugin initialization completes at JavaScript side.
    // To complete that initialization, result should be sent prior sending of any events.
    if(has_permissions)
    {
      this.is_method=false;

      try
      {
        o_processor.startup();
      }
      catch (Exception e)
      {
        // tearDown() can not be called because we need to return this exception into the browser.
        // This can only be done with a log event.
        // tearDown() deactivates sending of events.
        this.is_active=false;
        throw e;
      }
    }
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

    this.tearDown();

    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("s_message","Complete.");
    callbackContext.success(a_result);
  }

  /**
   * Fires a swipe event with specified card data. Used for testing purposes.
   *
   * @param a_card Card data.
   * @param callbackContext Callback context.
   */
  private void doTestSwipe(JSONObject a_card, CallbackContext callbackContext) throws JSONException
  {
    if(!this.is_active)
    {
      this.logError("[Wl_Pay_Ccr.doTestSwipe] Swipe event can not be fired when plugin is inactive.");

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Swipe event can not be fired when plugin is inactive.");
      a_result.put("s_error","inactive");

      callbackContext.error(a_result);
      return;
    }

    this.o_processor.testSwipe(a_card);

    JSONObject a_result=new JSONObject();
    a_result.put("a_log",this.logResult());
    a_result.put("s_message","Swipe even is fired.");

    callbackContext.success(a_result);
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
      else if(action.equals("testException"))
      {
        Wl_DeviceSid.testException();
        return false; // Exception is thrown above. Never returns.
      }
      else if(action.equals("testSwipe"))
      {
        this.doTestSwipe(args.getJSONObject(0),callbackContext);
        return true;
      }

      this.logError("[Wl_Pay_Ccr.execute] Method not found: "+action);

      JSONObject a_result=new JSONObject();
      a_result.put("a_log",this.logResult());
      a_result.put("s_message","Method not found.");
      callbackContext.error(a_result);
      return true;
    }
    catch (Exception e)
    {
      this._exception(callbackContext,e);
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
    a_event.put("a_log",this.logResult());

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

    this.fire("log",new JSONObject());
  }

  /**
   * Fires card swipe event.
   *
   * @param a_card Card data.
   */
  void fireSwipe(JSONObject a_card) throws JSONException
  {
    this.fire("swipe",a_card);
  }

  /**
   * Fires card swipe event.
   */
  void fireSwipeError() throws JSONException
  {
    this.fire("swipeError",new JSONObject());
  }

  /**
   * Writes an error message to debug logInfo.
   *
   * @param a_error Error object.
   */
  private void logError(JSONObject a_error) throws JSONException
  {
    a_error.put("is_error",true);
    this.logPut(a_error);
  }

  /**
   * Writes an error message to debug logInfo.
   *
   * @param s_message Error message to write to log.
   */
  void logError(String s_message) throws JSONException
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
    if(!this.is_method&&this.o_context_event!=null)
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
    try
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

        // First send reply that permissions are granted.
        // Only after that - events that can be fired by o_processor.startup()
        this.o_processor.startup();
      }
      else
      {
        a_result.put("s_message","Permission denied.");
        this.o_context_permission.error(a_result);
      }
    }
    catch (Exception e)
    {
      this._exception(this.o_context_permission,e);
    }
    finally
    {
      this.o_context_permission=null;
    }
  }

  /**
   * Verifies if all permissions are granted.
   *
   * @return Whether all permissions are granted.
   */
  private boolean permissionHas() throws JSONException
  {
    String[] a_permission=this.o_processor.permissionList();
    for (String s_permission : a_permission)
    {
      if(!cordova.hasPermission(s_permission))
        return false;
    }
    return true;
  }

  /**
   * Stops plugin.
   */
  private void tearDown() throws JSONException
  {
    if(this.o_processor!=null)
    {
      this.o_processor.tearDown();
      this.o_processor=null;
    }

    if(o_context_event!=null)
    {
      // **** BE ATTENTIVE ***
      // All tear down actions should be performed before the code the follows.
      // The following code sends final event.
      // No events can be sent after that.
      JSONObject a_result=new JSONObject();
      a_result.put("event","tearDown");

      PluginResult o_result = new PluginResult(PluginResult.Status.OK, a_result);
      o_result.setKeepCallback(false);

      this.o_context_event.sendPluginResult(o_result);
      this.o_context_event=null;
    }

    this.is_active=false;
  }
}
