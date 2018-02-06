package org.apache.cordova.plugin;

import android.Manifest;
import android.content.Context;

import com.directconnect.mobilesdk.device.AugustaDeviceManager;
import com.directconnect.mobilesdk.device.BTMagDeviceManager;
import com.directconnect.mobilesdk.device.CardData;
import com.directconnect.mobilesdk.device.Device;
import com.directconnect.mobilesdk.device.DeviceManager;
import com.directconnect.mobilesdk.device.PINData;
import com.directconnect.mobilesdk.device.UniMagDeviceManager;
import com.directconnect.mobilesdk.device.UniPayDeviceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connector for mobile SDK of NMI.
 */
public class Wl_Pay_Ccr_DirectConnect extends Wl_Pay_Ccr_Abstract implements DeviceManager.Listener
{
  /**
   * A list of devices.
   *
   * Result of <tt>XXXDeviceManager.getAvailableDevices();</tt>.
   */
  private Device[] devices;

  /**
   * Device manager object.
   */
  private DeviceManager deviceManager;

  /**
   * ID of device that is currently loaded.
   */
  private int id_device;

  private CardData o_card_last=null;

  /**
   * Creates a new Direct Connect interface object.
   *
   * @return Direct Connect interface object. <tt>null</tt> if interface object can not be created.
   */
  public static Wl_Pay_Ccr_DirectConnect create(Wl_Pay_Ccr o_controller) throws JSONException
  {
    o_controller.logInfo("Config: "+o_controller.config().toString());
    JSONObject a_config=o_controller.config().getJSONObject("a_processor");

    int id_device=a_config.getInt("id_device");
    Device[] devices;
    DeviceManager deviceManager;
    Context o_context=o_controller.cordova.getActivity().getApplicationContext();

    switch (id_device)
    {
      case Wl_DeviceSid.DC_IDT_AUGUSTA:
        devices = AugustaDeviceManager.getAvailableDevices();
        if(devices.length==0)
          return null;
        deviceManager = new AugustaDeviceManager(devices[0], o_context);
        break;
      case Wl_DeviceSid.DC_IDT_BT_MAG:
        devices = BTMagDeviceManager.getAvailableDevices();
        if(devices.length==0)
          return null;
        deviceManager = new BTMagDeviceManager(devices[0], o_context);
        break;
      case Wl_DeviceSid.DC_IDT_UNI_MAG:
        devices = UniMagDeviceManager.getAvailableDevices();
        if(devices.length==0)
          return null;
        deviceManager = new UniMagDeviceManager(devices[0], o_context);
        break;
      case Wl_DeviceSid.DC_IDT_UNI_PAY:
        devices = UniPayDeviceManager.getAvailableDevices();
        if(devices.length==0)
          return null;
        deviceManager = new UniPayDeviceManager(devices[0], o_context);
        break;
      default:
        return null;
    }

    Wl_Pay_Ccr_DirectConnect o_result=new Wl_Pay_Ccr_DirectConnect();
    o_result.devices=devices;
    o_result.deviceManager=deviceManager;
    o_result.id_device=id_device;
    return o_result;
  }

  @Override
  JSONObject debugGet() throws JSONException
  {
    JSONObject a_debug=new JSONObject();

    JSONArray a_device=new JSONArray();
    for (Device device : this.devices)
    {
      JSONObject a_device_item=new JSONObject();
      a_device_item.put("address",device.getAddress());
      a_device_item.put("name",device.getName());
      a_device_item.put("type",device.getType());
      a_device_item.put("class",device.getClass().getName());
      a_device.put(a_device_item);
    }

    a_debug.put("devices",a_device);
    a_debug.put("devices.length",this.devices==null?"[null]":this.devices.length);
    a_debug.put("deviceManager.class",this.deviceManager==null?"[null]":this.deviceManager.getClass().getSimpleName());
    a_debug.put("id_device",this.id_device);
    a_debug.put("this.class",this.getClass().getSimpleName());

    if(this.deviceManager!=null)
    {
      a_debug.put("deviceManager.isCardInserted",this.deviceManager.isCardInserted());
      a_debug.put("deviceManager.isConnected",this.deviceManager.isConnected());
    }

    if(this.o_card_last==null)
      a_debug.put("card","[null]");
    else
    {
      JSONObject a_card=new JSONObject();
      a_card.put("getCardholderName",this.o_card_last.getCardholderName());
      a_card.put("getDataBlock",this.o_card_last.getDataBlock());
      a_card.put("getDataType",this.o_card_last.getDataType().toString());
      a_card.put("getExpDate",this.o_card_last.getExpDate());
      a_card.put("getKSN",this.o_card_last.getKSN());
      a_card.put("getPAN",this.o_card_last.getPAN());
      a_card.put("getServiceCode",this.o_card_last.getServiceCode());
      a_card.put("getTrack1",this.o_card_last.getTrack1());
      a_card.put("getTrack2",this.o_card_last.getTrack2());
      a_card.put("getTrack3",this.o_card_last.getTrack3());

      a_debug.put("card",a_card);
    }

    return a_debug;
  }

