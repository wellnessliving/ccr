//
//  DCMobielSDK-Device.h
//  DCMobileSDK
//
//  Created by Francois Bergeon on 4/18/17.

#ifndef DCMobileSDK_Device_h
#define DCMobileSDK_Device_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

// YES: 1
// NO: 0
#define CANCEL -1

@interface DCGDevice : NSObject
@property (readonly) NSString *Name NS_SWIFT_NAME(Name);
@property (readonly) NSString *Address NS_SWIFT_NAME(Address);
@property (readonly) Class Type NS_SWIFT_NAME(Type);
@property (readonly) NSString *description;
@end

@interface DCGPINData : NSObject
@property (readonly) NSString *DataBlock NS_SWIFT_NAME(DataBlock);
@property (readonly) NSString *KSN NS_SWIFT_NAME(KSN);
@end

@interface DCGEncryptionParameters : NSObject
@property (readonly) NSString *HSMDevice NS_SWIFT_NAME(HSMDevice);
@property (readonly) NSString *TerminalType NS_SWIFT_NAME(TerminalType);
@property (readonly) NSString *EncryptionType NS_SWIFT_NAME(EncryptionType);
@end


typedef NS_ENUM (NSUInteger, DCGCardDataType) {
    DCGCardDataType_Nil = 0,
    DCGCardDataType_P2PE = 1,
    DCGCardDataType_track1 = 2,
    DCGCardDataType_track2 = 3,
    DCGCardDataType_PAN= 4
};

@interface DCGCardData : NSObject
@property (readonly) NSString *Track1 NS_SWIFT_NAME(Track1);
@property (readonly) NSString *Track2 NS_SWIFT_NAME(Track2);
@property (readonly) NSString *Track3 NS_SWIFT_NAME(Track3);
@property (readonly) NSString *PAN NS_SWIFT_NAME(PAN);
@property (readonly) NSString *ExpDate NS_SWIFT_NAME(ExpDate);
@property (readonly) NSString *ServiceCode NS_SWIFT_NAME(ServiceCode);
@property (readonly) NSString *CardholderName NS_SWIFT_NAME(CardholderName);
@property (readonly) NSString *DataBlock NS_SWIFT_NAME(DataBlock);
@property (readonly) NSString *KSN NS_SWIFT_NAME(KSN);
@property (readonly) DCGEncryptionParameters *EncryptionParameters NS_SWIFT_NAME(EncryptionParameters);
@property (readonly) DCGCardDataType DataType NS_SWIFT_NAME(DataType);
@end

@protocol DCGDeviceDelegate <NSObject>
@required
-(void)onConnected;
-(void)onDisconnected;
-(void)onCardSwiped:(DCGCardData *)cardData;
-(void)onCardInserted:(DCGCardData *)cardData;
-(void)onCardRemoved;
-(void)onPINEntered:(DCGPINData *)pinData;
-(void)onYNAnswered:(int)response;
-(void)onMenuSelected:(int)selection;
@end


@interface DCGDeviceManager : NSObject
@property (readonly) Boolean isConnected;
@property (readonly) Boolean isCardInserted;

+(NSArray *)getAvailableDevices;
-(id)init:(DCGDevice *)device;
-(Boolean)connect;
-(void)connect:(id<DCGDeviceDelegate>)delegate;
-(void)disconnect;
-(void)displayMessage:(NSString *)message;
-(void)displayMessages:(NSArray *)messages;
-(DCGCardData *)acceptCard:(NSString *)message;
// Cancel or timeout -> nil PINData, bypassed entry -> nil dataBlock & KSN in PINData object
-(DCGPINData *)acceptPIN:(NSString *)message withAmount:(NSString *)amount withCardData:(DCGCardData *)cardData NS_SWIFT_NAME(acceptPIN(message:amount:cardData:));
// -1: cancel, 0: No, 1: yes
-(int)askYNQuestion:(NSString *)message;
-(int)presentMenu:(NSArray *)messages;
@end

@interface DCGVirtualDeviceManager : DCGDeviceManager
@end

@interface DCGMiuraDeviceManager : DCGDeviceManager
@end

@interface DCGBTMagDeviceManager : DCGDeviceManager
@end

@interface DCGUnipayDeviceManager : DCGDeviceManager
@end

@interface DCGUniMagDeviceManager : DCGDeviceManager
@end

@interface DCGPAXDeviceManager : DCGDeviceManager
@end

@interface DCGIDynamoDeviceManager : DCGDeviceManager

@end


@interface DCGKDynamoDeviceManager : DCGDeviceManager

@end


@interface DCGEDynamoDeviceManager : DCGDeviceManager

@end


@interface DCGUADynamoDeviceManager : DCGDeviceManager

@end


@interface DCGKDynamaxDeviceManager : DCGDeviceManager

@end

#endif /* DCMobileSDK_h_h */
