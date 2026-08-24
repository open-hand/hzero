package org.hzero.iam.api.dto;

import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.entity.UserAuthorityLine;

import java.util.List;

/**
 * 用户数据权限头行结构DTO
 *
 * @author liang.jin@hand-china.com 2018/08/03 10:50
 */
public class UserAuthorityDTO {
    private UserAuthority userAuthority;
    private List<UserAuthorityLine> userAuthorityLineList;

    public UserAuthority getUserAuthority() {
        return userAuthority;
    }

    public void setUserAuthority(UserAuthority userAuthority) {
        this.userAuthority = userAuthority;
    }

    public List<UserAuthorityLine> getUserAuthorityLineList() {
        return userAuthorityLineList;
    }

    public void setUserAuthorityLineList(List<UserAuthorityLine> userAuthorityLineList) {
        this.userAuthorityLineList = userAuthorityLineList;
    }
}
