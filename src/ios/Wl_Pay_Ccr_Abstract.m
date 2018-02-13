#import "Wl_Pay_Ccr_Abstract.h"
#import "Wl_Pay_Ccr_Nmi.h"
#import "Wl_ProcessorSid.h"

@implementation Wl_Pay_Ccr_Abstract

-(Wl_Pay_Ccr*)controller
{
    return self->o_controller;
}

+(Wl_Pay_Ccr_Abstract*)create:(long)id_pay_processor forController:(Wl_Pay_Ccr*)o_controller
{
    Wl_Pay_Ccr_Abstract* o_processor = nil;
    switch (id_pay_processor)
    {
        case WL_PROCESSOR_DIRECT_CONNECT:
            break;
        case WL_PROCESSOR_NMI:
            o_processor = [[Wl_Pay_Ccr_Nmi alloc] init];
            break;
        default:
            break;
    }

    if(o_processor!=nil)
        o_processor->o_controller = o_controller;

    return o_processor;
}
    
@end