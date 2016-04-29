package com.huijimuhe.monolog.domain;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huijimuhe.monolog.ui.base.OnBaiduLBSListener;

/**
 * Created by Huijimuhe on 2016/3/20.
 * This is a part of Monolog
 * enjoy
 */
public class BaiduService {
    protected static final String TAG = "BaiduService";
    /**
     * 单例
     */
    private static BaiduService instance = null;

    /**
     * application context
     */
    private Context mContext = null;

    /**
     * 百度lbs代理
     */
    private LocationClient mLocationClient = null;

    /**
     * 位置监听
     */
    private OnBaiduLBSListener mBaiduListener;

    private BaiduService() {

    }

    public synchronized static BaiduService getInstance() {
        if (instance == null) {
            instance = new BaiduService();
        }
        return instance;
    }

    public void init(Context context){
        mContext=context;
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
    }

    public void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        mLocationClient.setLocOption(option);
    }

    public void StartLocation(OnBaiduLBSListener l) {
        mBaiduListener = l;
        mBaiduListener.onStart();
        mLocationClient.start();
    }

    public void StopLocation() {
        mLocationClient.stop();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            int code = location.getLocType();
            if (code != 61 && code != 161 && code != 66 && code != 65) {
                mBaiduListener.onFailed(code);
            } else {
                mBaiduListener.onComplete(code, location.getLongitude(), location.getLatitude(), location.getAddrStr());
            }
            StopLocation();
        }
    }
}
