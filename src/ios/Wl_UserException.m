#import "Wl_UserException.h"

@implementation Wl_UserException

    -(id)init:(NSString*)s_error message:(NSString*)s_message
    {
        self = [super init];
        if(self)
        {
            [self s_error:s_error];
            [self s_message:s_message];
        }
        return self;
    }

@end