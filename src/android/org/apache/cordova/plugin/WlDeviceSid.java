package org.apache.cordova.plugin;

import com.SafeWebServices.PaymentGateway.PGSwipeDevice;

/**
 * A list of devices supported by credit card reader plugin.
 */
class WlDeviceSid
{
  /**
   * Payment processor: NMI.
   *
   * Device: Enterprise.
   */
  private static final int NMI_ENTERPRISE=3;

  /**
   * Payment processor: NMI.
   *
   * Device: IPS.
   */
  private static final int NMI_IPS=2;

  /**
   * Payment processor: NMI.
   *
   * Device: Unimag.
   */
  private static final int NMI_UNIMAG=1;

  /**
   * Converts device ID to NMI device ID.
   *
   * @param id_device Device ID.
   * @return NMI device ID.
   */
  static PGSwipeDevice.SwipeDevice idNmi(int id_device)
  {
    switch(id_device)
    {
      case NMI_ENTERPRISE:
        return PGSwipeDevice.SwipeDevice.ENTERPRISE;
      case NMI_IPS:
        return PGSwipeDevice.SwipeDevice.IPS;
      case NMI_UNIMAG:
        return PGSwipeDevice.SwipeDevice.UNIMAG;
      default:
        throw new IllegalArgumentException("[WlDeviceSid.idNmi] Device ID is not registered.");
    }
  }

  /**
   * Converts device ID to processor ID.
   *
   * @param id_device Device ID.
   * @return Processor ID.
   */
  static int idProcessor(int id_device)
  {
    switch(id_device)
    {
      case NMI_ENTERPRISE:
      case NMI_IPS:
      case NMI_UNIMAG:
        return PayProcessorSid.NMI;
      default:
        throw new IllegalArgumentException("[WlDeviceSid.idProcessor] Device ID is not registered.");
    }
  }
}
