package com.directconnect.mobilesdk.transaction;

import android.os.AsyncTask;

/**
 * Asynchronous task to process a Request
 *
 * Created by Francois Bergeon on 2/24/17.
 */
public class AsyncRequest<requestT extends SendableRequest<responseT>, responseT extends Response> extends AsyncTask<requestT, responseT, responseT> {
    /**
     * Callback interface
     */
    public interface Listener<responseT extends Response> { void onProcessed(responseT response); }

    /**
     * Constructor initializes callback class
     */
    private Listener<responseT> listener = null;
    public AsyncRequest(Listener<responseT> listener) {
        this.listener = listener;
    }

    /**
     * Invoke requestT.process()
     * @param params requestT object
     * @return responseT object
     */
    @SuppressWarnings("unchecked")
	@Override
    protected responseT doInBackground(requestT... params) {
        requestT req = params[0];
        //noinspection unchecked
        return req.process();
    }

    /**
     * Send responseT object to the listener
     * @param result responseT
     */
    @Override
    protected void onPostExecute(responseT result) {
        //noinspection unchecked
        if (listener != null)
           listener.onProcessed(result);
    }

	@Override
	protected void onPreExecute() {}

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(responseT... progress) {}
}
