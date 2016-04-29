package com.huijimuhe.monolog.adapter.render;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractRender;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractViewHolder;

/**
 * Created by Administrator on 2016/3/7.
 */
public class HeaderRender extends AbstractRender{

    private ViewHolder mHolder;

    public HeaderRender(View view,int type,AbstractRenderAdapter adapter) {
        //这个type是拿来判断头里面显示什么用的
        //自己的账号就是发独白
        //别人的就是私信
        this.mHolder=new ViewHolder(view,type,adapter);
    }

    @Override
    public void bindData(int position) {
    }

    @Override
    public AbstractViewHolder getReusableComponent() {
        return this.mHolder;
    }

    public class ViewHolder extends AbstractViewHolder{
        public TextView mTvName;
        public ImageView mIvAvatar;
        public View mBtnChat;
        public View mBtnPublish;

        public ViewHolder(View v,int type,final AbstractRenderAdapter adapter) {
            super(v);
            mTvName = (TextView) v.findViewById(R.id.tv_name);
            mIvAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
            mBtnChat =  v.findViewById(R.id.btn_chat);
            mBtnPublish=v.findViewById(R.id.btn_publish);

            if(type== StatueListAdapter.RENDER_TYPE_MY_PROFILE) {
                mBtnPublish.setVisibility(View.VISIBLE);
                mBtnChat.setVisibility(View.GONE);

                mBtnPublish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_PUBLISH);
                    }
                });
            }else{
                mBtnPublish.setVisibility(View.GONE);
                mBtnChat.setVisibility(View.VISIBLE);

                mBtnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.mOnItemFunctionClickListener.onClick(v, getLayoutPosition(), adapter.BTN_CLICK_CHAT);
                    }
                });
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mOnItemClickListener.onItemClick(v, getLayoutPosition());
                }
            });
        }
    }
}
