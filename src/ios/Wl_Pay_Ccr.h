#import <Cordova/CDVPlugin.h>

@interface Wl_Pay_Ccr : CDVPlugin
{
    BOOL is_method;
}

- (void)debugInfo:(CDVInvokedUrlCommand*)command;

@end