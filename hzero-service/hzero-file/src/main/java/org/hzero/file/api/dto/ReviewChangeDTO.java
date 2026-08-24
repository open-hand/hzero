package org.hzero.file.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 变动预览VO
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:47:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewChangeDTO {

    private Long userId;

    private String userName;

    private Long date;

    private String type;

    private String text;

    public Long getUserId() {
        return userId;
    }

    public ReviewChangeDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public ReviewChangeDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Long getDate() {
        return date;
    }

    public ReviewChangeDTO setDate(Long date) {
        this.date = date;
        return this;
    }

    public String getType() {
        return type;
    }

    public ReviewChangeDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getText() {
        return text;
    }

    public ReviewChangeDTO setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "ReviewChange{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
