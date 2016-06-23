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
import com.huijimuhe.monolog.data.account.Account;

import java.util.ArrayList;
import java.util.List;

public class GuessFragment extends Fragment implements AbstractAdapter.onItemClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MainAdapter mMainAdapter;

    private ArrayList<Account> mGuessUsers;
    private GuessListener mGuessListener;

    public static GuessFragment newInstance() {
        GuessFragment fragment = new GuessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GuessFragment() {
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
            mGuessListener = (GuessListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GuessListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGuessListener = null;
    }

    public void notifyChange(List<Account> users) {
        mGuessUsers.clear();
        mGuessUsers.addAll(users);
        mMainAdapter.notifyDataSetChanged();
    }

    public interface GuessListener {
        void onGuessClick(Account user);
    }
}
