module.exports = [
  {
    path: '/hchg/service-charge',
    models: [],
    components: [
      {
        path: '/hchg/service-charge/list',
        component: () => import('../routes/ServiceCharge/List'),
        models: [() => import('../models/serviceCharge')],
      },
      {
        path: '/hchg/service-charge/create',
        component: () => import('../routes/ServiceCharge/Detail'),
        models: [() => import('../models/serviceCharge')],
      },
      {
        path: '/hchg/service-charge/detail/:id',
        component: () => import('../routes/ServiceCharge/Detail'),
        models: [() => import('../models/serviceCharge')],
      },
    ],
  },
  {
    path: '/hchg/purchase-detail',
    models: [],
    components: [
      {
        path: '/hchg/purchase-detail/list',
        component: () => import('../routes/PurchaseDetail/List'),
        models: [() => import('../models/purchaseDetail')],
      },
      {
        path: '/hchg/purchase-detail/detail/:id',
        component: () => import('../routes/PurchaseDetail/Detail'),
        models: [() => import('../models/purchaseDetail')],
      },
    ],
  },
  {
    path: '/hchg/service-bill',
    component: () => import('../routes/ServiceBill'),
    models: [() => import('../models/serviceBill')],
  },
  {
    path: '/hchg/bill',
    models: [],
    components: [
      {
        path: '/hchg/bill/list',
        component: () => import('../routes/Bill/List'),
        models: [],
      },
      {
        path: '/hchg/bill/line/:billNum',
        component: () => import('../routes/Bill/Detail'),
        models: [],
      },
    ],
  },
  // 账单记录
  {
    path: '/hchg/bill-callback-record',
    component: () => import('../routes/BillCallbackRecord'),
    models: [],
  },
  // 计费规则配置
  {
    path: '/hchg/charge-rule',
    models: [],
    components: [
      {
        path: '/hchg/charge-rule/list',
        component: () => import('../routes/ChargeRule/List'),
        models: [],
      },
      {
        path: '/hchg/charge-rule/create',
        component: () => import('../routes/ChargeRule/Detail'),
        models: [],
      },
      {
        path: '/hchg/charge-rule/detail/:id',
        component: () => import('../routes/ChargeRule/Detail'),
        models: [],
      },
    ],
  },
  // 账户余额
  {
    path: '/hchg/account-balance',
    models: [],
    components: [
      {
        path: '/hchg/account-balance/list',
        component: () => import('../routes/AccountBalance/List'),
        models: [],
      },
      {
        path: '/hchg/account-balance/create',
        component: () => import('../routes/AccountBalance/Create'),
        models: [],
      },
      {
        path: '/hchg/account-balance/detail/:id',
        component: () => import('../routes/AccountBalance/Detail'),
        models: [],
      },
      {
        path: '/hchg/account-balance/recharge/select',
        component: () => import('../routes/AccountBalance/Recharge'),
        models: [],
      },
      {
        path: '/hchg/account-balance/recharge/:balanceId',
        component: () => import('../routes/AccountBalance/Recharge'),
        models: [],
      },
    ],
  },
  // 充值记录
  {
    path: '/hchg/recharge-record',
    models: [],
    component: () => import('../routes/RechargeRecord'),
  },
  // 扣款记录
  {
    path: '/hchg/debit-record',
    models: [],
    component: () => import('../routes/DebitRecord'),
  },
  // 费用计算
  {
    path: '/hchg/payment-calculation',
    models: [],
    components: [
      {
        path: '/hchg/payment-calculation/calculate',
        component: () => import('../routes/PaymentCalculation'),
        models: [],
      },
      {
        path: '/hchg/payment-calculation/result/:ruleNum/:accountNum',
        component: () => import('../routes/PaymentCalculation/Detail'),
        models: [],
      },
    ],
  },
  {
    path: '/hchg/source-system-config',
    models: [],
    components: [
      {
        path: '/hchg/source-system-config/list',
        component: () => import('../routes/SourceSystemConfig/List'),
        models: [],
      },
      {
        path: '/hchg/source-system-config/create',
        component: () => import('../routes/SourceSystemConfig/Detail'),
        models: [],
      },
      {
        path: '/hchg/source-system-config/detail/:sourceSystemId',
        component: () => import('../routes/SourceSystemConfig/Detail'),
        models: [],
      },
    ],
  },
];
