module.exports = [
  // 前置机维护
  {
    path: '/hfnt/preposed-machine',
    component: () => import('../routes/PreposedMachine'),
  },
  // 定时任务配置
  {
    path: '/hfnt/scheduled-task',
    component: () => import('../routes/ScheduledTask'),
  },
  //前置机日志
  {
    path: '/hfnt/preposed-machine-log', // 路由
    component: () => import('../routes/PreposedMachineLogs'),
  },

  //前置机程序管理
  {
    path: '/hfnt/preposed-machine-management', // 路由
    component: () => import('../routes/PreposedMachineManagement'),
  },
];
