package org.hzero.gateway.helper.domain;

import io.choerodon.core.oauth.CustomUserDetails;
import org.hzero.gateway.helper.entity.CheckState;

import java.io.Serializable;

public class CustomUserDetailsWithResult implements Serializable {

    private static final long serialVersionUID = -2022325583037285394L;

    private CustomUserDetails customUserDetails;

    private CheckState state;

    private String message;

    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails;
    }

    public CustomUserDetailsWithResult() {
    }

    public CustomUserDetailsWithResult(CustomUserDetails customUserDetails, CheckState state) {
        this.customUserDetails = customUserDetails;
        this.state = state;
    }

    public CustomUserDetailsWithResult(CheckState state, String message) {
        this.state = state;
        this.message = message;
    }

    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails = customUserDetails;
    }

    public CheckState getState() {
        return state;
    }

    public void setState(CheckState state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomUserDetailsWithResult{" +
                "customUserDetails=" + customUserDetails +
                ", state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
