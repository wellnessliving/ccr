package org.apache.cordova.plugin;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract class for all connector.
 */
abstract public class Wl_Pay_Ccr_Abstract
{
  private Wl_Pay_Ccr o_controller=null;

  /**
   * Returns plugin controller object.
   */
  Wl_Pay_Ccr controller()
  {
    return this.o_controller;
  }

  /**
   * Create interface object for specified processor.
   *
   * @param id_pay_processor ID of a processor for which interface object should be created.
   * @return Interface object for specified processor.
   */
  public static Wl_Pay_Ccr_Abstract create(int id_pay_processor, Wl_Pay_Ccr o_controller)
  {
    Wl_Pay_Ccr_Abstract o_processor=null;
    switch (id_pay_processor)
    {
      case Wl_ProcessorSid.DIRECT_CONNECT:
        o_processor=new Wl_Pay_Ccr_DirectConnect();
        break;
      case Wl_ProcessorSid.NMI:
        o_processor=new Wl_Pay_Ccr_Nmi();
    }

    if(o_processor!=null)
      o_processor.o_controller=o_controller;

    return o_processor;
  }

  /**
   * Returns debugging information.
   *
   * @return Debugging information.
   */
  abstract JSONObject debugGet() throws JSONException;

  /**
   * Returns application context.
   *
   * @return Application context.
   */
  Context getApplicationContext()
  {
    return this.o_controller.cordova.getActivity().getApplicationContext();
  }

  /**
   * Writes a message to debug logInfo.
   *
   * @param s_message Message to write to logInfo.
   */
  void logInfo(String s_message) throws JSONException
  {
    this.o_controller.logInfo(s_message);
  }

  /**
   * Returns a list of permissions that are required by plugin.
   *
   * @return A list of permissions that are required by plugin.
   */
  abstract protected String[] permissionList();

  /**
   * Initializes plugin.
   */
  abstract public void startup() throws JSONException;

  /**
   * Tears plugin down.
   */
  abstract public void tearDown();

  /**
   * Fires a swipe event with specified card data. Used for testing purposes.
   */
  abstract public void testSwipe(JSONObject a_card) throws JSONException;
}
