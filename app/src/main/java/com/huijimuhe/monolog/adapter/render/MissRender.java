package com.huijimuhe.monolog.adapter.render;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRender;
import com.huijimuhe.monolog.adapter.base.AbstractViewHolder;
import com.huijimuhe.monolog.bean.StatueBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.utils.NumUtils;
import com.huijimuhe.monolog.utils.TimeUtils;

public class MissRender extends AbstractRender{

    private ViewHolder mHolder;
    private AbstractRenderAdapter mAdapter;

    public MissRender(ViewGroup parent, AbstractRenderAdapter adapter) {
        this.mAdapter =adapter;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_statue_miss,parent,false);
        this.mHolder=new ViewHolder(v,adapter);
    }

    @Override
    public void bindData(int position) {
        StatueBean model=(StatueBean) mAdapter.getItem(position);
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
        public TextView mTvReport;

        public ViewHolder(View v,final AbstractRenderAdapter adapter) {
            super(v);
            mTvText = (TextView) v.findViewById(R.id.tv_text);
            mTvCreatedAt = (TextView) v.findViewById(R.id.tv_createdAt);
            mTvRightCount = (TextView) v.findViewById(R.id.tv_right_count);
            mTvMissCount = (TextView) v.findViewById(R.id.tv_miss_count);
            mIvStatue = (ImageView) v.findViewById(R.id.iv_img);
            mTvReport = (TextView) v.findViewById(R.id.tv_delete);
            mTvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_REPORT);
                }
            });
            mIvStatue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_IMG);
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mOnItemClickListener.onItemClick(v, getLayoutPosition());
                }
            });
        }
    }
}
