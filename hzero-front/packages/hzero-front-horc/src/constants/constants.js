import intl from 'utils/intl';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';

export const ORCH_DEF_STATUS = [
  {
    status: 'ONLINE',
    color: 'green',
    text: intl.get('horc.orchestration.model.status.online').d('已上线'),
  },
  {
    status: 'OFFLINE',
    color: 'red',
    text: intl.get('horc.orchestration.model.status.offline').d('已下线'),
  },
];

export const TAG_ORCH_TASK_TYPE = [
  {
    status: 'HTTP',
    color: 'blue',
  },
  {
    status: 'TRANSFORM',
    color: 'purple',
  },
  {
    status: 'CAST',
    color: 'orange',
  },
  {
    status: 'CONDITIONS',
    color: 'purple',
  },
];

export const TAG_ORCH_PRIORITY_NUM = [
  {
    status: 'LOWEST',
    color: 'red',
    text: intl.get('horc.orchestration.view.level.lowest').d('最低'),
  },
  {
    status: 'LOW',
    color: 'gold',
    text: intl.get('horc.orchestration.view.level.low').d('低'),
  },
  {
    status: 'MEDIUM',
    color: 'blue',
    text: intl.get('horc.orchestration.view.level.medium').d('中等'),
  },
  {
    status: 'HIGH',
    color: 'purple',
    text: intl.get('horc.orchestration.view.level.high').d('高'),
  },
  {
    status: 'HIGHEST',
    color: 'green',
    text: intl.get('horc.orchestration.view.level.highest').d('最高'),
  },
];

// 编排实例状态
export const TAG_ORCH_INS_STATUS = [
  {
    status: 'SUBMITTED',
    color: 'orange',
    text: ORCHESTRATION_LANG.STATUS_SUBMITTED,
  },
  {
    status: 'RUNNING',
    color: 'blue',
    text: ORCHESTRATION_LANG.STATUS_RUNNING,
  },
  {
    status: 'PREPARING_PAUSE',
    color: 'orange',
    text: ORCHESTRATION_LANG.STATUS_PREPARING_PAUSE,
  },
  {
    status: 'PAUSED',
    color: 'cyan',
    text: ORCHESTRATION_LANG.STATUS_PAUSED,
  },
  {
    status: 'PREPARING_STOP',
    color: 'orange',
    text: ORCHESTRATION_LANG.STATUS_PREPARING_STOP,
  },
  {
    status: 'STOPPED',
    color: 'red',
    text: ORCHESTRATION_LANG.STATUS_STOPPED,
  },
  {
    status: 'FAILED',
    color: 'red',
    text: ORCHESTRATION_LANG.STATUS_FAILED,
  },
  {
    status: 'SUCCESSFUL',
    color: 'green',
    text: ORCHESTRATION_LANG.STATUS_SUCCESSFUL,
  },
  {
    status: 'NEED_FAULT_TOLERANCE',
    color: 'gold',
    text: ORCHESTRATION_LANG.STATUS_NEED_FAULT_TOLERANCE,
  },
  {
    status: 'KILLED',
    color: 'red',
    text: ORCHESTRATION_LANG.STATUS_KILLED,
  },
  {
    status: 'THREAD_WAITING',
    color: 'purple',
    text: ORCHESTRATION_LANG.STATUS_THREAD_THREAD_WAITING,
  },
  {
    status: 'DEPENDENCY_WAITING',
    color: 'purple',
    text: ORCHESTRATION_LANG.STATUS_DEPENDENCY_WAITING,
  },
];

