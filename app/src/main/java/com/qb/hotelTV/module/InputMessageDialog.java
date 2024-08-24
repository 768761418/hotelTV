package com.qb.hotelTV.module;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;

import java.util.concurrent.CountDownLatch;

public class InputMessageDialog extends Dialog {
    private EditText inputServerAddress, inputRoomNumber, inputTenant;
    private Button inputSubmit;
    private CommonData commonData = CommonData.getInstance();
    private String serverAddress,roomNumber,tenant;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private Context context;
    private SubmitCallback submitCallback;
    private String TAG = "InputMessageDialog";
    String[] result;

    public interface SubmitCallback{
        void onSubmitCallBack(String inputsServerAddress,String inputRoomNumber,String inputTenant);
    }

    public InputMessageDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUI();
        initEvent();
    }

    public InputMessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUI();
        initEvent();
    }

    protected InputMessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        initUI();
        initEvent();
    }

//    初始化
    private void initUI(){
        setContentView(R.layout.dialog_input_message);
        inputServerAddress = findViewById(R.id.input_server_address);
        inputRoomNumber = findViewById(R.id.input_room_number);
        inputTenant = findViewById(R.id.input_tenant);
        inputSubmit = findViewById(R.id.input_submit);
//        初始化保存文档
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
    }
//    设置点击事件
    private void initEvent(){
        inputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverAddress = inputServerAddress.getText().toString();
                roomNumber = inputRoomNumber.getText().toString();
                tenant = inputTenant.getText().toString();
                // 检查并去掉末尾的斜杠
                if (serverAddress.endsWith("/")) {
                    serverAddress = serverAddress.substring(0, serverAddress.length() - 1);
                }

                CountDownLatch latch = new CountDownLatch(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                    登录获取token
//                    TODO 登录失败的处理
                        result =  BackstageHttp.getInstance().loginSystem(serverAddress,roomNumber,tenant);
                        latch.countDown();
                    }
                }).start();
//
                try {
                    latch.await(); // 等待请求完成
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onClick: " + result.length);
                if (result[0]!=null){
                    //                如果code为0
                    if (result[0].equals("0")){
                        //保存下来方便后续使用
                        sharedPreferencesUtils.saveToken(result[1]);
                        Log.d(TAG, "daying: " + result[1]);
                        // 保存服务器地址和房间号到 SharedPreferences中
                        sharedPreferencesUtils.saveInitData(serverAddress,roomNumber,tenant);
                        if (submitCallback != null){
                            submitCallback.onSubmitCallBack(serverAddress,roomNumber,tenant);
                        }
                        dismiss();
                    } else {
                        Toast.makeText(context,result[1],Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,"请检查输入的信息是否正确",Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

//    显示的时候焦点管理在服务器选择上
    @Override
    public void show() {
        super.show();
        inputServerAddress.requestFocus();
    }

//    开放接口等他操作完操作
    public void setSubmitCallback(SubmitCallback submitCallback){
        this.submitCallback = submitCallback;
    }

//    设置初始值
    public void setMessage(String serverAddress,String roomNumber,String tenant){
        inputServerAddress.setText(serverAddress);
        inputRoomNumber.setText(roomNumber);
        inputTenant.setText(tenant);
    }

}
