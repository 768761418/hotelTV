package com.qb.hotelTV.Utils;

import android.view.KeyEvent;

public class KeyTypeUtils {


    public static boolean isOKButtonEvent( int currentKeyCode) {
        // 在这里检查是否是 OK 按钮事件
        // 你可以使用适当的逻辑来判断
        // 这里假设你有一个变量来保存当前按下的键码
        return currentKeyCode == KeyEvent.KEYCODE_ENTER || currentKeyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }
}
