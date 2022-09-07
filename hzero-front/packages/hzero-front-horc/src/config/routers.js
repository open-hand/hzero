module.exports = [
  {
    // 服务编排定义
    path: '/horc/orchestration-definition',
    components: [
      {
        path: '/horc/orchestration-definition/list',
        component: () => import('../routes/Orchestration/Definition'),
        models: [() => import('../models/orchestration')],
      },
      {
        path: '/horc/orchestration-definition/create',
        component: () => import('../routes/Orchestration/Detail'),
        models: [() => import('../models/orchestration')],
      },
      {
        path: '/horc/orchestration-definition/detail/:id',
        component: () => import('../routes/Orchestration/Detail'),
        models: [() => import('../models/orchestration')],
      },
    ],
  },
  {
    // 服务编排实例
    path: '/horc/orchestration-instance',
    components: [
      {
        path: '/horc/orchestration-instance/list',
        component: () => import('../routes/Orchestration/Instance'),
        models: [() => import('../models/orchestration')],
      },
      {
        path: '/horc/orchestration-instance/detail/:id',
        component: () => import('../routes/Orchestration/Detail'),
        models: [() => import('../models/orchestration')],
      },
    ],
  },
  {
    // 编排任务实例
    path: '/horc/orchestration-task-instance',
    components: [
      {
        path: '/horc/orchestration-task-instance/list',
        component: () => import('../routes/Orchestration/TaskInstance'),
        models: [() => import('../models/orchestration')],
      },
      {
        path: '/horc/orchestration-task-instance/detail/:id',
        component: () => import('../routes/Orchestration/Detail'),
        models: [() => import('../models/orchestration')],
      },
    ],
  },
  // 字段映射
  {
    path: '/horc/field-mapping',
    components: [
      {
        path: '/horc/field-mapping/list',
        component: () => import('../routes/FieldMapping/List'),
        models: [],
      },
      {
        path: '/horc/field-mapping/create',
        component: () => import('../routes/FieldMapping/Detail'),
        models: [],
      },
      {
        path: '/horc/field-mapping/detail/:id',
        component: () => import('../routes/FieldMapping/Detail'),
        models: [],
      },
      {
        path: '/horc/field-mapping/history/:id/:version',
        component: () => import('../routes/FieldMapping/Detail'),
        models: [],
      },
    ],
  },
  // 数据转化
  {
    path: '/horc/data-mapping',
    components: [
      {
        path: '/horc/data-mapping/list',
        component: () => import('../routes/DataMapping/List'),
        models: [() => import('../models/dataMapping')],
      },
      {
        path: '/horc/data-mapping/create',
        component: () => import('../routes/DataMapping/Detail'),
        models: [() => import('../models/dataMapping')],
      },
      {
        path: '/horc/data-mapping/detail/:id',
        component: () => import('../routes/DataMapping/Detail'),
        models: [() => import('../models/dataMapping')],
      },
      {
        path: '/horc/data-mapping/history/:id/:version',
        component: () => import('../routes/DataMapping/Detail'),
        models: [() => import('../models/dataMapping')],
      },
    ],
  },
];
