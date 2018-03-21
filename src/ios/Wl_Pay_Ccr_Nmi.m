#import "Wl_Pay_Ccr_Nmi.h"
#import "PGMobileSDK/PGEncrypt.h"
#import "PGMobileSDK/PGKeyedCard.h"
#import "PGMobileSDK/PGSwipeController.h"
#import "PGMobileSDK/PGSwipeDevice.h"
#import "Wl_DeviceSid.h"

@implementation Wl_Pay_Ccr_Nmi

-(NSDictionary*)debugInfo
{
    NSMutableDictionary* a_debug = [[NSMutableDictionary alloc] init];
    
    [a_debug setObject:@"Wl_Pay_Ccr_Nmi" forKey:@"s_class"];
    [a_debug setObject:s_key==nil?@"[null]":s_key forKey:@"s_key"];
    [a_debug setObject:swipeController==nil?@"[nil]":@"[set]" forKey:@"swipeController"];
    [a_debug setObject:device==nil?@"[nil]":@"[set]" forKey:@"device"];

    if(device!=nil)
    {
        [a_debug setObject:NSStringFromClass([device class]) forKey:@"device.class"];
        [a_debug setObject:[NSNumber numberWithBool:[device isConnected]] forKey:@"device.isConnected"];
        [a_debug setObject:[NSNumber numberWithBool:[device isActivated]] forKey:@"device.isActivated"];
        [a_debug setObject:[NSNumber numberWithBool:[device isReadyForSwipe]] forKey:@"device.isReadyForSwipe"];
    }
    
    if(card==nil)
    {
        [a_debug setObject:@"[nil]" forKey:@"card"];
    }
    else
    {
        NSMutableDictionary* a_card = [[NSMutableDictionary alloc] init];
        
        [a_card setObject:card.track1 forKey:@"track1"];
        [a_card setObject:card.track2 forKey:@"track2"];
        [a_card setObject:card.track3 forKey:@"track3"];
        [a_card setObject:card.maskedCardNumber forKey:@"maskedCardNumber"];
        [a_card setObject:card.expirationDate forKey:@"expirationDate"];
        [a_card setObject:card.cardholderName forKey:@"cardholderName"];
        [a_card setObject:[card getDirectPostStringWithCVVIncluded:YES] forKey:@"getDirectPostStringWithCVVIncluded"];

        [a_debug setObject:a_card forKey:@"card"];
    }
    
    return a_debug;
}

-(void)deviceActivationFinished:(PGSwipeDevice *)sender result:(SwipeActivationResult)result
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceActivationFinished]"];
    
    device=sender;
    if(result == SwipeActivationResultSuccess)
    {
        if([sender isMemberOfClass:[PGSwipeUniMag class]])
        {
            [swipeController.uniMagReader requestSwipe];
        }
    }
}

-(void)deviceAutodetectComplete:(CardReaderAutodetectResult)result
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceAutodetectComplete]"];
}

-(void)deviceAutodetectStarted
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceAutodetectStarted]"];
}

-(void)deviceBecameReadyForSwipe:(PGSwipeDevice *)sender
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceBecameReadyForSwipe]"];
}

-(void)deviceBecameUnreadyForSwipe:(PGSwipeDevice *)sender reason:(SwipeReasonUnreadyForSwipe)reason
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceBecameUnreadyForSwipe]"];
}

-(void)deviceConnected:(PGSwipeDevice *)sender
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceConnected]"];
}

-(void)deviceDeactivated:(PGSwipeDevice *)sender
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceDeactivated]"];
}

-(void)deviceDisconnected:(PGSwipeDevice *)sender
{
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.deviceDisconnected]"];
}

-(id)init
{
    self = [super init];
    if(self)
    {
        device = nil;
        s_key = nil;
        swipeController = nil;
    }
    return self;
}

-(void)didSwipeCard:(PGSwipedCard *)card device:(PGSwipeDevice *)sender
{
    self->card=card;
    [self logInfo:@"[Wl_Pay_Ccr_Nmi.didSwipeCard]"];
    if(card!=nil)
    {
        PGEncrypt* o_card_encrypt = [[PGEncrypt alloc] init];
        [o_card_encrypt setKey:s_key];
        
        NSMutableDictionary* a_card_event = [[NSMutableDictionary alloc] init];
        
        [a_card_event setObject:[o_card_encrypt encrypt:card includeCVV:YES] forKey:@"s_encrypt"];
        [a_card_event setObject:[card expirationDate] forKey:@"s_expire"];
        [a_card_event setObject:[card cardholderName] forKey:@"s_holder"];
        [a_card_event setObject:[card maskedCardNumber] forKey:@"s_number_mask"];
        
        [[self controller] fireSwipe:a_card_event];
    }
    else
    {
        [[self controller] fireSwipeError];
    }
}

-(int)idNmi:(int)id_device
{
    switch(id_device)
    {
        case WL_DEVICE_NMI_ENTERPRISE:
            return AudioJackReaderIpsEnterprise;
        case WL_DEVICE_NMI_IPS:
            return AudioJackReaderIps;
        case WL_DEVICE_NMI_UNIMAG:
            return AudioJackReaderUnimag;
        default:
            @throw [NSException exceptionWithName:@"nmi-device-nx" reason:@"[Wl_Pay_Ccr_Nmi.idNmi] Device ID is not registered." userInfo:@{@"id_device":[NSNumber numberWithInt:id_device]}];
    }
}

-(void) startup
{
    NSDictionary* a_config = [[[self controller] config] objectForKey:@"a_processor"];
    self->s_key = [a_config objectForKey:@"s_key"];
    
    int deviceType=[self idNmi:[[a_config objectForKey:@"id_device"] shortValue]];
    swipeController = [[PGSwipeController alloc] initWithDelegate:self audioReader:deviceType];
}

-(void)tearDown
{
    device=nil;
    swipeController=nil;
    card=nil;
    s_key=nil;
}

+(void)testException
{
    NSMutableDictionary * userInfo=[[NSMutableDictionary alloc] init];
    [userInfo setObject:@"Example data." forKey:@"s_example"];
    @throw [NSException exceptionWithName:@"example" reason:@"Example exception message." userInfo:userInfo];
}

-(void)testSwipe: (NSDictionary*)a_card
{
    if(s_key==nil)
    {
        [self logErrorMessage:@"[Wl_Pay_Ccr_Nmi.testSwipe] deviceManager is not initialized."];
        return;
    }

    PGKeyedCard* card = [[PGKeyedCard alloc] initWithCardNumber:[a_card objectForKey:@"s_number"] expirationDate:[a_card objectForKey:@"s_expire"] cvv:[a_card objectForKey:@"s_cvv"]];

    PGEncrypt* o_card_encrypt = [[PGEncrypt alloc] init];
    [o_card_encrypt setKey:s_key];

    NSMutableDictionary* a_card_event = [[NSMutableDictionary alloc] init];

    [a_card_event setObject:[o_card_encrypt encrypt:card includeCVV:YES] forKey:@"s_encrypt"];
    [a_card_event setObject:[a_card objectForKey:@"s_expire"] forKey:@"s_expire"];
    [a_card_event setObject:[a_card objectForKey:@"s_holder"] forKey:@"s_holder"];
    [a_card_event setObject:[NSString stringWithFormat:@"****%@",[[a_card objectForKey:@"s_number"] substringWithRange:NSMakeRange(12,4)]] forKey:@"s_number_mask"];

    [[self controller] fireSwipe:a_card_event];
}

@end