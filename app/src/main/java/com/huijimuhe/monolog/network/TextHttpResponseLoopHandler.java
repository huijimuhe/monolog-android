package com.huijimuhe.monolog.network;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Huijimuhe on 2016/3/21.
 * This is a part of Monolog
 * enjoy
 */
public abstract class TextHttpResponseLoopHandler extends AsyncHttpResponseHandler {
    private static final String LOG_TAG = "TextHttpRHEx";

    public TextHttpResponseLoopHandler(Looper looper) {
        super(looper);
        setCharset(DEFAULT_CHARSET);
    }

    /**
     * Creates new instance with given string encoding
     *
     * @param encoding String encoding, see {@link #setCharset(String)}
     */
    public TextHttpResponseLoopHandler(String encoding) {
        super();
        setCharset(encoding);
    }

    /**
     * Called when request fails
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     * @param throwable      throwable returned when processing request
     */
    public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

    /**
     * Called when request succeeds
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     */
    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public String getResponseString(byte[] stringBytes, String charset) {
        try {
            String toReturn = (stringBytes == null) ? null : new String(stringBytes, charset);
            if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (UnsupportedEncodingException e) {
            AsyncHttpClient.log.e(LOG_TAG, "Encoding response into string failed", e);
            return null;
        }
    }
}
