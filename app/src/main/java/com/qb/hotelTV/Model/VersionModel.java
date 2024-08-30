package com.qb.hotelTV.Model;

import androidx.dynamicanimation.animation.SpringAnimation;

public class VersionModel {

    private long code;
    private String apkName;
    private String apkUrl;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
