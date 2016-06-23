package com.huijimuhe.monolog.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.ui.base.AbstractFragment;
import com.huijimuhe.monolog.ui.statue.PublishActivity;

public class FailedFragment extends AbstractFragment implements View.OnClickListener {


    public static FailedFragment newInstance() {
        FailedFragment fragment = new FailedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FailedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_failed, container, false);
        v.findViewById(R.id.btn_publish).setOnClickListener(this);
        v.findViewById(R.id.btn_share).setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                //((MainActivity) getActivity()).openShare();
                break;
            case R.id.btn_publish:
                startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

}
