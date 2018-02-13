#import "Wl_Pay_Ccr.h"
#import <Foundation/Foundation.h>

@class Wl_Pay_Ccr;

@interface Wl_Pay_Ccr_Abstract: NSObject
    {
        Wl_Pay_Ccr *o_controller;
    }
    
    +(Wl_Pay_Ccr_Abstract*)create:(long)id_pay_processor forController:(Wl_Pay_Ccr*)o_controller;
    
    @end

