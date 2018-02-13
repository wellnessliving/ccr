#import "Wl_Pay_Ccr.h"
#import <Cordova/CDVPlugin.h>

@implementation Wl_Pay_Ccr
    - (void)_end
    {
      is_method=NO;
    }

    - (void)_error:(CDVInvokedUrlCommand*)command with:(NSDictionary *) a_result
    {
        CDVPluginResult* pluginResult = nil;

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:a_result];

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
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

    - (void)_tearDown
    {

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

    -(void)fire:(NSString*)s_event forData:(NSMutableDictionary*)a_event
    {
        if(self->callbackId==nil)
            return;

        [a_event setObject:s_event forKey:@"event"];
        [a_event setObject:[self logResult] forKey:@"a_log"];

        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_event];
        [pluginResult setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self->callbackId];
    }

    - (id)init
    {
        self=[super init];
        if(self)
        {
            self->a_config=nil;
            self->a_log = [[NSMutableArray alloc] init];
            self->callbackId = nil;
            self->is_active=NO;
            self->is_method=NO;
            self->o_processor = nil;
        }
        return self;
    }

    -(void)logErrorDictionary: (NSMutableDictionary*)a_error
    {
        [a_error setValue:[NSNumber numberWithBool:YES] forKey:@"is_error"];
        [self logPut:a_error];
    }

    - (void)logErrorMessage: (NSString*)s_message
    {
        NSMutableDictionary *a_item = [[NSMutableDictionary alloc] init];
        [a_item setValue:[NSNumber numberWithBool:YES] forKey:@"is_error"];
        [a_item setValue:s_message forKey:@"s_message"];
        [self logPut:a_item];
    }

    - (void)logInfo: (NSString*)s_message
    {
        NSMutableDictionary *a_item = [[NSMutableDictionary alloc] init];
        [a_item setValue:[NSNumber numberWithBool:NO] forKey:@"is_error"];
        [a_item setValue:s_message forKey:@"s_message"];
        [self logPut:a_item];
    }

    - (NSArray*)logResult
    {
        NSArray *a_log = self->a_log;
        self->a_log = [[NSMutableArray alloc] init];
        return a_log;
    }

    - (void)logPut: (NSDictionary*)o_message
    {
        [self->a_log addObject:o_message];
    }

    - (void)startup: (CDVInvokedUrlCommand*)command
    {
        [self _start];
        @try
        {
            if(self->is_active)
            {
                [self logErrorMessage:@"[Wl_Pay_Ccr.startup] Plugin is marked as active. Deactivating before activation."];
                [self _tearDown];
            }

            // a_config is used in DirectConnect version of create().
            self->a_config = [command.arguments objectAtIndex:0];

            long id_pay_processor = [[a_config objectForKey:@"id_pay_processor"] integerValue];
            Wl_Pay_Ccr_Abstract *o_processor = [Wl_Pay_Ccr_Abstract create:id_pay_processor forController:self];
            if(o_processor==nil)
            {
                NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
                [a_result setValue:[self logResult] forKey:@"a_log"];
                [a_result setObject:[NSNumber numberWithLong:id_pay_processor] forKey:@"id_pay_processor"];
                [a_result setObject:@"Interface for this payment processor is not implemented." forKey:@"s_message"];
                [a_result setObject:@"not-implemented" forKey:@"s_error"];

                [self _error:command with:a_result];
                return;
            }

            self->is_active = YES;
            self->callbackId = command.callbackId;
            self->o_processor = o_processor;

            NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
            [a_result setValue:[self logResult] forKey:@"a_log"];
            [a_result setObject:[NSNumber numberWithBool:YES] forKey:@"has_permissions"];

            CDVPluginResult* pluginResult = nil;

            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_result];
            [pluginResult setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

            // startup() may issue events.
            // These events should be issued AFTER plugin initialization completes at JavaScript side.
            // To complete that initialization, result should be sent prior sending of any events.

            self->is_method = NO;

            @try
            {
                [o_processor startup];
            }
            @catch (id e)
            {
                // tearDown() can not be called because we need to return this exception into the browser.
                // This can only be done with a log event.
                // tearDown() deactivates sending of events.
                self->is_active = NO;
                @throw e;
            }
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
@end