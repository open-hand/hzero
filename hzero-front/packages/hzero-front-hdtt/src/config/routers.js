module.exports = [
  {
    path: '/hdtt/producer-config',
    models: [],
    components: [
      {
        path: '/hdtt/producer-config/list',
        component: () => import('../routes/ProducerConfig/List'),
        models: [() => import('../models/producerConfig')],
      },
      {
        path: '/hdtt/producer-config/create',
        component: () => import('../routes/ProducerConfig/Detail'),
        models: [() => import('../models/producerConfig')],
      },
      {
        path: '/hdtt/producer-config/detail/:id',
        component: () => import('../routes/ProducerConfig/Detail'),
        models: [() => import('../models/producerConfig')],
      },
    ],
  },
  {
    path: '/hdtt/exception-monitoring',
    component: () => import('../routes/ExceptionMonitoring/List'),
    models: [() => import('../models/exceptionMonitoring')],
  },
  {
    path: '/hdtt/table-changelog',
    component: () => import('../routes/TableChangeLog'),
    models: [() => import('../models/tableChangeLog')],
  },
  {
    path: '/hdtt/data-check',
    models: [],
    components: [
      {
        path: '/hdtt/data-check/list',
        component: () => import('../routes/DataCheck/List'),
        models: [() => import('../models/dataCheck')],
      },
      {
        path: '/hdtt/data-check/detail',
        component: () => import('../routes/DataCheck/Detail'),
        models: [() => import('../models/dataCheck')],
      },
    ],
  },
];
