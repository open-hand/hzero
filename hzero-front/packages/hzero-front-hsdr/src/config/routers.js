module.exports = [
  {
    path: '/hsdr/conc-request',
    component: () => import('../routes/ConcRequest'),
    models: [() => import('../models/concRequest'), () => import('../models/jobLog')],
  },
  {
    path: '/hsdr/concurrent',
    models: [() => import('../models/concurrent')],
    components: [
      {
        path: '/hsdr/concurrent/list',
        component: () => import('../routes/Concurrent/List'),
        models: [() => import('../models/concurrent')],
      },
      {
        path: '/hsdr/concurrent/detail/:id',
        component: () => import('../routes/Concurrent/Detail'),
        models: [() => import('../models/concurrent')],
      },
    ],
  },
  {
    path: '/hsdr/executable',
    component: () => import('../routes/Executable'),
    models: [() => import('../models/executable')],
  },
  {
    path: '/hsdr/job-group',
    component: () => import('../routes/JobGroup'),
    models: [() => import('../models/jobGroup')],
  },
  {
    path: '/hsdr/job-info',
    models: [() => import('../models/jobInfo')],
    components: [
      {
        path: '/hsdr/job-info/list',
        component: () => import('../routes/JobInfo'),
        models: [() => import('../models/jobInfo'), () => import('../models/jobLog')],
      },
      {
        path: '/hsdr/job-info/glue/:id',
        component: () => import('../routes/JobInfo/Glue'),
        models: [() => import('../models/jobInfo')],
      },
      {
        path: '/hsdr/job-info/log/:jobId',
        component: () => import('../routes/JobLog'),
        models: [() => import('../models/jobLog')],
      },
    ],
  },
  {
    path: '/hsdr/job-log',
    component: () => import('../routes/JobLog'),
    models: [() => import('../models/jobLog')],
  },
];
