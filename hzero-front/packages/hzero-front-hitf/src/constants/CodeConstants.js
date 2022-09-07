import intl from 'utils/intl';

/**
 * 集值
 */

// 计费类型
export const CHARGE_TYPE = 'HITF.CHARGE_TYPE';
export const CHARGE_TYPE_FIELDS = {
  // 接口
  INTERFACE: 'INTERFACE',
  // 服务
  SERVER: 'SERVER',
};

// 计费类型Tag
export const CHARGE_TYPE_TAGS = [
  {
    status: 'INTERFACE',
    color: 'blue',
    text: intl.get('hitf.chargeSet.view.tag.typeCode.interface').d('接口'),
  },
  {
    status: 'SERVER',
    color: 'green',
    text: intl.get('hitf.chargeSet.view.tag.typeCode.server').d('服务'),
  },
];

// 结算周期
export const SETTLEMENT_PERIOD = 'HITF.SETTLEMENT_PERIOD';

// 付费模式
export const PAYMENT_MODEL = 'HCHG.RULE.PAYMENT_MODEL';
export const PAYMENT_MODEL_FIELDS = {
  // 预付费
  BEFORE: 'BEFORE',
  // 后付费
  AFTER: 'AFTER',
};

// 付费模式Tag
export const PAYMENT_MODEL_TAGS = [
  {
    status: 'BEFORE',
    color: 'blue',
    text: intl.get('hitf.chargeSet.view.tag.paymentModel.before').d('预付费'),
  },
  {
    status: 'AFTER',
    color: 'green',
    text: intl.get('hitf.chargeSet.view.tag.paymentModel.after').d('后付费'),
  },
];

// 计费方式
export const CHARGE_METHOD = 'HCHG.RULE.CHARGE_METHOD';
export const CHARGE_METHOD_FIELDS = {
  // 计量
  COUNT: 'COUNT',
  // 总包
  PACKAGE: 'PACKAGE',
};

// 计费方式Tag
export const CHARGE_METHOD_TAGS = [
  {
    status: 'COUNT',
    color: 'blue',
    text: intl.get('hitf.chargeSet.view.tag.chargeMethod.count').d('计量'),
  },
  {
    status: 'PACKAGE',
    color: 'green',
    text: intl.get('hitf.chargeSet.view.tag.chargeMethod.package').d('总包'),
  },
];

// 计费设置状态
export const CHARGE_SET_STATUS = 'HITF.CHARGE_SET_STATUS';
export const CHARGE_SET_STATUS_FIELDS = {
  // 新建
  NEW: 'NEW',
  // 已发布
  PUBLISHED: 'PUBLISHED',
  // 已撤销
  CANCELLED: 'CANCELLED',
};

// 计费设置状态Tag
export const CHARGE_SET_STATUS_TAGS = [
  {
    status: 'NEW',
    color: 'blue',
    text: intl.get('hitf.chargeSet.view.tag.statusCode.new').d('新建'),
  },
  {
    status: 'PUBLISHED',
    color: 'green',
    text: intl.get('hitf.chargeSet.view.tag.statusCode.published').d('已发布'),
  },
  {
    status: 'CANCELLED',
    color: 'red',
    text: intl.get('hitf.chargeSet.view.tag.statusCode.cancelled').d('已撤销'),
  },
];

// 组合计费设置状态
export const CHARGE_GROUP_STATUS = 'HITF.CHARGE_GROUP_STATUS';
export const CHARGE_GROUP_STATUS_FIELDS = {
  // 新建
  NEW: 'NEW',
  // 已发布
  PUBLISHED: 'PUBLISHED',
  // 已撤销
  CANCELLED: 'CANCELLED',
};

// 组合计费设置状态Tag
export const CHARGE_GROUP_STATUS_TAGS = [
  {
    status: 'NEW',
    color: 'blue',
    text: intl.get('hitf.chargeGroup.view.tag.statusCode.new').d('新建'),
  },
  {
    status: 'PUBLISHED',
    color: 'green',
    text: intl.get('hitf.chargeGroup.view.tag.statusCode.published').d('已发布'),
  },
  {
    status: 'CANCELLED',
    color: 'red',
    text: intl.get('hitf.chargeGroup.view.tag.statusCode.cancelled').d('已撤销'),
  },
];

