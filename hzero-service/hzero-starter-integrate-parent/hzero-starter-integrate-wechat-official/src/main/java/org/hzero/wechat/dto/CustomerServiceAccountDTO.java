package org.hzero.wechat.dto;

public class CustomerServiceAccountDTO {

//           "kf_account" : "test1@test",
//             "nickname" : "客服1",
//             "password" : "pswmd5"
    private String kf_account;
    private String nickname;
    private String password;

    public String getKf_account() {
        return kf_account;
    }

    public void setKf_account(String kf_account) {
        this.kf_account = kf_account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
