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

import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.presenter.statues.StatueContract;
import com.huijimuhe.monolog.ui.main.PhotoViewActivity;
import com.huijimuhe.monolog.ui.base.AbstractListFragment;


import java.util.ArrayList;

public class StatueListFragment extends AbstractListFragment {
    public static StatueListFragment newInstance(int type) {
        StatueListFragment fragment = new StatueListFragment();
        Bundle args = new Bundle();
        args.putInt(StatueListActivity.RENDER_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public StatueListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public AbstractRenderAdapter getAdapter() {
        StatueListAdapter adapter = new StatueListAdapter(new ArrayList<Statue>(), mListType);
        return adapter;
    }

    @Override
    public void onItemNormalClick(View view, int postion) {

    }

    @Override
    public void onItemFunctionClick(View view, final int position, int type) {
        final Statue data=(Statue) mAdapter.getItem(position);
        switch (type) {
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
            case AbstractRenderAdapter.BTN_CLICK_PROFILE:
                startActivity(AccountStatueListActivity.newIntent(StatueContract.RENDER_TYPE_OTHER_PROFILE,data.getUser()));
                break;
        }
    }

    @Override
    public void getListType() {
        mListType = getArguments().getInt(StatueListActivity.RENDER_TYPE);
        mAccount=null;
    }
}
