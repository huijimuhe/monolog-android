package com.huijimuhe.monolog.ui.statue;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.api.StatueApi;
import com.huijimuhe.monolog.bean.StatueBean;
import com.huijimuhe.monolog.bean.StatueListResponseBean;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.db.StatueDao;
import com.huijimuhe.monolog.ui.main.PhotoViewActivity;
import com.huijimuhe.monolog.ui.base.AbstractListFragment;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

public class StatueListFragment  extends AbstractListFragment {
    private ArrayList<StatueBean> mDataset;
    private int mType;

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
        mDataset = new ArrayList<>();
        mType =getArguments().getInt(StatueListActivity.RENDER_TYPE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public AbstractRenderAdapter getAdapter() {
        StatueListAdapter adapter = new StatueListAdapter(mDataset, mType);
        return adapter;
    }

    @Override
    public void onItemNormalClick(View view, int postion) {

    }

    @Override
    public void onItemFunctionClick(View view, final int position, int type) {
        switch (type) {
            case AbstractRenderAdapter.BTN_CLICK_REPORT:
                new AlertDialog.Builder(getActivity())
                        .setTitle("举报")
                        .setMessage("该信息有违反国家法律法规的内容")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postReport(mDataset.get(mAdapter.getRealPosition(position)).getId());
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
                startActivity(PhotoViewActivity.newIntent(mDataset.get(mAdapter.getRealPosition(position)).getImg_path()));
                break;
            case AbstractRenderAdapter.BTN_CLICK_PROFILE:
                startActivity(AccountStatueListActivity.newIntent(StatueListAdapter.RENDER_TYPE_USER_PROFILE, mDataset.get(mAdapter.getRealPosition(position)).getUser()));
                break;
        }
    }

    @Override
    public void loadNewData() {
        mCurrentPage = 0;
        hideEmptyLayout();
        switch (mType){
            case StatueListAdapter.RENDER_TYPE_MISS:
                StatueApi.getMyGuess(StatueApi.GUESS_MISS,String.valueOf(mCurrentPage), newDataHandler);
                break;
            case StatueListAdapter.RENDER_TYPE_RIGHT:
                StatueApi.getMyGuess(StatueApi.GUESS_RIGHT,String.valueOf(mCurrentPage), newDataHandler);
                break;
        }
    }

    @Override
    public void loadOldData() {
        switch (mType){
            case StatueListAdapter.RENDER_TYPE_MY_PROFILE:
                StatueApi.getMyStatues(String.valueOf(mCurrentPage), oldDataHandler);
                break;
            case StatueListAdapter.RENDER_TYPE_MISS:
                StatueApi.getMyGuess(StatueApi.GUESS_MISS, String.valueOf(mCurrentPage), oldDataHandler);
                break;
            case StatueListAdapter.RENDER_TYPE_RIGHT:
                StatueApi.getMyGuess(StatueApi.GUESS_RIGHT, String.valueOf(mCurrentPage), oldDataHandler);
                break;
        }
    }

    TextHttpResponseHandlerEx oldDataHandler= new TextHttpResponseHandlerEx() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Gson gson = new Gson();
            StatueListResponseBean response = gson.fromJson(responseString, StatueListResponseBean.class);
            //refresh datasource
            if (response != null && response.getItems() != null) {
                if (response.getItems().size() != 0) {
                    mDataset.addAll(mDataset.size(), response.getItems());
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage++;
                }
            }
        }
    };

    TextHttpResponseHandlerEx newDataHandler =new TextHttpResponseHandlerEx(){
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            showEmptyLayout();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Gson gson = new Gson();
            StatueListResponseBean response = gson.fromJson(responseString, StatueListResponseBean.class);
            //refresh datasource
            if (response != null && response.getItems() != null) {
                if (response.getItems().size() != 0) {
                    mDataset.clear();
                    mDataset.addAll(response.getItems());
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage++;
                }
            }
        }
    };

    private void postReport(final String id) {
        StatueApi.postReport(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(getActivity(), "网络错误请重试");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ToastUtils.show(getActivity(), "举报成功,我们将即时处理!\n谢谢您的支持");
            }
        });
    }

    private class readDbCacheTask extends AsyncTask<Void, ArrayList<StatueBean>, ArrayList<StatueBean>> {
        int mPage;

        public readDbCacheTask(int page) {
            mPage = page;
        }

        @Override
        protected void onPostExecute(ArrayList<StatueBean> statueBeans) {
            super.onPostExecute(statueBeans);
        }

        @Override
        protected ArrayList<StatueBean> doInBackground(Void... params) {
            UserBean owner= PrefService.getInstance(getActivity()).getUser();
            return StatueDao.getList(owner.getId(), String.valueOf(mPage));
        }
    }

}
