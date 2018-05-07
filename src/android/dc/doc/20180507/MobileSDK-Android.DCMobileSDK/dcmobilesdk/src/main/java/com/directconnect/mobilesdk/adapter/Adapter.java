package com.directconnect.mobilesdk.adapter;

import com.directconnect.mobilesdk.transaction.Request;
import com.directconnect.mobilesdk.device.CardData;

/**
 * Adapter - Base adapter class used to populate a Request object from a CardData object
 *
 * Created by Francois Bergeon on 4/5/17.
 */
public abstract class Adapter<requestT extends Request> {
    protected requestT request;

    public Adapter(requestT request) {
        this.request = request;
    }

    // Methods to be overridden
    public abstract void populate(CardData cardData);
}
