module.exports = [
  {
    path: '/hpay/pay-config',
    component: () => import('../routes/PayConfig'),
    models: [() => import('../models/payConfig')],
  },
  {
    path: '/hpay/payment-order',
    component: () => import('../routes/PaymentOrder'),
    models: [() => import('../models/paymentOrder')],
  },
  {
    path: '/hpay/refund-order',
    component: () => import('../routes/RefundOrder'),
    models: [() => import('../models/refundOrder')],
  },
];
