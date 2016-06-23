package com.huijimuhe.monolog.adapter.base;

import android.annotation.TargetApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.huijimuhe.monolog.adapter.render.HeaderRender;

import java.util.List;

public abstract class AbstractRenderAdapter<T> extends RecyclerView.Adapter<AbstractViewHolder> {

    public static final int BTN_CLICK_DELETE = 0;
    public static final int BTN_CLICK_REPORT = 1;
    public static final int BTN_CLICK_CHAT = 2;
    public static final int BTN_CLICK_IMG = 4;
    public static final int BTN_CLICK_PROFILE = 5;
    public static final int BTN_CLICK_PUBLISH = 6;

    public static final int RENDER_TYPE_HEADER = 0;
    public static final int RENDER_TYPE_FOOTER = 1;

    public int mViewType;
    public List<T> mDataset;

    public onItemClickListener mOnItemClickListener;
    public onItemFunctionClickListener mOnItemFunctionClickListener;

    protected View mHeaderView;
    protected View mFooterView;

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeaderView()) {
            return RENDER_TYPE_HEADER;
        }
        if(position==getItemCount()-1 && hasFooterView()){
            return RENDER_TYPE_FOOTER;
        }
        return mViewType;
    }

    @TargetApi(4)
    public AbstractViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case RENDER_TYPE_HEADER: {
                HeaderRender head = new HeaderRender(mHeaderView, mViewType, this);
                AbstractViewHolder headHolder=head.getReusableComponent();
                headHolder.itemView.setTag(android.support.design.R.id.list_item,head);
                return headHolder;
            }
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDataset.size() : mDataset.size() + 1;
    }

    public void replace(List<T> data){
        this.mDataset.clear();
        this.mDataset=data;
        notifyDataSetChanged();
    }

    public void remove(T data){

        int position=mDataset.indexOf(data);
        this.mDataset.remove(data);
        notifyItemRemoved(hasHeaderView() && position!=0 ? position +1: position);
    }

    public void addAll(List<T> data){
        this.mDataset.addAll(data);
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return this.mDataset;
    }

    public int getRealPosition(int position) {
        return hasHeaderView() && position!=0 ? position - 1: position ;
    }

    public T getItem(int position) {
        int r=getRealPosition(position);
        return mDataset.get(getRealPosition(position));
    }

    public void setOnItemClickListener(onItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemFunctionClickListener(onItemFunctionClickListener l) {
        mOnItemFunctionClickListener = l;
    }

    public interface onItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public interface onItemFunctionClickListener {
        public void onClick(View view, int postion, int type);
    }

    public boolean hasHeaderView(){
        return mHeaderView!=null;
    }
    public void setHeaderView(View view) {
        mHeaderView = view;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public boolean hasFooterView(){
        return mFooterView!=null;
    }
    public void setFooterView(View view) {
        mFooterView = view;
    }

    public View getFooterView() {
        return mFooterView;
    }
}
