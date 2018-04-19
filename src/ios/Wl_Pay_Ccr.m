#import "Wl_Pay_Ccr.h"
#import "Wl_Pay_Ccr_DirectConnect.h"
#import "Wl_Pay_Ccr_Nmi.h"
#import "Wl_UserException.h"
#import <Cordova/CDVPlugin.h>

@implementation Wl_Pay_Ccr
    - (void)_end
    {
      is_method=NO;
    }

    - (void)_error:(CDVInvokedUrlCommand*)command with:(NSMutableDictionary *) a_result
    {
        CDVPluginResult* pluginResult = nil;

        [a_result setObject:[self logResult] forKey:@"a_log"];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:a_result];

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }

- (void)_exception:(id)e forCommand:(CDVInvokedUrlCommand*) command
    {
        NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
        [a_result setObject:NSStringFromClass([e class]) forKey:@"s_class"];
        [a_result setObject:@"internal" forKey:@"s_error"];

        if([e isKindOfClass:[Wl_UserException class]])
        {
            [a_result setObject:[e messageGet] forKey:@"s_message"];
            [a_result setObject:[e errorGet] forKey:@"s_error"];
        }
        else if([e isKindOfClass:[NSException class]])
        {
            [a_result setObject:[e reason] forKey:@"s_message"];
            [a_result setObject:[e name] forKey:@"s_name"];
            if([e userInfo]!=nil)
                [a_result setObject:[e userInfo] forKey:@"a_info"];
            [a_result setObject:[e callStackSymbols] forKey:@"s_stack"];
        }

        if(is_method)
            [self _error:command with:a_result];
        else
            [self logErrorDictionary:a_result];
    }

    - (void)_start
    {
      is_method=YES;
    }

    - (void)_success:(CDVInvokedUrlCommand*)command with:(NSMutableDictionary *) a_result
    {
        CDVPluginResult* pluginResult = nil;

        [a_result setObject:[self logResult] forKey:@"a_log"];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_result];

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }

    - (void)_tearDown
    {
        if(o_processor!=nil)
        {
            [o_processor tearDown];
            o_processor=nil;
        }

        if(callbackId!=nil)
        {
            // **** BE ATTENTIVE ***
            // All tear down actions should be performed before the code the follows.
            // The following code sends final event.
            // No events can be sent after that.
            NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
            [a_result setObject:@"tearDown" forKey:@"event"];

            CDVPluginResult* pluginResult = nil;
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_result];
            [pluginResult setKeepCallbackAsBool:NO];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];


            callbackId = nil;
        }

        is_active = NO;

    }

    -(NSDictionary*)config
    {
        return self->a_config;
    }

    - (void)debugInfo:(CDVInvokedUrlCommand*)command
    {
        [self _start];
        @try
        {
            NSMutableDictionary * a_result=[[NSMutableDictionary alloc] init];
            [a_result setValue:[NSNumber numberWithBool:self->is_active] forKey:@"is_active"];

            if(o_processor==nil)
            {
                [a_result setValue:@"[nil]" forKey:@"o_processor"];
            }
            else
            {
                [a_result setValue:[o_processor debugInfo] forKey:@"o_processor"];
            }

            [a_result setValue:[Wl_Pay_Ccr_DirectConnect debugGlobal] forKey:@"dc"];
            
            [self _success:command with:a_result];
        }
        @catch(id e)
        {
            [self _exception:e forCommand:command];
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

        [a_event setObject:[self logResult] forKey:@"a_log"];
        [a_event setObject:s_event forKey:@"event"];

        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:a_event];
        [pluginResult setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self->callbackId];
    }

- (void)fireLog
{
    if([a_log count]==0)
        return;
    
    [self fire:@"log" forData:[[NSMutableDictionary alloc] init]];
}

- (void)fireSwipe:(NSMutableDictionary*)a_card
{
    [self fire:@"swipe" forData:a_card];
}

- (void)fireSwipeError
{
    [self fire:@"swipeError" forData:[[NSMutableDictionary alloc] init]];
}

    - (void)pluginInitialize
    {
        self->a_config=nil;
        self->a_log = [[NSMutableArray alloc] init];
        self->callbackId = nil;
        self->is_active=NO;
        self->is_method=NO;
        self->o_processor = nil;
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
        if(!is_method&&callbackId!=nil)
            [self fireLog];
    }

    - (void)startup: (CDVInvokedUrlCommand*)command
    {
        [self _start];
        @try
        {
            if(self->is_active)
            {
                [self logInfo:@"[Wl_Pay_Ccr.startup] Plugin is marked as active. Deactivating before activation."];
                [self _tearDown];
            }

            // a_config is used in DirectConnect version of create().
            self->a_config = [command.arguments objectAtIndex:0];

            long id_pay_processor = [[a_config objectForKey:@"id_pay_processor"] integerValue];
            Wl_Pay_Ccr_Abstract *o_processor = [Wl_Pay_Ccr_Abstract create:id_pay_processor forController:self];
            if(o_processor==nil)
            {
                NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
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
            [self _exception:e forCommand:command];
        }
        @finally
        {
            [self _end];
        }
    }

- (void)tearDown:(CDVInvokedUrlCommand*)command
{
    [self _start];
    @try
    {
        if(!is_active)
        {
            [self logErrorMessage:@"[Wl_Pay_Ccr.tearDown] It is not allowed to tear down plugin that is not initialized."];
            
            NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
            [a_result setObject:@"Not initialized." forKey:@"s_message"];
            
            [self _error:command with:a_result];
            return;
        }
        
        [self _tearDown];
        
        NSMutableDictionary* a_result = [[NSMutableDictionary alloc] init];
        [a_result setObject:@"Complete." forKey:@"s_message"];
        
        [self _success:command with:a_result];
    }
    @catch(id e)
    {
        [self _exception:e forCommand:command];
    }
    @finally
    {
        [self _end];
    }

}

-(void)testException:(CDVInvokedUrlCommand *)command
{
    [self _start];
    @try
    {
        [Wl_Pay_Ccr_Nmi testException];
    }
    @catch(id e)
    {
        [self _exception:e forCommand:command];
    }
    @finally
    {
        [self _end];
    }
}

- (void)testSwipe:(CDVInvokedUrlCommand*)command
{
    [self _start];
    @try
    {
        if(!is_active)
        {
            [self logErrorMessage:@"[Wl_Pay_Ccr.doTestSwipe] Swipe event can not be fired when plugin is inactive."];
            
            NSMutableDictionary *a_result=[[NSMutableDictionary alloc] init];
            [a_result setObject:@"Swipe event can not be fired when plugin is inactive." forKey:@"s_message"];
            [a_result setObject:@"inactive" forKey:@"s_error"];
            [self _error:command with:a_result];
            return;
        }
        
        NSDictionary *a_card = [command.arguments objectAtIndex:0];
        
        [o_processor testSwipe:a_card];
         
        NSMutableDictionary *a_result=[[NSMutableDictionary alloc] init];
        [a_result setObject:@"Swipe event is fired." forKey:@"s_message"];
        [self _success:command with:a_result];
    }
    @catch(id e)
    {
        [self _exception:e forCommand:command];
    }
    @finally
    {
        [self _end];
    }
}

@end
