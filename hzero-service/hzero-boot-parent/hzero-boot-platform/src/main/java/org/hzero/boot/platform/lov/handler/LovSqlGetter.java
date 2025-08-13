package org.hzero.boot.platform.lov.handler;


/**
 * SQL语句获取器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午8:57:54
 */
public interface LovSqlGetter {

    /**
     * 从平台服务中获取客制化SQL语句
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @return 客制化SQL语句
     */
    default String getCustomSql(String lovCode, Long tenantId) {
        return getCustomSql(lovCode, tenantId, null, false);
    }

    /**
     * 从平台服务中获取客制化SQL语句
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @param lang 语言
     * @param publicQuery 是否为公开API查询
     * @return 客制化SQL语句
     */
    String getCustomSql(String lovCode, Long tenantId, String lang, Boolean publicQuery);


    /**
     * 从平台服务中获取反查SQL语句
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @return 反查SQL语句
     */
   default String getTranslationSql(String lovCode, Long tenantId) {
       return getTranslationSql(lovCode, tenantId, null, false);
   }

    /**
     * 从平台服务中获取反查SQL语句
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @param lang 语言
     * @param publicQuery 是否为公开API查询
     * @return 反查SQL语句
     */
    String getTranslationSql(String lovCode, Long tenantId, String lang, Boolean publicQuery);
}
