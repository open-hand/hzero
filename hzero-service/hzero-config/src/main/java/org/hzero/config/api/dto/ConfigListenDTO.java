package org.hzero.config.api.dto;

import java.util.Arrays;

/**
 * @author XCXCXCXCX
 * @date 2020/5/11 1:36 下午
 */
public class ConfigListenDTO {

    private String serviceName;
    private String label;
    private String[] keys;
    private String notifyAddr;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String getNotifyAddr() {
        return notifyAddr;
    }

    public void setNotifyAddr(String notifyAddr) {
        this.notifyAddr = notifyAddr;
    }

    @Override
    public String toString() {
        return "ConfigListenDTO{" +
                "serviceName='" + serviceName + '\'' +
                ", label='" + label + '\'' +
                ", keys=" + Arrays.toString(keys) +
                ", notifyAddr='" + notifyAddr + '\'' +
                '}';
    }
}
