package com.huijimuhe.monolog.ui.main;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.MainAdapter;
import com.huijimuhe.monolog.adapter.base.AbstractAdapter;
import com.huijimuhe.monolog.bean.UserBean;

import java.util.ArrayList;

public class MainFragment extends Fragment implements AbstractAdapter.onItemClickListener {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MainAdapter mMainAdapter;

    private ArrayList<UserBean> mGuessUsers;
    private OnGuessListener mGuessListener;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mGuessUsers = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_gusses);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set adapter
        mMainAdapter = new MainAdapter(mGuessUsers, getActivity());
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.notifyDataSetChanged();
        mMainAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int postion) {
        getView().setEnabled(false);
        mGuessListener.onGuessClick(mGuessUsers.get(postion));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mGuessListener = (OnGuessListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGuessListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGuessListener = null;
    }

    public void notifyChange(ArrayList<UserBean> users) {
        ArrayList<UserBean> tempUsers = users;
        mGuessUsers.clear();
        mGuessUsers.addAll(users);
        mMainAdapter.notifyDataSetChanged();
        try{
            for (UserBean u :
                    users) {
                String strTemp = u.getId();
            }
            for (UserBean u :
                    mGuessUsers) {
                String strTemp = u.getId();
            }
            for (UserBean u :
                    tempUsers) {
                String strTemp = u.getId();
            }
        }catch (Exception e){
           MainActivity mmmain=((MainActivity) getActivity());
            e.printStackTrace();
        }
    }

    public interface OnGuessListener {
        void onGuessClick(UserBean user);
    }
}
