module.exports = [
  {
    path: '/hres/rules',
    components: [
      {
        path: '/hres/rules/list',
        component: () => import('../routes/rules/list/ListPage'),
      },
      {
        path: '/hres/rules/flow/detail/:code',
        component: () => import('../routes/rules/flow/FlowPage'),
      },
      {
        path: '/hres/rules/test/:code',
        component: () => import('../routes/rules/test/TestPage'),
      },
      {
        path: '/hres/rules/flow/formula/detail/:code/:id',
        component: () => import('../routes/formula/detail/DetailPage'),
      },
      {
        path: '/hres/rules/flow/grouping/detail/:code/:id',
        component: () => import('../routes/grouping/detail/DetailPage'),
      },
      {
        path: '/hres/rules/flow/sql/detail/:code/:id',
        component: () => import('../routes/sql-component/list/ListPage'),
      },
      {
        path: '/hres/rules/flow/rule-component/detail/:code/:id',
        component: () => import('../routes/rule-component/list/ListPage'),
      },
      {
        path: '/hres/rules/flow/mapping/detail/:code/:id',
        component: () => import('../routes/mapping/list/ListPage'),
      },
    ],
  },
  {
    path: '/hres/execute',
    components: [
      {
        path: '/hres/execute/list',
        component: () => import('../routes/execute/list/ListPage'),
      },
      {
        path: '/hres/execute/detail/:id/:code',
        component: () => import('../routes/execute/detail/DetailPage'),
      },
      // {
      //   // 组件执行记录页面
      //   path: '/hres/execute/record/:id/:name/:lineid',
      //   component: () => import('../routes/execute/detail/ExecuteRecord'),
      // },
      {
        path: '/hres/execute/flow/:code/:id',
        component: () => import('../routes/execute/detail/FlowPage'),
      },
    ],
  },
];
