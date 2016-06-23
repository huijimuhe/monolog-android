package com.huijimuhe.monolog.ui.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.presenter.statues.StatueContract;
import com.huijimuhe.monolog.presenter.statues.StatuesPresenter;

import java.util.List;

public abstract class AbstractListFragment extends AbstractFragment implements StatueContract.View {

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mEmptyLayout;
    protected LinearLayoutManager mLayoutManager;
    protected AbstractRenderAdapter mAdapter;
    protected int lastVisibleItem = 0;
    protected StatuesPresenter mPresenter;
    protected int mListType;
    protected Account mAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //get list type
        getListType();

        //empty layout
        mEmptyLayout = (LinearLayout) v.findViewById(R.id.list_empty_bg);

        //swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(true,mListType,mAccount);
            }
        });

        //recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == mAdapter.getItemCount()) {
                    mPresenter.loadPage(false,mListType,mAccount);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        //presenter
        mPresenter = new StatuesPresenter(this);
        mPresenter.start();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set adapter
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AbstractRenderAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                onItemNormalClick(view, postion);
            }
        });
        mAdapter.setOnItemFunctionClickListener(new AbstractRenderAdapter.onItemFunctionClickListener() {
            @Override
            public void onClick(View view, int postion, int type) {
                onItemFunctionClick(view, postion, type);
            }
        });

        //loadGuess new data
        mPresenter.loadPage(true,mListType,mAccount);
    }

    @Override
    public void showLoading(final boolean active) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
                mSwipeRefreshLayout.setEnabled(!active);
            }
        });
    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showContent(List<Statue> statues, boolean isNew) {
        mEmptyLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (isNew) {
            mAdapter.replace(statues);
        } else {
            mAdapter.addAll(statues);
        }
    }

    @Override
    public void showEmpty() {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void removeItem(Statue item) {
        mAdapter.remove(item);
    }

    public abstract AbstractRenderAdapter getAdapter();
    public abstract void getListType();
    public abstract void onItemNormalClick(View view, int postion);
    public abstract void onItemFunctionClick(View view, int postion, int type);
}
