package com.huijimuhe.monolog.ui.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.AuthApi;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.db.ContactDao;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactListActivity extends AbstractActivity {
    private View mListBackground;
    private EaseContactListFragment contactListFragment;
   private DBTask task = new DBTask();

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                ContactListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("联系人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mListBackground = findViewById(R.id.list_empty_bg);

        //获取数据库联系人缓存
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(true);
        task=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_contact:
                startActivity(ChatListActivity.newIntent());
                break;
        }
        return true;
    }

    private void initUI(Map<String, EaseUser> users) {
        mListBackground.setVisibility(View.GONE);
        contactListFragment = new EaseContactListFragment();
        contactListFragment.setContactsMap(users);
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(ContactListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, contactListFragment).show(contactListFragment).commit();
    }

    private void asyncContacts() {
        AuthApi.contacts(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(ContactListActivity.this, "更新列表失败," + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                final ArrayList<UserBean> contacts = gson.fromJson(responseString, new TypeToken<ArrayList<UserBean>>() {
                }.getType());

                if (contacts.size() == 0) {
                    return;
                }

                //更新联系人列表
                Map<String, EaseUser> easeContacts = new HashMap<>();
                for (int i = 0; i < contacts.size(); i++) {
                    String name = contacts.get(i).getName();
                    String id = contacts.get(i).getId();
                    String avatar = contacts.get(i).getAvatar();
                    EaseUser u = new EaseUser(id);
                    u.setAvatar(avatar);
                    u.setNick(name);
                    easeContacts.put(id, u);
                }

                if(contactListFragment==null){
                    initUI(easeContacts);
                }else {
                    contactListFragment.setContactsMap(easeContacts);
                    contactListFragment.refresh();
                }

                //更新数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContactDao.asyncReplaceAll(contacts, AppContext.getInstance().getUser().getId());
                    }
                }).start();

            }
        });
    }

    private class DBTask extends AsyncTask<Void, Void, Map<String, EaseUser>> {

        private DBTask() {

        }

        @Override
        protected Map<String, EaseUser> doInBackground(Void... params) {
            Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
            try {
                ArrayList<EaseUser> users = ContactDao.getContacts(AppContext.getInstance().getUser().getId());
                synchronized(contacts){
                    for (int i = 0; i < users.size(); i++) {
                        contacts.put(users.get(i).getUsername(), users.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(Map<String, EaseUser> contacts) {
            if (contacts.size()!= 0) {
                initUI(contacts);
            }
            asyncContacts();
        }
    }
}
