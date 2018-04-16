@interface Wl_UserException : NSException
{
    NSString *s_error;
    NSString *s_message;
}

-(NSString*)errorGet;
-(NSString*)messageGet;

@end