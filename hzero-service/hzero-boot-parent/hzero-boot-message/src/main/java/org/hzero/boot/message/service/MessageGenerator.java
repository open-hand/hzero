package org.hzero.boot.message.service;

import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 消息生成接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/6 星期一 20:12
 */
public interface MessageGenerator {
    // ----------------------------- 平台级

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, Map<String, String> args) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, null, args, true, null);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, Map<String, String> args, String lang) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, null, args, true, lang);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, Map<String, String> args, boolean sqlEnable) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, null, args, sqlEnable, null);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, Map<String, String> args, boolean sqlEnable, String lang) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, null, args, sqlEnable, lang);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, String serverTypeCode, Map<String, String> args) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, serverTypeCode, args, true, null);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, String serverTypeCode, Map<String, String> args, String lang) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, serverTypeCode, args, true, lang);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, serverTypeCode, args, sqlEnable, null);
    }

    /**
     * 生成消息内容
     *
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable, String lang) {
        return generateMessage(BaseConstants.DEFAULT_TENANT_ID, templateCode, serverTypeCode, args, sqlEnable, lang);
    }

    // ----------------------------- 租户级

    /**
     * 生成消息内容
     *
     * @param tenantId     租户ID
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, Map<String, String> args) {
        return generateMessage(tenantId, templateCode, null, args, true, null);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId     租户ID
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, Map<String, String> args, String lang) {
        return generateMessage(tenantId, templateCode, null, args, true, lang);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId     租户ID
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, Map<String, String> args, boolean sqlEnable) {
        return generateMessage(tenantId, templateCode, null, args, sqlEnable, null);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId     租户ID
     * @param templateCode 消息模板编码
     * @param args         自定义参数
     * @param sqlEnable    是否使用sqlValue
     * @param lang         语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, Map<String, String> args, boolean sqlEnable, String lang) {
        return generateMessage(tenantId, templateCode, null, args, sqlEnable, lang);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId       租户ID
     * @param templateCode   消息模板编码
     * @param serverTypeCode 服务类型编码，短信
     * @param args           自定义参数
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args) {
        return generateMessage(tenantId, templateCode, serverTypeCode, args, true, null);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId       租户ID
     * @param templateCode   消息模板编码
     * @param serverTypeCode 服务类型编码，短信
     * @param args           自定义参数
     * @param lang           语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, String lang) {
        return generateMessage(tenantId, templateCode, serverTypeCode, args, true, lang);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId       租户ID
     * @param templateCode   消息模板编码
     * @param serverTypeCode 服务类型编码，短信
     * @param args           自定义参数
     * @param sqlEnable      是否使用sqlValue
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    default Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable) {
        return generateMessage(tenantId, templateCode, serverTypeCode, args, sqlEnable, null);
    }

    /**
     * 生成消息内容
     *
     * @param tenantId       租户ID
     * @param templateCode   消息模板编码
     * @param serverTypeCode 服务类型编码，短信
     * @param args           自定义参数
     * @param sqlEnable      是否使用sqlValue
     * @param lang           语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable, String lang);

    /**
     * 生成消息内容
     *
     * @param tenantId       租户ID
     * @param templateCode   消息模板编码
     * @param serverTypeCode 服务类型编码，短信
     * @param objectArgs     自定义参数
     * @param sqlEnable      是否使用sqlValue
     * @param lang           语言
     * @return 消息内容，返回消息主题，消息内容
     * @deprecated 不要在消息客户端生成消息模板
     */
    Message generateMessageObjectArgs(long tenantId, String templateCode, String serverTypeCode, Map<String, Object> objectArgs, boolean sqlEnable, String lang);

    /**
     * 将sql执行结果插入消息参数
     *
     * @param tenantId     租户ID
     * @param templateCode 消息模板编码
     * @param objectArgs   模板参数
     * @param lang         语言
     * @return 参数
     */
    Map<String, Object> appendSqlParam(long tenantId, String templateCode, Map<String, Object> objectArgs, String lang);
}
