/**
 * 定时任务-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'hfnt.frontalLogs';

export default {
  PREFIX,
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  RETRY: intl.get('hzero.common.table.column.retry').d('重试'),

  HEADER: intl.get(`${PREFIX}.view.title.header`).d('前置机程序执行日志'),

  FRONTAL_CODE: intl.get(`${PREFIX}.model.frontalLogs.frontalCode`).d('前置机代码'),
  FRONTAL_NAME: intl.get(`${PREFIX}.model.frontalLogs.frontalName`).d('前置机名称'),
  CLASS_NAME: intl.get(`${PREFIX}.model.frontalLogs.className`).d('类名'),
  METHOD_NAME: intl.get(`${PREFIX}.model.frontalLogs.methodName`).d('方法名'),
  METHOD_PARAM_VALUE: intl.get(`${PREFIX}.model.frontalLogs.methodValue`).d('方法参数值'),
  CREATION_DATE: intl.get(`${PREFIX}.model.frontalLogs.creationDate`).d('创建日期'),
  CACHE_DATE: intl.get(`${PREFIX}.model.frontalLogs.cacheDate`).d('缓存日期'),
  SOURCE_TYPE: intl.get(`${PREFIX}.model.frontalLogs.sourceType`).d('数据类型'),
  CACHE_FOLDER: intl.get(`${PREFIX}.model.frontalLogs.cacheFolder`).d('缓存目录'),
  STATUS_CODE: intl.get(`${PREFIX}.model.frontalLogs.statusCode`).d('运行状态'),
  STATUS_DESC: intl.get(`${PREFIX}.model.frontalLogs.statusDesc`).d('运行状态描述'),
  TENANT_NAME: intl.get(`${PREFIX}.model.frontalLogs.tenantName`).d('租户名称'),
  ERROR_STACK: intl.get(`${PREFIX}.model.frontalLogs.errorStack`).d('错误堆栈'),
  TRANSLATE_ERROR: intl.get(`${PREFIX}model.frontalLogs.translateError`).d('字段解析失败'),
};
