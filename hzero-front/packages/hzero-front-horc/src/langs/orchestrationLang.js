/**
 * 服务编排-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-4-26
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'horc.orchestration';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  DELETE: intl.get('hzero.common.button.delete').d('删除'),
  SURE: intl.get('hzero.common.button.ok').d('确定'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  HEADER: intl.get(`${PREFIX}.view.title.orchestration`).d('编排定义'),
  HTTP_TITLE: intl.get(`${PREFIX}.view.title.createLine`).d('HTTP信息'),
  GANTT_TITLE: intl.get(`${PREFIX}.view.title.ganttTitle`).d('编排实例甘特图'),
  FIELD_MAPPING: intl.get(`${PREFIX}.view.title.fieldMapping`).d('字段映射'),
  DATA_TRANSFORM: intl.get(`${PREFIX}.view.title.fieldMapping`).d('数据转化'),
  ENSURE_ADD: intl.get(`${PREFIX}.view.title.ensureAdd`).d('确定'),
  ONLINE_CONFIRM: intl.get(`${PREFIX}.view.title.onlineConfirm`).d('确定上线吗'),
  OFFLINE_CONFIRM: intl.get(`${PREFIX}.view.title.offlineConfirm`).d('确定下线吗'),
  HTTP_METHOD_CHANGE_CONFIRM: intl
    .get(`${PREFIX}.view.title.httpMethodChangeConfirm`)
    .d('切换请求方法将清空请求'),

  TOOL: intl.get(`${PREFIX}.model.orchestration.tool`).d('工具栏'),
  TOOL_TIP: intl
    .get(`${PREFIX}.model.orchestration.tool.tip`)
    .d('请选中下方条目拖拽至右方画布中进行编排操作，仅支持在(已下线)状态下操作。'),
  SEQ_NUMBER: intl.get(`${PREFIX}.model.orchestration.seqNumber`).d('序号'),
  BASIC: intl.get(`${PREFIX}.model.orchestration.basic`).d('编排信息'),
  BASIC_TIP: intl.get(`${PREFIX}.model.orchestration.basic.tip`).d('编排信息'),
  TASK_NAME: intl.get(`${PREFIX}.model.orchestration.http.task_name`).d('任务名称'),
  THREAD_MECHANISM: intl.get(`${PREFIX}.model.definition.threadMechanism`).d('线程执行机制'),
  THREAD_MECHANISM_TIP: intl
    .get(`${PREFIX}.model.definition.threadMechanism.tip`)
    .d(
      '线程执行机制，同步或异步，默认同步。同步即按照编排定义的流程顺序依次执行。选择异步时，当前任务将以异步线程执行，任务线程将直接返回，改结果非最终的任务结果。最终结果以节点执行结果为准。'
    ),
  FAILURE_STRATEGY: intl.get(''.concat(PREFIX, '.model.definition.failureStrategy')).d('失败策略'),
  FAILURE_STRATEGY_TIP: intl
    .get(''.concat(PREFIX, '.model.definition.failureStrategy.tip'))
    .d(
      '失败策略，终止或继续。任务节点的失败策略为空时使用编排实例的失败策略(全局)。任务的失败策略优先(覆盖)编排实例失败策略。' +
        '特别地，当编排实例(全局)失败策略为"终止"，节点失败策略若未设置或设置为"终止"时，当前节点失败则触发剩余节点强制结束；' +
        '当编排实例(全局)设置为"继续"，节点失败策略设置为"终止"时，当前节点失败仅中断当前分支。'
    ),
  TASK_NAME_TIP: intl
    .get(`${PREFIX}.model.orchestration.http.task_name.tip`)
    .d('名称，请确保同一编排内任务名称唯一。'),
  REQUEST_SETTING: intl.get(`${PREFIX}.model.orchestration.requestSetting`).d('请求设置'),
  REQUEST_SETTING_TIP: intl
    .get(`${PREFIX}.model.orchestration.requestSetting.tip`)
    .d(
      '请求基本信息设置，设置请求客户端(Http Client)的相关参数。仅当请求方法为PUT、POST、PATCH时，可设置请求体'
    ),
  REQUEST_QUERY: intl.get(`${PREFIX}.model.orchestration.requestQuery`).d('查询参数'),
  REQUEST_QUERY_TIP: intl
    .get(`${PREFIX}.model.orchestration.requestQuery.tip`)
    .d(
      '查询参数，设置请求客户端的查询参数，支持SpEL表达式，结合SpEL表达式从上个编排中获取(固定相应参数为result(map结构)，通过#result进行关联值获取，示例：#result["url"])上个请求的响应作为参数值。'
    ),
  TRANSFORM_TYPE: intl.get(`${PREFIX}.model.orchestration.transformType`).d('字段映射类型'),
  ADD_REQUEST_HEADER: intl.get(`${PREFIX}.view.button.addRequestHeader`).d('添加请求头'),
  CLEAR_REQUEST_HEADER: intl.get(`${PREFIX}.view.button.clearRequestHeader`).d('清空请求头'),

  ADD_PARAM: intl.get(`${PREFIX}.view.button.addParam`).d('添加参数'),
  CLEAR_PARAM: intl.get(`${PREFIX}.view.button.clearParam`).d('清空参数'),

  ONLINE: intl.get(`${PREFIX}.view.button.online`).d('上线'),
  OFFLINE: intl.get(`${PREFIX}.view.button.offline`).d('下线'),
  EXECUTE: intl.get(`${PREFIX}.view.button.execute`).d('运行'),
  DUPLICATE: intl.get(`${PREFIX}.view.button.duplicate`).d('克隆'),
  RERUN: intl.get(`${PREFIX}.view.button.rerun`).d('重跑'),
  PAUSE: intl.get(`${PREFIX}.view.button.pause`).d('暂停'),
  STOP: intl.get(`${PREFIX}.view.button.stop`).d('停止'),
  RESUME: intl.get(`${PREFIX}.view.button.resume`).d('恢复'),

  ADD_ASSERTION: intl.get(`${PREFIX}.view.button.addAssertion`).d('添加断言'),
  CLEAR_ASSERTION: intl.get(`${PREFIX}.view.button.clearAssertion`).d('清空断言'),

  FIELD_DATA: intl.get(`${PREFIX}.view.modal.fieldData`).d('字段数据'),

  STRUCTURE_NAME: intl.get(`${PREFIX}.model.orchestration.structureName`).d('最外层结构名称'),
  ORC_NAME_TIP: intl
    .get(`${PREFIX}.model.orchestration.orc_name.tip`)
    .d(
      '编排定义名称。确保唯一。当触发克隆动作时，默认使用"被克隆"定义名称+"_copy"(可后台自定义)作为新定义名称，字符超长则截断，若名称与已有名称重复则使用默认规则生成，编辑保存时可更改。'
    ),

  EXECUTE_TITLE: intl.get(`${PREFIX}.view.title.execute`).d('启动前请先设置参数'),
  FAILED_STRATEGY: intl.get(`${PREFIX}.model.orchestration.failedStrategy`).d('失败策略'),
  WARNING_TYPE: intl.get(`${PREFIX}.model.orchestration.warningType`).d('告警类型'),
  WORKER_GROUP: intl.get(`${PREFIX}.model.orchestration.workerGroup`).d('工作组'),
  TIMES: intl.get(`${PREFIX}.model.orchestration.times`).d('次'),
  MINUTES: intl.get(`${PREFIX}.model.orchestration.minutes`).d('分钟'),
  SECONDS: intl.get(`${PREFIX}.model.orchestration.minutes`).d('秒'),
  MILLI: intl.get(`${PREFIX}.model.orchestration.milli`).d('毫秒'),
  WORKER_GROUP_TIP: intl
    .get(`${PREFIX}.model.orchestration.workerGroup.tip`)
    .d('工作组，亦即任务分组，同一工作组的任务将会被对应的工作组的管理节点及工作负载节点处理。'),
  PREFERENCE: intl.get(`${PREFIX}.model.orchestration.preference`).d('偏好'),
  PREFERENCE_PLACEHOLDER: intl
    .get(`${PREFIX}.model.orchestration.preference_placeholder`)
    .d('偏好(用于编排任务获取筛选)'),
  COMPLEMENT: intl.get(`${PREFIX}.model.orchestration.complement`).d('补数'),
  EXEC_METHOD: intl.get(`${PREFIX}.model.orchestration.execMethod`).d('执行方式'),
  SERIAL_EXEC: intl.get(`${PREFIX}.model.orchestration.serialExec`).d('串行执行'),
  PARALLEL_EXEC: intl.get(`${PREFIX}.model.orchestration.parallelExec`).d('并行执行'),
  DISPATCH_DATE: intl.get(`${PREFIX}.model.orchestration.dispatchDate`).d('调度日期'),

  PARAM: intl.get(`${PREFIX}.model.orchestration.param`).d('参数'),

  DEFINITION_NAME: intl.get(`${PREFIX}.model.orchestration.definitionName`).d('编排定义名称'),
  NODE_NAME: intl.get(`${PREFIX}.model.orchestration.nodeName`).d('节点名称'),
  STATUS: intl.get(`${PREFIX}.model.orchestration.status`).d('状态'),
  DESCRIPTION: intl.get(`${PREFIX}.model.orchestration.description`).d('描述'),
  WORK_GROUP: intl.get(`${PREFIX}.model.orchestration.workGroup`).d('工作组'),
  PRIORITY: intl.get(`${PREFIX}.model.orchestration.priority`).d('优先级'),
  RETRY_TIMES: intl.get(`${PREFIX}.model.orchestration.retryTimes`).d('失败重试次数'),
  RETRY_TIMES_TIP: intl
    .get(`${PREFIX}.model.orchestration.retryTimes.tip`)
    .d('失败重试次数，失败后重试的次数，数值范围0-99，默认0即不重试。'),
  RETRY_INTERVAL: intl.get(`${PREFIX}.model.orchestration.retryInterval`).d('失败重试间隔'),
  RETRY_INTERVAL_TIP: intl
    .get(`${PREFIX}.model.orchestration.retryInterval.tip`)
    .d('失败重试间隔，数值范围0-99999，默认1，单位：分钟'),
  ALERT_FLAG: intl.get(`${PREFIX}.model.orchestration.alertFlag`).d('启用告警'),
  ALERT_FLAG_TIP: intl
    .get(`${PREFIX}.model.orchestration.timeoutWarning.tip`)
    .d(
      '告警标志，是否启用超时告警。启用时，可设置告警策略，设置告警时将执行告警任务，设置失败时即直接失败。'
    ),
  TIMEOUT_STRATEGY: intl.get(`${PREFIX}.model.orchestration.timeoutStrategy`).d('超时策略'),
  TIMEOUT_STRATEGY_TIP: intl
    .get(`${PREFIX}.model.orchestration.timeoutStrategy.tip`)
    .d(
      '超时策略，选择告警时，任务超时根据告警代码发送告警消息，默认发送任务超时告警消息；选择失败时，系统将任务超时作为一种失败情况，触发失败策略。'
    ),
  TIMEOUT_STRATEGY_WARNING: intl
    .get(`${PREFIX}.model.orchestration.timeoutStrategyWarning`)
    .d('警告'),
  TIMEOUT_STRATEGY_FAILURE: intl
    .get(`${PREFIX}.model.orchestration.timeoutStrategyFailure`)
    .d('失败'),
  STATUS_OFFLINE: intl.get(`${PREFIX}.model.orchestration.statusOffline`).d('已下线'),

  TIMEOUT_TIME: intl.get(`${PREFIX}.model.orchestration.timeoutTime`).d('超时时长'),
  REQUEST_METHOD: intl.get(`${PREFIX}.model.orchestration.requestMethod`).d('请求方法'),
  REQUEST_METHOD_TIP: intl
    .get(`${PREFIX}.model.orchestration.requestMethod.tip`)
    .d(
      '请求方法，支持GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS。仅当POST、PUT、PATCH时可设置请求体。'
    ),
  REQUEST_ADDRESS: intl.get(`${PREFIX}.model.orchestration.requestAddress`).d('请求地址'),
  REQUEST_CHARSET: intl.get(`${PREFIX}.model.orchestration.requestCharset`).d('请求字符集'),
  RESPONSE_CHARSET: intl.get(`${PREFIX}.model.orchestration.responseCharset`).d('响应字符集'),
  READ_TIMEOUT: intl.get(`${PREFIX}.model.orchestration.readTimeout`).d('读超时'),
  READ_TIMEOUT_TIP: intl
    .get(`${PREFIX}.model.orchestration.readTimeout.tip`)
    .d(
      '读超时时间，即readTimeout，服务器返回数据(response)的时间，单位（ms），超过该时间抛出read timeout，默认30s超时'
    ),
  CONNECTION_TIMEOUT: intl.get(`${PREFIX}.model.orchestration.connectionTimeout`).d('连接超时'),
  CONNECTION_TIMEOUT_TIP: intl
    .get(`${PREFIX}.model.orchestration.connectionTimeout.tip`)
    .d(
      '连接超时时间，即connectionTimeout，连接上服务器(握手成功)的时间，单位（ms），超出该时间抛出connect timeout，默认30s超时'
    ),
  ENABLE_RESULT_PROPAGATION: intl
    .get(`${PREFIX}.model.orchestration.enableResultPropagation`)
    .d('允许结果传递'),
  ENABLE_RESULT_PROPAGATION_TIP: intl
    .get(`${PREFIX}.model.orchestration.enableResultPropagation.tip`)
    .d(
      '允许结果传递。若开启则启动任务之间的结果传递，即上一个任务的结果作为下一个任务的参数，否之则否。' +
        '一般将上一个任务结果构造为map结果或直接将原文本内容进行传递，根据内容类型而定。默认开启。特别地，若当前任务的"线程执行机制"为"异步"，此功能将无效。'
    ),
  CHARSET_TIP: intl
    .get(`${PREFIX}.model.orchestration.charset_tip`)
    .d(
      '字符集。若需扩展异步移步至相应值集拓展即可，值集的值请确保为正确且真实的字符集编码。默认UTF-8'
    ),
  REQUEST_HEADER: intl.get(`${PREFIX}.model.orchestration.requestHeader`).d('请求头'),
  REQUEST_HEADER_TIP: intl
    .get(`${PREFIX}.model.orchestration.requestHeader.tip`)
    .d('请求头设置。当"可"设置请求体时，可通过点击内容类型设定请求头:Content-Type'),
  REQUEST_BODY: intl.get(`${PREFIX}.model.orchestration.requestBody`).d('请求体'),
  REQUEST_BODY_TIP: intl
    .get(`${PREFIX}.model.orchestration.requestBody.tip`)
    .d(
      '请求体设置，仅当请求方法为PUT、POST、PATCH时，可设置请求体。选择右侧下拉选切换请求体类型：Form即传统表单，内设单行可选择文件或者文件；File即上传单文件；Text可用来设置请求文本'
    ),
  EXPR_ENABLED: intl.get(`${PREFIX}.model.orchestration.exprEnable`).d('启用表达式'),
  ASSERTION: intl.get(`${PREFIX}.model.orchestration.assertion`).d('断言'),
  ASSERTION_TIP: intl
    .get(`${PREFIX}.model.orchestration.assertion.tip`)
    .d(
      '断言，即通过下列选项设定判定条件，设定多个条件时共为"与"的逻辑关系。不满足条件则终止当前任务，最终编排实例的结果需通过失败策略决定。'
    ),
  JSON_BODY: intl.get(`${PREFIX}.model.orchestration.assertion.jsonBody`).d('JSON Path'),
  JSON_BODY_TIP: intl
    .get(`${PREFIX}.model.orchestration.assertion.jsonBody.tip`)
    .d('JSON响应体需要通过JSON Path语法匹配对应值,可参考：'),
  XML_BODY: intl.get(`${PREFIX}.model.orchestration.assertion.xmlBody`).d('XPath 1.0'),
  XML_BODY_TIP: intl
    .get(`${PREFIX}.model.orchestration.assertion.xmlBody.tip`)
    .d('XML响应体需要通过XPath 1.0语法匹配对应值,可参考：'),
  SUBJECT: intl.get(`${PREFIX}.model.orchestration.subject`).d('对象'),
  CONDITION: intl.get(`${PREFIX}.model.orchestration.condition`).d('条件'),
  FIELD: intl.get(`${PREFIX}.model.orchestration.field`).d('字段'),
  KEY: intl.get(`${PREFIX}.model.orchestration.key`).d('键'),
  VALUE: intl.get(`${PREFIX}.model.orchestration.value`).d('值'),
  TYPE: intl.get(`${PREFIX}.model.orchestration.type`).d('类型'),
  EXPECTATION: intl.get(`${PREFIX}.model.orchestration.expectation`).d('期望值'),

  STATUS_SUBMITTED: intl.get(`${PREFIX}.model.orchestration.status_submitted`).d('已提交'),
  STATUS_RUNNING: intl.get(`${PREFIX}.model.orchestration.status_running`).d('正在运行'),
  STATUS_PREPARING_PAUSE: intl
    .get(`${PREFIX}.model.orchestration.status_preparing_pause`)
    .d('准备暂停'),
  STATUS_PAUSED: intl.get(`${PREFIX}.model.orchestration.status_paused`).d('已暂停'),
  STATUS_PREPARING_STOP: intl
    .get(`${PREFIX}.model.orchestration.status_preparing_stop`)
    .d('准备停止'),
  STATUS_STOPPED: intl.get(`${PREFIX}.model.orchestration.status_stopped`).d('已停止'),
  STATUS_FAILED: intl.get(`${PREFIX}.model.orchestration.status_failed`).d('已失败'),
  STATUS_SUCCESSFUL: intl.get(`${PREFIX}.model.orchestration.status_successful`).d('已成功'),
  STATUS_NEED_FAULT_TOLERANCE: intl
    .get(`${PREFIX}.model.orchestration.status_need_fault_tolerance`)
    .d('需容错'),
  STATUS_KILLED: intl.get(`${PREFIX}.model.orchestration.status_killed`).d('强制结束'),
  STATUS_THREAD_THREAD_WAITING: intl
    .get(`${PREFIX}.model.orchestration.status_thread_thread_waiting`)
    .d('等待线程资源'),
  STATUS_DEPENDENCY_WAITING: intl
    .get(`${PREFIX}.model.orchestration.status_dependency_waiting`)
    .d('等待依赖任务'),

  STATEMENT_START: intl.get(`${PREFIX}.model.orchestration.statement_start`).d('全新编排'),
  STATEMENT_START_CURRENT_TASK: intl
    .get(`${PREFIX}.model.orchestration.statement_start_current_task`)
    .d('从当前任务节点开始'),
  RECOVER_TOLERANCE_FAULT: intl
    .get(`${PREFIX}.model.orchestration.recover_tolerance_fault`)
    .d('恢复容错'),
  RECOVER_SUSPENDED: intl.get(`${PREFIX}.model.orchestration.recover_suspended`).d('恢复暂停'),
  START_FAILURE_TASK: intl
    .get(`${PREFIX}.model.orchestration.start_failure_task`)
    .d('从失败节点开始'),
  COMPLEMENT_DATA: intl.get(`${PREFIX}.model.orchestration.complement_data`).d('补数'),
  SCHEDULER: intl.get(`${PREFIX}.model.orchestration.scheduler`).d('调度任务'),
  REPEAT_RUNNING: intl.get(`${PREFIX}.model.orchestration.repeat_running`).d('重新运行'),
  RECOVER_WAITING_THREAD: intl
    .get(`${PREFIX}.model.orchestration.recover_waiting_thread`)
    .d('从等待线程恢复'),

  LOWEST: intl.get(`${PREFIX}.model.orchestration.lowest`).d('最低'),
  LOW: intl.get(`${PREFIX}.model.orchestration.low`).d('低'),
  MEDIUM: intl.get(`${PREFIX}.model.orchestration.medium`).d('中等'),
  HIGH: intl.get(`${PREFIX}.model.orchestration.high`).d('高'),
  HIGHEST: intl.get(`${PREFIX}.model.orchestration.highest`).d('最高'),

  SOURCE_TITLE: intl.get(`${PREFIX}.model.orchestration.sourceTitle`).d('来源结构'),
  TARGET_TITLE: intl.get(`${PREFIX}.model.orchestration.targetTitle`).d('目标结构'),

  FLOW_SMOOTH: intl.get(`${PREFIX}.view.editor.flowSmooth`).d('平滑线'),
  FLOW_POLYLINE: intl.get(`${PREFIX}.view.editor.flowPolyline`).d('折线'),
  FLOW_POLYLINE_ROUND: intl.get(`${PREFIX}.view.editor.flowPolylineRound`).d('平滑折线'),
  LABEL: intl.get(`${PREFIX}.view.editor.label`).d('说明'),
  SHAPE: intl.get(`${PREFIX}.view.editor.shape`).d('类型'),
  NODE: intl.get(`${PREFIX}.view.editor.node`).d('节点'),
  EDGE: intl.get(`${PREFIX}.view.editor.edge`).d('边线'),
  UNDO: intl.get(`${PREFIX}.view.editor.undo`).d('撤销'),
  COPY: intl.get(`${PREFIX}.view.editor.copy`).d('复制'),
  REDO: intl.get(`${PREFIX}.view.editor.redo`).d('重做'),
  LOCATION: intl.get(`${PREFIX}.view.editor.location`).d('定位'),
  PASTE: intl.get(`${PREFIX}.view.editor.paste`).d('粘贴'),
  ZOOM_IN: intl.get(`${PREFIX}.view.editor.zoomIn`).d('放大窗口'),
  ZOOM_OUT: intl.get(`${PREFIX}.view.editor.zoomOut`).d('缩小窗口'),
  AUTO_ZOOM: intl.get(`${PREFIX}.view.editor.autoZoom`).d('自适应窗口'),
  RESET_ZOOM: intl.get(`${PREFIX}.view.editor.resetZoom`).d('实际窗口'),
  MINI_MAP: intl.get(`${PREFIX}.view.editor.miniMap`).d('缩略图'),
  GRAPH_NOT_EMPTY: intl.get(`${PREFIX}.validation.graph.notEmpty`).d('编排定义画布未包含任何节点'),
  GRAPH_NODE_REPEAT: intl.get(`${PREFIX}.validation.node.repeat`).d('存在相同名称label'),
  GRAPH_CHANGED: intl.get(`${PREFIX}.validation.node.changed`).d('当前编排定义未保存，是否离开？'),
  CRON: intl.get(`${PREFIX}.view.button.cron`).d('cron计划'),
  EXE: intl.get(`${PREFIX}.view.button.exe`).d('执行'),
  START_PARAM: intl.get(`${PREFIX}.view.button.startParam`).d('启动参数'),
  GLOBAL_PARAM: intl.get(`${PREFIX}.view.button.globalParam`).d('全局参数'),
  LOCAL_PARAM: intl.get(`${PREFIX}.view.button.localParam`).d('局部参数'),
  VIEW_VAR: intl.get(`${PREFIX}.view.button.viewVar`).d('查看变量'),

  ALERT_CODE: intl.get(`${PREFIX}.model.orchestration.alertCode`).d('告警代码'),
  ALERT_CODE_TIP: intl
    .get(`${PREFIX}.model.orchestration.alertCode.tip`)
    .d(
      '告警代码，超时策略选择告警时，任务超时根据告警代码发送告警消息，默认发送任务超时告警消息。'
    ),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.orchestration.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.preposedMachine.saveEmpty`).d('无修改内容,无需保存'),

  CLEAR_HTTP_BODY_CONFIRM: intl
    .get(`${PREFIX}.view.message.clearHttpBodyConfirm`)
    .d('切换操作将会清空请求体的内容，确定切换吗'),
  UPLOAD_FILE: intl.get(`${PREFIX}.view.button.uploadFile`).d('上传文件'),

  GATEWAY_TITLE: intl.get(`${PREFIX}.modal.orchestration.condition.gateway.title`).d('网关信息'),
  GATEWAY: intl.get(`${PREFIX}.modal.orchestration.condition.gateway`).d('条件'),
  DEPENDENCY: intl.get(`${PREFIX}.modal.orchestration.condition.dependency`).d('维护'),
  CONDITION_RESULT: intl.get(`${PREFIX}.modal.orchestration.condition.result`).d('网关'),
  CONDITION_SUCCESS_NODE: intl.get(`${PREFIX}.modal.orchestration.condition.success`).d('成功'),
  CONDITION_FAILED_NODE: intl.get(`${PREFIX}.modal.orchestration.condition.failed`).d('失败'),
  GATEWAY_TIP: intl
    .get(`${PREFIX}.modal.orchestration.condition.gateway.tip`)
    .d('条件判定结果作为网关条件节点的执行结果。判定结果失败时会优先触发失败策略'),
  DEPENDENCY_TIP: intl
    .get(`${PREFIX}.modal.orchestration.condition.dependency.tip`)
    .d(
      '前置节点运行结果作为条件，未维护条件则判定失败。前置节点为多个时，未运行的节点，其运行结果默认失败。前置节点包含异步节点时，将忽略异步节点'
    ),
  CONDITION_RESULT_TIP: intl
    .get(`${PREFIX}.modal.orchestration.condition.result.tip`)
    .d('通过执行结果决定运行哪些节点,不指定则成功继续，失败终止'),
  CONDITION_SUCCESS_NODE_TIP: intl
    .get(`${PREFIX}.modal.orchestration.condition.success.tip`)
    .d('执行结果为成功时运行的节点'),
  CONDITION_FAILED_NODE_TIP: intl
    .get(`${PREFIX}.modal.orchestration.condition.failed.tip`)
    .d('执行结果为失败时运行的节点'),
};
