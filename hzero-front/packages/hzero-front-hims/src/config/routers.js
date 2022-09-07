module.exports = [
  {
    path: '/hims/customer-group',
    component: () => import('../routes/CustomerGroup'),
  },
  {
    path: '/hims/message-center',
    component: () => import('../routes/MessageCenter'),
  },
  {
    path: '/hims/inner-customer-group',
    component: () => import('../routes/InnerCustomerGroup'),
  },
  {
    path: '/hims/knowledge-category',
    component: () => import('../routes/KnowledgeCategory'),
  },
  {
    path: '/hims/knowledge',
    component: () => import('../routes/KnowledgeVindicate'),
  },
  {
    path: '/hims/basic-config',
    component: () => import('../routes/BasicConfig'),
  },
  {
    path: '/hims/evaluation-manage',
    component: () => import('../routes/EvaluationManage'),
  },
];
