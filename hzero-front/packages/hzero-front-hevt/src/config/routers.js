/*
 * @Descripttion:
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-14 18:08:29
 * @Copyright: Copyright (c) 2020, Hand
 */

module.exports = [
  {
    path: '/hevt/event',
    models: [],
    components: [
      {
        path: '/hevt/event/list',
        component: () => import('../routes/Event/index'), // 页面组件
        models: [() => import('../models/event')],
      },
      {
        path: '/hevt/event/handle',
        component: () => import('../routes/Event/EventHandle'), // 页面组件
      },
    ],
  },
  {
    path: '/hevt/event-message',
    component: () => import('../routes/EventMessage'), // 页面组件
    models: [() => import('../models/eventMessage')],
  },
  {
    path: '/hevt/event-category',
    component: () => import('../routes/EventCategory'), // 页面组件
    models: [() => import('../models/eventCategory')],
  },
  {
    path: '/hevt/event-source',
    authorized: true,
    component: () => import('../routes/EventSource'), // 页面组件
    models: [],
  },
];
