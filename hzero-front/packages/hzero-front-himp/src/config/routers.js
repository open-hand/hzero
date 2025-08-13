module.exports = [
  {
    path: '/himp/template',
    models: [() => import('../models/template')],
    components: [
      {
        path: '/himp/template/list',
        component: () => import('../routes/Template/List'),
        models: [() => import('../models/template')],
      },
      {
        path: '/himp/template/column/:id/:sheetId/:templateType',
        component: () => import('../routes/Template/Detail/Column'),
        models: [() => import('../models/template')],
      },
      {
        path: '/himp/template/detail/:id',
        component: () => import('../routes/Template/Detail'),
        models: [() => import('../models/template')],
      },
    ],
  },
  {
    path: '/himp/commentImport/:code',
    component: () => import('../routes/CommentImport'),
    models: [() => import('../models/commentImport')],
  },
  {
    path: '/himp/history',
    models: [],
    components: [
      {
        path: '/himp/history/list',
        component: () => import('../routes/ImportHistory'),
        models: [],
      },
      {
        path: '/himp/history/detail/:importId/:templateCode/:batch',
        component: () => import('../routes/ImportHistory/Detail'),
        models: [],
      },
    ],
  },
];
