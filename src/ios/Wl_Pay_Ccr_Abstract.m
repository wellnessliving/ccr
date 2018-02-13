//
//  Wl_Pay_Ccr_Abstract.m
//  WellnessLiving Elevate
//
//  Created by Koins on 13.02.2018.
//

#import "Wl_Pay_Ccr_Abstract.h"

@implementation Wl_Pay_Ccr_Abstract
    
    +(Wl_Pay_Ccr_Abstract*)create:(long)id_pay_processor forController:(Wl_Pay_Ccr*)o_controller
    {
        return [[Wl_Pay_Ccr_Abstract alloc] init];
    }
    
@end
