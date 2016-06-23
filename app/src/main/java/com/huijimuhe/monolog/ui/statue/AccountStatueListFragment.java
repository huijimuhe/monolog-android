package com.huijimuhe.monolog.ui.statue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.EaseConstant;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractListFragment;
import com.huijimuhe.monolog.ui.chat.ChatActivity;
import com.huijimuhe.monolog.ui.main.PhotoViewActivity;


import java.util.ArrayList;

public class AccountStatueListFragment extends AbstractListFragment {


    public static AccountStatueListFragment newInstance(int type, Account user) {
        AccountStatueListFragment fragment = new AccountStatueListFragment();
        Bundle args = new Bundle();
        args.putInt(AccountStatueListActivity.RENDER_TYPE, type);
        args.putParcelable(AccountStatueListActivity.ACCOUNT, user);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountStatueListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PublishActivity.REQUEST_PUBLISH) {
            if (resultCode == getActivity().RESULT_OK) {
                // mPresenter.loadGuess(true);
            }
        }
    }

    @Override
    public void getListType() {
        mListType = getArguments().getInt(AccountStatueListActivity.RENDER_TYPE);
        mAccount = getArguments().getParcelable(AccountStatueListActivity.ACCOUNT);
    }

    @Override
    public AbstractRenderAdapter getAdapter() {
        StatueListAdapter adapter = new StatueListAdapter(new ArrayList<Statue>(), mListType, mAccount);
        //设置头
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_user_statue_header, null);
        TextView name = (TextView) header.findViewById(R.id.tv_name);
        ImageView avatar = (ImageView) header.findViewById(R.id.iv_avatar);
        name.setText(mAccount.getName());
        AppContext.getInstance().loadImg(avatar, mAccount.getAvatar());
        adapter.setHeaderView(header);
        return adapter;
    }

    @Override
    public void onItemNormalClick(View view, int postion) {

    }

    @Override
    public void onItemFunctionClick(View view, final int position, int type) {
        final Statue data=(Statue)mAdapter.getItem(position);
        switch (type) {
            case AbstractRenderAdapter.BTN_CLICK_DELETE:

                new AlertDialog.Builder(getActivity())
                        .setTitle("删除")
                        .setMessage("确定要删除吗")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.delete(data);
                                // postDelete(String.valueOf(mDataset.get(mAdapter.getRealPosition(position)).getId()), mAdapter.getRealPosition(position));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
            case AbstractRenderAdapter.BTN_CLICK_REPORT:
                new AlertDialog.Builder(getActivity())
                        .setTitle("举报")
                        .setMessage("该信息有违反国家法律法规的内容")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.report(data.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case AbstractRenderAdapter.BTN_CLICK_IMG:
                Intent intent = PhotoViewActivity.newIntent(data.getImg_path());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), view, PhotoViewActivity.TRANSIT_PIC);
                ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
                break;
            case AbstractRenderAdapter.BTN_CLICK_CHAT:
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mAccount.getId()));
                break;
            case AbstractRenderAdapter.BTN_CLICK_PUBLISH:
                startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
                break;
        }
    }
}
