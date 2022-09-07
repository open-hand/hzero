module.exports = [
  // 甘特图配置
  {
    path: '/hpfm/gantt',
    models: [],
    components: [
      {
        path: '/hpfm/gantt/list',
        component: () => import('../routes/GanttConfig'),
      },
      {
        path: '/hpfm/gantt/detail/:id/:code',
        component: () => import('../routes/GanttConfig/Detail'),
      },
      {
        path: '/hpfm/gantt/preview/:id/:code',
        component: () => import('../routes/GanttConfig/Preview'),
      },
    ],
  },
];
