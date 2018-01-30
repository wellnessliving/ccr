package org.apache.cordova.plugin;

/**
 * Abstract class for all connector.
 */
abstract public class Wl_Pay_Ccr_Abstract
{
  /**
   * Create interface object for specified processor.
   *
   * @param id_pay_processor ID of a processor for which interface object should be created.
   * @return Interface object for specified processor.
   */
  public static Wl_Pay_Ccr_Abstract create(int id_pay_processor)
  {
    switch (id_pay_processor)
    {
      case PayProcessorSid.DIRECT_CONNECT:
        return new Wl_Pay_Ccr_DirectConnect();
      case PayProcessorSid.NMI:
        return new Wl_Pay_Ccr_Nmi();
    }
    return null;
  }

  /**
   * Returns a list of permissions that are required by plugin.
   *
   * @return A list of permissions that are required by plugin.
   */
  abstract protected String[] permissionList();
}
