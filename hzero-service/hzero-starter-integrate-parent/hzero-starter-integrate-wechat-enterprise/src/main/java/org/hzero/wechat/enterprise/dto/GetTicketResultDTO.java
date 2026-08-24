package org.hzero.wechat.enterprise.dto;

public class GetTicketResultDTO extends DefaultResultDTO {
//               "errcode":0,
//               "errmsg":"ok",
//               "ticket":"pIKi3wRPNWCGF-pyP-YU5KWjDDD",
//               "expires_in":7200
    private String ticket;
    private Long expires_in;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }
}
