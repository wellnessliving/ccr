//
//  DCMobielSDK+CreditCard.h
//  DCMobileSDK
//
//  Created by Francois Bergeon on 4/18/17.
//  Copyright © 2017 Direct Connect. All rights reserved.
//

#ifndef DCMobileSDK_CreditCard_h
#define DCMobileSDK_CreditCard_h

#import "DCMobileSDK-Transaction.h"
#import "DCMobileSDK-Device.h"


@interface DCGCreditCardResponse : DCGResponse
@property (class, readonly) NSString *AuthorizedAmount NS_SWIFT_NAME(AuthorizedAmount);
@property (class, readonly) NSString *Balance NS_SWIFT_NAME(Balance);
@property (class, readonly) NSString *Result NS_SWIFT_NAME(Result);
@property (class, readonly) NSString *RespMSG NS_SWIFT_NAME(RespMSG);
@property (class, readonly) NSString *Message NS_SWIFT_NAME(Message);
@property (class, readonly) NSString *Message1 NS_SWIFT_NAME(Message1);
@property (class, readonly) NSString *Message2 NS_SWIFT_NAME(Message2);
@property (class, readonly) NSString *AuthCode NS_SWIFT_NAME(AuthCode);
@property (class, readonly) NSString *PNRef NS_SWIFT_NAME(PNRef);
@property (class, readonly) NSString *HostCode NS_SWIFT_NAME(HostCode);
@property (class, readonly) NSString *HostURL NS_SWIFT_NAME(HostURL);
@property (class, readonly) NSString *ReceiptURL NS_SWIFT_NAME(ReceiptURL);
@property (class, readonly) NSString *GetAVSResult NS_SWIFT_NAME(GetAVSResult);
@property (class, readonly) NSString *GetAVSResultTXT NS_SWIFT_NAME(GetAVSResultTXT);
@property (class, readonly) NSString *GetAVSStreetMatchTXT NS_SWIFT_NAME(GetAVSStreetMatchTXT);
@property (class, readonly) NSString *GetAVSZipMatchTXT NS_SWIFT_NAME(GetAVSZipMatchTXT);
@property (class, readonly) NSString *GetCVResult NS_SWIFT_NAME(GetCVResult);
@property (class, readonly) NSString *GetCVResultTXT NS_SWIFT_NAME(GetCVResultTXT);
@property (class, readonly) NSString *GetOrigResult NS_SWIFT_NAME(GetOrigResult);
@property (class, readonly) NSString *GetCommercialCard NS_SWIFT_NAME(GetCommercialCard);
@property (class, readonly) NSString *WorkingKey NS_SWIFT_NAME(WorkingKey);
@property (class, readonly) NSString *KeyPointer NS_SWIFT_NAME(KeyPointer);
// ExtData elements
@property (class, readonly) NSString *BatchNum NS_SWIFT_NAME(BatchNum);
@property (class, readonly) NSString *CardType NS_SWIFT_NAME(CardType);
@property (class, readonly) NSString *ExpDate NS_SWIFT_NAME(ExpDate);
@property (class, readonly) NSString *EmvResponseData NS_SWIFT_NAME(EmvResponseData);
// ExtData.ReceiptData elements
@property (class, readonly) NSString *Requested_Amt NS_SWIFT_NAME(Requested_Amt);
@property (class, readonly) NSString *Approved_Amt NS_SWIFT_NAME(Approved_Amt);
@end


@protocol DCGCreditCardDelegate <DCGRequestDelegate>
@required
-(void)onProcessed:(DCGCreditCardResponse *)response;
@end


@interface DCGCreditCardRequest : DCGSendableRequest
-(id)init:(NSString *)endpoint;
-(DCGCreditCardResponse *)process;
-(DCGCreditCardResponse *)processWithTimeout:(NSUInteger)timeout NS_SWIFT_NAME(process(timeout:));
-(void)processWithDelegate:(id<DCGCreditCardDelegate>)delegate NS_SWIFT_NAME(process(delegate:));
-(void)processWithCompletion:(void (^)(DCGCreditCardResponse *))completion NS_SWIFT_NAME(process(completion:));

