module.exports = [
  {
    path: '/hivc/manual-inspection/create',
    component: () => import('../routes/ManualInspection'),
    models: [() => import('../models/manualInspection')],
  },
  {
    path: '/hivc/manual-inspection/:id',
    component: () => import('../routes/ManualInspection'),
    models: [() => import('../models/manualInspection')],
  },
  {
    path: '/hivc/ocr-inspection/create',
    component: () => import('../routes/OCRInspection'),
    models: [() => import('../models/ocrInspection')],
  },
  {
    path: '/hivc/inspection-history',
    // component: "InspectionHistory",
    components: [
      {
        path: '/hivc/inspection-history/list',
        models: [() => import('../models/inspectionHistory')],
        component: () => import('../routes/InspectionHistory'),
      },
      {
        path: '/hivc/inspection-history/detail/:id',
        component: () => import('../routes/ManualInspection'),
        models: [() => import('../models/manualInspection')],
      },
      {
        path: '/hivc/inspection-history/create-detail',
        key: '/hivc/inspection-history/create-detail',
        component: () => import('../routes/ManualInspection/CreateDetail'),
      },
    ],
    models: [
      () => import('../models/inspectionHistory'),
      () => import('../models/manualInspection'),
    ],
  },
  {
    path: '/hivc/api-config',
    key: '/hivc/api-config',
    components: [
      {
        path: '/hivc/api-config/list',
        component: () => import('../routes/ApiConfig'),
        key: '/hivc/api-config/list',
      },
      {
        path: '/hivc/api-config/edit/:apiConfigId',
        component: () => import('../routes/ApiConfig/Edit'),
        key: '/hivc/api-config/edit/:apiConfigId',
      },
    ],
  },
  {
    path: '/hivc/select',
    key: '/hivc/select',
    components: [
      {
        path: '/hivc/select/list',
        exact: true,
        component: () => import('../routes/Select'),
        key: '/hivc/select/list',
      },
      {
        path: '/hivc/select/detail/:id',
        component: () => import('../routes/Select/Detail'),
        key: '/hivc/select/detail/:id',
      },
    ],
  },
  {
    path: '/hivc/certification',
    key: '/hivc/certification',
    components: [
      {
        path: '/hivc/certification/list',
        component: () => import('../routes/Certification'),
        key: '/hivc/certification/list',
      },
      {
        path: '/hivc/certification/detail/:id',
        component: () => import('../routes/Certification/Detail'),
        key: '/hivc/certification/detail/:id',
      },
    ],
  },
];
