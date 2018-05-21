#import "Wl_Pay_Ccr_DirectConnect.h"
#import "Wl_DeviceSid.h"
#import "Wl_UserException.h"

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
        /* Not implemented for iOS.
         case WL_DEVICE_DC_IDT_AUGUSTA:
            devices = AugustaDeviceManager.getAvailableDevices();
            if(devices==null||devices.length==0)
                return null;
            deviceManager = new AugustaDeviceManager(devices[0], o_context);
            break;*/
        /*case WL_DEVICE_DC_IDT_BT_MAG:
            devices = [DCGBTMagDeviceManager getAvailableDevices];
            if(devices==nil)
                @throw [Wl_UserException exceptionWithName:@"bt-mag-null" reason:@"Can not initialize IDTech BtMag Device Manager ([DCGBTMagDeviceManager getAvailableDevices] returns null)." userInfo:nil];
            if([devices count]==0)
                @throw [Wl_UserException exceptionWithName:@"bt-mag-empty" reason:@"Can not initialize IDTech BtMag Device Manager ([DCGBTMagDeviceManager getAvailableDevices] returns an empty array)." userInfo:nil];
            deviceManager = [[DCGBTMagDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;
        case WL_DEVICE_DC_IDT_UNI_MAG:
            devices = [DCGUniMagDeviceManager getAvailableDevices];
            if(devices==nil)
                @throw [Wl_UserException exceptionWithName:@"uni-mag-null" reason:@"Can not initialize IDTech UniMag Device Manager ([DCGUniMagDeviceManager getAvailableDevices] returns null)." userInfo:nil];
            if([devices count]==0)
                @throw [Wl_UserException exceptionWithName:@"uni-mag-empty" reason:@"Can not initialize IDTech UniMag Device Manager ([DCGUniMagDeviceManager getAvailableDevices] returns an empty array)." userInfo:nil];
            deviceManager = [[DCGUniMagDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;*/
        case WL_DEVICE_DC_MAGTEK_AUDIO:
            devices = [DCGUADynamoDeviceManager getAvailableDevices];
            if(devices==nil)
                @throw [Wl_UserException exceptionWithName:@"magtek-null" reason:@"Can not initialize Magtek Device Manager ([DCGUADynamoDeviceManager getAvailableDevices] returns null)." userInfo:nil];
            if([devices count]==0)
                @throw [Wl_UserException exceptionWithName:@"magtek-empty" reason:@"Can not initialize Magtek Device Manager ([DCGUADynamoDeviceManager getAvailableDevices] returns an empty array)." userInfo:nil];
            deviceManager = [[DCGUADynamoDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;
        /* Not implemented for iOS.
        case WL_DEVICE_DC_IDT_UNI_PAY:
            devices = [DCGUnipayDeviceManager getAvailableDevices];
            if(devices==nil||[devices count]==0)
                return nil;
            deviceManager = [[DCGUnipayDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;*/
        /* Not implemented for iOS.
        case WL_DEVICE_DC_MIURA:
            devices = [DCGMiuraDeviceManager getAvailableDevices];
            if(devices==nil)
                @throw [Wl_UserException exceptionWithName:@"miura-null" reason:@"Can not initialize IDTech Miura Device Manager ([DCGMiuraDeviceManager getAvailableDevices] returns null)." userInfo:nil];
            if([devices count]==0)
                @throw [Wl_UserException exceptionWithName:@"miura-empty" reason:@"Can not initialize IDTech Miura Device Manager ([DCGMiuraDeviceManager getAvailableDevices] returns an empty array)." userInfo:nil];
            deviceManager = [[DCGMiuraDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;*/
        /* Not implemented for iOS.
        case WL_DEVICE_DC_PAX:
             devices = [DCGPAXDeviceManager getAvailableDevices];
             if(devices==nil||[devices count]==0)
             return nil;
             deviceManager = [[DCGPAXDeviceManager alloc] init:[devices objectAtIndex:0]];
             break;*/
        case WL_DEVICE_VIRTUAL:
             devices = [DCGVirtualDeviceManager getAvailableDevices];
            if(devices==nil)
                @throw [Wl_UserException exceptionWithName:@"virtual-null" reason:@"Can not initialize Virtual Device Manager ([DCGVirtualDeviceManager getAvailableDevices] returns null)." userInfo:nil];
            if([devices count]==0)
                @throw [Wl_UserException exceptionWithName:@"virtual-empty" reason:@"Can not initialize Virtual Device Manager ([DCGVirtualDeviceManager getAvailableDevices] returns an empty array)." userInfo:nil];
            deviceManager = [[DCGVirtualDeviceManager alloc] init:[devices objectAtIndex:0]];
            break;
        default:
            @throw [Wl_UserException exceptionWithName:@"dc-not-implemented" reason:@"Interface for DirecConnect device is not implemented." userInfo:nil];
    }

    Wl_Pay_Ccr_DirectConnect* o_result = [[Wl_Pay_Ccr_DirectConnect alloc] init];
    o_result->devices = devices;
    o_result->deviceManager = deviceManager;
    o_result->id_device = id_device;
    return o_result;

}

