package com.qb.hotelTV.Handler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    Context context;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        Log.e("CrashHandler","報錯了");
        String logPath;

            logPath = context.getFilesDir()
                    .getPath().toString()
                    + File.separator
                    + "HotelTv_ERROR_LOG";

            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                FileWriter fw = new FileWriter(logPath + File.separator
                        + "crash.log", true);
                fw.write(new Date() + "err：\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = arg1.getStackTrace();
                fw.write(arg1.getMessage() + "\n");
                for (int i = 0; i < stackTrace.length; i++) {
                    fw.write("file:" + stackTrace[i].getFileName() + " class:"
                            + stackTrace[i].getClassName() + " method:"
                            + stackTrace[i].getMethodName() + " line:"
                            + stackTrace[i].getLineNumber() + "\n");
                }
                fw.write("\n");
                fw.close();

            } catch (IOException e) {
                Log.e("crash handler", "load file failed...", e.getCause());
                System.exit(0);
            }
        arg1.printStackTrace();
       System.exit(0);
    }

}
