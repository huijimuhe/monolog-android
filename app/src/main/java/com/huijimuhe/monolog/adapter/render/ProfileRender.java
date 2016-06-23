package com.huijimuhe.monolog.adapter.render;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRender;
import com.huijimuhe.monolog.adapter.base.AbstractViewHolder;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.presenter.statues.StatueContract;
import com.huijimuhe.monolog.utils.NumUtils;
import com.huijimuhe.monolog.utils.TimeUtils;

/**
 * Created by Administrator on 2016/2/21.
 * 我的资料
 */
public class ProfileRender extends AbstractRender{

    private ViewHolder mHolder;
    private AbstractRenderAdapter mAdapter;
    private Account mUser;
    private int mType;

    public ProfileRender() {

    }

    public ProfileRender(ViewGroup parent, AbstractRenderAdapter adapter, int type, Account user) {
        this.mAdapter =adapter;
        this.mType =type;
        this.mUser=user;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_user_statue,parent,false);
        this.mHolder=new ViewHolder(v,mType,adapter);
    }


    @Override
    public void bindData(int position) {
        Statue model=(Statue)mAdapter.getItem(position);
        mHolder.mTvText.setText(model.getText());
        mHolder.mTvCreatedAt.setText(TimeUtils.getTime(model.getCreated_at()));
        mHolder.mTvRightCount.setText(NumUtils.converNumToString(model.getRight_count()));
        mHolder.mTvMissCount.setText(NumUtils.converNumToString(model.getMiss_count()));
        AppContext.getInstance().loadImg(mHolder.mIvStatue, model.getImg_path());
    }

    @Override
    public AbstractViewHolder getReusableComponent() {
        return this.mHolder;
    }

    public class ViewHolder extends AbstractViewHolder{
        public TextView mTvText;
        public ImageView mIvStatue;
        public TextView mTvCreatedAt;
        public TextView mTvRightCount;
        public TextView mTvMissCount;
        public TextView mTvDelete;
        public TextView mTvReport;
        public ViewHolder(View v, int type,final AbstractRenderAdapter adapter) {
            super(v);
            mTvText = (TextView) v.findViewById(R.id.tv_text);
            mTvCreatedAt = (TextView) v.findViewById(R.id.tv_createdAt);
            mTvRightCount = (TextView) v.findViewById(R.id.tv_right_count);
            mTvMissCount = (TextView) v.findViewById(R.id.tv_miss_count);
            mIvStatue = (ImageView) v.findViewById(R.id.iv_img);
            mTvReport = (TextView) v.findViewById(R.id.tv_report);
            mTvDelete = (TextView) v.findViewById(R.id.tv_delete);
           if(type== StatueContract.RENDER_TYPE_OTHER_PROFILE) {
               mTvDelete.setVisibility(View.GONE);
               mTvReport.setVisibility(View.VISIBLE);
               mTvReport.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_REPORT);
                   }
               });
           }else {
               mTvReport.setVisibility(View.GONE);
               mTvDelete.setVisibility(View.VISIBLE);
               mTvDelete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_DELETE);
                   }
               });
           }
            mIvStatue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(),adapter.BTN_CLICK_IMG);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mOnItemClickListener.onItemClick(v,getLayoutPosition());
                }
            });
        }


    }
}
