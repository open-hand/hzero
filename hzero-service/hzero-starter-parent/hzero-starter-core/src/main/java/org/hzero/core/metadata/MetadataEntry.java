package org.hzero.core.metadata;

/**
 * 元数据KV键
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 9:18 上午
 */
public final class MetadataEntry {

    public static final String METADATA_VERSION = "VERSION";
    public static final String METADATA_CONTEXT = "CONTEXT";

    private String key;

    private String value;

    public MetadataEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
