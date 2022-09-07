module.exports = [
  {
    path: '/hmsg/email',
    component: () => import('../routes/Email'),
    models: [() => import('../models/email')],
  },
  {
    path: '/hmsg/message-query',
    component: () => import('../routes/MessageQuery'),
    models: [() => import('../models/messageQuery')],
  },
  {
    path: '/hmsg/message-template',
    models: [],
    components: [
      {
        path: '/hmsg/message-template/list',
        component: () => import('../routes/MessageTemplate/List'),
        models: [() => import('../models/messageTemplate')],
      },
      {
        path: '/hmsg/message-template/create',
        component: () => import('../routes/MessageTemplate/Detail'),
        models: [() => import('../models/messageTemplate')],
      },
      {
        path: '/hmsg/message-template/detail/:id',
        component: () => import('../routes/MessageTemplate/Detail'),
        models: [() => import('../models/messageTemplate')],
      },
    ],
  },
  {
    path: '/hmsg/receive-config',
    component: () => import('../routes/ReceiveConfig'),
    models: [() => import('../models/receiveConfig')],
  },
  {
    path: '/hmsg/receiver-type',
    component: () => import('../routes/ReceiverType'),
    models: [() => import('../models/receiverType')],
  },
  {
    path: '/hmsg/send-config',
    models: [() => import('../models/sendConfig')],
    components: [
      {
        path: '/hmsg/send-config/list',
        component: () => import('../routes/SendConfig/List'),
        models: [() => import('../models/sendConfig')],
      },
      {
        path: '/hmsg/send-config/create',
        component: () => import('../routes/SendConfig/Detail'),
        models: [() => import('../models/sendConfig')],
      },
      {
        path: '/hmsg/send-config/detail/:id',
        component: () => import('../routes/SendConfig/Detail'),
        models: [() => import('../models/sendConfig')],
      },
    ],
  },
  {
    path: '/hmsg/sms-config',
    component: () => import('../routes/SMSConfig'),
    models: [() => import('../models/smsConfig')],
  },
  {
    path: '/hmsg/user-message',
    models: [],
    components: [
      {
        authorized: true,
        title: 'hzero.common.title.userMessage',
        key: '/hmsg/user-message',
        path: '/hmsg/user-message/list',
        component: () => import('../routes/UserMessage'),
        models: [() => import('../models/userMessage')],
      },
      {
        authorized: true,
        title: 'hzero.common.title.userMessage',
        key: '/hmsg/user-message',
        // 当详情页展示的公告信息时, userMessageId 时 noticeId
        path: '/hmsg/user-message/detail/:type/:userMessageId',
        component: () => import('../routes/UserMessage/MessageDetail'),
        models: [() => import('../models/userMessage')],
      },
      {
        authorized: true,
        title: 'hzero.common.title.userMessage',
        key: '/hmsg/user-message',
        // 当详情页展示的公告信息时, userMessageId 时 noticeId
        path: '/hmsg/user-message/detail/:userMessageId',
        component: () => import('../routes/UserMessage/MessageDetail'),
        models: [() => import('../models/userMessage')],
      },
    ],
  },
  {
    path: '/hmsg/notices',
    models: [],
    components: [
      {
        path: '/hmsg/notices/list',
        component: () => import('../routes/Notice'),
        models: [() => import('../models/hmsgNotice')],
      },
      {
        path: '/hmsg/notices/detail/:noticeId',
        component: () => import('../routes/Notice/NoticeDetail'),
        models: [() => import('../models/hmsgNotice')],
      },
    ],
  },
  {
    path: '/hmsg/wechat-config',
    component: () => import('../routes/WechatConfig'),
    models: [],
  },
  {
    path: '/hmsg/official-accounts-config',
    component: () => import('../routes/OfficialAccountsConfig'),
    models: [],
  },
  {
    path: '/hmsg/ding-talk-config',
    component: () => import('../routes/DingTalkConfig'),
    models: [],
  },
  {
    path: '/hmsg/call-server',
    component: () => import('../routes/CallServer'),
  },
  {
    path: '/hmsg/webhook-config',
    authorized: true,
    component: () => import('../routes/WebHookConfig'),
  },
];