// 组合计费服务类型
export const GROUP_SERVER_TYPE = 'HITF.GROUP_SERVER_TYPE';
export const GROUP_SERVER_TYPE_FIELDS = {
  // 接口
  INTERFACE: 'INTERFACE',
  // 服务
  SERVER: 'SERVER',
};

// 组合计费类型Tag
export const GROUP_SERVER_TYPE_TAGS = [
  {
    status: 'INTERFACE',
    color: 'blue',
    text: intl.get('hitf.chargeGroup.view.tag.typeCode.interface').d('接口'),
  },
  {
    status: 'SERVER',
    color: 'green',
    text: intl.get('hitf.chargeGroup.view.tag.typeCode.server').d('服务'),
  },
];

// 计费规则Lov
export const CHARGE_RULE = 'HCHG.RULE_HEADER';

// 计费服务Lov
export const CHARGE_SERVER = 'HITF.CHARGE_SERVER';

// 计费接口Lov
export const CHARGE_INTERFACE = 'HITF.CHARGE_INTERFACE';

// 服务类型
export const PURCHASE_TYPE = 'HITF.PURCHASE_TYPE';
export const PURCHASE_TYPE_FIELDS = {
  // 接口
  INTERFACE: 'INTERFACE',
  // 服务
  SERVER: 'SERVER',
  // 组合
  GROUP: 'GROUP',
};

// 服务类型Tag
export const PURCHASE_TYPE_TAGS = [
  {
    status: 'INTERFACE',
    color: 'blue',
    text: intl.get('hitf.purchase.view.tag.typeCode.interface').d('接口'),
  },
  {
    status: 'SERVER',
    color: 'green',
    text: intl.get('hitf.purchase.view.tag.typeCode.server').d('服务'),
  },
  {
    status: 'GROUP',
    color: 'orange',
    text: intl.get('hitf.purchase.view.tag.typeCode.group').d('组合'),
  },
];

// 服务可用状态
export const AVAILABLE_STATUS = 'HITF.AVAILABLE_STATUS';
export const AVAILABLE_STATUS_FIELDS = {
  // 账单支付
  NEED_PAY: 'NEED_PAY',
  // 可用
  AVAILABLE: 'AVAILABLE',
  // 结算中
  BILLING: 'BILLING',
  // 已到期
  USE_UP: 'USE_UP',
};

// 服务类型Tag
export const AVAILABLE_STATUS_TAGS = [
  {
    status: 'NEED_PAY',
    color: 'blue',
    text: intl.get('hitf.purchase.view.tag.availableStatus.needPay').d('账单支付'),
  },
  {
    status: 'AVAILABLE',
    color: 'green',
    text: intl.get('hitf.purchase.view.tag.availableStatus.available').d('可用'),
  },
  {
    status: 'BILLING',
    color: 'orange',
    text: intl.get('hitf.purchase.view.tag.availableStatus.billing').d('结算中'),
  },
  {
    status: 'USE_UP',
    color: 'red',
    text: intl.get('hitf.purchase.view.tag.availableStatus.useUp').d('已到期'),
  },
];
// 租户
export const TENANT = 'HPFM.TENANT';
// 服务地址
export const ROUTE_INFORMATION = 'HADM.ROUTE_INFORMATION';
// 证书
export const CERTIFICATE = 'HPFM.CERTIFICATE';
// 加密方式
export const SOAP_WSS_PASSWORD_TYPE = 'HITF.SOAP_WSS_PASSWORD_TYPE';
// SOAP版本
export const SOAP_VERSION = 'HITF.SOAP_VERSION';
// 请求方式
export const REQUEST_METHOD = 'HITF.REQUEST_METHOD';
// 请求头
export const REQUEST_HEADER = 'HITF.REQUEST_HEADER';
// 发布类型
export const SERVICE_TYPE = 'HITF.SERVICE_TYPE';
// 发布类型
export const INTERFACE_STATUS = 'HITF.INTERFACE_STATUS';
// 服务类别
export const SERVICE_CATEGORY = 'HITF.SERVICE_CATEGORY';
// 协议
export const PROTOCOL = 'HITF.PROTOCOL';
// 数据源类型
export const DATABASE_TYPE = 'HPFM.DATABASE_TYPE';
// 数据源
export const DATA_SOURCE = 'HITF.DATASOURCE';
// 表达式类型
export const EXPRESSION_TYPE = 'HITF.SVC.EXPRESSION_TYPE';
// 属性字段类型
export const SVC_COL_TYPE = 'HITF.SVC.COL_TYPE';
// 参数类型
export const SVC_PARAM_TYPE = 'HITF.SVC.PARAM_TYPE';
// 隐私级别
export const SVC_PRIVACY_LEVEL = 'HITF.SVC.PRIVACY_LEVEL';
// 操作符
export const SVC_MODEL_OPERATOR = 'HITF.SVC.MODEL_OPERATOR';

