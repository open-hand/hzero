package org.hzero.iam.infra.constant;

import org.hzero.core.base.BaseConstants;

import io.choerodon.core.exception.CommonException;

/**
 * @author qingsheng.chen@hand-china.com
 */
public enum DocTypeScript {

    /**
     * 单据权限SQL脚本（维度规则类型为列类型），sql逻辑如下：
     *
     * 1、给角色分配了单据权限，未分配维度 => 能看到这个单据对应表的所有数据
     * 2、给角色分配了单据权限，分配维度，用户分配维度值 =>  根据角色分配的维度+用户分配的维度值来做限制
     * 3、给角色分配了单据权限，分配维度，角色分配了维度值，用户分配维度值 => 根据角色分配的维度+用户分配的维度值来做限制
     * 4、给角色分配了单据权限，分配维度，角色分配了维度值，用户未分配维度值 => 根据角色分配的维度+角色分配的维度值来做限制
     */
    BACC(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_COLUMN, Constants.DocType.DOC_CUSTOM_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(tenantId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader\"> \n" +
            "        1=2 \n" +
            "    </when> \n" +
            "    <when test=\"!roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{tenantId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{tenantId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    BOCC(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_COLUMN, Constants.DocType.DOC_CUSTOM_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(tenantId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{tenantId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{tenantId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    BACU(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_COLUMN, Constants.DocType.DOC_USER_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(organizationId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader\"> \n" +
            "        1=2 \n" +
            "    </when> \n" +
            "    <when test=\"!roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{organizationId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{organizationId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    BOCU(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_COLUMN, Constants.DocType.DOC_USER_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(organizationId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{organizationId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{organizationId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id = ${tableAlias}.${fieldName}))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    /**
     * 单据权限SQL脚本（维度规则类型为子查询类型）
     */
    BASC(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_SUB_SELECT, Constants.DocType.DOC_CUSTOM_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(tenantId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader\"> \n" +
            "        1=2 \n" +
            "    </when> \n" +
            "    <when test=\"!roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{tenantId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{tenantId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    /**
     * 单据权限SQL脚本（维度规则类型为子查询类型）
     */
    BOSC(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_SUB_SELECT, Constants.DocType.DOC_CUSTOM_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(tenantId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{tenantId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{tenantId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    BASU(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_SUB_SELECT, Constants.DocType.DOC_USER_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(organizationId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader\"> \n" +
            "        1=2 \n" +
            "    </when> \n" +
            "    <when test=\"!roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{organizationId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{organizationId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    BOSU(Constants.AUTHORITY_SCOPE_CODE.BIZ, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_SUB_SELECT, Constants.DocType.DOC_USER_TENANT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;BIZ&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;BIZ&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"userAuthAssign\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkUserAuthAssign(organizationId, &quot;${authorityTypeCode}&quot;, userId)\" /> \n" +
            "<choose> \n" +
            "    <when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	     1=1 \n" +
            "	 </when> \n" +
            "	 <when test=\"!userAuthAssign\"> \n" +
            "	     (EXISTS ( \n" +
            "		      SELECT 1  \n" +
            "		      FROM hiam_role_auth_data hrad  \n" +
            "		      LEFT JOIN hiam_role_auth_data_line hradl ON hrad.auth_data_id = hradl.auth_data_id  \n" +
            "		      WHERE hrad.tenant_id = #{organizationId}  \n" +
            "		      AND hrad.role_id IN \n" +
            "             <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "                             #{roleMergeId}" +
            "             \n</foreach> \n" +
            "		      AND hrad.authority_type_code = '${authorityTypeCode}' \n" +
            "		      AND (hrad.include_all_flag = 1 OR hradl.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <when test=\"userAuthAssign\"> \n" +
            "        (EXISTS ( \n" +
            "            SELECT 1  \n" +
            "            FROM hiam_user_authority hua1  \n" +
            "            LEFT JOIN hiam_user_authority_line hual1 ON hua1.authority_id = hual1.authority_id  \n" +
            "            WHERE hua1.tenant_id = #{organizationId} \n" +
            "            AND hua1.user_id = #{userId} \n" +
            "            AND hua1.authority_type_code = '${authorityTypeCode}'  \n" +
            "            AND (hua1.include_all_flag = 1 OR hual1.data_id IN (${fieldName})))) \n" +
            "	 </when> \n" +
            "	 <otherwise> \n" +
            "        1=2 \n" +
            "	 </otherwise> \n" +
            "</choose> \n"),

    UAC(Constants.AUTHORITY_SCOPE_CODE.USER, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_COLUMN, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;USER&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;USER&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<choose> \n" +
            "	<when test=\"!roleAuthHeader\"> \n" +
            "		1=2 \n" +
            "	</when> \n" +
            "	<when test=\"!roleAuthLine\"> \n" +
            "	    1=1 \n" +
            "	</when> \n" +
            "	<when test=\"roleAuthLine\"> \n" +
            "	    (EXISTS ( \n" +
            "                 SELECT 1 \n" +
            "	 		      FROM hiam_role_authority_line hral \n" +
            "	 		      JOIN hiam_role_authority hra ON (hra.auth_scope_code = 'USER' AND hra.role_auth_id = hral.role_auth_id) \n" +
            "	 		      JOIN hiam_doc_type hdt ON (hdt.doc_type_id = hra.auth_doc_type_id AND hdt.enabled_flag = 1 AND hdt.doc_type_id = ${docTypeId}) \n" +
            "	 		      JOIN hiam_doc_type_auth_dim hdtad ON hdtad.doc_type_id = hdt.doc_type_id AND hdtad.auth_type_code = hral.auth_type_code \n" +
            "	 		      WHERE hral.auth_type_code = '${authorityTypeCode}' \n" +
            "	 		      AND hral.role_id IN \n" +
            "	              <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "	                              #{roleMergeId}" +
            "	              \n</foreach> \n" +
            "	 		      AND #{userId} = ${tableAlias}.${fieldName})) \n" +
            "	</when> \n" +
            "	<otherwise> \n" +
            "	    1=2 \n" +
            "	</otherwise> \n" +
            "</choose> \n"),

    UOC(Constants.AUTHORITY_SCOPE_CODE.USER, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_COLUMN, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;USER&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;USER&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<choose> \n" +
            "	<when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	    1=1 \n" +
            "	</when> \n" +
            "	<when test=\"roleAuthLine\"> \n" +
            "	    (EXISTS ( \n" +
            "                 SELECT 1 \n" +
            "	 		      FROM hiam_role_authority_line hral \n" +
            "	 		      JOIN hiam_role_authority hra ON (hra.auth_scope_code = 'USER' AND hra.role_auth_id = hral.role_auth_id) \n" +
            "	 		      JOIN hiam_doc_type hdt ON (hdt.doc_type_id = hra.auth_doc_type_id AND hdt.enabled_flag = 1 AND hdt.doc_type_id = ${docTypeId}) \n" +
            "	 		      JOIN hiam_doc_type_auth_dim hdtad ON hdtad.doc_type_id = hdt.doc_type_id AND hdtad.auth_type_code = hral.auth_type_code \n" +
            "	 		      WHERE hral.auth_type_code = '${authorityTypeCode}' \n" +
            "	 		      AND hral.role_id IN \n" +
            "	              <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">\n" +
            "	                              #{roleMergeId}" +
            "	              \n</foreach> \n" +
            "	 		      AND #{userId} = ${tableAlias}.${fieldName})) \n" +
            "	</when> \n" +
            "	<otherwise> \n" +
            "	    1=2 \n" +
            "	</otherwise> \n" +
            "</choose> \n"),

    UAS(Constants.AUTHORITY_SCOPE_CODE.USER, Constants.DocType.NEW_AUTH_TYPE_ALL, Constants.DocType.RULE_TYPE_SUB_SELECT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;USER&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;USER&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<choose> \n" +
            "	<when test=\"!roleAuthHeader\"> \n" +
            "		1=2 \n" +
            "	</when> \n" +
            "	<when test=\"!roleAuthLine\"> \n" +
            "	    1=1 \n" +
            "	</when> \n" +
            "	<when test=\"roleAuthLine\"> \n" +
            "	    (EXISTS ( \n" +
            "                 SELECT 1 \n" +
            "	 		      FROM hiam_role_authority_line hral \n" +
            "	 		      JOIN hiam_role_authority hra ON (hra.auth_scope_code = 'USER' AND hra.role_auth_id = hral.role_auth_id) \n" +
            "	 		      JOIN hiam_doc_type hdt ON (hdt.doc_type_id = hra.auth_doc_type_id AND hdt.enabled_flag = 1 AND hdt.doc_type_id = ${docTypeId}) \n" +
            "	 		      JOIN hiam_doc_type_auth_dim hdtad ON hdtad.doc_type_id = hdt.doc_type_id AND hdtad.auth_type_code = hral.auth_type_code \n" +
            "	 		      WHERE hral.auth_type_code = '${authorityTypeCode}' \n" +
            "	 		      AND hral.role_id IN \n" +
            "	              <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">" +
            "	                              #{roleMergeId}\n" +
            "	              \n</foreach> \n" +
            "	 		      AND #{userId} IN (${fieldName}))) \n" +
            "	</when> \n" +
            "	<otherwise> \n" +
            "	    1=2 \n" +
            "	</otherwise> \n" +
            "</choose> \n"),

    UOS(Constants.AUTHORITY_SCOPE_CODE.USER, Constants.DocType.AUTH_TYPE_ONLY_CONFIG, Constants.DocType.RULE_TYPE_SUB_SELECT, "\n" +
            "<bind name=\"roleMergeIdList\" value=\"@io.choerodon.core.oauth.DetailsHelper@getUserDetails().roleMergeIds()\" /> \n" +
            "<bind name=\"roleAuthHeader\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthHeaderAssign(${docTypeId}L, &quot;USER&quot;, roleMergeIdList)\" /> \n" +
            "<bind name=\"roleAuthLine\" value=\"@org.hzero.boot.platform.data.permission.util.DocRedisUtils@checkRoleAuthLineAssign(${docTypeId}L, &quot;USER&quot;, &quot;${authorityTypeCode}&quot;, roleMergeIdList)\" /> \n" +
            "<choose> \n" +
            "	<when test=\"!roleAuthHeader or !roleAuthLine\"> \n" +
            "	    1=1 \n" +
            "	</when> \n" +
            "	<when test=\"roleAuthLine\"> \n" +
            "	    (EXISTS ( \n" +
            "                 SELECT 1 \n" +
            "	 		      FROM hiam_role_authority_line hral \n" +
            "	 		      JOIN hiam_role_authority hra ON (hra.auth_scope_code = 'USER' AND hra.role_auth_id = hral.role_auth_id) \n" +
            "	 		      JOIN hiam_doc_type hdt ON (hdt.doc_type_id = hra.auth_doc_type_id AND hdt.enabled_flag = 1 AND hdt.doc_type_id = ${docTypeId}) \n" +
            "	 		      JOIN hiam_doc_type_auth_dim hdtad ON hdtad.doc_type_id = hdt.doc_type_id AND hdtad.auth_type_code = hral.auth_type_code \n" +
            "	 		      WHERE hral.auth_type_code = '${authorityTypeCode}' \n" +
            "	 		      AND hral.role_id IN \n" +
            "	              <foreach collection=\"roleMergeIdList\" open=\"(\" separator=\",\" item=\"roleMergeId\" close=\")\">" +
            "	                              #{roleMergeId}\n" +
            "	              \n</foreach> \n" +
            "	 		      AND #{userId} IN (${fieldName}))) \n" +
            "	</when> \n" +
            "	<otherwise> \n" +
            "	    1=2 \n" +
            "	</otherwise> \n" +
            "</choose> \n");

    private String dimension;
    private String authorizeControl;
    private String ruleType;
    private String script;
    /**
     * 单据权限租户类型，USER_TENANT（用户所属租户），CUSTOM_TENANT（当前租户）
     */
    private String docTenantType;

    DocTypeScript(String dimension, String authorizeControl, String ruleType, String docTenantType, String script) {
        this.dimension = dimension;
        this.authorizeControl = authorizeControl;
        this.ruleType = ruleType;
        this.script = script;
        this.docTenantType = docTenantType;
    }

    DocTypeScript(String dimension, String authorizeControl, String ruleType, String script) {
        this.dimension = dimension;
        this.authorizeControl = authorizeControl;
        this.ruleType = ruleType;
        this.script = script;
    }

    public static String getScript(String dimension, String authorizeControl, String ruleType, String docTenantType) {
        for (DocTypeScript docTypeScript : DocTypeScript.values()) {
            if (Constants.AUTHORITY_SCOPE_CODE.USER.equalsIgnoreCase(dimension)) {
                // 用户维度
                if (docTypeScript.dimension.equalsIgnoreCase(dimension)
                        && docTypeScript.authorizeControl.equalsIgnoreCase(authorizeControl)
                        && docTypeScript.ruleType.equalsIgnoreCase(ruleType)) {
                    return docTypeScript.script;
                }
            } else {
                // 业务维度
                if (docTypeScript.dimension.equalsIgnoreCase(dimension)
                        && docTypeScript.authorizeControl.equalsIgnoreCase(authorizeControl)
                        && docTypeScript.ruleType.equalsIgnoreCase(ruleType)
                        && docTypeScript.docTenantType.equalsIgnoreCase(docTenantType)) {
                    return docTypeScript.script;
                }
            }
        }
        throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
    }
}
