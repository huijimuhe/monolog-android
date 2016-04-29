package com.huijimuhe.monolog.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huijimuhe.monolog.R;

public class GuideFragment extends Fragment implements View.OnClickListener {
    public static final int TYPE_IMAGE_ONLY = 0;
    public static final int TYPE_WITH_BUTTON = 1;

    private int mBackground;
    private int mType;
    private Button mBtnJump;

    public static GuideFragment newInstance(int background, int type) {
        GuideFragment fragment = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt("background", background);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    public GuideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBackground = getArguments().getInt("background");
            mType = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_guide, container, false);
        //bind view
        mBtnJump = (Button) v.findViewById(R.id.btn_jump);
        //set view
        v.setBackgroundDrawable(getResources().getDrawable(mBackground));
        if (mType == TYPE_WITH_BUTTON) {
            mBtnJump.setVisibility(View.VISIBLE);
        } else {
            mBtnJump.setVisibility(View.GONE);
        }
        mBtnJump.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jump:
                startActivity(MainActivity.newIntent());
                getActivity().finish();
                break;
        }
    }
}
