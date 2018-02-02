package org.apache.cordova.plugin;

import org.json.JSONException;

/**
 * Abstract class for all connector.
 */
abstract public class Wl_Pay_Ccr_Abstract
{
  private Wl_Pay_Ccr o_controller=null;

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
      case PayProcessorSid.DIRECT_CONNECT:
        o_processor=new Wl_Pay_Ccr_DirectConnect();
      case PayProcessorSid.NMI:
        o_processor=new Wl_Pay_Ccr_Nmi();
    }

    if(o_processor!=null)
      o_processor.o_controller=o_controller;

    return o_processor;
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
  abstract public void startup();

  /**
   * Tears plugin down.
   */
  abstract public void tearDown();
}
