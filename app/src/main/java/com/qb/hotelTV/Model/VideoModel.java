package com.qb.hotelTV.Model;

public class VideoModel {
    private Integer id;
    private String streamUrl;
    private String streamName;
    private String description;

    public VideoModel(Integer id, String streamUrl, String streamName, String description) {
        this.id = id;
        this.streamUrl = streamUrl;
        this.streamName = streamName;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
