/**
 * 定时任务-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'hfnt.scheduledTask';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  HEADER: intl.get(`${PREFIX}.view.title.header`).d('定时任务配置'),
  CREATE_LINE: intl.get(`${PREFIX}.view.title.createLine`).d('创建定时任务'),
  EDIT_LINE: intl.get(`${PREFIX}.view.title.editLine`).d('修改定时任务'),

  ENABLED: intl.get(`${PREFIX}.model.scheduledTask.enable`).d('启用'),
  DISABLED: intl.get(`${PREFIX}.model.scheduledTask.disable`).d('失效'),
  PUBLISH: intl.get(`${PREFIX}.model.scheduledTask.publish`).d('发布'),
  SEQ_NUMBER: intl.get(`${PREFIX}.model.scheduledTask.seqNumber`).d('序号'),
  MACHINE_CODE: intl.get(`${PREFIX}.model.scheduledTask.machineCode`).d('前置机代码'),
  MACHINE_NAME: intl.get(`${PREFIX}.model.scheduledTask.machineName`).d('前置机名称'),
  SCHEDULED_TASK_CODE: intl
    .get(`${PREFIX}.model.scheduledTask.scheduledTaskCode`)
    .d('定时任务编号'),
  SCHEDULED_TASK_NAME: intl
    .get(`${PREFIX}.model.scheduledTask.scheduledTaskName`)
    .d('定时任务名称'),
  TYPE: intl.get(`${PREFIX}.model.scheduledTask.type`).d('类型'),
  CRON: intl.get(`${PREFIX}.model.scheduledTask.cron`).d('cron表达式'),
  CRON_MEANING: intl.get(`${PREFIX}.model.scheduledTask.cronMeaning`).d('cron表达式含义'),
  PROGRAM_NAME: intl.get(`${PREFIX}.model.scheduledTask.programName`).d('程序名称'),
  CLASS_NAME: intl.get(`${PREFIX}.model.scheduledTask.className`).d('类名'),
  METHOD_NAME: intl.get(`${PREFIX}.model.scheduledTask.methodName`).d('方法名'),
  PARAM: intl.get(`${PREFIX}.model.scheduledTask.param`).d('参数'),
  STATUS: intl.get(`${PREFIX}.model.scheduledTask.status`).d('状态'),

  PARAM_TITLE: intl.get(`${PREFIX}.view.title.paramTitle`).d('参数维护'),
  PARAM_MAINTAIN: intl.get(`${PREFIX}.view.title.paramMaintain`).d('维护参数'),
  PARAM_VIEW: intl.get(`${PREFIX}.view.title.paramView`).d('查看参数'),
  PARAM_TYPE: intl.get(`${PREFIX}.model.scheduledTask.paramType`).d('参数类型'),
  PARAM_NAME: intl.get(`${PREFIX}.model.scheduledTask.paramName`).d('参数名称'),
  PARAM_VALUE: intl.get(`${PREFIX}.model.scheduledTask.paramValue`).d('参数值'),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.scheduledTask.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.preposedMachine.saveEmpty`).d('无修改内容,无需保存'),
};
