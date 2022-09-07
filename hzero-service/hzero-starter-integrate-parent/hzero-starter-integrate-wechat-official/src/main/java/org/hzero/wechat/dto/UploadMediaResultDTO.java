package org.hzero.wechat.dto;

public class UploadMediaResultDTO extends DefaultResultDTO {
   //{"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
    private String type;
    private String media_id;
    private Long created_at;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
