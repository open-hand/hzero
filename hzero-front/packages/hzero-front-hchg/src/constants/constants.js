import intl from 'utils/intl';

/**
 * Constants 常量
 */
export const STATUS_LIST = [
  {
    status: 'SUCCESS',
    color: 'green',
    text: intl.get('hchg.bill.model.bill.status.success').d('充值成功'),
  },
  {
    status: 'ERROR',
    color: 'red',
    text: intl.get('hchg.bill.model.bill.status.error').d('充值失败'),
  },
  {
    status: 'PROCESSING',
    color: 'gold',
    text: intl.get('hchg.bill.model.bill.status.processing').d('充值未完成'),
  },
  {
    status: 'FAILURE',
    color: 'red',
    text: intl.get('hchg.bill.model.bill.status.failure').d('扣款失败'),
  },
  {
    status: 'NEW',
    color: 'green',
    text: intl.get('hzero.common.button.create').d('新建'),
  },
  {
    status: 'RELEASED',
    color: 'green',
    text: intl.get('hchg.bill.model.bill.status.released').d('已发布'),
  },
  {
    status: 'CANCELLED',
    color: 'red',
    text: intl.get('hchg.bill.model.bill.status.canceled').d('已取消'),
  },
];

export const CHARGE_RULE_CONSTANT = {
  COUNT: 'COUNT',
  PACKAGE: 'PACKAGE',
  NEW: 'NEW',
  BEFORE: 'BEFORE',
  AFTER: 'AFTER',
  TENANT: 'TENANT',
  USER: 'USER',
  RELEASED: 'RELEASED',
};

export const ACCOUNT_BALANCE_CONSTANT = {
  WXPAY: 'wxpay',
  ALIPAY: 'alipay',
  UNIONPAY: 'unionpay',
  USER: 'USER',
};

// 账单状态
export const BILL_STATUS_FIELDS = {
  // 未出账
  UNBILLED: 'UNBILLED',
  // 已出账
  BILLED: 'BILLED',
  // 已结账
  SETTLED: 'SETTLED',
};

// 账单状态Tag
export const BILL_STATUS_TAGS = [
  {
    status: 'UNBILLED',
    color: 'blue',
    text: intl.get('hchg.bill.model.billHeader.statusCode.unBilled').d('未出账'),
  },
  {
    status: 'BILLED',
    color: 'green',
    text: intl.get('hchg.bill.model.billHeader.billed').d('已出账'),
  },
  {
    status: 'SETTLED',
    color: 'red',
    text: intl.get('hchg.bill.model.billHeader.settled').d('已结账'),
  },
];

// 支付状态
export const PAYMENT_STATUS_FIELDS = {
  // 未支付
  UNPAID: 'UNPAID',
  // 支付中
  PROCESSING: 'PROCESSING',
  // 已支付
  PAID: 'PAID',
  // 支付失败
  FAILED: 'FAILED',
};

// 支付状态Tag
export const PAYMENT_STATUS_TAGS = [
  {
    status: 'UNPAID',
    color: 'blue',
    text: intl.get('hchg.bill.model.billHeader.statusCode.unpaid').d('未支付'),
  },
  {
    status: 'PROCESSING',
    color: 'orange',
    text: intl.get('hchg.bill.model.billHeader.statusCode.processing').d('支付中'),
  },
  {
    status: 'PAID',
    color: 'green',
    text: intl.get('hchg.bill.model.billHeader.statusCode.paid').d('已支付'),
  },
  {
    status: 'FAILED',
    color: 'red',
    text: intl.get('hchg.bill.model.billHeader.statusCode.failed').d('支付失败'),
  },
];

// 启用/禁用
export const ENABLED_FLAG_FIELDS = {
  // 是
  YES: 1,
  // 否
  NO: 0,
};
