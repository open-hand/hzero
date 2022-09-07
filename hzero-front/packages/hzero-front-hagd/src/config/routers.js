module.exports = [
  {
    path: '/hagd/saga',
    component: () => import('../routes/Saga'),
    models: [() => import('../models/saga')],
  },
  {
    path: '/hagd/saga-instance',
    component: () => import('../routes/SagaInstance'),
    models: [() => import('../models/sagaInstance')],
  },
];
