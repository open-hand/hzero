import intl from 'utils/intl';

export const SERVICE_CONSTANT = {
  NEW: 'NEW',
  ENABLED: 'ENABLED',
  DISABLED: 'DISABLED',
  DISABLED_INPROGRESS: 'DISABLED_INPROGRESS',
  DS: 'DS',
  COMPOSITE: 'COMPOSITE',
  INTERNAL: 'INTERNAL',
  EXTERNAL: 'EXTERNAL',
  REST: 'REST',
  SOAP: 'SOAP',
};

// 字符串类型
export const STRING_LIST = ['VARCHAR', 'CHAR', 'LONGTEXT'];

// 数字类型
export const NUMBER_LIST = ['INT', 'FLOAT', 'TINYINT', 'BIGINT', 'DECIMAL'];

// 日期类型
export const DATE_LIST = ['DATE'];

// 时间类型
export const DATETIME_LIST = ['DATETIME', 'TIMESTAMP'];

export const FRONTAL_MACHINE_STATUS = {
  EDIT: ['NEW', 'DISABLED'],
  ENABLE: ['DISABLED'],
  DISABLE: ['ONLINE', 'OFFLINE'],
};

// 接口文档地址
export const DOCS_URI = 'http://hzerodoc.saas.hand-china.com/zh/docs/user-guide';

export const FRONTAL_JOB_JOB_STATUS = {
  EDIT: ['NEW', 'MODIFIED', 'PUBLISHED'],
  PUBLISH: ['NEW', 'MODIFIED'],
  DISABLE: ['PUBLISHED'],
  ENABLE: ['DISABLED'],
};

// 动态消息队列配置选项分类
export const DYNAMIC_MQ_OPTION_CLASS = {
  BINDER: 'BINDER', // 消息中间件
  BINDING: 'BINDING', // 消息绑定
};

// 动态消息队列配置选项分类
export const BINDING_TYPE = {
  PRODUCER: 'PRODUCER', // 生产者
};

export const BINDER_TYPE_STATUS = [
  {
    status: 'RABBITMQ',
    color: 'green',
    text: 'Rabbit MQ',
  },
  {
    status: 'ROCKETMQ',
    color: 'red',
    text: 'Rocket MQ',
  },
  {
    status: 'KAKFA',
    color: 'gold',
    text: 'Kafka',
  },
  {
    status: 'REDIS',
    color: 'green',
    text: 'Redis',
  },
];

export const BINDING_TYPE_STATUS = [
  {
    status: 'PRODUCER',
    color: 'green',
    text: intl.get('hitf.dynamicMqConfig.model.dynamicMqConfig.producer').d('生产者'),
  },
  {
    status: 'CONSUMER',
    color: 'gold',
    text: intl.get('hitf.dynamicMqConfig.model.dynamicMqConfig.consumer').d('消费者'),
  },
];

export const CHARSET_STATUS = [
  {
    status: 'UTF-8',
    color: 'green',
    text: 'UTF-8',
  },
  {
    status: 'GBK',
    color: 'gold',
    text: 'GBK',
  },
];

export const CONTENT_TYPE_STATUS = [
  {
    status: 'application/json',
    color: 'green',
    text: 'application/json',
  },
  {
    status: 'application/x',
    color: 'red',
    text: 'application/x-www-form-urlencoded',
  },
  {
    status: 'image/jpeg',
    color: 'gold',
    text: 'image/jpeg',
  },
  {
    status: 'image/png',
    color: 'green',
    text: 'image/png',
  },
  {
    status: 'multipart/form-data',
    color: 'orange',
    text: 'multipart/form-data',
  },
  {
    status: 'text/asp',
    color: 'blue',
    text: 'text/asp',
  },
  {
    status: 'text/css',
    color: 'yellow',
    text: 'text/css',
  },
  {
    status: 'text/html',
    color: 'blue',
    text: 'text/html',
  },
  {
    status: 'text/html; charset=UTF-8',
    color: 'lime',
    text: 'text/html',
  },
  {
    status: 'text/plain',
    color: 'green',
    text: 'text/plain',
  },
  {
    status: 'text/xml',
    color: 'purple',
    text: 'text/xml',
  },
];

export const FRONTAL_SERVER_TAG_STATUS = [
  {
    status: 'NEW',
    color: 'green',
    text: intl.get('hitf.preposedMachine.model.status.new').d('新建'),
  },
  {
    status: 'ONLINE',
    color: 'green',
    text: intl.get('hitf.preposedMachine.model.status.online').d('已上线'),
  },
  {
    status: 'OFFLINE',
    color: 'red',
    text: intl.get('hitf.preposedMachine.model.status.offline').d('已断开'),
  },
  {
    status: 'DISABLED',
    color: 'red',
    text: intl.get('hitf.preposedMachine.model.status.stop').d('已停用'),
  },
];

export const FRONTAL_JOB_TAG_JOB_STATUS = [
  {
    status: 'NEW',
    color: 'green',
    text: intl.get('hitf.scheduledTask.model.status.new').d('新建'),
  },
  {
    status: 'MODIFIED',
    color: 'gold',
    text: intl.get('hitf.scheduledTask.model.status.modefied').d('修改中'),
  },
  {
    status: 'PUBLISHED',
    color: 'green',
    text: intl.get('hitf.scheduledTask.model.status.published').d('已发布'),
  },
  {
    status: 'DISABLED',
    color: 'red',
    text: intl.get('hitf.scheduledTask.model.status.disabed').d('已失效'),
  },
];

// 需要展示Field的subject
export const SUBJECT = ['HEADER', 'JSON_BODY', 'XML_BODY'];

export const CAST_TYPE_MAP = {
  EXPR: 'EXPR', // 表达式
  SQL: 'SQL', // SQL转换
  URL: 'URL', // 接口调用
  LOV: 'LOV', // LOV值转换
  VAL: 'VAL', // 值映射
};

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

// 服务状态
export const SERVICE_STATUS_TAGS = [
  {
    status: 'NEW',
    color: 'gold',
  },
  {
    status: 'OFFLINE',
    color: 'red',
  },
  {
    status: 'PUBLISHED',
    color: 'green',
  },
];

// 接口状态
export const INTERFACE_STATUS_TAGS = [
  {
    status: 'ENABLED',
    color: 'green',
  },
  {
    status: 'DISABLED',
    color: 'red',
  },
  {
    status: 'DISABLED_INPROGRESS',
    color: 'orange',
  },
  {
    status: 'NEW',
    color: 'blue',
  },
];

// 服务类型
export const SERVICE_TYPE_TAGS = [
  {
    status: 'SOAP',
    color: 'blue',
  },
  {
    status: 'REST',
    color: 'purple',
  },
];

// 服务类别
export const SERVICE_CATEGORY_TAGS = [
  {
    status: 'INTERNAL',
    color: 'blue',
  },
  {
    status: 'EXTERNAL',
    color: 'orange',
  },
  {
    status: 'COMPOSITE',
    color: 'green',
  },
  {
    status: 'DS',
    color: 'gold',
  },
  {
    status: 'FILE',
    color: 'cyan',
  },
];
