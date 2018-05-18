#import "Wl_Pay_Ccr_Abstract.h"
// Do not forget to back NMI!
//#import "Wl_Pay_Ccr_Nmi.h"
#import "Wl_Pay_Ccr_DirectConnect.h"
#import "Wl_ProcessorSid.h"

@implementation Wl_Pay_Ccr_Abstract

-(Wl_Pay_Ccr*)controller
{
    return self->o_controller;
}

+(Wl_Pay_Ccr_Abstract*)create:(long)id_pay_processor forController:(Wl_Pay_Ccr*)o_controller
{
    Wl_Pay_Ccr_Abstract* o_processor = nil;
    o_processor = [Wl_Pay_Ccr_DirectConnect create:o_controller];
    // Do not forget to back NMI!
//    switch (id_pay_processor)
//    {
//        case WL_PROCESSOR_DIRECT_CONNECT:
//            o_processor = [Wl_Pay_Ccr_DirectConnect create:o_controller];
//            break;
//        // Do not forget to back NMI!
//        //case WL_PROCESSOR_NMI:
//        //    o_processor = [[Wl_Pay_Ccr_Nmi alloc] init];
//        //    break;
//        default:
//            break;
//    }

    if(o_processor!=nil)
        o_processor->o_controller = o_controller;

    return o_processor;
}

-(NSDictionary*)debugInfo
{
    return [[NSDictionary alloc] init];
}

- (void)logInfo: (NSString*)s_message
{
    [o_controller logInfo:s_message];
}

- (void)logErrorMessage: (NSString*)s_message
{
    [o_controller logErrorMessage:s_message];
}

-(void)startup
{
    @throw [NSException exceptionWithName:@"internal" reason:@"[Wl_Pay_Ccr_Abstract.startup] Call to abstract method." userInfo:nil];
}

-(void)tearDown
{
    @throw [NSException exceptionWithName:@"internal" reason:@"[Wl_Pay_Ccr_Abstract.tearDown] Call to abstract method." userInfo:nil];
}

-(void)testSwipe: (NSDictionary*)a_card
{
    @throw [NSException exceptionWithName:@"internal" reason:@"[Wl_Pay_Ccr_Abstract.testSwipe] Call to abstract method." userInfo:a_card];
}

@end
