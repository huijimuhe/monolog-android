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

import org.apache.http.Header;

public abstract class AbstractListFragment extends AbstractFragment
        implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mEmptyLayout;
    protected LinearLayoutManager mLayoutManager;
    protected AbstractRenderAdapter mAdapter;
    protected int mCurrentPage = 0;
    protected int lastVisibleItem = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //empty layout
        mEmptyLayout=(LinearLayout)v.findViewById(R.id.list_empty_bg);

        //swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem  == mAdapter.getItemCount()) {
                    loadOldData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
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
        //get data
        setSwipeRefreshLoading();
        loadNewData();
        mState = STATE_REFRESH;
    }

    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        loadNewData();
    }


    protected void setSwipeRefreshLoading() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mSwipeRefreshLayout.setEnabled(false);
                }
            });
        }
    }

    protected void setSwipeRefreshNormal() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                }
            });
        }
    }

    protected void showEmptyLayout(){
        mEmptyLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected void hideEmptyLayout(){
         mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
    }
    public abstract AbstractRenderAdapter getAdapter();

    public abstract void loadNewData();

    public abstract void loadOldData();

    public abstract void onItemNormalClick(View view, int postion);

    public abstract void onItemFunctionClick(View view, int postion, int type);

    public class TextHttpResponseHandlerEx extends com.loopj.android.http.TextHttpResponseHandler {
        @Override
        public void onStart() {
            super.onStart();
            mState = STATE_REFRESH;
            setSwipeRefreshLoading();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mState = STATE_NORMAL;
            setSwipeRefreshNormal();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

        }
    }
}
