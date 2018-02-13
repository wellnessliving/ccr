#import <Foundation/Foundation.h>
#import "Wl_Pay_Ccr_Abstract.h"
#import "PGMobileSDK/PGSwipeController.h"
#import "PGMobileSDK/PGSwipeDelegate.h"
#import "PGMobileSDK/PGSwipeDevice.h"
#import "PGMobileSDK/PGSwipedCard.h"

@interface Wl_Pay_Ccr_Nmi: Wl_Pay_Ccr_Abstract <PGSwipeDelegate>
{
    PGSwipedCard* card;
    PGSwipeDevice* device;
    NSString* s_key;
    PGSwipeController* swipeController;
}

@end