// Element names
@property (class, readonly) NSString *UserName NS_SWIFT_NAME(UserName);
@property (class, readonly) NSString *Password NS_SWIFT_NAME(Password);
@property (class, readonly) NSString *TransType NS_SWIFT_NAME(TransType);
@property (class, readonly) NSString *CardNum NS_SWIFT_NAME(CardNum);
@property (class, readonly) NSString *ExpDate NS_SWIFT_NAME(ExpDate);
@property (class, readonly) NSString *MagData NS_SWIFT_NAME(MagData);
@property (class, readonly) NSString *NameOnCard NS_SWIFT_NAME(NameOnCard);
@property (class, readonly) NSString *Amount NS_SWIFT_NAME(Amount);
@property (class, readonly) NSString *InvNum NS_SWIFT_NAME(InvNum);
@property (class, readonly) NSString *PNRef NS_SWIFT_NAME(PNRef);
@property (class, readonly) NSString *Zip NS_SWIFT_NAME(Zip);
@property (class, readonly) NSString *Street NS_SWIFT_NAME(Street);
@property (class, readonly) NSString *CVNum NS_SWIFT_NAME(CVNum);
// ExtData elements
@property (class, readonly) NSString *AltMerchName NS_SWIFT_NAME(AltMerchName);
@property (class, readonly) NSString *AltMerchAddr NS_SWIFT_NAME(AltMerchAddr);
@property (class, readonly) NSString *AltMerchCity NS_SWIFT_NAME(AltMerchCity);
@property (class, readonly) NSString *AltMerchState NS_SWIFT_NAME(AltMerchState);
@property (class, readonly) NSString *AltMerchZip NS_SWIFT_NAME(AltMerchZip);
@property (class, readonly) NSString *AuthCode NS_SWIFT_NAME(AuthCode);
@property (class, readonly) NSString *Authentication NS_SWIFT_NAME(Authentication);
@property (class, readonly) NSString *BillPayment NS_SWIFT_NAME(BillPayment);
@property (class, readonly) NSString *BillToState NS_SWIFT_NAME(BillToState);
@property (class, readonly) NSString *BypassAvsCvv NS_SWIFT_NAME(BypassAvsCvv);
@property (class, readonly) NSString *City NS_SWIFT_NAME(City);
@property (class, readonly) NSString *Clinical_Amount NS_SWIFT_NAME(Clinical_Amount);
@property (class, readonly) NSString *ConvenienceAmt NS_SWIFT_NAME(ConvenienceAmt);
@property (class, readonly) NSString *CustCode NS_SWIFT_NAME(CustCode);
@property (class, readonly) NSString *CustomerID NS_SWIFT_NAME(CustomerID);
@property (class, readonly) NSString *CVPresence NS_SWIFT_NAME(CVPresence);
@property (class, readonly) NSString *Dental_Amount NS_SWIFT_NAME(Dental_Amount);
@property (class, readonly) NSString *EmvData NS_SWIFT_NAME(Dental_Amount);
@property (class, readonly) NSString *EntryMode NS_SWIFT_NAME(EntryMode);
@property (class, readonly) NSString *ExternalIP NS_SWIFT_NAME(ExternalIP);
@property (class, readonly) NSString *Force NS_SWIFT_NAME(Force);
@property (class, readonly) NSString *IIAS_Indicator NS_SWIFT_NAME(IIAS_Indicator);
@property (class, readonly) NSString *Level3Amt NS_SWIFT_NAME(Level3Amt);
@property (class, readonly) NSString *PartialIndicator NS_SWIFT_NAME(PartialIndicator);
@property (class, readonly) NSString *PONum NS_SWIFT_NAME(PONum);
@property (class, readonly) NSString *QHP_Amount NS_SWIFT_NAME(QHP_Amount);
@property (class, readonly) NSString *RegisterNum NS_SWIFT_NAME(RegisterNum);
@property (class, readonly) NSString *RX_Amount NS_SWIFT_NAME(RX_Amount);
@property (class, readonly) NSString *SequenceNum NS_SWIFT_NAME(SequenceNum);
@property (class, readonly) NSString *SequenceCount NS_SWIFT_NAME(SequenceCount);
@property (class, readonly) NSString *ServerID NS_SWIFT_NAME(ServerID);
@property (class, readonly) NSString *Target NS_SWIFT_NAME(Target);
@property (class, readonly) NSString *TaxAmt NS_SWIFT_NAME(TaxAmt);
@property (class, readonly) NSString *Timeout NS_SWIFT_NAME(Timeout);
@property (class, readonly) NSString *TipAmt NS_SWIFT_NAME(TipAmt);
@property (class, readonly) NSString *TrainingMode NS_SWIFT_NAME(TrainingMode);
@property (class, readonly) NSString *TransactionID NS_SWIFT_NAME(TransactionID);
@property (class, readonly) NSString *Vision_Amount NS_SWIFT_NAME(Vision_Amount);
// ExtData.P2PE elements
@property (class, readonly) NSString *HSMDevice NS_SWIFT_NAME(HSMDevice);
@property (class, readonly) NSString *TerminalType NS_SWIFT_NAME(TerminalType);
@property (class, readonly) NSString *EncryptionType NS_SWIFT_NAME(EncryptionType);
@property (class, readonly) NSString *KSN NS_SWIFT_NAME(KSN);
@property (class, readonly) NSString *DataBlock NS_SWIFT_NAME(DataBlock);
// ExtData.Presentation elements
@property (class, readonly) NSString *CardPresent NS_SWIFT_NAME(CardPresent);
// ExtData.CustomFields elements
//@property (class, readonly) NSString *CustomFields NS_SWIFT_NAME(CustomFields);
@end


@interface DCGCreditCardAdapter : NSObject
-(id)init:(DCGCreditCardRequest *)request;
-(void)populate:(DCGCardData *)cardData;
@end

#endif
