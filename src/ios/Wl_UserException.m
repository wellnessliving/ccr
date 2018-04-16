#import "Wl_UserException.h"

@implementation Wl_UserException

    -(id)initWithName:(NSExceptionName)aName reason:(NSString *)aReason userInfo:(NSDictionary *)aUserInfo
    {
        self = [super initWithName:aName reason:aReason userInfo:aUserInfo];
        if(self)
        {
            s_error = aName;
            s_message = aReason;
        }
        return self;
    }

    -(NSString*)errorGet
    {
        return s_error;
    }

    -(NSString*)messageGet
    {
        return s_message;
    }

@end