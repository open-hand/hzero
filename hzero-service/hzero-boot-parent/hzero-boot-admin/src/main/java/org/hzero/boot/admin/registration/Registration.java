package org.hzero.boot.admin.registration;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 5:18 下午
 */
public interface Registration {

    String getServiceName();

    String getVersion();

    String getHealthUrl();

    Map<String, String> getMetadata();

}
