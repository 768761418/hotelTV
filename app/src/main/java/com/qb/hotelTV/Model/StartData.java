package com.qb.hotelTV.Model;

public class StartData {
//    图片或视频地址
    private String url;
//    是否隐藏，1开启 0隐藏
    private int open;
//    类型：1视频 2图片
    private int type;

//    播放时间：单位秒
    private long second;
//    是否渲染html ：  0隐藏 1渲染
    private int openTxt;
//    渲染的html内容
    private String content;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public int getOpenTxt() {
        return openTxt;
    }

    public void setOpenTxt(int openTxt) {
        this.openTxt = openTxt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
