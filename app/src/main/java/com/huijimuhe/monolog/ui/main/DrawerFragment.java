package com.huijimuhe.monolog.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.DrawerListAdapter;
import com.huijimuhe.monolog.network.AuthApi;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.presenter.statues.StatueContract;
import com.huijimuhe.monolog.ui.auth.SignInActivity;
import com.huijimuhe.monolog.ui.chat.ChatListActivity;
import com.huijimuhe.monolog.ui.setting.SettingActivity;
import com.huijimuhe.monolog.ui.statue.AccountStatueListActivity;
import com.huijimuhe.monolog.ui.statue.StatueListActivity;
import com.huijimuhe.monolog.utils.NumUtils;
import com.huijimuhe.monolog.domain.PrefManager;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class DrawerFragment extends Fragment {
    private NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvMenu;
    private View headerView;
    private AppContext app = AppContext.getInstance();

    public DrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_drawer, container, false);
        initMenu(inflater, v);
        return v;
    }

    private void initMenu(LayoutInflater inflater, View v) {
        lvMenu = (ListView) v.findViewById(R.id.id_lv_left_menu);
        headerView = inflater.inflate(R.layout.listitem_drawer_header, lvMenu, false);
        initHeader(headerView);
        lvMenu.addHeaderView(headerView, lvMenu, false);
        lvMenu.setAdapter(new DrawerListAdapter(getActivity()));
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             *
             *      Arrays.asList(
             new LvMenuItem(),
             new LvMenuItem(R.drawable.ic_action_email, "私信"),
             new LvMenuItem(),
             new LvMenuItem(R.drawable.ic_action_settings, "修改资料"),
             new LvMenuItem(),
             new LvMenuItem(R.drawable.ic_action_favorite, "推荐打分"),
             new LvMenuItem(),
             new LvMenuItem(R.drawable.ic_action_discard, "关于独白"),
             new LvMenuItem(),
             new LvMenuItem(R.drawable.ic_action_undo, "用户退出"),
             new LvMenuItem()
             ));
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 2:
                        //私信
                        startActivity(ChatListActivity.newIntent());
                        break;
                    case 4:
                        //修改资料
                        startActivity(SettingActivity.newIntent());
                        break;
                    case 6:
                        //关于独白
                        //((MainActivity)getActivity()).openShare();
                        break;
                }
                hideDrawer();
            }
        });
    }

    private void postSignOut() {
        PrefManager.getInstance().cleanUser();
        startActivity(SignInActivity.newIntent());
        AuthApi.signOut(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
            }
        });
        getActivity().finish();
    }

    private void initHeader(View header) {
        LinearLayout layout_profile = (LinearLayout) header.findViewById(R.id.layout_profile);
        LinearLayout layout_unSigned = (LinearLayout) header.findViewById(R.id.layout_unsigned);
        if (AppContext.getInstance().getToken()==null) {
            layout_profile.setVisibility(View.GONE);
            layout_unSigned.setVisibility(View.VISIBLE);
            //find element
            Button btnSignIn = (Button) header.findViewById(R.id.btn_signin);
            Button btnSignUp = (Button) header.findViewById(R.id.btn_signup);
            //bind listener
            btnSignIn.setOnClickListener(clickListener);
            btnSignUp.setOnClickListener(clickListener);
        } else {
            layout_profile.setVisibility(View.VISIBLE);
            layout_unSigned.setVisibility(View.GONE);

            //find element
            TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
            TextView tv_gender = (TextView) header.findViewById(R.id.tv_gender);
            TextView tv_statueNm = (TextView) header.findViewById(R.id.tv_statue_num);
            TextView tv_rightNm = (TextView) header.findViewById(R.id.tv_right_num);
            TextView tv_missNm = (TextView) header.findViewById(R.id.tv_miss_num);
            ImageView iv_avatar = (ImageView) header.findViewById(R.id.iv_avatar);
            View btn_statue=  header.findViewById(R.id.layout_statue_num);
            View btn_right=  header.findViewById(R.id.layout_right_num);
            View btn_miss=  header.findViewById(R.id.layout_miss_num);

            //bind data
            Account owner= PrefManager.getInstance().getUser();
            tv_name.setText(owner.getName());
            tv_gender.setText(owner.getGender().equals("m") ? "男" : "女");
            tv_statueNm.setText(NumUtils.converNumToString(String.valueOf(owner.getStatue_count())));
            tv_rightNm.setText(NumUtils.converNumToString(String.valueOf(owner.getRight_count())));
            tv_missNm.setText(NumUtils.converNumToString(String.valueOf(owner.getMiss_count())));
            AppContext.getInstance().loadImg(iv_avatar,owner.getAvatar());

            //bind listener
            btn_statue.setOnClickListener(clickListener);
            btn_right.setOnClickListener(clickListener);
            btn_miss.setOnClickListener(clickListener);
        }
    }

    public void updateNums() {
        if (getActivity() == null)
            return;

        //find element
        TextView tv_name = (TextView) headerView.findViewById(R.id.tv_name);
        TextView tv_gender = (TextView) headerView.findViewById(R.id.tv_gender);
        TextView tv_statueNm = (TextView) headerView.findViewById(R.id.tv_statue_num);
        TextView tv_rightNm = (TextView) headerView.findViewById(R.id.tv_right_num);
        TextView tv_missNm = (TextView) headerView.findViewById(R.id.tv_miss_num);
        ImageView iv_avatar = (ImageView) headerView.findViewById(R.id.iv_avatar);

        //bind data
        Account owner= PrefManager.getInstance().getUser();
        tv_name.setText(owner.getName());
        tv_gender.setText(owner.getGender().equals("m") ? "男" : "女");
        tv_statueNm.setText(NumUtils.converNumToString(String.valueOf(owner.getStatue_count())));
        tv_rightNm.setText(NumUtils.converNumToString(String.valueOf(owner.getRight_count())));
        tv_missNm.setText(NumUtils.converNumToString(String.valueOf(owner.getMiss_count())));
        AppContext.getInstance().loadImg(iv_avatar,owner.getAvatar());

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_statue_num:
                    Account user= PrefManager.getInstance().getUser();
                    startActivity(AccountStatueListActivity.newIntent(StatueContract.RENDER_TYPE_MY_PROFILE,user));
                    break;
                case R.id.layout_miss_num:
                    startActivity(StatueListActivity.newIntent(StatueContract.RENDER_TYPE_MISS));
                    break;
                case R.id.layout_right_num:
                    startActivity(StatueListActivity.newIntent(StatueContract.RENDER_TYPE_RIGHT));
                    break;
                case R.id.btn_signin:
                    startActivity(SignInActivity.newIntent());
                    getActivity().finish();
                    break;
            }
        }
    };

    public void setUp(int fragmentId, Toolbar toolbar, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
               R.string.crop__done,  /* "open drawer" description for accessibility */
               R.string.crop__cancel /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                updateNums();
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };
        //初始化的时候打开抽屉菜单

//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void hideDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
//        mDrawerLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mDrawerToggle.syncState();
//            }
//        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface NavigationDrawerCallbacks {
        void onDrawerItemSelected(int position);
    }
}
