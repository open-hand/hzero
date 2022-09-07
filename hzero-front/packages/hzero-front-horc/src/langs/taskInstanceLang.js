/**
 * 服务编排-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'horc.taskInstance';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  DELETE: intl.get('hzero.common.button.delete').d('删除'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  HEADER: intl.get(`${PREFIX}.view.title.header`).d('任务实例'),
  CREATE_LINE: intl.get(`${PREFIX}.view.title.createLine`).d('创建节点'),
  EDIT_LINE: intl.get(`${PREFIX}.view.title.editLine`).d('修改节点'),
  FIELD_MAPPING: intl.get(`${PREFIX}.view.title.fieldMapping`).d('字段映射'),
  ENSURE_ADD: intl.get(`${PREFIX}.view.title.ensureAdd`).d('确定'),
  ONLINE_CONFIRM: intl.get(`${PREFIX}.view.title.onlineConfirm`).d('确定上线吗'),
  OFFLINE_CONFIRM: intl.get(`${PREFIX}.view.title.offlineConfirm`).d('确定下线吗'),
  HTTP_METHOD_CHANGE_CONFIRM: intl
    .get(`${PREFIX}.view.title.httpMethodChangeConfirm`)
    .d('切换请求方法将清空请求'),
  LOG_DETAIL: intl.get(`${PREFIX}.view.title.logDetail`).d('日志详情'),

  LOG: intl.get(`${PREFIX}.view.button.log`).d('日志'),
  NEW_WINDOW_SHOW: intl.get(`${PREFIX}.view.button.newWindowShow`).d('新窗口展示'),
  TASK_RESULT: intl.get(`${PREFIX}.view.title.taskResult`).d('任务结果'),

  SEQ_NUMBER: intl.get(`${PREFIX}.model.taskInstance.seqNumber`).d('序号'),
  TASK_NAME: intl.get(`${PREFIX}.model.taskInstance.taskName`).d('任务实例名称'),
  THREAD_MECHANISM: intl.get(`${PREFIX}.view.taskInstance.threadMechanism`).d('线程执行机制'),
  FAILED_STRATEGY: intl.get(`${PREFIX}.model.taskInstance.failedStrategy`).d('失败策略'),
  INSTANCE_NAME: intl.get(`${PREFIX}.model.taskInstance.instanceName`).d('编排实例名称'),
  TASK_TYPE: intl.get(`${PREFIX}.model.taskInstance.taskType`).d('任务类型'),
  STATUS: intl.get(`${PREFIX}.model.taskInstance.status`).d('任务实例状态'),
  SUBMITTED_TIME: intl.get(`${PREFIX}.model.taskInstance.submittedTime`).d('提交时间'),
  START_TIME: intl.get(`${PREFIX}.model.taskInstance.startTime`).d('任务开始时间'),
  END_TIME: intl.get(`${PREFIX}.model.taskInstance.endTime`).d('任务结束时间'),
  TIME_CONSUMPTION: intl.get(`${PREFIX}.model.taskInstance.timeConsumption`).d('耗时(ms)'),
  TIME_CONSUMPTION_DESC: intl.get(`${PREFIX}.model.taskInstance.timeConsumptionDesc`).d('耗时'),
  HOST: intl.get(`${PREFIX}.model.taskInstance.host`).d('任务执行主机'),

  ALERT_FLAG: intl.get(`${PREFIX}.model.taskInstance.alertFlag`).d('告警标志'),
  RETRY_TIMES: intl.get(`${PREFIX}.model.taskInstance.retryTimes`).d('重试次数'),
  INSTANCE_PRIORITY: intl.get(`${PREFIX}.model.taskInstance.instancePriority`).d('优先级'),
  WORK_GROUP: intl.get(`${PREFIX}.model.taskInstance.workGroup`).d('工作组'),
  REMARK: intl.get(`${PREFIX}.model.taskInstance.remark`).d('备注说明'),
  FAILURE_STRATEGY: intl.get(''.concat(PREFIX, '.model.instance.failureStrategy')).d('失败策略'),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.taskInstance.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.preposedMachine.saveEmpty`).d('无修改内容,无需保存'),

  SUBMITTED_TIME_LOW: intl.get(`${PREFIX}.model.taskInstance.submitted_time_low`).d('提交时间从'),
  SUBMITTED_TIME_HIGH: intl.get(`${PREFIX}.model.taskInstance.submitted_time_high`).d('提交时间至'),
  RETRY_TIMES_LOW: intl.get(`${PREFIX}.model.taskInstance.retry_times_low`).d('重试次数从'),
  RETRY_TIMES_HIGH: intl.get(`${PREFIX}.model.taskInstance.retry_times_high`).d('重试次数至'),

  TASK_INSTANCE_VO_LIST: intl.get(`${PREFIX}.model.taskInstance.taskInstanceVOList`).d('前序任务'),
  TEXT_TYPE: intl.get(`${PREFIX}.model.taskInstance.textType`).d('响应文本类型'),
  CONTENT_TYPE: intl.get(`${PREFIX}.model.taskInstance.contentType`).d('响应内容类型'),
  PROCESS_TIME: intl.get(`${PREFIX}.model.taskInstance.processTime`).d('处理时间'),
  RESULT: intl.get(`${PREFIX}.model.taskInstance.result`).d('响应结果'),
};