// 句柄类型
export const TAG_ORCH_STATEMENT_TYPE = [
  {
    status: 'START',
    color: 'green',
    text: ORCHESTRATION_LANG.STATEMENT_START,
  },
  {
    status: 'START_CURRENT_TASK',
    color: 'green',
    text: ORCHESTRATION_LANG.STATEMENT_START_CURRENT_TASK,
  },
  {
    status: 'RECOVER_TOLERANCE_FAULT',
    color: 'cyan',
    text: ORCHESTRATION_LANG.RECOVER_TOLERANCE_FAULT,
  },
  {
    status: 'RECOVER_SUSPENDED',
    color: 'gold',
    text: ORCHESTRATION_LANG.RECOVER_SUSPENDED,
  },
  {
    status: 'START_FAILURE_TASK',
    color: 'purple',
    text: ORCHESTRATION_LANG.START_FAILURE_TASK,
  },
  {
    status: 'COMPLEMENT_DATA',
    color: 'orange',
    text: ORCHESTRATION_LANG.COMPLEMENT_DATA,
  },
  {
    status: 'SCHEDULER',
    color: 'blue',
    text: ORCHESTRATION_LANG.SCHEDULER,
  },
  {
    status: 'REPEAT_RUNNING',
    color: 'blue',
    text: ORCHESTRATION_LANG.REPEAT_RUNNING,
  },
  {
    status: 'PAUSE',
    color: 'orange',
    text: ORCHESTRATION_LANG.PAUSE,
  },
  {
    status: 'STOP',
    color: 'red',
    text: ORCHESTRATION_LANG.STOP,
  },
  {
    status: 'RECOVER_WAITING_THREAD',
    color: 'gold',
    text: ORCHESTRATION_LANG.RECOVER_WAITING_THREAD,
  },
];

// 失败策略
export const TAG_ORCH_FAILURE_STRATEGY = [
  {
    status: 'FINISH',
    color: 'red',
    text: intl.get('horc.orchestration.view.failure.finish').d('终止'),
  },
  {
    status: 'CONTINUE',
    color: 'blue',
    text: intl.get('horc.orchestration.view.failure.continue').d('继续'),
  },
];

// 线程运行机制
export const TAG_ORCH_THREAD_MECHANISM = [
  {
    status: 'SYNC',
    color: 'blue',
    text: intl.get('horc.orchestration.view.threadMechanism.sync').d('同步'),
  },
  {
    status: 'ASYNC',
    color: 'purple',
    text: intl.get('horc.orchestration.view.threadMechanism.async').d('异步'),
  },
];

// 文本类型
export const TAG_ORCH_TEXT_TYPE = [
  {
    status: 'JSON',
    color: 'blue',
    text: 'JSON',
  },
  {
    status: 'XML',
    color: 'yellow',
    text: 'XML',
  },
  {
    status: 'TEXT',
    color: 'green',
    text: 'TEXT',
  },
  {
    status: 'OTHERS',
    color: 'grey',
    text: 'OTHERS',
  },
];

// 优先级
export const ORCH_PRIORITY = {
  0: 'LOWEST',
  25: 'LOW',
  50: 'MEDIUM',
  75: 'HIGH',
  100: 'HIGHEST',
};

// 优先级
export const ORCH_PRIORITY_NUM = {
  LOWEST: 0,
  LOW: 25,
  MEDIUM: 50,
  HIGH: 75,
  HIGHEST: 100,
};

// 现在BODY的方法
export const BODY_METHOD = ['PUT', 'POST', 'PATCH'];

// 编排定义状态-已下线
export const ORCH_DEF_STATUS_OFFLINE = 'OFFLINE';

// 编排定义状态-已上线
export const ORCH_DEF_STATUS_ONLINE = 'ONLINE';

// 需要展示Field的subject
export const SUBJECT = ['HEADER', 'JSON_BODY', 'XML_BODY'];

// 请求头content-type
export const CONTENT_TYPE = [
  {
    value: 'text/plain',
    meaning: 'Text',
    type: 'Text',
  },
  {
    value: 'application/json',
    meaning: 'JSON',
    type: 'Text',
  },
  {
    value: 'application/xml',
    meaning: 'XML',
    type: 'Text',
  },
  {
    value: 'text/html',
    meaning: 'HTML',
    type: 'Text',
  },
  {
    value: 'application/x-www-form-urlencoded',
    meaning: 'x-www-form-urlencoded',
    type: 'Form',
  },
  {
    value: 'multipart/form-data',
    meaning: 'form-data',
    type: 'Form',
  },
];

// work group
export const WORK_GROUP = [
  {
    value: 'default',
    meaning: 'default',
  },
];

// 请求体类型
export const REQUEST_BODY_TYPE = [
  {
    value: 'Text',
    meaning: 'Text',
  },
  {
    value: 'File',
    meaning: 'File',
  },
  {
    value: 'Form',
    meaning: 'Form',
  },
];

