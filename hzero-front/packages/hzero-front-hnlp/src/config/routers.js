module.exports = [
  {
    path: '/hnlp',
    components: [
      {
        path: '/hnlp/basic-data',
        component: () => import('../routes/BasicData'),
        models: [() => import('../models/nlpBasicData')],
      },
      {
        path: '/hnlp/template',
        component: () => import('../routes/Template'),
        models: [() => import('../models/nlpTemplate')],
      },
      {
        path: '/hnlp/config-template',
        component: () => import('../routes/ConfigTemplate'),
        models: [() => import('../models/nlpConfigTemplate')],
      },
      {
        path: '/hnlp/tenant-word',
        component: () => import('../routes/TenantWord'),
        models: [() => import('../models/nlpTenantWord')],
      },
      {
        path: '/hnlp/word-template',
        component: () => import('../routes/WordTemplate'),
        models: [() => import('../models/nlpWordTemplate')],
      },
      {
        path: '/hnlp/text-extract-log',
        component: () => import('../routes/TextExtractionLog'),
        models: [() => import('../models/nlpTextExtractionLog')],
      },
      {
        path: '/hnlp/extract-text',
        component: () => import('../routes/TextExtraction'),
        models: [() => import('../models/nlpTextExtraction')],
      },
      {
        authorized: true,
        path: '/hnlp/data-import/:code',
        component: () => import('../routes/himp/CommentImport'),
        models: [],
      },
    ],
  },
];