+(NSDictionary*)debugGlobal
{
    NSMutableDictionary* a_debug = [[NSMutableDictionary alloc] init];

    NSArray* devices;

    /*devices = [DCGBTMagDeviceManager getAvailableDevices];
    if(devices==nil)
        [a_debug setObject:@"[null]" forKey:@"[DCGBTMagDeviceManager getAvailableDevices]"];
    else if ([devices count]==0)
        [a_debug setObject:@"[empty array]" forKey:@"[DCGBTMagDeviceManager getAvailableDevices]"];
    else
        [a_debug setValue:[NSNumber numberWithLong:[devices count]] forKey:@"[DCGBTMagDeviceManager getAvailableDevices]"];

    devices = [DCGUniMagDeviceManager getAvailableDevices];
    if(devices==nil)
        [a_debug setObject:@"[null]" forKey:@"[DCGUniMagDeviceManager getAvailableDevices]"];
    else if ([devices count]==0)
        [a_debug setObject:@"[empty array]" forKey:@"[DCGUniMagDeviceManager getAvailableDevices]"];
    else
        [a_debug setValue:[NSNumber numberWithLong:[devices count]] forKey:@"[DCGUniMagDeviceManager getAvailableDevices]"];*/

    devices = [DCGUADynamoDeviceManager getAvailableDevices];
    if(devices==nil)
        [a_debug setObject:@"[null]" forKey:@"[DCGUADynamoDeviceManager getAvailableDevices]"];
    else if ([devices count]==0)
        [a_debug setObject:@"[empty array]" forKey:@"[DCGUADynamoDeviceManager getAvailableDevices]"];
    else
        [a_debug setValue:[NSNumber numberWithLong:[devices count]] forKey:@"[DCGUADynamoDeviceManager getAvailableDevices]"];

    /* Not implemented for iOS.
    devices = [DCGMiuraDeviceManager getAvailableDevices];
    if(devices==nil)
        [a_debug setObject:@"[null]" forKey:@"[DCGMiuraDeviceManager getAvailableDevices]"];
    else if ([devices count]==0)
        [a_debug setObject:@"[empty array]" forKey:@"[DCGMiuraDeviceManager getAvailableDevices]"];
    else
        [a_debug setValue:[NSNumber numberWithLong:[devices count]] forKey:@"[DCGMiuraDeviceManager getAvailableDevices]"];*/

    devices = [DCGVirtualDeviceManager getAvailableDevices];
    if(devices==nil)
        [a_debug setObject:@"[null]" forKey:@"[DCGVirtualDeviceManager getAvailableDevices]"];
    else if ([devices count]==0)
        [a_debug setObject:@"[empty array]" forKey:@"[DCGVirtualDeviceManager getAvailableDevices]"];
    else
        [a_debug setValue:[NSNumber numberWithLong:[devices count]] forKey:@"[DCGVirtualDeviceManager getAvailableDevices]"];

    return a_debug;
}

- (NSDictionary*)debugInfo
{
    NSMutableDictionary* a_debug = [[NSMutableDictionary alloc] init];

    NSMutableArray* a_device = [[NSMutableArray alloc] init];
    for(DCGDevice* device in devices)
    {
        NSMutableDictionary* a_device_item = [[NSMutableDictionary alloc] init];
        [a_device_item setObject:[device Address] forKey:@"address"];
        [a_device_item setObject:[device Name] forKey:@"name"];
        [a_device_item setObject:[device Type] forKey:@"type"];
        [a_device_item setObject:[device description] forKey:@"description"];
        [a_device addObject:a_device_item];
    }

    [a_debug setObject:a_device forKey:@"devices"];
    [a_debug setObject:devices==nil?@"[nil]":[NSNumber numberWithInteger:[devices count]] forKey:@"devices.length"];
    [a_debug setObject:deviceManager==nil?@"[nil]":NSStringFromClass([deviceManager class]) forKey:@"deviceManager.class"];
    [a_debug setObject:[NSNumber numberWithInteger:id_device] forKey:@"id_device"];
    [a_debug setObject:NSStringFromClass([self class]) forKey:@"this.class"];

    if(deviceManager!=nil)
    {
        [a_debug setObject:[NSNumber numberWithBool:[deviceManager isCardInserted]] forKey:@"deviceManager.isCardInserted"];
        [a_debug setObject:[NSNumber numberWithBool:[deviceManager isConnected]] forKey:@"deviceManager.isConnected"];
    }

    if(o_card_last==nil)
    {
        [a_debug setObject:@"[nil]" forKey:@"card"];
    }
    else
    {
        NSMutableDictionary* a_card = [[NSMutableDictionary alloc] init];
        if([o_card_last CardholderName]==nil)
            [a_card setObject:@"[nil]" forKey:@"CardholderName"];
        else
            [a_card setObject:[o_card_last CardholderName] forKey:@"CardholderName"];
        [a_card setObject:[o_card_last DataBlock] forKey:@"DataBlock"];
        [a_card setObject:[NSNumber numberWithInteger:[o_card_last DataType]] forKey:@"DataType"];
        [a_card setObject:[o_card_last ExpDate] forKey:@"ExpDate"];
        [a_card setObject:[o_card_last KSN] forKey:@"KSN"];
        [a_card setObject:[o_card_last PAN] forKey:@"PAN"];
        [a_card setObject:[o_card_last ServiceCode] forKey:@"ServiceCode"];
        [a_card setObject:[o_card_last Track1] forKey:@"Track1"];
        [a_card setObject:[o_card_last Track2] forKey:@"Track2"];
        [a_card setObject:[o_card_last Track3] forKey:@"Track3"];

        [a_debug setObject:a_card forKey:@"card"];
    }

    return a_debug;
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

- (void)onCardInserted:(DCGCardData *)cardData
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onCardInserted]"];
}

