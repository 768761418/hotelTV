package com.qb.hotelTV.huibuTv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;

import com.qb.hotelTV.R;

import java.util.Date;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;
    /* 计算User有几秒没有动作的 */
    private long timePeriod;
    /* 静止超过N秒将自动进入屏保 */
    private float mHoldStillTime = 10;
    /*标识当前是否进入了屏保*/
    private boolean isAuthorized = false;
    /*时间间隔*/
    private long intervalAuthorized = 100;
    private long intervalKeypadSaver = 1000;
    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();
    PageAndListRowFragment pageAndListRowFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "进来绑定界面 ");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new PageAndListRowFragment(), "PageAndListRowFragment")
                    .commitNow();
            Log.d(TAG, "初始化1 ");
        }
        /* 初始取得User可触碰屏幕的时间 */
        lastUpdateTime = new Date(System.currentTimeMillis());
        mHandler02.postDelayed(mTask02, intervalAuthorized);
        Log.d(TAG, "初始化2 ");
        pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");
        Log.d(TAG, "初始化3 ");
    }
    /**
     * 计时线程
     */
    private Runnable mTask01 = new Runnable() {

        @Override
        public void run() {
            Date timeNow = new Date(System.currentTimeMillis());
            /* 计算User静止不动作的时间间距 */
            /**当前的系统时间 - 上次触摸屏幕的时间 = 静止不动的时间**/
            timePeriod = (long) timeNow.getTime() - (long) lastUpdateTime.getTime();
            /*将静止时间毫秒换算成秒*/
            float timePeriodSecond = ((float) timePeriod / 1000);

//            Log.d(TAG, "timePeriodSecond: " + timePeriodSecond);
//            Log.d(TAG, "isShowingHeaders: " + pageAndListRowFragment.isShowingHeaders());
            if(pageAndListRowFragment.isShowingHeaders()){
                if(timePeriodSecond > mHoldStillTime){
//                Toast.makeText(MainActivity.this, "10s未操作", Toast.LENGTH_SHORT).show();
//                模拟点击当前焦点的位置
                    pageAndListRowFragment.getView().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER));
                    pageAndListRowFragment.getView().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER));
//                打印pageAndListRowFragment.isShowingHeaders();
                    updateUserActionTime();
                }
            }else{
                updateUserActionTime();
            }

            /*反复调用自己进行检查*/
            mHandler01.postDelayed(mTask01, intervalKeypadSaver);
        }
    };
    /**
     * 持续屏保显示线程
     */
    private Runnable mTask02 = new Runnable() {

        @Override
        public void run() {
//
            if(isAuthorized){
                //        开始计时
                updateUserActionTime();
                mHandler01.postDelayed(mTask01, intervalKeypadSaver);
            }
            else {
//                获取pageAndListRowFragment实例
                PageAndListRowFragment pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");
                if(pageAndListRowFragment != null){
                    isAuthorized = pageAndListRowFragment.isAuthorized();
                }
                /*反复调用自己进行检查*/
                mHandler02.postDelayed(mTask02, intervalAuthorized);
            }
        }
    };


    /*用户有操作的时候不断重置静止时间和上次操作的时间*/
    public void updateUserActionTime() {
        Date timeNow = new Date(System.currentTimeMillis());
        lastUpdateTime.setTime(timeNow.getTime());
    }


////    获取activity返回的结果
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == -1){
////            Toast.makeText(this, "返回结果", Toast.LENGTH_SHORT).show();
////            获取pageAndListRowFragment实例
//            PageAndListRowFragment pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");
//            if(pageAndListRowFragment != null){
//                pageAndListRowFragment.setSelectedPosition(MyApplication.getChannelIndex(), false);
//                pageAndListRowFragment.showHeader();
//            }
//        }
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                pageAndListRowFragment.toggleChannel(false);
                Log.d(TAG, "onKeyDown: 上");
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                pageAndListRowFragment.toggleChannel(true);
                Log.d(TAG, "onKeyDown: 下");
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
//                pageAndListRowFragment.toggleHeader();
                Log.d(TAG, "onKeyDown: 左");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.d(TAG, "onKeyDown: 右");
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
//                pageAndListRowFragment.toggleHeader();
                Log.d(TAG, "onKeyDown: 确定");
                break;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                Log.d(TAG, "onKeyDown: 返回");
                return  true;

            case KeyEvent.KEYCODE_MENU:
                Log.d(TAG, "onKeyDown: 菜单");
                break;
        }
//        return true;
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        updateUserActionTime();
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        updateUserActionTime();
        if(isAuthorized){
            /*activity显示的时候启动线程*/
            mHandler01.postAtTime(mTask01, intervalKeypadSaver);
        }else
            mHandler02.postAtTime(mTask02, intervalAuthorized);
        super.onResume();
    }

    @Override
    protected void onPause() {
        /*activity不可见的时候取消线程*/
        mHandler01.removeCallbacks(mTask01);
        mHandler02.removeCallbacks(mTask02);
        super.onPause();
    }


}