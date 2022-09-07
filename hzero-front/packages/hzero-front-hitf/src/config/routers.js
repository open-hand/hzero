module.exports = [
  {
    path: '/hitf/application',
    component: () => import('../routes/Application'),
    models: [() => import('../models/application')],
  },
  // 接口监控
  {
    path: '/hitf/interface-logs',
    models: [() => import('../models/interfaceLogs')],
    components: [
      {
        path: '/hitf/interface-logs/list',
        component: () => import('../routes/InterfaceLogs'),
        models: [() => import('../models/interfaceLogs')],
      },
      {
        path: '/hitf/interface-logs/detail/:interfaceLogId',
        component: () => import('../routes/InterfaceLogs/Detail'),
        models: [() => import('../models/interfaceLogs')],
      },
      {
        path: '/hitf/interface-logs/outer-detail/:invokeKey',
        component: () => import('../routes/InterfaceLogs/Detail'),
        models: [() => import('../models/interfaceLogs')],
      },
    ],
  },
  {
    path: '/private/hitf/interface-logs',
    key: '/private/hitf/interface-logs',
    authorized: true,
    components: [
      {
        path: '/private/hitf/interface-logs/list',
        key: '/private/hitf/interface-logs/list',
        authorized: true,
        component: () => import('../routes/InterfaceLogs'),
        models: [() => import('../models/interfaceLogs')],
      },
      {
        path: '/private/hitf/interface-logs/detail/:interfaceLogId',
        key: '/private/hitf/interface-logs/detail/:interfaceLogId',
        authorized: true,
        component: () => import('../routes/InterfaceLogs/Detail'),
        models: [() => import('../models/interfaceLogs')],
      },
    ],
    models: [() => import('../models/interfaceLogs')],
  },
  // 健康状况监控
  {
    path: '/hitf/interface-statistics',
    component: () => import('../routes/InterfaceStatistics'),
    models: [() => import('../models/interfaceStatistics')],
  },
  {
    path: '/private/hitf/interface-statistics',
    key: '/private/hitf/interface-statistics',
    authorized: true,
    component: () => import('../routes/InterfaceStatistics'),
    models: [() => import('../models/interfaceStatistics')],
  },
  {
    path: '/hitf/import-history',
    component: () => import('../routes/ImportHistory/List'),
  },
  {
    path: '/private/hitf/import-history',
    key: '/private/hitf/import-history',
    authorized: true,
    component: () => import('../routes/ImportHistory/List'),
  },
  {
    path: '/hitf/services',
    models: [() => import('../models/services')],
    components: [
      {
        path: '/hitf/services/list',
        component: () => import('../routes/Services'),
        models: [() => import('../models/services')],
      },
      {
        path: '/hitf/services/detail/:id',
        component: () => import('../routes/Services/Detail'),
        models: [() => import('../models/services')],
      },
      {
        path: '/hitf/services/create',
        component: () => import('../routes/Services/Detail'),
        models: [() => import('../models/services')],
      },
    ],
  },
  {
    path: '/hitf/client-auth',
    component: () => import('../routes/ClientAuth'),
    models: [() => import('../models/clientAuth')],
  },
  {
    path: '/private/hitf/client-auth',
    key: '/private/hitf/client-auth',
    authorized: true,
    component: () => import('../routes/ClientAuth'),
    models: [() => import('../models/clientAuth')],
  },
  {
    path: '/hitf/interfaces',
    models: [() => import('../models/interfaces')],
    components: [
      {
        path: '/hitf/interfaces/list',
        component: () => import('../routes/Interfaces'),
        models: [() => import('../models/interfaces')],
      },
      {
        path: '/hitf/interfaces/auth-config/:interfaceId',
        component: () => import('../routes/Interfaces/AuthConfig'),
        models: [() => import('../models/interfaces')],
      },
    ],
  },
  {
    path: '/private/hitf/interfaces',
    key: '/private/hitf/interfaces',
    authorized: true,
    components: [
      {
        path: '/private/hitf/interfaces/list',
        key: '/private/hitf/interfaces/list',
        authorized: true,
        component: () => import('../routes/Interfaces'),
        models: [() => import('../models/interfaces')],
      },
      {
        path: '/private/hitf/interfaces/auth-config/:interfaceId',
        key: '/private/hitf/interfaces/auth-config/:interfaceId',
        authorized: true,
        component: () => import('../routes/Interfaces/AuthConfig'),
        models: [() => import('../models/interfaces')],
      },
    ],
    models: [() => import('../models/interfaces')],
  },
  {
    path: '/hitf/client-role',
    component: () => import('../routes/ClientRole'),
    models: [() => import('../models/clientRole')],
  },
  {
    path: '/private/hitf/client-role',
    key: '/private/hitf/client-role',
    authorized: true,
    component: () => import('../routes/ClientRole'),
    models: [() => import('../models/clientRole')],
  },
  {
    path: '/pub/hitf/document-view/:interfaceId',
    component: () => import('../routes/Services/DocumentView'),
    authorized: true, // authorized 不需要菜单权限就可以打开的页面
    key: '/pub/hitf/document-view/:interfaceId',
    models: [() => import('../models/services')],
  },
  {
    path: '/hitf/application-type-definition',
    components: [
      {
        path: '/hitf/application-type-definition/list',
        component: () => import('../routes/TypeDefinition/List'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/hitf/application-type-definition/detail/:id',
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/hitf/application-type-definition/create',
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
    ],
  },
  {
    path: '/hitf/application-type-definition',
    components: [
      {
        path: '/hitf/application-type-definition/list',
        component: () => import('../routes/TypeDefinition/List'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/hitf/application-type-definition/detail/:id',
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/hitf/application-type-definition/create',
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
    ],
  },
  {
    path: '/private/hitf/application-type-definition',
    key: '/private/hitf/application-type-definition',
    authorized: true,
    components: [
      {
        path: '/private/hitf/application-type-definition/list',
        key: '/private/hitf/application-type-definition/list',
        authorized: true,
        component: () => import('../routes/TypeDefinition/List'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/private/hitf/application-type-definition/create',
        key: '/private/hitf/application-type-definition/create',
        authorized: true,
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
      {
        path: '/private/hitf/application-type-definition/detail/:id',
        key: '/private/hitf/application-type-definition/detail/:id',
        authorized: true,
        component: () => import('../routes/TypeDefinition/Detail'),
        models: [() => import('../models/typeDefinition')],
      },
    ],
  },
  {
    path: '/hitf/charge-set',
    key: '/hitf/charge-set',
    components: [
      {
        path: '/hitf/charge-set/list',
        key: '/hitf/charge-set/list',
        component: () => import('../routes/ChargeSet/List'),
        models: [],
      },
      {
        path: '/hitf/charge-set/create',
        key: '/hitf/charge-set/create',
        component: () => import('../routes/ChargeSet/Detail'),
        models: [],
      },
      {
        path: '/hitf/charge-set/line/:setHeaderId',
        key: '/hitf/charge-set/line/:setHeaderId',
        component: () => import('../routes/ChargeSet/Detail'),
        models: [],
      },
      {
        path: '/hitf/charge-set/purchase-list/:typeCode/:id',
        key: '/hitf/charge-set/purchase-list/:typeCode/:id',
        component: () => import('../routes/ChargeSet/PurchaseList'),
        models: [],
      },
    ],
  },
  {
    path: '/hitf/charge-group',
    key: '/hitf/charge-group',
    components: [
      {
        path: '/hitf/charge-group/list',
        key: '/hitf/charge-group/list',
        component: () => import('../routes/ChargeGroup/List'),
        models: [],
      },
      {
        path: '/hitf/charge-group/create',
        key: '/hitf/charge-group/create',
        component: () => import('../routes/ChargeGroup/Detail'),
        models: [],
      },
      {
        path: '/hitf/charge-group/line/:groupHeaderId',
        key: '/hitf/charge-group/line/:groupHeaderId',
        component: () => import('../routes/ChargeGroup/Detail'),
        models: [],
      },
      {
        path: '/hitf/charge-group/rule/:groupHeaderId',
        key: '/hitf/charge-group/rule/:groupHeaderId',
        component: () => import('../routes/ChargeGroup/Rule'),
        models: [],
      },
      {
        path: '/hitf/charge-group/server/:groupHeaderId',
        key: '/hitf/charge-group/server/:groupHeaderId',
        component: () => import('../routes/ChargeGroup/Server'),
        models: [],
      },
      // {
      //   path: '/hitf/charge-group/purchase/:groupHeaderId',
      //   key: '/hitf/charge-group/purchase/:groupHeaderId',
      //   component: () => import('../routes/ChargeGroup/Purchase'),
      //   models: [],
      // },
      {
        path: '/hitf/charge-group/purchase-list/:typeCode/:id',
        key: '/hitf/charge-group/purchase-list/:typeCode/:id',
        component: () => import('../routes/ChargeSet/PurchaseList'),
        models: [],
      },
    ],
  },
  {
    path: '/hitf/user-purchase',
    key: '/hitf/user-purchase',
    components: [
      {
        path: '/hitf/user-purchase/list',
        key: '/hitf/user-purchase/list',
        component: () => import('../routes/Purchase/UserPurchase'),
        models: [],
      },
      {
        path: '/hitf/user-purchase/rule/:ruleHeaderId',
        key: '/hitf/user-purchase/rule/:ruleHeaderId',
        component: () => import('../routes/ChargeRule/Detail'),
        models: [],
      },
    ],
  },
  {
    path: '/hitf/available-purchase',
    key: '/hitf/available-purchase',
    components: [
      {
        path: '/hitf/available-purchase/list',
        key: '/hitf/available-purchase/list',
        component: () => import('../routes/Purchase/AvailablePurchase'),
        models: [],
      },
      {
        path: '/hitf/available-purchase/server/:groupHeaderId',
        key: '/hitf/available-purchase/server/:groupHeaderId',
        component: () => import('../routes/ChargeGroup/Server'),
        models: [],
      },
      {
        path: '/hitf/available-purchase/rule/:ruleHeaderId',
        key: '/hitf/available-purchase/rule/:ruleHeaderId',
        component: () => import('../routes/ChargeRule/Detail'),
        models: [],
      },
    ],
  },
  // 结构字段
  {
    path: '/hitf/structure-field',
    components: [
      {
        path: '/hitf/structure-field/list',
        component: () => import('../routes/StructureField/List'),
        models: [],
      },
      {
        path: '/hitf/structure-field/line/:type/:headerId',
        component: () => import('../routes/StructureField/Detail'),
        models: [],
      },
    ],
  },
];
