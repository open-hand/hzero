module.exports = [
  {
    path: '/hdsc/data-flow-pipeline',
    component: () => import('../routes/DataFlowPipeline'),
  },
  {
    path: '/hdsc/data-define',
    component: () => import('../routes/DataDefine'),
  },
];
