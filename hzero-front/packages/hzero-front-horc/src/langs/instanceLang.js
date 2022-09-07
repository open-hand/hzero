/**
 * 服务编排-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'horc.instance';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  DELETE: intl.get('hzero.common.button.delete').d('删除'),
  CLOSE: intl.get('hzero.common.button.close').d('关闭'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  HEADER: intl.get(`${PREFIX}.view.title.header`).d('编排实例'),
  CREATE_LINE: intl.get(`${PREFIX}.view.title.createLine`).d('创建节点'),
  EDIT_LINE: intl.get(`${PREFIX}.view.title.editLine`).d('修改节点'),
  FIELD_MAPPING: intl.get(`${PREFIX}.view.title.fieldMapping`).d('字段映射'),
  ENSURE_ADD: intl.get(`${PREFIX}.view.title.ensureAdd`).d('确定'),
  ONLINE_CONFIRM: intl.get(`${PREFIX}.view.title.onlineConfirm`).d('确定上线吗'),
  OFFLINE_CONFIRM: intl.get(`${PREFIX}.view.title.offlineConfirm`).d('确定下线吗'),
  HTTP_METHOD_CHANGE_CONFIRM: intl
    .get(`${PREFIX}.view.title.httpMethodChangeConfirm`)
    .d('切换请求方法将清空请求'),

  ONLINE: intl.get(`${PREFIX}.view.button.online`).d('上线'),
  OFFLINE: intl.get(`${PREFIX}.view.button.offline`).d('下线'),
  EXECUTE: intl.get(`${PREFIX}.view.button.execute`).d('运行'),
  RERUN: intl.get(`${PREFIX}.view.button.rerun`).d('重跑'),
  PAUSE: intl.get(`${PREFIX}.view.button.pause`).d('暂停'),
  STOP: intl.get(`${PREFIX}.view.button.stop`).d('停止'),
  RESUME: intl.get(`${PREFIX}.view.button.resume`).d('恢复'),
  GANTT: intl.get(`${PREFIX}.view.button.gantt`).d('甘特图'),

  SEQ_NUMBER: intl.get(`${PREFIX}.model.instance.status`).d('序号'),
  INSTANCE_NAME: intl.get(`${PREFIX}.model.instance.instanceName`).d('编排实例名称'),
  INSTANCE_STATUS: intl.get(`${PREFIX}.model.instance.instanceStatus`).d('实例状态'),
  STATEMENT_TYPE: intl.get(`${PREFIX}.model.instance.statementType`).d('执行类型'),
  STATEMENT_START_TIME: intl.get(`${PREFIX}.model.instance.statementStartTime`).d('执行触发时间'),
  START_TIME: intl.get(`${PREFIX}.model.instance.startTime`).d('运行开始时间'),
  END_TIME: intl.get(`${PREFIX}.model.instance.endTime`).d('运行结束时间'),
  TIME_CONSUMPTION: intl.get(`${PREFIX}.model.instance.timeConsumption`).d('耗时(ms)'),
  TIME_CONSUMPTION_DESC: intl.get(`${PREFIX}.model.instance.timeConsumptionDesc`).d('耗时'),
  FAILOVER_FLAG: intl.get(`${PREFIX}.model.instance.failoverFlag`).d('容错标志'),
  FAILURE_STRATEGY: intl.get(`${PREFIX}.model.instance.failureStrategy`).d('失败策略'),
  HOST: intl.get(`${PREFIX}.model.instance.host`).d('主机地址'),
  WORK_GROUP: intl.get(`${PREFIX}.model.instance.workGroup`).d('工作组'),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.instance.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.preposedMachine.saveEmpty`).d('无修改内容,无需保存'),

  STATEMENT_START_TIME_LOW: intl
    .get(`${PREFIX}.model.instance.statement_start_time_low`)
    .d('执行触发时间从'),
  STATEMENT_START_TIME_HIGH: intl
    .get(`${PREFIX}.model.instance.statement_start_time_high`)
    .d('执行触发时间至'),
  START_TIME_LOW: intl.get(`${PREFIX}.model.instance.start_time_low`).d('运行开始时间从'),
  START_TIME_HIGH: intl.get(`${PREFIX}.model.instance.start_time_high`).d('运行开始时间至'),
  END_TIME_LOW: intl.get(`${PREFIX}.model.instance.end_time_low`).d('运行结束时间从'),
  END_TIME_HIGH: intl.get(`${PREFIX}.model.instance.end_time_high`).d('运行结束时间至'),
  TIME_CONSUMPTION_LOW: intl.get(`${PREFIX}.model.instance.time_consumption_low`).d('耗时从'),
  TIME_CONSUMPTION_HIGH: intl.get(`${PREFIX}.model.instance.time_consumption_high`).d('耗时至'),
};
