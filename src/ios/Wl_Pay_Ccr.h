#import <Cordova/CDVPlugin.h>
#import "Wl_Pay_Ccr_Abstract.h"

@class Wl_Pay_Ccr_Abstract;

@interface Wl_Pay_Ccr : CDVPlugin
{
    // If you add new field here, do not forget to initialize it in "init" method.

    NSDictionary *a_config;
    NSMutableArray *a_log;
    BOOL is_active;
    BOOL is_method;
    NSString *callbackId;
    Wl_Pay_Ccr_Abstract *o_processor;
}

- (NSDictionary*) config;
- (void)debugInfo:(CDVInvokedUrlCommand*)command;
- (void)logErrorMessage: (NSString*)s_message;
- (void)logInfo: (NSString*)s_message;
- (void)startup:(CDVInvokedUrlCommand*)command;

@end