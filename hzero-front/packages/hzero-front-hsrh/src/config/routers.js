module.exports = [
  // 索引配置
  {
    path: '/hsrh/search-config',
    models: [],
    components: [
      {
        // authorized: true,
        path: '/hsrh/search-config/list',
        component: () => import('../routes/SearchConfig'),
        models: [],
      },
      {
        // authorized: true,
        path: '/hsrh/search-config/:type/:indexId',
        component: () => import('../routes/SearchConfig/Detail'),
        models: [],
      },
    ],
  },
  // 查询配置
  {
    path: '/hsrh/inquiry-config',
    models: [],
    components: [
      {
        path: '/hsrh/inquiry-config/list',
        component: () => import('../routes/InquiryConfig'),
        models: [],
      },
      {
        path: '/hsrh/inquiry-config/:type/:configId',
        component: () => import('../routes/InquiryConfig/Detail'),
        models: [() => import('../models/inquiryConfig')],
      },
    ],
  },
  // 索引同步同步配置
  {
    path: '/hsrh/increment-sync',
    models: [],
    components: [
      {
        path: '/hsrh/increment-sync/list',
        component: () => import('../routes/IncrementSyncConfig'),
        models: [],
      },
      {
        path: '/hsrh/increment-sync/:type/:syncConfId',
        component: () => import('../routes/IncrementSyncConfig/Detail'),
        models: [],
      },
    ],
  },
  // 接口定义
  // {
  //   path: '/hsrh/interface-definition',
  //   models: [() => import('../models/scInterfaceDefinition')],
  //   components: [
  //     {
  //       path: '/hsrh/interface-definition/list',
  //       models: [() => import('../models/scInterfaceDefinition')],
  //       component: () => import('../routes/InterfaceDefinition'),
  //     },
  //     {
  //       path: '/hsrh/interface-definition/detail/:interfaceId',
  //       models: [() => import('../models/scInterfaceDefinition')],
  //       component: () => import('../routes/InterfaceDefinition/Detail'),
  //     },
  //   ],
  // },

  // 同步日志查询
  {
    path: '/hsrh/sync-log',
    models: [],
    components: [
      {
        path: '/hsrh/sync-log/list',
        component: () => import('../routes/SyncLog'),
        models: [],
      },
      {
        path: '/hsrh/sync-log/detail/:syncLogId',
        component: () => import('../routes/SyncLog/Detail'),
        models: [],
      },
    ],
  },

  // 搜索数据
  {
    authorized: true,
    path: '/hsrh/search-data',
    component: () => import('../routes/SearchData'),
    models: [],
  },
];
