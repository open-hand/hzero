package org.hzero.wechat.dto;

public class AddMaterialResultDTO extends  DefaultResultDTO {

//    {
//        "media_id":MEDIA_ID,
//            "url":URL
//    }
    private  String media_id;
    private  String URL;

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
