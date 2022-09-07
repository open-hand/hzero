module.exports = [
  // 告警高级配置
  {
    path: '/halt/alert-advanced-config',
    component: () => import('../routes/AlarmSenior/List/ListPage'),
  },
  // 告警事件管理
  {
    path: '/halt/alert-event',
    component: () => import('../routes/AlertEvent'),
  },
  // 告警规则配置
  {
    path: '/halt/alert-rule',
    component: () => import('../routes/AlertRule'),
  },
];
