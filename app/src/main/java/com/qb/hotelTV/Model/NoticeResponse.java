package com.qb.hotelTV.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoticeResponse {

    @SerializedName("code")
    private Integer code;
    @SerializedName("data")
    private DataDTO data;
    @SerializedName("msg")
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataDTO {
        @SerializedName("total")
        private Integer total;
        @SerializedName("list")
        private List<ListDTO> list;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public static class ListDTO {
            @SerializedName("id")
            private Integer id;
            @SerializedName("title")
            private String title;
            @SerializedName("type")
            private Integer type;
            @SerializedName("content")
            private String content;
            @SerializedName("status")
            private Integer status;
            @SerializedName("createTime")
            private Long createTime;
            @SerializedName("textColor")
            private Object textColor;
            @SerializedName("locationType")
            private String locationType;
            @SerializedName("configData")
            private ConfigDataDTO configData;
            @SerializedName("startTime")
            private Integer startTime;
            @SerializedName("endTime")
            private Object endTime;
            @SerializedName("userIds")
            private List<Integer> userIds;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public Long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Long createTime) {
                this.createTime = createTime;
            }

            public Object getTextColor() {
                return textColor;
            }

            public void setTextColor(Object textColor) {
                this.textColor = textColor;
            }

            public String getLocationType() {
                return locationType;
            }

            public void setLocationType(String locationType) {
                this.locationType = locationType;
            }

            public ConfigDataDTO getConfigData() {
                return configData;
            }

            public void setConfigData(ConfigDataDTO configData) {
                this.configData = configData;
            }

            public Integer getStartTime() {
                return startTime;
            }

            public void setStartTime(Integer startTime) {
                this.startTime = startTime;
            }

            public Object getEndTime() {
                return endTime;
            }

            public void setEndTime(Object endTime) {
                this.endTime = endTime;
            }

            public List<Integer> getUserIds() {
                return userIds;
            }

            public void setUserIds(List<Integer> userIds) {
                this.userIds = userIds;
            }

            public static class ConfigDataDTO {
                @SerializedName("type")
                private Integer type;
                @SerializedName("second")
                private Integer second;
                @SerializedName("timing")
                private Integer timing;
                @SerializedName("everyday")
                private Integer everyday;

                public Integer getType() {
                    return type;
                }

                public void setType(Integer type) {
                    this.type = type;
                }

                public Integer getSecond() {
                    return second;
                }

                public void setSecond(Integer second) {
                    this.second = second;
                }

                public Integer getTiming() {
                    return timing;
                }

                public void setTiming(Integer timing) {
                    this.timing = timing;
                }

                public Integer getEveryday() {
                    return everyday;
                }

                public void setEveryday(Integer everyday) {
                    this.everyday = everyday;
                }
            }
        }
    }
}
