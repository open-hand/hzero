package org.hzero.iam.infra.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 标签分配类型
 *
 * @author bo.he02@hand-china.com 2020-04-26 14:02:19
 * @see org.hzero.iam.domain.entity.LabelRel#assignType
 */
public enum LabelAssignType {
    /**
     * 自动分配
     */
    AUTO("A", "自动分配"),
    /**
     * 手动分配
     */
    MANUAL("M", "手动分配");

    private static final Map<String, LabelAssignType> TYPE_MAP = new HashMap<>(4);

    static {
        TYPE_MAP.put(LabelAssignType.AUTO.getCode(), LabelAssignType.AUTO);
        TYPE_MAP.put(LabelAssignType.MANUAL.getCode(), LabelAssignType.MANUAL);
    }

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    LabelAssignType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据Code获取分配类型
     *
     * @param code Code
     * @return 分配类型，不存在指定的分配类型，就返回默认的分配类型 手动分配
     */
    public static LabelAssignType ofDefault(String code) {
        return TYPE_MAP.getOrDefault(code, LabelAssignType.MANUAL);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
