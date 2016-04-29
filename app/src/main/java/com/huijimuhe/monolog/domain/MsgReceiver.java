package com.huijimuhe.monolog.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Huijimuhe on 2016/3/20.
 * This is a part of Monolog
 * enjoy
 */
public class MsgReceiver extends BroadcastReceiver {
    public static final String NEW_MSG_BROADCAST = "com.huijimuhe.monolog.newMsg";
   private newMsgReceiveListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onNewMsg();
    }

    public void setNewMsgReceiveListener(newMsgReceiveListener l){
        listener=l;
    }

    public interface newMsgReceiveListener {
        void onNewMsg();
    }
}