  @Override
  public void onConnected()
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onConnected]");
      this.deviceManager.acceptCard("Swipe Card");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onDisconnected()
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onDisconnected]");
    }
    catch (JSONException ignored)
    {
    }
  }

  /**
   * Receives card swiped event.
   *
   * @param cardData Populated <tt>CardData</tt> object. A <tt>null</tt> value indicates the
   *   operation was timed out or was cancelled by user. A non-null <tt>CardData</tt> with
   *   <tt>DataType.nil</tt> indicates a bad swipe.
   */
  @Override
  public void onCardSwiped(CardData cardData)
  {
    this.o_card_last=cardData;

    try
    {
      if(cardData!=null&&cardData.getDataType()!=CardData.DataType.nil)
      {
        JSONObject a_card=new JSONObject();

        a_card.put("s_number",cardData.getPAN());
        a_card.put("s_expire",cardData.getExpDate());
        a_card.put("s_holder",cardData.getCardholderName());
        a_card.put("s_stripe",cardData.getTrack1()+cardData.getTrack2()+cardData.getTrack3());
        a_card.put("s_track_1",cardData.getTrack1());
        a_card.put("s_track_2",cardData.getTrack2());
        a_card.put("s_track_3",cardData.getTrack3());

        a_card.put("getDataBlock",cardData.getDataBlock());
        a_card.put("getDataType",this.o_card_last.getDataType().toString());
        a_card.put("getKSN",cardData.getKSN());
        a_card.put("getServiceCode",cardData.getServiceCode());

        this.controller().fireSwipe(a_card);
      }
      else
        this.controller().fireSwipeError();

      this.deviceManager.acceptCard("Swipe Card");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onCardInserted(CardData cardData)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onCardInserted]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onCardRemoved()
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onCardRemoved]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onPINEntered(PINData pinData)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onPINEntered]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onYNAnswered(int i)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onYNAnswered]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  public void onMenuSelected(int i)
  {
    try
    {
      this.logInfo("[Wl_Pay_Ccr_DirectConnect.onMenuSelected]");
    }
    catch (JSONException ignored)
    {
    }
  }

  @Override
  protected String[] permissionList() throws JSONException
  {
    switch (this.id_device)
    {
      case Wl_DeviceSid.DC_IDT_AUGUSTA:
      case Wl_DeviceSid.DC_IDT_BT_MAG:
      case Wl_DeviceSid.DC_IDT_UNI_MAG:
      case Wl_DeviceSid.DC_IDT_UNI_PAY:
        // Union of all privileges from these files:
        // \src\android\dc\DCMobileSDK.aar\AndroidManifest.xml
        // \src\android\dc\DCMobileSDK-IDT.aar\AndroidManifest.xml
        return new String[]{
          Manifest.permission.MODIFY_AUDIO_SETTINGS,
          Manifest.permission.RECORD_AUDIO,
          // Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.INTERNET,
          Manifest.permission.ACCESS_NETWORK_STATE,
          Manifest.permission.BLUETOOTH,
          Manifest.permission.BLUETOOTH_ADMIN
        };
      default:
        this.logError("[Wl_Pay_Ccr_DirectConnect.permissionList] Device ID is not known.");
        return new String[]{};
    }
  }

  @Override
  public void startup() throws JSONException
  {
    this.deviceManager.connect(this);
  }

  @Override
  public void tearDown()
  {
    if(this.deviceManager!=null)
    {
      this.deviceManager.disconnect();
      this.deviceManager=null;
    }
    this.devices=null;
  }

  @Override
  public void testSwipe(JSONObject a_card) throws JSONException
  {
    JSONObject a_card_event=new JSONObject();

    a_card_event.put("s_expire",a_card.getString("s_expire"));
    a_card_event.put("s_holder",a_card.getString("s_holder"));
    a_card_event.put("s_number",a_card.getString("s_number"));
    a_card_event.put("s_stripe",a_card.getString("s_stripe"));
    a_card_event.put("s_track_1",a_card.getString("s_track_1"));
    a_card_event.put("s_track_2",a_card.getString("s_track_2"));
    a_card_event.put("s_track_3",a_card.getString("s_track_3"));

    this.controller().fireSwipe(a_card_event);
  }
}