export const INSTANCE_BUTTONS = {
  RERUN: 'rerunning',
  PAUSE: 'paused',
  STOP: 'stopped',
  RESUME: 'resumed',
  EDIT: 'edit',
  DELETE: 'delete',
  GANTT: 'gantt',
};

export const ORCH_INS_STATUS = {
  SUBMITTED: 'SUBMITTED',
  RUNNING: 'RUNNING',
  PREPARING_PAUSE: 'PREPARING_PAUSE',
  PAUSED: 'PAUSED',
  PREPARING_STOP: 'PREPARING_STOP',
  STOPPED: 'STOPPED',
  FAILED: 'FAILED',
  SUCCESSFUL: 'SUCCESSFUL',
  NEED_FAULT_TOLERANCE: 'NEED_FAULT_TOLERANCE',
  KILLED: 'KILLED',
  THREAD_WAITING: 'THREAD_WAITING',
  DEPENDENCY_WAITING: 'DEPENDENCY_WAITING',
};

export const CAST_TYPE_MAP = {
  EXPR: 'EXPR', // 表达式
  SQL: 'SQL', // SQL转换
  URL: 'URL', // 接口调用
  LOV: 'LOV', // LOV值转换
  VAL: 'VAL', // 值映射
};

// Badge显示success的code
export const SUCCESS_STATUS = ['ONLINE', 'SUBMITTED', 'SUCCESSFUL'];

// Badge显示error的code
export const ERROR_STATUS = ['FAILED', 'KILLED'];

// Badge显示processing的code
export const PROCESSING_STATUS = ['RUNNING'];

// Badge显示warning的code
export const WARNING_STATUS = [
  'OFFLINE',
  'PREPARING_PAUSE',
  'PAUSED',
  'PREPARING_STOP',
  'STOPPED',
  'NEED_FAULT_TOLERANCE',
];

export const TRANSFORM_STATUS = {
  NEW: 'NEW',
  PUBLISHED: 'PUBLISHED',
  MODIFYING: 'MODIFYING',
};

// 字段映射状态
export const FIELD_MAPPING_TAG_STATUS = [
  {
    status: 'NEW',
    color: 'blue',
  },
  {
    status: 'MODIFYING',
    color: 'orange',
  },
  {
    status: 'PUBLISHED',
    color: 'green',
  },
];

// 数据映射状态
export const DATA_MAPPING_STATUS = {
  NEW: 'NEW',
  PUBLISHED: 'PUBLISHED',
  MODIFYING: 'MODIFYING',
};

// 数据映射状态
export const DATA_MAPPING_TAG_STATUS = [
  {
    status: 'NEW',
    color: 'blue',
  },
  {
    status: 'MODIFYING',
    color: 'orange',
  },
  {
    status: 'PUBLISHED',
    color: 'green',
  },
];

// 甘特图颜色
export const INS_STATUS_GANTT = {
  SUBMITTED: '#d4b7e0',
  RUNNING: '#169bce',
  PREPARING_PAUSE: '#f7bf09',
  PAUSED: '#d4bcb5',
  PREPARING_STOP: '#c8563c',
  STOPPED: '#ec2d0b',
  FAILED: '#ec2d0b',
  SUCCESSFUL: '#6fe534',
  NEED_FAULT_TOLERANCE: '#e57c48',
  KILLED: '#ec2d0b',
  THREAD_WAITING: '#b3d6b6',
  DEPENDENCY_WAITING: '#6a1bf1',
};

// 编排实例状态
export const INS_STATUS = {
  SUBMITTED: 'SUBMITTED',
  RUNNING: 'RUNNING',
  PREPARING_PAUSE: 'PREPARING_PAUSE',
  PAUSED: 'PAUSED',
  PREPARING_STOP: 'PREPARING_STOP',
  STOPPED: 'STOPPED',
  FAILED: 'FAILED',
  SUCCESSFUL: 'SUCCESSFUL',
  NEED_FAULT_TOLERANCE: 'NEED_FAULT_TOLERANCE',
  KILLED: 'KILLED',
  THREAD_WAITING: 'THREAD_WAITING',
  DEPENDENCY_WAITING: 'DEPENDENCY_WAITING',
};

export const WBS_KEY_ORCH = 'OrchTaskInstanceKey';
