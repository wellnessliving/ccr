#import "Wl_Pay_Ccr.h"
#import <Cordova/CDVPlugin.h>

@implementation Wl_Pay_Ccr

- (void)_end
{
  is_method=NO;
}

- (void)_exception:(id)e
{
  // TODO Not implemented
}

- (void)_start
{
  is_method=YES;
}

- (void)_success:(CDVInvokedUrlCommand*)command with:(NSDictionary *) a_result
{
    CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_result];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)debugInfo:(CDVInvokedUrlCommand*)command
{
    [self _start];
    @try
    {
        NSMutableDictionary * a_result=[[NSMutableDictionary alloc] init];
        [a_result setValue:[NSNumber numberWithBool:self->is_active] forKey:@"is_active"];
        [self _success:command with:a_result];
    }
    @catch(id e)
    {
        [self _exception:e];
    }
    @finally
    {
        [self _end];
    }
}

- (id)init
{
    self=[super init];
    if(self)
    {
        self->is_active=NO;
        self->is_method=NO;
    }
    return self;
}

@end