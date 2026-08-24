module.exports = [
  {
    path: '/hrpt/data-set',
    models: [() => import('../models/dataSet')],
    components: [
      {
        path: '/hrpt/data-set/list',
        component: () => import('../routes/DataSet/List'),
        models: [() => import('../models/dataSet')],
      },
      {
        path: '/hrpt/data-set/create',
        component: () => import('../routes/DataSet/Detail'),
        models: [() => import('../models/dataSet')],
      },
      {
        path: '/hrpt/data-set/detail/:id',
        component: () => import('../routes/DataSet/Detail'),
        models: [() => import('../models/dataSet')],
      },
    ],
  },
  {
    path: '/hrpt/report-definition',
    models: [() => import('../models/reportDefinition')],
    components: [
      {
        path: '/hrpt/report-definition/list',
        component: () => import('../routes/ReportDefinition/List'),
        models: [() => import('../models/reportDefinition')],
      },
      {
        path: '/hrpt/report-definition/create',
        component: () => import('../routes/ReportDefinition/Detail'),
        models: [() => import('../models/reportDefinition')],
      },
      {
        path: '/hrpt/report-definition/detail/:id',
        component: () => import('../routes/ReportDefinition/Detail'),
        models: [() => import('../models/reportDefinition')],
      },
      {
        path: '/hrpt/report-definition/u-report',
        component: () => import('../routes/ReportDefinition/Detail/UReportEditor'),
        models: [() => import('../models/reportDefinition')],
      },
    ],
  },
  // 报表详情菜单1
  {
    path: '/hrpt/report-detail',
    models: [() => import('../models/reportQuery')],
    component: () => import('../routes/ReportQuery/Detail'),
  },
  // 报表详情菜单, 将参数放在路径上，保证唯一key
  {
    path: '/hrpt/report-detail-path/:code',
    models: [() => import('../models/reportQuery')],
    component: () => import('../routes/ReportQuery/ReportDetail'),
  },
  {
    path: '/hrpt/report-query',
    models: [() => import('../models/reportQuery')],
    components: [
      {
        path: '/hrpt/report-query/list',
        component: () => import('../routes/ReportQuery/List'),
        models: [() => import('../models/reportQuery')],
      },
      {
        path: '/hrpt/report-query/detail/:id/:name',
        component: () => import('../routes/ReportQuery/Detail'),
        models: [() => import('../models/reportQuery')],
      },
    ],
  },
  {
    path: '/hrpt/personal-report-query',
    component: () => import('../routes/PersonalReport'),
  },
  {
    path: '/hrpt/report-request',
    component: () => import('../routes/ReportRequest'),
    models: [() => import('../models/reportRequest')],
  },
  {
    path: '/hrpt/template-manage',
    models: [() => import('../models/templateManage')],
    components: [
      {
        path: '/hrpt/template-manage/list',
        component: () => import('../routes/TemplateManage/List'),
        models: [() => import('../models/templateManage')],
      },
      {
        path: '/hrpt/template-manage/create',
        component: () => import('../routes/TemplateManage/Detail'),
        models: [() => import('../models/templateManage')],
      },
      {
        path: '/hrpt/template-manage/detail/:id',
        component: () => import('../routes/TemplateManage/Detail'),
        models: [() => import('../models/templateManage')],
      },
      {
        path: '/hrpt/template-manage/word-editor/:fileId',
        component: () => import('../routes/TemplateManage/Detail/DetailWordEditor'),
        models: [() => import('../models/templateManage')],
      },
    ],
  },
  // 标签模板管理
  {
    path: '/hrpt/label-template',
    components: [
      {
        path: '/hrpt/label-template/list',
        component: () => import('../routes/LabelTemplate'),
      },
      {
        path: '/hrpt/label-template/detail/:labelTemplateId',
        exact: true,
        component: () => import('../routes/LabelTemplate/Detail'),
      },
      {
        path:
          '/hrpt/label-template/detail/edit-template/:labelTemplateId/:templateHigh/:templateWidth',
        component: () => import('../routes/LabelTemplate/EditTemplate'),
      },
      {
        path: '/hrpt/label-template/print/:templateCode',
        component: () => import('../routes/LabelTemplate/Print'),
      },
    ],
  },
];
