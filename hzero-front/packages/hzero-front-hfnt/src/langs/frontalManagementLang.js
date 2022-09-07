/**
 * 前置机程序管理-多语言
 * @author changwen.yu@hand-china.com
 * @date 2020-8-13
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'hfnt.frontalManagement';

export default {
  PREFIX,
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  ISSUE: intl.get(`${PREFIX}.view.table.column.issue`).d('下发'),
  ISSUE_DETAIL: intl.get(`${PREFIX}.view.table.column.issueDetail`).d('下发详情'),
  ISSUE_PROGRAM: intl.get(`${PREFIX}.view.table.column.issueProgram`).d('程序下发'),
  EDIT: intl.get('hzero.common.table.column.edit').d('编辑'),
  DISABLED: intl.get('hzero.common.table.column.disabled').d('禁用'),
  ENABLED: intl.get('hzero.common.table.column.enabled').d('启用'),

  HEADER: intl.get(`${PREFIX}.view.title.header`).d('前置机程序管理'),

  TENANT: intl.get(`${PREFIX}.model.frontalManagement.tenant`).d('租户'),
  TENANT_NAME: intl.get(`${PREFIX}.model.frontalManagement.tenantName`).d('租户名称'),
  PROGRAM_CODE: intl.get(`${PREFIX}.model.frontalManagement.programCode`).d('程序编码'),
  PROGRAM_TYPE: intl.get(`${PREFIX}.model.frontalManagement.programType`).d('程序类型'),
  PROGRAM_NAME: intl.get(`${PREFIX}.model.frontalManagement.programName`).d('程序名称'),
  PROGRAM_DESC: intl.get(`${PREFIX}.model.frontalManagement.programDesc`).d('程序说明'),
  CREATION_DATE: intl.get(`${PREFIX}.model.frontalManagement.creationDate`).d('上传日期'),
  ISSUE_DATE: intl.get(`${PREFIX}.model.frontalManagement.issueDate`).d('下发日期'),
  STATUS: intl.get(`${PREFIX}.model.frontalManagement.status`).d('状态'),
  FRONTAL_STATUS: intl.get(`${PREFIX}.model.frontalManagement.frontalStatus`).d('前置机状态'),
  UPLOAD: intl.get(`${PREFIX}.model.frontalManagement.upload`).d('上传'),
  FRONTAL_CODE: intl.get(`${PREFIX}.model.frontalManagement.frontalCode`).d('前置机代码'),
  FRONTAL_ADDRESS: intl.get(`${PREFIX}.model.frontalManagement.frontalAddress`).d('前置机地址'),

  PROGRAM_CODE_JAR: intl.get(`${PREFIX}.model.frontalManagement.programCode.jar`).d('jar包编码'),
  PROGRAM_CODE_CLASS: intl.get(`${PREFIX}.model.frontalManagement.programCode.class`).d('短类名称'),

  LOADED: intl.get(`${PREFIX}.view.table.column.operator.loaded`).d('加载'),
  UNLOAD: intl.get(`${PREFIX}.view.table.column.operator.unload`).d('卸载'),

  ERROR_STACK: intl.get(`${PREFIX}.model.frontalManagement.errorStack`).d('异常堆栈'),
};
