package org.hzero.dd.dto;

import java.util.Date;

public class UploadMediaDTO extends DefaultResultDTO{
    /**
     *      "type": "image",
     *     "media_id": "@dsa8d87yxxxxxx",
     *     "created_at":1442027997327
     */
    private String type;
    private String media_id;
    private Date  created_at;

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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
