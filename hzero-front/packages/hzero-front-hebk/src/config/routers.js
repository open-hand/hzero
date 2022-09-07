module.exports = [
  {
    path: '/hebk/proxy',
    component: () => import('../routes/Proxy'),
  },
  {
    path: '/hebk/event',
    component: () => import('../routes/Event'),
  },
  {
    path: '/hebk/bank-account',
    component: () => import('../routes/BankAccount'),
  },
  {
    path: '/hebk/bill-transaction-record',
    component: () => import('../routes/BillTransactionRecord'),
  },
  {
    path: '/hebk/draft',
    components: [
      {
        path: '/hebk/draft/list',
        component: () => import('../routes/Draft'),
      },
      {
        path: '/hebk/draft/detail/:draftId',
        component: () => import('../routes/Draft/Detail'),
      },
      {
        path: '/hebk/draft/flow',
        component: () => import('../routes/Draft/Flow'),
      },
    ],
  },
  {
    path: '/hebk/trade-business',
    components: [
      {
        path: '/hebk/trade-business/list',
        component: () => import('../routes/TradeBusiness'),
      },
      {
        path: '/hebk/trade-business/detail/:businessId',
        component: () => import('../routes/TradeBusiness/Detail'),
      },
    ],
  },
];
