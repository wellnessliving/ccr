package com.directconnect.mobilesdk.adapter;

import android.os.StrictMode;
import android.util.Log;

import com.directconnect.mobilesdk.device.CardData;
import com.directconnect.mobilesdk.device.EncryptionParameters;
import com.directconnect.mobilesdk.transaction.CreditCardRequest;

import java.util.concurrent.TimeUnit;

/**
 * CreditCardAdapter - Adapter class used to populate a CreditCardRequest from a CardData object
 *
 * Created by Francois Bergeon on 4/5/17.
 */
public class CreditCardAdapter extends Adapter<CreditCardRequest> {
    private static final String TAG = "CreditCardAdapter";

    public CreditCardAdapter(CreditCardRequest request) {
        super(request);
    }

    /**
     * Populate CreditCardRequest fields from CardData object
     * @param cardData CardData object
     */
    @Override
    public void populate(CardData cardData) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        CardData.DataType dataType = cardData.getDataType();
       // P2PEModel p2pe = new P2PEModel();
        if (dataType == CardData.DataType.P2PE)
        {
//            request.setValue(CreditCardRequest.CardNum, cardData.getPAN());
//            request.setValue(CreditCardRequest.ExpDate, cardData.getExpDate());
            request.setValue(CreditCardRequest.NameOnCard, cardData.getCardholderName());
            request.setValue(CreditCardRequest.DataBlock, cardData.getDataBlock());
            request.setValue(CreditCardRequest.KSN, cardData.getKSN());
            EncryptionParameters encryptionParameters = cardData.getEncryptionParameters();
            request.setValue(CreditCardRequest.HSMDevice, encryptionParameters.getHSMDevice());
            request.setValue(CreditCardRequest.TerminalType, encryptionParameters.getTerminalType());
            request.setValue(CreditCardRequest.EncryptionType, encryptionParameters.getEncryptionType());

            //REstful
          /*  p2pe.setDataBlock(cardData.getDataBlock());
            p2pe.setEncryptionType(encryptionParameters.getEncryptionType());
            p2pe.setKeySerialNumber(cardData.getKSN());
            p2pe.setHardwareSecurityModuleDevice(encryptionParameters.getHSMDevice());
            p2pe.setTerminalType(encryptionParameters.getTerminalType());*/

        } else if (dataType == CardData.DataType.track1) {
            request.setValue(CreditCardRequest.NameOnCard, cardData.getCardholderName());
            request.setValue(CreditCardRequest.MagData, cardData.getTrack1());
            request.setValue(CreditCardRequest.CardNum, cardData.getPAN());
            request.setValue(CreditCardRequest.ExpDate, cardData.getExpDate());

        } else if (dataType == CardData.DataType.track2) {
            request.setValue(CreditCardRequest.NameOnCard, cardData.getCardholderName());
            request.setValue(CreditCardRequest.MagData, cardData.getTrack2());
            request.setValue(CreditCardRequest.CardNum, cardData.getPAN());
            request.setValue(CreditCardRequest.ExpDate, cardData.getExpDate());

        } else if (dataType == CardData.DataType.PAN) {
            request.setValue(CreditCardRequest.NameOnCard, cardData.getCardholderName());
            request.setValue(CreditCardRequest.CardNum, cardData.getPAN());
            request.setValue(CreditCardRequest.ExpDate, cardData.getExpDate());
        }
/*
        ApiClient clients = new ApiClient();
        clients.setConnectTimeout(30000); // connect timeout
        clients.setReadTimeout(30000);
        clients.setWriteTimeout(30000);
        clients.setBasePath(" https://gatewaystage.1directconnect.com/api");
        IdentityApi apiInstance = new IdentityApi(clients);
        Log.d(TAG, "Afteridentity");

        IdentityResponse results = new IdentityResponse();
        Log.d(TAG, "results");
        ApplicationUserModel applicationUser = new ApplicationUserModel(); // ApplicationUserModel | User Credentials to generate the token.
        applicationUser.setUsername("MobileSDK");
        applicationUser.setPassword("7TM5EAwC");
        applicationUser.setGatewayId("46");

        Log.d(TAG, "AFTER application: ");
        try {
            results = apiInstance.v1IdentityPost(applicationUser);
            System.out.println(results.getAccessToken());
            Log.d(TAG, "BEFORE application: ");
        } catch (ApiException e) {
            Log.d("FAILS", "populate: ");
            System.err.println("Exception when calling IdentityApi#v1IdentityPost");
            e.printStackTrace();
        }
        // have bearer now
        request.setValue(CreditCardRequest.AccessToken, results.getAccessToken());


        Log.d("BEFORE mdl", "populate: ");
        CreateTransactionsModel mdl = new CreateTransactionsModel();
        Log.d("BEFORE cardModel", "populate: ");
        CreateCreditCardTransactionsModel cardModel = new CreateCreditCardTransactionsModel();
        cardModel.setForce(CreateCreditCardTransactionsModel.ForceEnum.fromValue("true"));
        cardModel.setPurchaseOrderNumber("1210022");
        cardModel.setAmount("12.50");
        cardModel.setPointToPointEncryption(p2pe);
        mdl.setCreditCard(cardModel);
        Log.d("After sets", "populate: ");


        TransactionsApi tranInstance = new TransactionsApi();
        tranInstance.setApiClient(clients);
        Log.d("PASS tranInstance", "populate: ");
        try {
            TransactionResponse result =  tranInstance.v1TransactionsPost(results.getAccessToken(), mdl);
            System.out.println("APPROVAL CODE" + result);

        } catch (ApiException e) {
            System.err.println("Exception when calling TransactionsApi#v1TransactionsByPaymentReferenceNumberGet");
            e.printStackTrace();
        }*/

    }
}
