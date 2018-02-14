#import "Wl_Pay_Ccr_DirectConnect.h"
#import "Wl_DeviceSid.h"

@implementation Wl_Pay_Ccr_DirectConnect

+ (Wl_Pay_Ccr_DirectConnect*)create: (Wl_Pay_Ccr*)o_controller
{
    [o_controller logInfo:[NSString stringWithFormat:@"Config: %@",[o_controller config]]];
    
    NSDictionary* a_config = [[o_controller config] objectForKey:@"a_processor"];
    
    short id_device = [[a_config objectForKey:@"id_device"] shortValue];
    NSArray* devices;
    DCGDeviceManager* deviceManager;
    
    switch(id_device)
    {
        /*case WL_DEVICE_DC_IDT_AUGUSTA:
            devices = AugustaDeviceManager.getAvailableDevices();
            if(devices==null||devices.length==0)
                return null;
            deviceManager = new AugustaDeviceManager(devices[0], o_context);
            break;
        case WL_DEVICE_DC_IDT_BT_MAG:
            devices = BTMagDeviceManager.getAvailableDevices();
            if(devices==null||devices.length==0)
                return null;
            deviceManager = new BTMagDeviceManager(devices[0], o_context);
            break;
        case WL_DEVICE_DC_IDT_UNI_MAG:
            devices = UniMagDeviceManager.getAvailableDevices();
            if(devices.length==0)
                return null;
            deviceManager = new UniMagDeviceManager(devices[0], o_context);
            break;
        case WL_DEVICE_DC_IDT_UNI_PAY:
            devices = UniPayDeviceManager.getAvailableDevices();
            if(devices.length==0)
                return null;
            deviceManager = new UniPayDeviceManager(devices[0], o_context);
            break;
        case WL_DEVICE_DC_MIURA:
            devices = [DCGMiuraDeviceManager getAvailableDevices];
            if(devices==nil||[devices count]==0)
                return nil;
            deviceManager = [[DCGMiuraDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;*/
        default:
            return nil;
    }
    
    Wl_Pay_Ccr_DirectConnect* o_result = [[Wl_Pay_Ccr_DirectConnect alloc] init];
    o_result->devices = devices;
    o_result->deviceManager = deviceManager;
    o_result->id_device = id_device;
    return o_result;

}

-(id)init
{
    self = [super init];
    if(self)
    {
        o_card_last = nil;
    }
    return self;
}

- (void)testSwipe: (NSDictionary*)a_card
{
    NSMutableDictionary* a_card_event = [[NSMutableDictionary alloc] init];
    
    [a_card_event setObject:[a_card objectForKey:@"s_expire"] forKey:@"s_expire"];
    [a_card_event setObject:[a_card objectForKey:@"s_holder"] forKey:@"s_holder"];
    [a_card_event setObject:[a_card objectForKey:@"s_number"] forKey:@"s_number"];
    [a_card_event setObject:[a_card objectForKey:@"s_stripe"] forKey:@"s_stripe"];
    [a_card_event setObject:[a_card objectForKey:@"s_stack_1"] forKey:@"s_stack_1"];
    [a_card_event setObject:[a_card objectForKey:@"s_stack_2"] forKey:@"s_stack_2"];
    [a_card_event setObject:[a_card objectForKey:@"s_stack_3"] forKey:@"s_stack_3"];

    [[self controller] fireSwipe:a_card_event];
}

- (void)onCardInserted:(DCGCardData *)cardData { 
 [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onCardInserted]"];
}

- (void)onCardRemoved {
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onCardRemoved]"];
}

- (void)onCardSwiped:(DCGCardData *)cardData { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.cardData]"];
}

- (void)onConnected { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onConnected]"];
}

- (void)onDisconnected { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onDisconnected]"];
}

- (void)onMenuSelected:(int)selection { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onMenuSelected]"];
}

- (void)onPINEntered:(DCGPINData *)pinData { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onPINEntered]"];
}

- (void)onYNAnswered:(int)response { 
    [self logInfo:@"[Wl_Pay_Ccr_DerectConnect.onYNAnswered]"];
}

@end
