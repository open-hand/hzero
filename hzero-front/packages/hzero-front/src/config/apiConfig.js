/**
 * 配置 utils/config 下的常量
 */

/**
 * deps 和 get 必须同时出现
 * get 和 init 是互斥的
 * 如果有 route, 那么必须有init
 *
 * @typedef {Object|string} CONFIG
 * @property {?Function} init - 返回返回字符串包裹的字符串模板; 参照 CLIENT_ID
 * @property {?boolean} noChange - 是否能改变 false/undefined => 能改变, true => 不能改变; 参照 CLIENT_ID
 * // * @property {?boolean} route - 是否能被环境变量routeMap改变 false/undefined => 可以改变, true => 不能改变; 参照 HZERO_PLATFORM
 * @property {?string[]} deps - 依赖的CONFIG, 当 依赖的CONFIG 改变时 会同时改变; 参照 AUTH_SELF_URL 的配置
 * @property {?Function} get - 依赖改变生成自己的方法; (...deps: string[]) => string; 参照 AUTH_SELF_URL 的配置
 */

/**
 * @type {Map<string, Config>}
 */
const config = {
  // 客户端id
  CLIENT_ID: {
    init: () => {
      return '`${process.env.CLIENT_ID}`';
    },
    noChange: true,
  },
  // 后端服务网关地址
  API_HOST: {
    init: () => {
      return '`${process.env.API_HOST}`';
    },
    noChange: true,
  },
  // 工作流地址
  BPM_HOST: {
    init: () => {
      return '`${process.env.BPM_HOST}`';
    },
  },
  // 新版工作流地址
  WFP_EDITOR: {
    init: () => {
      return '`${process.env.WFP_EDITOR}`';
    },
  },
  // websocket 地址
  WEBSOCKET_URL: {
    init: () => {
      return '`${process.env.WEBSOCKET_HOST}`';
    },
  },
  // 是否开启 im
  IM_ENABLE: {
    init: () => {
      return '`${process.env.IM_ENABLE}`';
    },
  },
  // 是否开启 日志追溯分析
  TRACE_LOG_ENABLE: {
    init: () => {
      return '`${process.env.TRACE_LOG_ENABLE}`';
    },
  },
  // 是否开启 UED配置
  MULTIPLE_SKIN_ENABLE: {
    init: () => {
      return '`${process.env.MULTIPLE_SKIN_ENABLE}`';
    },
  },
  // 是否按照标签并集查询(即只要存在一个标签即可), 默认否(false)
  TOP_MENU_UNION_LABEL: {
    init: () => {
      return '`${process.env.BUILD_TOP_MENU_UNION_LABEL}`';
    },
  },
  // 环境标识
  ENV_SIGN: {
    init: () => {
      return '`${process.env.BUILD_ENV_SIGN}`';
    },
  },
  // 菜单标签
  TOP_MENU_LABELS: {
    init: () => {
      return '`${process.env.BUILD_TOP_MENU_LABELS}`';
    },
  },

  // 过期时间(单位分钟)
  INVALID_TIME: {
    init: () => {
      return '`${process.env.INVALID_TIME}`';
    },
  },
  // 客制化的iconfont font family 名称
  CUSTOMIZE_ICON_NAME: {
    init: () => {
      return '`${process.env.CUSTOMIZE_ICON_NAME}`';
    },
  },
  // hpfm 模块
  HZERO_PLATFORM: {
    init: () => {
      return "'/hpfm'";
    },
    route: true,
  },
  // iam 模块
  HZERO_IAM: {
    init: () => {
      return "'/iam'";
    },
    route: true,
  },
  // hdtt 模块
  HZERO_DTT: {
    init: () => {
      return "'/hdtt'";
    },
    route: true,
  },
  // hmsg 模块
  HZERO_MSG: {
    init: () => {
      return "'/hmsg'";
    },
    route: true,
  },
  // hptl 模块
  HZERO_PTL: {
    init: () => {
      return "'/hptl'";
    },
    route: true,
  },
  HZERO_WFL: {
    init: () => {
      return "'/hwfl'"; // hwfl 模块
    },
    route: true,
  },
  // hdtw 模块
  HZERO_DTW: {
    init: () => {
      return "'/hdtw'";
    },
    // // TODO: 不能有重复的 配置名
    // route: true,
  },
  // hdtw 模块
  HZERO_HDTW: {
    init: () => {
      return "'/hdtw'";
    },
    route: true,
  },
  // hsdr 模块
  HZERO_SDR: {
    init: () => {
      return "'/hsdr'";
    },
    route: true,
  },
  // hsgp 模块
  HZERO_HSGP: {
    init: () => {
      return "'/hsgp'";
    },
    route: true,
  },
  // hitf 模块
  HZERO_HITF: {
    init: () => {
      return "'/hitf'";
    },
    route: true,
  },
  // hfle 模块
  HZERO_HFLE: {
    init: () => {
      return "'/hfle'";
    },
    route: true,
  },
  // oauth 模块
  HZERO_OAUTH: {
    init: () => {
      return "'/oauth'";
    },
    route: true,
  },
  // hagd 模块
  HZERO_ASGARD: {
    init: () => {
      return "'/hagd'";
    },
    route: true,
  },
  // himp 模块
  HZERO_IMP: {
    init: () => {
      return "'/himp'";
    },
    route: true,
  },
  // hrpt 模块
  HZERO_RPT: {
    init: () => {
      return "'/hrpt'";
    },
    route: true,
  },
  // hcnf 模块
  HZERO_HCNF: {
    init: () => {
      return "'/hcnf'";
    },
    route: true,
  },
  // hwfp 模块
  HZERO_HWFP: {
    init: () => {
      return "'/hwfp'";
    },
    route: true,
  },
  HZERO_FILE: {
    deps: ['API_HOST', 'HZERO_HFLE'],
    get: (API_HOST, HZERO_HFLE) => {
      return `${API_HOST}${HZERO_HFLE}`;
    },
  },
  // hnlp 模块
  HZERO_NLP: {
    init: () => {
      return "'/hnlp'";
    },
    route: true,
  },
  // hpay 模块
  HZERO_HPAY: {
    init: () => {
      return "'/hpay'";
    },
    route: true,
  },
  // hexl 模块
  HZERO_HEXL: {
    init: () => {
      return "'/hexl'";
    },
    route: true,
  },
  // hmnt 模块
  HZERO_MNT: {
    init: () => {
      return "'/hmnt'";
    },
    route: true,
  },
  // hivc 模块
  HZERO_INVOICE: {
    init: () => {
      return "'/hivc'";
    },
    route: true,
  },
  // im 模块
  HZERO_IM: {
    init: () => {
      return "'/hims'";
    },
    route: true,
  },
  // ocr 模块
  HZERO_OCR: {
    init: () => {
      return "'/hocr'";
    },
    route: true,
  },
  // hchg 模块
  HZERO_CHG: {
    init: () => {
      return "'/hchg'";
    },
    route: true,
  },
  // hadm 模块
  HZERO_ADM: {
    init: () => {
      return "'/hadm'";
    },
    route: true,
  },
  // hdpmm 模块
  HZERO_DPM: {
    init: () => {
      return "'/hdpm'";
    },
    route: true,
  },
  // hsrh 模块
  HZERO_HSRH: {
    init: () => {
      return "'/hsrh'";
    },
    route: true,
  },
  // hmde 模块
  HZERO_HMDE: {
    init: () => {
      return "'/hmde'";
    },
    route: true,
  },
  // hres 模块
  HZERO_HRES: {
    init: () => {
      return "'/hres'";
    },
    route: true,
  },
  // low-code 模块
  HZERO_HLCD: {
    init: () => {
      return "'/hlcd'";
    },
    route: true,
  },
  // 新low-code 模块
  HZERO_HLOD: {
    init: () => {
      return "'/hlod'";
    },
    route: true,
  },
  HZERO_HMDE: {
    init: () => {
      return "'/hmde'";
    },
    route: true,
  },
  HZERO_HIOT: {
    init: () => {
      return "'/hiot'";
    },
    route: true,
  },
  HZERO_HEBK: {
    init: () => {
      return "'/hebk'";
    },
    route: true,
  },
  HZERO_ALT: {
    init: () => {
      return "'/halt'";
    },
    route: true,
  },
  HZERO_HEVT: {
    init: () => {
      return "'/hevt'";
    },
    route: true,
  },
  HZERO_HDSC: {
    init: () => {
      return "'/hdsc'";
    },
    route: true,
  },
  // horc 模块
  HZERO_HORC: {
    init: () => {
      return "'/horc'";
    },
    route: true,
  },
  // hfnt 模块
  HZERO_HFNT: {
    init: () => {
      return "'/hfnt'";
    },
    route: true,
  },
  // hwkf 模块
  HZERO_HWKF: {
    init: () => {
      return "'/hwkf'";
    },
    route: true,
  },

  //
  AUTH_HOST: {
    deps: ['API_HOST', 'HZERO_OAUTH'],
    get: (API_HOST, HZERO_OAUTH) => {
      return `${API_HOST}${HZERO_OAUTH}`;
    },
  },
  // 如果没有配置使用 默认配置需要使用AUTH_HOST, 所以必须在 AUTH_HOST 之后声明
  // 认证地址
  // LOGIN_URL
  LOGIN_URL: {
    init: () => {
      return "`${process.env.LOGIN_URL || (AUTH_HOST + '/oauth/authorize?response_type=token&client_id=' + CLIENT_ID)}`";
    },
  },
  // 登出地址
  // LOGOUT_URL
  LOGOUT_URL: {
    init: () => {
      return "`${process.env.LOGOUT_URL || (AUTH_HOST + '/logout')}`";
    },
  },
  // self 接口地址
  AUTH_SELF_URL: {
    deps: ['API_HOST', 'HZERO_IAM'],
    get: (API_HOST, HZERO_IAM) => {
      return `${process.env.AUTH_SELF_URL || `${API_HOST}${HZERO_IAM}/hzero/v1/users/self`}`;
    },
  },
  // 是否是 OP 版本
  VERSION_IS_OP: {
    deps: ['PLATFORM_VERSION'],
    get: (PLATFORM_VERSION) => {
      return `${PLATFORM_VERSION}` === 'OP'; //  OP版
    },
    init: () => {
      return "`${process.env.PLATFORM_VERSION}`  ===  'OP'"; //  OP版
    },
  },

  // 文件存储 桶名
  BKT_PUBLIC: {
    init: () => "`${process.env.BKT_PUBLIC || 'public'}`",
  },
  BKT_PLATFORM: {
    init: () => "`${process.env.BKT_PLATFORM || 'hpfm'}`",
  },
  BKT_MSG: {
    init: () => "`${process.env.BKT_MSG || 'hmsg'}`",
  },
  BKT_SDR: {
    init: () => "`${process.env.BKT_SDR || 'hsdr'}`",
  },
  BKT_RPT: {
    init: () => "`${process.env.BKT_RPT || 'hrpt'}`",
  },
  BKT_INVOICE: {
    init: () => "`${process.env.BKT_INVOICE || 'hivc'}`",
  },
  BKT_OCR: {
    init: () => "`${process.env.BKT_OCR || 'hocr'}`",
  },
  BKT_ADM: {
    init: () => "`${process.env.BKT_ADM || 'hadm'}`",
  },
  BKT_HWFP: {
    init: () => "`${process.env.BKT_HWFP || 'hwfp'}`",
  },
  BKT_HIMS: {
    init: () => "`${process.env.BKT_HIMS || 'hims'}`",
  },
  BKT_HITF: {
    init: () => "`${process.env.BKT_HITF || 'hitf'}`",
  },
  BKT_HIMP: {
    init: () => "`${process.env.BKT_HIMP || 'himp'}`",
  },
  BKT_HFILE: {
    init: () => "`${process.env.BKT_HFILE || 'hfle'}`",
  },
  BKT_HIOT: {
    init: () => "`${process.env.BKT_HFILE || 'hiot'}`",
  },
  BKT_HORC: {
    init: () => "`${process.env.BKT_HORC || 'horc'}`",
  },
  BKT_HFNT: {
    init: () => "`${process.env.BKT_HFNT || 'hfnt'}`",
  },
  BKT_HWKF: {
    init: () => "`${process.env.BKT_HWKF || 'hwkf'}`",
  },
};

const prefix = `/**
 * 不要直接修改这个文件, 请修改 config/apiConfig 文件
 * Config - 全局统一配置
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
`;

const suffix = '';

module.exports = {
  config,
  prefix,
  suffix,
};
