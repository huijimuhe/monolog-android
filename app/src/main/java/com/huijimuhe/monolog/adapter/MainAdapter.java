package com.huijimuhe.monolog.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.base.AbstractAdapter;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;

import java.util.ArrayList;

public class MainAdapter extends AbstractAdapter<MainAdapter.ViewHolder> {
    private ArrayList<Account> mDataset;
    private Activity mActivity;
    private  final int mGridWidth;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mAvatar;
        public View mBackground;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tv_name);
            mAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
            mBackground=v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, getLayoutPosition());
                }
            });
        }
    }

    public MainAdapter(ArrayList<Account> users, Activity activity) {
        mDataset = users;
        mActivity=activity;

        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = (width-20*2*3) / 3;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_main_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       //固定item尺寸
        ViewGroup.LayoutParams param=holder.mBackground.getLayoutParams();
        param.width=mGridWidth;
        holder.mBackground.setLayoutParams(param);

        //固定avatar尺寸
        ViewGroup.LayoutParams param2=holder.mAvatar.getLayoutParams();
        param2.width=mGridWidth-40;
        holder.mAvatar.setLayoutParams(param2);

        //数据绑定
        try {
            AppContext.getInstance().loadImg(holder.mAvatar, mDataset.get(position).getAvatar());//, mGridWidth);
            holder.mTextView.setText(mDataset.get(position).getName());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}