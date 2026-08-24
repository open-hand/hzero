package io.choerodon.mybatis.domain;

import java.util.Date;

/**
 * Created by xausky on 3/20/17.
 */
public class Audit {
    private Date now;
    private Long user;

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
