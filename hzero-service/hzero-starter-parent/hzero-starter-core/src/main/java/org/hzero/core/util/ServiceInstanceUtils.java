package org.hzero.core.util;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 元数据处理工具类 {@link ServiceInstance#getMetadata()}
 * @author XCXCXCXCX
 * @date 2020/1/17 2:48 下午
 */
public class ServiceInstanceUtils {

    public static final String METADATA_CONTEXT = "CONTEXT";
    public static final String METADATA_VERSION = "VERSION";
    public static final String METADATA_MANAGEMENT_PORT = "management.port";

    public static final String NULL_VERSION = "null_version";

    public static String getVersionFromMetadata(ServiceInstance serviceInstance) {
        return getValueFromMetadata(serviceInstance, METADATA_VERSION, NULL_VERSION);
    }

    public static String getContextFromMetadata(ServiceInstance serviceInstance) {
        return getValueFromMetadata(serviceInstance, METADATA_CONTEXT, "");
    }

    public static String getManagementPortFromMetadata(ServiceInstance serviceInstance) {
        return getValueFromMetadata(serviceInstance, METADATA_MANAGEMENT_PORT, null);
    }

    public static boolean matchTags(ServiceInstance instance, Map<String, String> tags) {
        Map<String, String> metadata = instance.getMetadata();
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String tagValue = entry.getValue();
            String metadataValue = metadata.get(entry.getKey());
            if (!Objects.equals(tagValue, metadataValue)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containTag(ServiceInstance instance, String tagKey) {
        if (StringUtils.isEmpty(tagKey)) {
            return true;
        }
        return instance.getMetadata().containsKey(tagKey);
    }

    public static String getValueFromMetadata(ServiceInstance serviceInstance, String key){
        if (serviceInstance.getMetadata() == null) {
            throw new IllegalArgumentException("The serviceInstance object has no metadata!");
        }
        if (serviceInstance.getMetadata().get(key) == null) {
            throw new IllegalArgumentException("The serviceInstance's metadata has no the value {key = " + key + "}!");
        }
        return serviceInstance.getMetadata().get(key);
    }

    public static String getValueFromMetadata(ServiceInstance serviceInstance, String key, String defaultValue){
        return serviceInstance.getMetadata() == null ?
                defaultValue : (serviceInstance.getMetadata().get(key) == null ?
                defaultValue : serviceInstance.getMetadata().get(key));
    }

}
