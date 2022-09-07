module.exports = [
  // 数据点类型管理
  {
    path: '/hiot/dptm',
    components: [
      {
        path: '/hiot/dptm/list', // 列表
        component: () => import('../routes/DataPointTypeManagement'),
      },
      {
        path: '/hiot/dptm/:operation/:id', // 详情/编辑
        component: () => import('../routes/DataPointTypeManagement/Detail'),
      },
      {
        path: '/hiot/dptm/new', // 新建
        component: () => import('../routes/DataPointTypeManagement/Detail'),
      },
    ],
  },
  // 设备模板
  {
    path: '/hiot/device-temp',
    components: [
      {
        path: '/hiot/device-temp/list',
        component: () => import('../routes/DeviceTemplate'),
      },
      {
        path: '/hiot/device-temp/new',
        component: () => import('../routes/DeviceTemplate/Detail'),
      },
      {
        path: '/hiot/device-temp/:operation/:id',
        component: () => import('../routes/DeviceTemplate/Detail'),
      },
    ],
  },
  {
    path: '/hiot/data-point/template', // 数据点模板
    components: [
      {
        path: '/hiot/data-point/template/list', // 数据点模板列表管理
        component: () => import('../routes/DataPointTemplate'),
      },
      {
        path: '/hiot/data-point/template/create', // 数据点模板列表创建
        component: () => import('../routes/DataPointTemplate/Create'),
      },
      {
        path: '/hiot/data-point/template/:action/:templateId', // 数据点模板列表编辑/详情
        component: () => import('../routes/DataPointTemplate/Detail'),
      },
    ],
  },
  {
    path: '/hiot/iot-warn/template', // 路由 iot告警模板
    components: [
      {
        path: '/hiot/iot-warn/template/list', // 路由 iot告警模板创建
        component: () => import('../routes/IoTWarnTemplate'), // 页面组件
      },
      {
        path: '/hiot/iot-warn/template/create', // 路由 iot告警模板创建
        component: () => import('../routes/IoTWarnTemplate/Create'), // 页面组件
      },
      {
        path: '/hiot/iot-warn/template/:action/:messageTemplateId', // 路由 iot告警模板详情/编辑
        component: () => import('../routes/IoTWarnTemplate/Detail'), // 页面组件
      },
    ],
  },
  {
    path: '/hiot/device/manage', // 设备管理
    components: [
      {
        path: '/hiot/device/manage/list', // 设备管理-列表页
        component: () => import('../routes/DeviceManage'),
      },
      {
        path: '/hiot/device/manage/create', // 设备管理-创建
        component: () => import('../routes/DeviceManage/Create'),
      },
      {
        path: '/hiot/device/manage/detail/:deviceId', // 设备管理-详情
        component: () => import('../routes/DeviceManage/Detail'),
      },
      {
        path: '/hiot/device/manage/edit/:deviceId', // 设备管理-编辑
        exact: true,
        component: () => import('../routes/DeviceManage/Edit'),
      },
      {
        path: '/hiot/device/manage/edit/data-point/:deviceId/:pointId', // 设备管理-编辑-数据点编辑页面
        component: () => import('../routes/DeviceManage/Edit/DataPointEdit'),
      },
    ],
  },
  {
    path: '/hiot/gateway/manage', // 网关管理
    components: [
      {
        path: '/hiot/gateway/manage/list', // 网关管理-列表页
        component: () => import('../routes/GatewayManage'),
      },
      {
        path: '/hiot/gateway/manage/create', // 网关管理-创建页
        component: () => import('../routes/GatewayManage/Create'),
      },
      {
        path: '/hiot/gateway/manage/:action/:gatewayId', // 网关管理-详情/编辑页
        exact: true,
        component: () => import('../routes/GatewayManage/Detail'),
      },
      {
        path: '/hiot/gateway/manage/device/:action/:gatewayId/:thingId', // 网关管理-子设备详情/编辑页/绑定页(绑定页面的deviceId为-1)
        component: () => import('../routes/GatewayManage/Detail/SubDeviceDetail'),
      },
    ],
  },
  {
    path: '/hiot/iot-warn/event', // 告警事件
    components: [
      {
        path: '/hiot/iot-warn/event/list', // 告警事件列表页
        component: () => import('../routes/IoTEvent'),
      },
      {
        path: '/hiot/iot-warn/event/:alertEventId', // 告警事件列表页
        component: () => import('../routes/IoTEvent/Detail'),
      },
    ],
  },
  // 云账号配置
  {
    path: '/hiot/cloud-account/config',
    components: [
      {
        path: '/hiot/cloud-account/config/list',
        component: () => import('../routes/CloudAccountConf'),
      },
      {
        path: '/hiot/cloud-account/config/action/new',
        component: () => import('../routes/CloudAccountConf/Detail'),
      },
      {
        path: '/hiot/cloud-account/config/action/:operation/:id',
        component: () => import('../routes/CloudAccountConf/Detail'),
      },
      {
        path: '/hiot/cloud-account/config/consumer-group/:configId',
        component: () => import('../routes/CloudAccountConf/ConsumerGroups'),
      },
    ],
  },
  // 设备分组
  {
    path: '/hiot/project-manage',
    components: [
      {
        path: '/hiot/project-manage/list',
        component: () => import('../routes/ProjectManage'),
      },
      {
        path: '/hiot/project-manage/sub-device/:deviceId',
        component: () => import('../routes/ProjectManage'),
      },
    ],
  },
  // OTA升级包
  {
    path: '/hiot/ota-upgrade/package',
    components: [
      {
        path: '/hiot/ota-upgrade/package/list',
        component: () => import('../routes/OTAUpgradePackage'),
      },
      {
        path: '/hiot/ota-upgrade/package/detail/:id',
        component: () => import('../routes/OTAUpgradePackage/Detail'),
      },
    ],
  },
  // OTA升级任务
  {
    path: '/hiot/ota-upgrade/task',
    components: [
      {
        path: '/hiot/ota-upgrade/task/list',
        component: () => import('../routes/OTAUpgradeTask'),
      },
      {
        path: '/hiot/ota-upgrade/task/create',
        component: () => import('../routes/OTAUpgradeTask/Create'),
      },
      {
        path: '/hiot/ota-upgrade/task/create/:id',
        component: () => import('../routes/OTAUpgradeTask/Create'),
      },
      {
        path: '/hiot/ota-upgrade/task/detail/:id',
        component: () => import('../routes/OTAUpgradeTask/Detail'),
      },
    ],
  },
  // 导览工作台
  {
    path: '/hiot/workbench',
    components: [
      {
        path: '/hiot/workbench/list',
        component: () => import('../routes/Workbench'),
      },
    ],
  },
  // 网关模板
  {
    path: '/hiot/gateway-temp',
    components: [
      {
        path: '/hiot/gateway-temp/list',
        component: () => import('../routes/GatewayTemplate'),
      },
      {
        path: '/hiot/gateway-temp/:operation/:id',
        component: () => import('../routes/GatewayTemplate/Detail'),
      },
    ],
  },
  // //消费者组
  // {
  //   path: '/hiot/consumer-groups',
  //   component: () => import('../routes/ConsumerGroups'),
  // },
  // 设备采集
  {
    authorized: true,
    path: '/hiot/device-collection',
    components: [
      {
        path: '/hiot/device-collection/list',
        component: () => import('../routes/DeviceCollection'),
      },
      {
        path: '/hiot/device-collection/detail/:id',
        component: () => import('../routes/DeviceCollection/Detail'),
      },
    ],
  },
  // 指令下发日志监控
  {
    path: '/hiot/directive-issued-log',
    component: () => import('../routes/DirectiveIssuedLog'),
  },
  {
    path: '/hiot/data-report-log',
    component: () => import('../routes/DataReportLog'),
  },
  // 设备模拟上报
  {
    path: '/hiot/device-simulation',
    component: () => import('../routes/DeviceSimulation'),
  },
  // 报文模板管理
  {
    path: '/hiot/message-template',
    components: [
      {
        path: '/hiot/message-template/list',
        component: () => import('../routes/MessageTemplate'),
      },
      {
        path: '/hiot/message-template/detail/:action',
        component: () => import('../routes/MessageTemplate/Detail'),
      },
    ],
  },
  // 数据目的地管理
  {
    path: '/hiot/data-destination',
    component: () => import('../routes/DataDestination'),
  },
  // 数据处理
  {
    path: '/hiot/data-process',
    components: [
      {
        path: '/hiot/data-process/list',
        component: () => import('../routes/DataProcess'),
      },
      {
        path: '/hiot/data-process/detail/:action',
        component: () => import('../routes/DataProcess/Detail'),
      },
    ],
  },
];
