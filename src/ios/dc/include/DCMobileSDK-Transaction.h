//
//  DCMobielSDK-Transaction.h
//  DCMobileSDK
//
//  Created by Francois Bergeon on 4/18/17.

#ifndef DCMobileSDK_Transaction_h
#define DCMobileSDK_Transaction_h

#import <Foundation/Foundation.h>

typedef enum DCGType : NSInteger {
    DC_UNDEF,
    DC_STRING,
    DC_INT,
    DC_BOOLEAN
} DCGType;

@interface DCGElement : NSObject
@property (readonly) DCGType type;
@property (readonly) int length;
@end

@interface DCGCollection : NSObject
@end

@interface DCGRequest : DCGCollection
-(void)setValue:(id)value forKey:(NSString *)name;
@end

@interface DCGSendableRequest : DCGRequest
@end

@interface DCGResponse : DCGCollection
-(id)getValue:(NSString *)name;
@end

@protocol DCGRequestDelegate <NSObject>
@required
-(void)onProcessed:(DCGResponse *)response;
@end

#endif /* DCMobileSDK_Transaction_h */
