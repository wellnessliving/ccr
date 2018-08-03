#import <Cordova/CDVPlugin.h>
#import "Wl_Pay_Ccr_Abstract.h"

@class Wl_Pay_Ccr_Abstract;

@interface Wl_Pay_Ccr : CDVPlugin
{
    // If you add new field here, do not forget to initialize it in "init" method.

    NSDictionary *a_config;
    NSMutableArray *a_log;
    float f_volume;
    BOOL is_active;
    BOOL is_method;
    NSString *callbackId;
    Wl_Pay_Ccr_Abstract *o_processor;
}

- (NSDictionary*) config;
- (void)_exception:(id)e forCommand:(CDVInvokedUrlCommand*)command;
- (void)debugInfo:(CDVInvokedUrlCommand*)command;
- (void)fireSwipe:(NSMutableDictionary*)a_card;
- (void)fireSwipeError;
- (void)logErrorMessage: (NSString*)s_message;
- (void)logInfo: (NSString*)s_message;
- (void)startup:(CDVInvokedUrlCommand*)command;
- (void)tearDown:(CDVInvokedUrlCommand*)command;
- (void)testException:(CDVInvokedUrlCommand*)command;
- (void)testSwipe:(CDVInvokedUrlCommand*)command;

@end
