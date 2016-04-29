package com.huijimuhe.monolog.adapter;

import android.annotation.TargetApi;
import android.view.ViewGroup;

import com.huijimuhe.monolog.adapter.base.AbstractRender;
import com.huijimuhe.monolog.adapter.base.AbstractRenderAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractViewHolder;
import com.huijimuhe.monolog.adapter.render.HeaderRender;
import com.huijimuhe.monolog.adapter.render.ProfileRender;
import com.huijimuhe.monolog.adapter.render.RightRender;
import com.huijimuhe.monolog.adapter.render.MissRender;
import com.huijimuhe.monolog.bean.StatueBean;
import com.huijimuhe.monolog.bean.UserBean;

import java.util.ArrayList;

public class StatueListAdapter extends AbstractRenderAdapter<StatueBean> {
    public static final int RENDER_TYPE_MISS = 1;
    public static final int RENDER_TYPE_RIGHT = 2;
    public static final int RENDER_TYPE_MY_PROFILE = 3;
    public static final int RENDER_TYPE_USER_PROFILE = 4;

    private UserBean mUser;

    public StatueListAdapter(ArrayList<StatueBean> statues, int type) {
        this.mDataset = statues;
        this.mViewType = type;
    }

    public StatueListAdapter(ArrayList<StatueBean> statues, int type, UserBean user) {
        this.mDataset = statues;
        this.mViewType = type;
        this.mUser = user;
    }

    @TargetApi(4)
    public AbstractViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //header view 的判断
        AbstractViewHolder holder = super.onCreateViewHolder(viewGroup, viewType);
        if (holder != null) {
            return holder;
        }

        switch (viewType) {
            case RENDER_TYPE_RIGHT:
                RightRender right = new RightRender(viewGroup, this);
                AbstractViewHolder rightHolder=right.getReusableComponent();
                rightHolder.itemView.setTag(android.support.design.R.id.list_item,right);
                return rightHolder;
            case RENDER_TYPE_MISS:
                MissRender miss = new MissRender(viewGroup, this);
                AbstractViewHolder missHolder=miss.getReusableComponent();
                missHolder.itemView.setTag(android.support.design.R.id.list_item,miss);
                return missHolder;

            case RENDER_TYPE_MY_PROFILE:
                ProfileRender profile =new ProfileRender(viewGroup, this, RENDER_TYPE_MY_PROFILE, mUser);
                AbstractViewHolder profileHolder=profile.getReusableComponent();
                profileHolder.itemView.setTag(android.support.design.R.id.list_item,profile);
                return profileHolder;

            case RENDER_TYPE_USER_PROFILE:
                ProfileRender profile2 =new ProfileRender(viewGroup, this, RENDER_TYPE_USER_PROFILE, mUser);
                AbstractViewHolder profileHolder2=profile2.getReusableComponent();
                profileHolder2.itemView.setTag(android.support.design.R.id.list_item,profile2);
                return profileHolder2;
            default:
                return null;
        }
    }

    @TargetApi(4)
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        AbstractRender render=(AbstractRender) holder.itemView.getTag(android.support.design.R.id.list_item);
        render.bindData(position);
    }

}