- (void)onCardRemoved
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onCardRemoved]"];
}

- (void)onCardSwiped:(DCGCardData *)cardData
{
    @try
    {
        o_card_last = cardData;

        [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.cardData]"];

        if(cardData!=nil&&[cardData DataType]!=DCGCardDataType_Nil&&[cardData EncryptionParameters]!=nil)
        {
            NSMutableDictionary* a_card = [[NSMutableDictionary alloc] init];

            NSMutableDictionary* a_encrypt = [[NSMutableDictionary alloc] init];
            [a_encrypt setObject:[cardData DataBlock] forKey:@"DataBlock"];
            [a_encrypt setObject:[[cardData EncryptionParameters] EncryptionType] forKey:@"EncryptionType"];
            [a_encrypt setObject:[[cardData EncryptionParameters] HSMDevice] forKey:@"HSMDevice"];
            [a_encrypt setObject:[cardData KSN] forKey:@"KSN"];
            [a_encrypt setObject:[[cardData EncryptionParameters] TerminalType] forKey:@"TerminalType"];

            [a_card setObject:a_encrypt forKey:@"a_encrypt"];
            [a_card setObject:[cardData PAN] forKey:@"s_number_mask"];
            [a_card setObject:[cardData ExpDate] forKey:@"s_expire"];
            [a_card setObject:[NSString stringWithFormat:@"ios.dc.%@",NSStringFromClass([deviceManager class])] forKey:@"s_device"];
            NSString *s_holder = [cardData CardholderName];
            if(s_holder!=nil)
                [a_card setObject:[s_holder stringByReplacingOccurrencesOfString:@"/" withString:@" "] forKey:@"s_holder"];

            [o_controller fireSwipe:a_card];
        }
        else
        {
            [o_controller fireSwipeError];
        }

        if(id_device!=WL_DEVICE_VIRTUAL)
            [deviceManager acceptCard:@"Swipe Card"];
    }
    @catch(id e)
    {
        [o_controller _exception:e forCommand:nil];
    }
}

- (void)onConnected
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onConnected]"];
    if(id_device!=WL_DEVICE_VIRTUAL)
        [deviceManager acceptCard:@"Swipe Card"];
}

- (void)onDisconnected
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onDisconnected]"];
}

- (void)onMenuSelected:(int)selection
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onMenuSelected]"];
}

- (void)onPINEntered:(DCGPINData *)pinData
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onPINEntered]"];
}

- (void)onYNAnswered:(int)response {
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.onYNAnswered]"];
}

-(void) startup
{
    [self logInfo:@"[Wl_Pay_Ccr_DirectConnect.startup]"];
    [deviceManager connect:self];
}

-(void)tearDown
{
    if(deviceManager!=nil)
    {
        [deviceManager disconnect];
        deviceManager = nil;
    }
    devices = nil;
}

- (void)testSwipe: (NSDictionary*)a_card
{
    if(deviceManager==nil)
    {
        [self logErrorMessage:@"[Wl_Pay_Ccr_DirectConnect.testSwipe] deviceManager is not initialized."];
        return;
    }

    if(id_device!=WL_DEVICE_VIRTUAL)
    {
        [self logErrorMessage:@"[Wl_Pay_Ccr_DirectConnect.testSwipe] Can not do testSwipe() because current device is not for testing purposes."];
        return;
    }

    [deviceManager acceptCard:@"Enter card data."];
}

@end