// 结构分类
export const STRUCTURE_CATEGORY = 'HITF.STRUCTURE_CATEGORY';
export const STRUCTURE_CATEGORY_FIELDS = {
  // 结构映射
  MAPPING: 'MAPPING',
};

// 结构分类Tags
export const STRUCTURE_CATEGORY_TAGS = [
  {
    status: 'MAPPING',
    color: 'blue',
    text: intl.get('hitf.structureField.view.tag.structureCategory.mapping').d('结构映射'),
  },
];

// 业务用途
export const STRUCTURE_BIZ_USAGE = 'HITF.STRCTURE.LINE.BIZ_USAGE';

// 业务用途Tags
export const STRUCTURE_BIZ_USAGE_TAGS = [
  {
    status: 'ITG_PAYLOAD',
    color: 'blue',
    text: intl.get('hitf.structureField.view.tag.bizUsage.itgPayload').d('集成平台响应映射'),
  },
];

// 启用/禁用
export const ENABLED_FLAG = 'HPFM.ENABLED_FLAG';
// 是/否
export const YES_OR_NO_FLAG = 'HPFM.FLAG';
export const ENABLED_FLAG_FIELDS = {
  // 是
  YES: 1,
  // 否
  NO: 0,
};

// 字段类型
export const STRUCTURE_FIELD_TYPE = 'HITF.STRCTURE.LINE.FILED_TYPE';
export const STRUCTURE_FIELD_TYPE_FIELDS = {
  // 对象
  OBJECT: 'OBJECT',
  // 数组
  ARRAY: 'ARRAY',
  // 字符
  STRING: 'STRING',
  // 数字
  DIGITAL: 'DIGITAL',
  // 布尔
  BOOL: 'BOOL',
};
export const STRUCTURE_FIELD_TYPE_VALUES = {
  OBJECT: {
    value: 'OBJECT',
    meaning: intl.get('hitf.structureField.model.structureField.object').d('对象'),
  },
  ARRAY: {
    value: 'ARRAY',
    meaning: intl.get('hitf.structureField.model.structureField.array').d('数组'),
  },
  STRING: {
    value: 'STRING',
    meaning: intl.get('hitf.structureField.model.structureField.char').d('字符'),
  },
  DIGITAL: {
    value: 'DIGITAL',
    meaning: intl.get('hitf.structureField.model.structureField.number').d('数字'),
  },
  BOOL: {
    value: 'BOOL',
    meaning: intl.get('hitf.structureField.model.structureField.boolean').d('布尔'),
  },
};

// 构建方式
export const STRUCTURE_COMPOSITION = 'HITF.STRCTURE.COMPOSITION';
export const STRUCTURE_COMPOSITION_FIELDS = {
  // 行结构
  ROW: 'ROW',
  // 树结构
  TREE: 'TREE',
};

// 构建方式Tags
export const STRUCTURE_COMPOSITION_TAGS = [
  {
    status: 'ROW',
    color: 'blue',
    text: intl.get('hitf.structureField.view.tag.composition.row').d('行结构'),
  },
  {
    status: 'TREE',
    color: 'green',
    text: intl.get('hitf.structureField.view.tag.composition.tree').d('树结构'),
  },
];

