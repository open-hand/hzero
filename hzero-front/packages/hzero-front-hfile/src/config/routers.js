module.exports = [
  {
    path: '/hfile/edit-log',
    component: () => import('../routes/EditLog'),
    models: [() => import('../models/editLog')],
  },
  {
    path: '/hfile/file-aggregate',
    components: [
      {
        path: '/hfile/file-aggregate/list',
        component: () => import('../routes/FileAggregate'),
        models: [() => import('../models/fileAggregate')],
      },
      {
        path: '/hfile/file-aggregate/word-editor/:fileId',
        component: () => import('../routes/FileAggregate/DetailWordEditor'),
        models: [() => import('../models/fileAggregate')],
      },
    ],
  },
  {
    path: '/hfile/file-upload',
    component: () => import('../routes/FileUpload'),
    models: [() => import('../models/fileUpload')],
  },
  {
    path: '/hfile/server-upload',
    models: [() => import('../models/serverUpload')],
    components: [
      {
        path: '/hfile/server-upload/list',
        component: () => import('../routes/ServerUpload/List'),
        models: [() => import('../models/serverUpload')],
      },
      {
        path: '/hfile/server-upload/detail/:id',
        component: () => import('../routes/ServerUpload/Detail'),
        models: [() => import('../models/serverUpload')],
      },
    ],
  },
  {
    path: '/hfile/storage',
    component: () => import('../routes/Storage'),
    models: [() => import('../models/storage')],
  },
  {
    path: '/hfile/water-mark-config',
    component: () => import('../routes/WaterMarkConfig'),
  },
];
