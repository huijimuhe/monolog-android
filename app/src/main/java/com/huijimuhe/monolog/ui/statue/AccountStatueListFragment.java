package com.huijimuhe.monolog.ui.statue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.EaseConstant;
import com.google.gson.Gson;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.api.StatueApi;
import com.huijimuhe.monolog.bean.StatueBean;
import com.huijimuhe.monolog.bean.StatueListResponseBean;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.db.StatueDao;
import com.huijimuhe.monolog.ui.base.AbstractListFragment;
import com.huijimuhe.monolog.ui.chat.ChatActivity;
import com.huijimuhe.monolog.ui.main.PhotoViewActivity;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

public class AccountStatueListFragment extends AbstractListFragment {
    private ArrayList<StatueBean> mDataset;
    private int mType;
    private UserBean mUser;

    public static AccountStatueListFragment newInstance(int type, UserBean user) {
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
        mDataset = new ArrayList<>();
        mType = getArguments().getInt(AccountStatueListActivity.RENDER_TYPE);
        mUser = getArguments().getParcelable(AccountStatueListActivity.ACCOUNT);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PublishActivity.REQUEST_PUBLISH) {
            if (resultCode == getActivity().RESULT_OK) {
                loadNewData();
            }
        }
    }

    @Override
    public AbstractRenderAdapter getAdapter() {
        StatueListAdapter adapter = new StatueListAdapter(mDataset, mType, mUser);
        //设置头
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_user_statue_header, null);
        TextView name = (TextView) header.findViewById(R.id.tv_name);
        ImageView avatar = (ImageView) header.findViewById(R.id.iv_avatar);
        name.setText(mUser.getName());
        AppContext.getInstance().loadImg(avatar, mUser.getAvatar());
        adapter.setHeaderView(header);
        return adapter;
    }

    @Override
    public void onItemNormalClick(View view, int postion) {

    }

    @Override
    public void onItemFunctionClick(View view, final int position, int type) {
        switch (type) {
            case AbstractRenderAdapter.BTN_CLICK_DELETE:
                new AlertDialog.Builder(getActivity())
                        .setTitle("删除")
                        .setMessage("确定要删除吗")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postDelete(mDataset.get(mAdapter.getRealPosition(position)).getId(), mAdapter.getRealPosition(position));
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
            case AbstractRenderAdapter.BTN_CLICK_CHAT:
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mUser.getId()));
                break;
            case AbstractRenderAdapter.BTN_CLICK_PUBLISH:
                startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
                break;
        }
    }

    @Override
    public void loadNewData() {
        mCurrentPage = 0;
        hideEmptyLayout();
        switch (mType) {
            case StatueListAdapter.RENDER_TYPE_MY_PROFILE:
                StatueApi.getMyStatues(String.valueOf(mCurrentPage), newDataHandler);
                break;
            case StatueListAdapter.RENDER_TYPE_USER_PROFILE:
                StatueApi.getUserStatues(mUser.getId(), String.valueOf(mCurrentPage), newDataHandler);
                break;
        }
    }

    @Override
    public void loadOldData() {
        switch (mType) {
            case StatueListAdapter.RENDER_TYPE_MY_PROFILE:
                StatueApi.getMyStatues(String.valueOf(mCurrentPage), oldDataHandler);
                break;
            case StatueListAdapter.RENDER_TYPE_USER_PROFILE:
                StatueApi.getUserStatues(mUser.getId(), String.valueOf(mCurrentPage), newDataHandler);
                break;
        }
    }

    TextHttpResponseHandlerEx oldDataHandler = new TextHttpResponseHandlerEx() {
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

    TextHttpResponseHandlerEx newDataHandler = new TextHttpResponseHandlerEx() {
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

    private void postDelete(String id, final int position) {
        if (!mDataset.get(position).getUser().getId().equals(mUser.getId())) {
            ToastUtils.show(getActivity(), "无操作权限");
            return;
        }
        StatueApi.postDeleteStatue(id, new TextHttpResponseHandlerEx() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(getActivity(), responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //refresh datasource
                mDataset.remove(position);
                mAdapter.notifyDataSetChanged();
                //更新计数
                UserBean owner2 = PrefService.getInstance(getActivity()).getUser();
                owner2.setStatue_count(owner2.getStatue_count() - 1);
                PrefService.getInstance(getActivity()).setUser(owner2);
            }
        });
    }

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
            UserBean owner = PrefService.getInstance(getActivity()).getUser();
            return StatueDao.getList(owner.getId(), String.valueOf(mPage));
        }
    }

}
