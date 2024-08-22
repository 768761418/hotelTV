package com.qb.hotelTV.huibuTv;


import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qb.hotelTV.R;

import java.security.Key;
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
    private PageAndListRowFragment pageAndListRowFragment;
    private TextView channel;


    // 声明一个成员变量，用于保存用户输入的数字
    private StringBuilder inputBuffer = new StringBuilder();

    // 声明一个Handler，用于处理输入超时的情况
    private Handler inputTimeoutHandler = new Handler(Looper.getMainLooper());

    // 定义一个延迟任务，用于在超时后执行输入处理
    private Runnable inputTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            processInputBuffer();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        channel = findViewById(R.id.channel);
        int itemTV = getIntent().getIntExtra("itemTV",-1);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new PageAndListRowFragment(itemTV), "PageAndListRowFragment")
                    .commitNow();
            Log.d(TAG, "onCreate: PageAndListRowFragment");
        }
        /* 初始取得User可触碰屏幕的时间 */
        lastUpdateTime = new Date(System.currentTimeMillis());
        mHandler02.postDelayed(mTask02, intervalAuthorized);
        pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");



        Log.d(TAG, "itemTV: " + itemTV);
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
            Log.d(TAG, "isShowingHeaders: " + pageAndListRowFragment.isShowingHeaders());
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
            case KeyEvent.KEYCODE_SPACE:
                Log.d(TAG, "onKeyDown: 空格");
                // 添加空格键的处理逻辑
                break;
            case KeyEvent.KEYCODE_ENTER:
                Log.d(TAG, "onKeyDown: 回车");
                // 添加回车键的处理逻辑
                break;
            default:
                // 处理数字键
                if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) ||
                        (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9)) {
//                    pageAndListRowFragment.showTvEdit();
                    // 取消之前的超时任务
                    inputTimeoutHandler.removeCallbacks(inputTimeoutRunnable);

                    // 获取按键对应的数字并添加到缓冲区
                    int number = (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) ?
                            keyCode - KeyEvent.KEYCODE_0 : keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    // 根据需求，处理用户输入的 "001" 或 "012" 类似的情况
                    inputBuffer.append(number);
                    channel.setVisibility(View.VISIBLE);
                    channel.setText(inputBuffer.toString());
                    // 如果输入已经达到3个数字，立即处理
                    if (inputBuffer.length() == 3) {
                        processInputBuffer();
                    } else {
                        // 启动3秒超时任务
                        inputTimeoutHandler.postDelayed(inputTimeoutRunnable, 3000);
                    }

                    return true;
                }
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

    // 处理输入缓冲区的方法
    private void processInputBuffer() {
        // 移除输入缓冲区中数字前导的零
        String input = inputBuffer.toString().replaceFirst("^0+(?!$)", "");
        // 将输入缓冲区转换为整数，并执行频道切换
        int channelNumber = Integer.parseInt(input);
        pageAndListRowFragment.numberChangeChannel(channelNumber);
        // 清空缓冲区
        inputBuffer.setLength(0);
        channel.setVisibility(View.GONE);
    }

}