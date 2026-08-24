package org.hzero.gateway.helper.entity;

public class CheckResponse {

    private String jwt;

    private String message;

    private CheckState status;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CheckState getStatus() {
        return status;
    }

    public void setStatus(CheckState status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "jwt='" + jwt + '\'' +
                ", message='" + message + '\'' +
                ", checkState=" + status +
                '}';
    }
}
