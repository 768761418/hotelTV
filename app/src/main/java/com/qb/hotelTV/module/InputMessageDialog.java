package com.qb.hotelTV.module;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;

public class InputMessageDialog extends Dialog {
    private EditText inputServerAddress, inputRoomNumber, inputTenant;
    private Button inputSubmit;
    private CommonData commonData = CommonData.getInstance();
    private String serverAddress,roomNumber,tenant;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private Context context;
    private SubmitCallback submitCallback;

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
//                将数据保存到内存共享，让其他Activity也可用
                commonData.setData(serverAddress,tenant,roomNumber);
                // 保存服务器地址和房间号到 SharedPreferences中
                sharedPreferencesUtils.saveInitData(serverAddress,roomNumber,tenant);
//                dismiss();
                if (submitCallback != null){
                    submitCallback.onSubmitCallBack(serverAddress,roomNumber,tenant);
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
