/**
 * 前置机-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'hfnt.preposedMachine';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  ENABLED: intl.get('hzero.common.button.enable').d('启用'),
  DISABLED: intl.get('hzero.common.button.disable').d('禁用'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  HEADER: intl.get(`${PREFIX}.view.title.header`).d('前置机配置'),
  CREATE_LINE: intl.get(`${PREFIX}.view.title.createLine`).d('创建前置机'),
  EDIT_LINE: intl.get(`${PREFIX}.view.title.editLine`).d('修改前置机'),

  SEQ_NUMBER: intl.get(`${PREFIX}.model.preposedMachine.seqNumber`).d('序号'),
  MACHINE_CODE: intl.get(`${PREFIX}.model.preposedMachine.groupCode`).d('前置机代码'),
  MACHINE_NAME: intl.get(`${PREFIX}.model.preposedMachine.groupName`).d('前置机名称'),
  REQUEST_URL: intl.get(`${PREFIX}.model.preposedMachine.topicCode`).d('前置机地址'),
  STATUS: intl.get(`${PREFIX}.model.preposedMachine.status`).d('状态'),
  CLIENT: intl.get(`${PREFIX}.model.preposedMachine.client`).d('客户端'),
  TENANT: intl.get(`${PREFIX}.model.preposedMachine.tenant`).d('租户'),
  REMARK: intl.get(`${PREFIX}.model.preposedMachine.remark`).d('备注'),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.preposedMachine.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.preposedMachine.saveEmpty`).d('无修改内容,无需保存'),

  PROGRAM_LIST: intl.get(`${PREFIX}.model.preposedMachine.client`).d('程序列表'),
};
