package com.huijimuhe.monolog.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.ui.base.AbstractFragment;
import com.huijimuhe.monolog.ui.statue.PublishActivity;

public class NoneFragment extends AbstractFragment implements View.OnClickListener {
    private static final String TAG = "youmi";
    public static NoneFragment newInstance() {
        NoneFragment fragment = new NoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_none, container, false);
        v.findViewById(R.id.btn_publish).setOnClickListener(this);
        v.findViewById(R.id.btn_share).setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                ((MainActivity) getActivity()).openShare();
                break;
            case R.id.btn_publish:
                startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
                break;
        }
    }
}
