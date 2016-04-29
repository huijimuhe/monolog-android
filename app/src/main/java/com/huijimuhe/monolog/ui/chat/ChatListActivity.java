package com.huijimuhe.monolog.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.util.EMLog;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.ui.statue.PublishActivity;

public class ChatListActivity extends AbstractActivity {
    private EaseConversationListFragment conversationListFragment;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                ChatListActivity.class);
        return intent;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EaseUI.getInstance().popActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("私信");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        registerEaseMobEventListener();

        //easemob chat
        EaseUI.getInstance().pushActivity(this);

        conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(ChatListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, conversationListFragment).show(conversationListFragment).commit();
    }

    protected void registerEaseMobEventListener() {
        final EaseUI easeUI=EaseUI.getInstance();

        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;

                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.d("ChatListActivity", "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }

                if(event.getEvent()== EMNotifierEvent.Event.EventNewMessage){
                    //更新私信未读提示
                    if(conversationListFragment!=null){
                            conversationListFragment.refresh();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_contact:
                startActivity(ContactListActivity.newIntent());
                break;
        }
        return true;
    }
}
