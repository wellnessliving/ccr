#import <Foundation/Foundation.h>
#import "Wl_Pay_Ccr.h"
#import "Wl_Pay_Ccr_Abstract.h"
#import "include/DCMobileSDK-Device.h"

@interface Wl_Pay_Ccr_DirectConnect: Wl_Pay_Ccr_Abstract <DCGDeviceDelegate>
{
    NSArray* devices;
    DCGDeviceManager* deviceManager;
    short id_device;
    DCGCardData* o_card_last;
}

+ (Wl_Pay_Ccr_DirectConnect*)create: (Wl_Pay_Ccr*)o_controller;
+ (NSDictionary*)debugGlobal;
- (void)testSwipe: (NSDictionary*)a_card;

@end
