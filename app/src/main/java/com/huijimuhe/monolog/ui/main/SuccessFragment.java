package com.huijimuhe.monolog.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.EaseConstant;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractFragment;
import com.huijimuhe.monolog.ui.chat.ChatActivity;

public class SuccessFragment extends AbstractFragment implements View.OnClickListener {

    private ImageView mIvAvatar;
    private TextView mTvName;

    private UserBean mGuessedUser;

    public static SuccessFragment newInstance() {
        SuccessFragment fragment = new SuccessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGuessedUser = new UserBean();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("user", mGuessedUser);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_success, container, false);
        //user box
        mIvAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
        mTvName = (TextView) v.findViewById(R.id.tv_name); 
        //function
        v.findViewById(R.id.btn_chat).setOnClickListener(this);
        v.findViewById(R.id.btn_share).setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mGuessedUser = savedInstanceState.getParcelable("user");
            notifyChange(mGuessedUser);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                ((MainActivity) getActivity()).openShare();
                break;
            case R.id.btn_chat:
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mGuessedUser.getId()));
                break;
        }
    }

    public void notifyChange(UserBean user) {
        if (!isAdded()) {
            return;
        }
        mGuessedUser = user;
        mTvName.setText(user.getName());
        AppContext.getInstance().loadImg(mIvAvatar, user.getAvatar());

    }

    private String getUserName() {
        return  mGuessedUser.getId();
    }

}
