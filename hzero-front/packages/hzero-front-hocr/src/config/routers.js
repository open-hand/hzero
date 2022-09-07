module.exports = [
  // 通用OCR
  {
    path: '/hocr/common-ocr',
    models: [],
    components: [
      {
        path: '/hocr/common-ocr/list',
        component: () => import('../routes/CommonOcr'),
        models: [() => import('../models/commonOcr')],
      },
      {
        path: '/hocr/common-ocr/vat-detail/:imageUrl/:vatType',
        component: () => import('../routes/CommonOcr/VatDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/license-detail/:imageUrl',
        component: () => import('../routes/CommonOcr/LicenseDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/id-detail/:imageUrlBack/:imageUrlFront',
        component: () => import('../routes/CommonOcr/IdDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/taxi-detail/:imageUrl',
        component: () => import('../routes/CommonOcr/TaxiDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/train-detail/:imageUrl',
        component: () => import('../routes/CommonOcr/TrainDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/multi-detail/:imageUrl',
        component: () => import('../routes/CommonOcr/MultiDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/common-ocr/single-detail/:imageUrl',
        component: () => import('../routes/CommonOcr/SingleDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
    ],
  },
  // OCR记录
  {
    path: '/hocr/ocr-record',
    models: [],
    components: [
      {
        path: '/hocr/ocr-record/list',
        component: () => import('../routes/OcrRecord'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/vat-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/VatDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/license-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/LicenseDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/id-detail/:recordDetailId/:recordId',
        component: () => import('../routes/OcrRecord/IdDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/taxi-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/TaxiDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/train-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/TrainDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/quota-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/QuotaDetail'),
        models: [() => import('../models/ocrRecord')],
      },
      {
        path: '/hocr/ocr-record/multi-detail/:recordDetailId/:resourceUrl1',
        component: () => import('../routes/OcrRecord/MultiDetail'),
        models: [() => import('../models/commonOcr'), () => import('../models/ocrRecord')],
      },
    ],
  },
];