// 中间件类型
export const DYNAMIC_MQ_BINDER_TYPE = 'HITF.DYNAMIC_MQ.BINDER_TYPE';
// 绑定类型
export const DYNAMIC_MQ_BINDING_TYPE = 'HITF.DYNAMIC_MQ.BINDING_TYPE';
// 字符集
export const CHARSET = 'HITF.CHARSET';
// 内容类型
export const CONTENT_TYPE = 'HITF.CONTENT_TYPE';
// 编排定义状态
export const ORCH_DEF_STATUS = 'HORC.ORCH.DEF_STATUS';
// 参数类型
export const PARAM_VALUE_TYPE = 'HITF.PARAM_VALUE_TYPE';
// 断言主题
export const ORCH_ASSERTION_SUBJECT = 'HORC.ORCH.ASSERTION_SUBJECT';
// 断言操作符
export const ORCH_ASSERTION_CONDITION = 'HORC.ORCH.ASSERTION_CONDITION';
// 断言请求方法
export const ORCH_HTTP_METHOD = 'HORC.ORCH.HTTP_METHOD';
// 断言请求体参数类型
export const ORCH_HTTP_PARAMETER_TYPE = 'HORC.ORCH.HTTP_PARAMETER_TYPE';
// 编排优先级
export const ORCH_PRIORITY = 'HORC.ORCH.PRIORITY';
// 编排告警类型
export const ORCH_WARNING_TYPE = 'HORC.ORCH.WARNING_TYPE';
// 失败策略
export const ORCH_FAILURE_STRATEGY = 'HORC.ORCH.FAILURE_STRATEGY';
// 编排实例状态
export const ORCH_INSTANCE_STATUS = 'HORC.ORCH.INSTANCE_STATUS';
// 执行类型
export const ORCH_STATEMENT_TYPE = 'HORC.ORCH.STATEMENT.STATEMENT_TYPE';
// 任务类型
export const ORCH_TASK_TYPE = 'HORC.ORCH.TASK_TYPE';
// 线程执行机制
export const ORCH_THREAD_MECHANISM = 'HORC.ORCH.THREAD_MECHANISM';
// 映射转化类型
export const TRANSFORM_TYPE = 'HORC.TRANSFORM_TYPE';
// 数据转换来源数据类型
export const CAST_DATA_TYPE = 'HORC.CAST_DATA_TYPE';
// 数据转换类型
export const CAST_TYPE = 'HORC.CAST_TYPE';
// 映射字段类型
export const MAPPING_FIELD_TYPE = 'HORC.MAPPING_FIELD_TYPE';
// 映射 - 平台级
export const TRANSFORM_LIST_SITE = 'HORC.TRANSFORM_LIST.SITE';
// 映射
export const TRANSFORM_LIST = 'HORC.TRANSFORM_LIST';
// 转化 - 平台级
export const CAST_LIST_SITE = 'HORC.CAST_LIST.SITE';
// 转化
export const CAST_LIST = 'HORC.CAST_LIST';
// 数据转化比较判定条件类型
export const CAST_COMPARISON_TYPE = 'HORC.CAST_COMPARISON_TYPE';
// 数据转化映射来源多条件连接符
export const CAST_CONJUNCTION_TYPE = 'HORC.CAST_CONJUNCTION_TYPE';
// 数据转化表达式字段来源类型
export const CAST_EXPR_FIELD_SOURCE_TYPE = 'HORC.CAST_EXPR_FIELD_SOURCE_TYPE';
// 语言
export const LANGUAGE = 'HPFM.LANGUAGE';
// 字段映射版本状态
export const TRANSFORM_STATUS = 'HORC.TRANSFORM_STATUS';
// 数据映射映射版本状态
export const CAST_HEADER_STATUS = 'HORC.CAST_HEADER.STATUS';
// 服务状态
export const SERVICE_STATUS = 'HITF.INTERFACE_SERVER.STATUS';
// 日志调用详情
export const LOG_RECORD_TYPE = 'HITF.LOG_RECORD_TYPE';
// 认证层级
export const AUTH_LEVEL = 'HITF.AUTH_LEVEL';
// 认证模式
export const AUTH_TYPE = 'HITF.AUTH_TYPE';
// 认证层级值
export const SELF_TENANT = 'HITF.SELF_TENANT';
// 认证层级值
export const USER_ROLE = 'HITF.USER_ROLE';
// 认证层级值
export const APPLICATION_CLIENT = 'HITF.APPLICATION.CLIENT';
