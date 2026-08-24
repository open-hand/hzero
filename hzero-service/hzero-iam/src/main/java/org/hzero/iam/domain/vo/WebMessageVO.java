package org.hzero.iam.domain.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站内信参数
 *
 * @author jiaxu.cui@hand-china.com 2018/10/19 10:57
 */
public class WebMessageVO {

    private static final String ARGS_LOGIN_NAME = "loginName";

    /**
     * 消息模板代码
     */
    private String templateCode;

    /**
     * 消息内容
     */
    private Map<String, String> args;

    /**
     * 消息接收人列表
     */
    private List<Long> receiverAddressList;

    public void prefectContent(Long userId, String username, String templateCode) {
        this.setTemplateCode(templateCode);
        Map<String, String> map = new HashMap<>(1);
        map.put(WebMessageVO.ARGS_LOGIN_NAME, username);
        this.setArgs(map);
        List<Long> list = new ArrayList<>();
        list.add(userId);
        this.setReceiverAddressList(list);
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public List<Long> getReceiverAddressList() {
        return receiverAddressList;
    }

    public void setReceiverAddressList(List<Long> receiverAddressList) {
        this.receiverAddressList = receiverAddressList;
    }
}
