package com.huijimuhe.monolog.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public abstract class  AbstractAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected onItemClickListener mOnItemClickListener;
    protected onItemFunctionClickListener mOnItemFunctionClickListener;

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOnItemClickListener(onItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemFunctionClickListener(onItemFunctionClickListener l) {
        mOnItemFunctionClickListener = l;
    }

    public interface onItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface onItemFunctionClickListener {
        public void onClick(View view, int position, int type);
    }
}
