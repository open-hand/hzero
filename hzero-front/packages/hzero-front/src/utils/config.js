/* eslint-disable no-shadow,camelcase,import/no-mutable-exports,no-console */
// TODO: 自动生成的 src/utils/config 禁用了部分 eslint, 请查看 scripts/genConfig.js
/**
 * 不要直接修改这个文件, 请修改 config/apiConfig 文件
 * Config - 全局统一配置
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

// #region initConfig
const CLIENT_ID = `${process.env.CLIENT_ID}`;
const API_HOST = `${process.env.API_HOST}`;
let BPM_HOST = `${process.env.BPM_HOST}`;
let WFP_EDITOR = `${process.env.WFP_EDITOR}`;
let WEBSOCKET_URL = `${process.env.WEBSOCKET_HOST}`;
let IM_ENABLE = `${process.env.IM_ENABLE}`;
let TRACE_LOG_ENABLE = `${process.env.TRACE_LOG_ENABLE}`;
let MULTIPLE_SKIN_ENABLE = `${process.env.MULTIPLE_SKIN_ENABLE}`;
let TOP_MENU_LABELS = `${process.env.TOP_MENU_LABELS}`;
let TOP_MENU_UNION_LABEL = `${process.env.TOP_MENU_UNION_LABEL}`;
let ENV_SIGN = `${process.env.ENV_SIGN}`;
let INVALID_TIME = `${process.env.INVALID_TIME}`;
let CUSTOMIZE_ICON_NAME = `${process.env.CUSTOMIZE_ICON_NAME}`;
let MULTIPLE_LANGUAGE_ENABLE = `${process.env.MULTIPLE_LANGUAGE_ENABLE}`;
let BAIDU_MAP_AK = `${process.env.BAIDU_MAP_AK}`;
let HZERO_PLATFORM = '/hpfm';
let HZERO_IAM = '/iam';
let HZERO_DTT = '/hdtt';
let HZERO_MSG = '/hmsg';
let HZERO_PTL = '/hptl';
let HZERO_WFL = '/hwfl';
let HZERO_DTW = '/hdtw';
let HZERO_HDTW = '/hdtw';
let HZERO_SDR = '/hsdr';
let HZERO_HSGP = '/hsgp';
let HZERO_HITF = '/hitf';
let HZERO_HFLE = '/hfle';
let HZERO_OAUTH = '/oauth';
let HZERO_ASGARD = '/hagd';
let HZERO_IMP = '/himp';
let HZERO_RPT = '/hrpt';
let HZERO_HCNF = '/hcnf';
let HZERO_HWFP = '/hwfp';
let HZERO_FILE = changeHZERO_FILE(API_HOST, HZERO_HFLE);
let HZERO_NLP = '/hnlp';
let HZERO_HPAY = '/hpay';
let HZERO_HEXL = '/hexl';
let HZERO_MNT = '/hmnt';
let HZERO_INVOICE = '/hivc';
let HZERO_IM = '/hims';
let HZERO_OCR = '/hocr';
let HZERO_CHG = '/hchg';
let HZERO_ADM = '/hadm';
let HZERO_DPM = '/hdpm';
let HZERO_HSRH = '/hsrh';
let HZERO_HMDE = '/hmde';
let HZERO_HRES = '/hres';
let HZERO_HLCD = '/hlcd';
let HZERO_HLOD = '/hlod';
let HZERO_HIOT = '/hiot';
let HZERO_HEBK = '/hebk';
let HZERO_ALT = '/halt';
let HZERO_HEVT = '/hevt';
let HZERO_HDSC = '/hdsc';
let HZERO_HORC = '/horc';
let HZERO_HFNT = '/hfnt';
let HZERO_HWKF = '/hwkf';
let AUTH_HOST = changeAUTH_HOST(API_HOST, HZERO_OAUTH);
let LOGIN_URL = `${
  process.env.LOGIN_URL || `${AUTH_HOST}/oauth/authorize?response_type=token&client_id=${CLIENT_ID}`
}`;
let LOGOUT_URL = `${process.env.LOGOUT_URL || `${AUTH_HOST}/logout`}`;
let AUTH_SELF_URL = changeAUTH_SELF_URL(API_HOST, HZERO_IAM);
let VERSION_IS_OP = `${process.env.PLATFORM_VERSION}` === 'OP';
let BKT_PUBLIC = `${process.env.BKT_PUBLIC || 'public'}`;
let BKT_PLATFORM = `${process.env.BKT_PLATFORM || 'hpfm'}`;
let BKT_MSG = `${process.env.BKT_MSG || 'hmsg'}`;
let BKT_SDR = `${process.env.BKT_SDR || 'hsdr'}`;
let BKT_RPT = `${process.env.BKT_RPT || 'hrpt'}`;
let BKT_INVOICE = `${process.env.BKT_INVOICE || 'hivc'}`;
let BKT_OCR = `${process.env.BKT_OCR || 'hocr'}`;
let BKT_ADM = `${process.env.BKT_ADM || 'hadm'}`;
let BKT_HWFP = `${process.env.BKT_HWFP || 'hwfp'}`;
let BKT_HIMS = `${process.env.BKT_HIMS || 'hims'}`;
let BKT_HITF = `${process.env.BKT_HITF || 'hitf'}`;
let BKT_HIMP = `${process.env.BKT_HIMP || 'himp'}`;
let BKT_HFILE = `${process.env.BKT_HFILE || 'hfle'}`;
let BKT_HIOT = `${process.env.BKT_HIOT || 'hiot'}`;
let BKT_HORC = `${process.env.BKT_HORC || 'horc'}`;
let BKT_HFNT = `${process.env.BKT_HFNT || 'hfnt'}`;
let BKT_HWKF = `${process.env.BKT_HWKF || 'hwkf'}`;
// #endregion

// #region changeConfig Funcs
function changeHZERO_FILE(API_HOST, HZERO_HFLE) {
  return `${API_HOST}${HZERO_HFLE}`;
}
function changeAUTH_HOST(API_HOST, HZERO_OAUTH) {
  return `${API_HOST}${HZERO_OAUTH}`;
}
function changeAUTH_SELF_URL(API_HOST, HZERO_IAM) {
  return `${process.env.AUTH_SELF_URL || `${API_HOST}${HZERO_IAM}/hzero/v1/users/self`}`;
}
function changeVERSION_IS_OP(PLATFORM_VERSION) {
  return `${PLATFORM_VERSION}` === 'OP'; //  OP版
}
// #endregion

// #region changeRoute
window.changeRoute = function changeRoute(key, value) {
  if (key && value) {
    switch (key) {
      case 'BPM_HOST':
        BPM_HOST = value;
        break;
      case 'WFP_EDITOR':
        WFP_EDITOR = value;
        break;
      case 'WEBSOCKET_URL':
        WEBSOCKET_URL = value;
        break;
      case 'IM_ENABLE':
        IM_ENABLE = value;
        break;
      case 'TRACE_LOG_ENABLE':
        TRACE_LOG_ENABLE = value;
        break;
      case 'MULTIPLE_SKIN_ENABLE':
        MULTIPLE_SKIN_ENABLE = value;
        break;
      case 'TOP_MENU_LABELS':
        TOP_MENU_LABELS = value;
        break;
      case 'TOP_MENU_UNION_LABEL':
        TOP_MENU_UNION_LABEL = value;
        break;
      case 'ENV_SIGN':
        ENV_SIGN = value;
        break;
      case 'INVALID_TIME':
        INVALID_TIME = value;
        break;
      case 'CUSTOMIZE_ICON_NAME':
        CUSTOMIZE_ICON_NAME = value;
        break;
      case 'MULTIPLE_LANGUAGE_ENABLE':
        MULTIPLE_LANGUAGE_ENABLE = value;
        break;
      case 'BAIDU_MAP_AK':
        BAIDU_MAP_AK = value;
        break;
      case 'HZERO_PLATFORM':
        HZERO_PLATFORM = value;
        break;
      case 'HZERO_IAM':
        HZERO_IAM = value;
        AUTH_SELF_URL = changeAUTH_SELF_URL(API_HOST, value);
        break;
      case 'HZERO_DTT':
        HZERO_DTT = value;
        break;
      case 'HZERO_MSG':
        HZERO_MSG = value;
        break;
      case 'HZERO_PTL':
        HZERO_PTL = value;
        break;
      case 'HZERO_WFL':
        HZERO_WFL = value;
        break;
      case 'HZERO_DTW':
        HZERO_DTW = value;
        break;
      case 'HZERO_HDTW':
        HZERO_HDTW = value;
        break;
      case 'HZERO_SDR':
        HZERO_SDR = value;
        break;
      case 'HZERO_HSGP':
        HZERO_HSGP = value;
        break;
      case 'HZERO_HITF':
        HZERO_HITF = value;
        break;
      case 'HZERO_HFLE':
        HZERO_HFLE = value;
        HZERO_FILE = changeHZERO_FILE(API_HOST, value);
        break;
      case 'HZERO_OAUTH':
        HZERO_OAUTH = value;
        AUTH_HOST = changeAUTH_HOST(API_HOST, value);
        break;
      case 'HZERO_ASGARD':
        HZERO_ASGARD = value;
        break;
      case 'HZERO_IMP':
        HZERO_IMP = value;
        break;
      case 'HZERO_RPT':
        HZERO_RPT = value;
        break;
      case 'HZERO_HCNF':
        HZERO_HCNF = value;
        break;
      case 'HZERO_HWFP':
        HZERO_HWFP = value;
        break;
      case 'HZERO_FILE':
        HZERO_FILE = value;
        break;
      case 'API_HOST':
        HZERO_FILE = changeHZERO_FILE(value, HZERO_HFLE);
        AUTH_HOST = changeAUTH_HOST(value, HZERO_OAUTH);
        AUTH_SELF_URL = changeAUTH_SELF_URL(value, HZERO_IAM);
        break;
      case 'HZERO_NLP':
        HZERO_NLP = value;
        break;
      case 'HZERO_HPAY':
        HZERO_HPAY = value;
        break;
      case 'HZERO_HEXL':
        HZERO_HEXL = value;
        break;
      case 'HZERO_MNT':
        HZERO_MNT = value;
        break;
      case 'HZERO_INVOICE':
        HZERO_INVOICE = value;
        break;
      case 'HZERO_IM':
        HZERO_IM = value;
        break;
      case 'HZERO_OCR':
        HZERO_OCR = value;
        break;
      case 'HZERO_CHG':
        HZERO_CHG = value;
        break;
      case 'HZERO_ADM':
        HZERO_ADM = value;
        break;
      case 'HZERO_DPM':
        HZERO_DPM = value;
        break;
      case 'HZERO_HSRH':
        HZERO_HSRH = value;
        break;
      case 'HZERO_HMDE':
        HZERO_HMDE = value;
        break;
      case 'HZERO_HRES':
        HZERO_HRES = value;
        break;
      case 'HZERO_HLCD':
        HZERO_HLCD = value;
        break;
      case 'HZERO_HLOD':
        HZERO_HLOD = value;
        break;
      case 'HZERO_HIOT':
        HZERO_HIOT = value;
        break;
      case 'HZERO_HEBK':
        HZERO_HEBK = value;
        break;
      case 'HZERO_ALT':
        HZERO_ALT = value;
        break;
      case 'HZERO_HEVT':
        HZERO_HEVT = value;
        break;
      case 'HZERO_HDSC':
        HZERO_HDSC = value;
        break;
      case 'HZERO_HORC':
        HZERO_HORC = value;
        break;
      case 'HZERO_HFNT':
        HZERO_HFNT = value;
        break;
      case 'HZERO_HWKF':
        HZERO_HWKF = value;
        break;

      case 'AUTH_HOST':
        AUTH_HOST = value;
        break;
      case 'LOGIN_URL':
        LOGIN_URL = value;
        break;
      case 'LOGOUT_URL':
        LOGOUT_URL = value;
        break;
      case 'AUTH_SELF_URL':
        AUTH_SELF_URL = value;
        break;
      case 'VERSION_IS_OP':
        VERSION_IS_OP = value;
        break;
      case 'PLATFORM_VERSION':
        VERSION_IS_OP = changeVERSION_IS_OP(value);
        break;
      case 'BKT_PUBLIC':
        BKT_PUBLIC = value;
        break;
      case 'BKT_PLATFORM':
        BKT_PLATFORM = value;
        break;
      case 'BKT_MSG':
        BKT_MSG = value;
        break;
      case 'BKT_SDR':
        BKT_SDR = value;
        break;
      case 'BKT_RPT':
        BKT_RPT = value;
        break;
      case 'BKT_INVOICE':
        BKT_INVOICE = value;
        break;
      case 'BKT_OCR':
        BKT_OCR = value;
        break;
      case 'BKT_ADM':
        BKT_ADM = value;
        break;
      case 'BKT_HWFP':
        BKT_HWFP = value;
        break;
      case 'BKT_HIMS':
        BKT_HIMS = value;
        break;
      case 'BKT_HITF':
        BKT_HITF = value;
        break;
      case 'BKT_HIMP':
        BKT_HIMP = value;
        break;
      case 'BKT_HFILE':
        BKT_HFILE = value;
        break;
      case 'BKT_HIOT':
        BKT_HIOT = value;
        break;
      case 'BKT_HORC':
        BKT_HORC = value;
        break;
      case 'BKT_HFNT':
        BKT_HFNT = value;
        break;
      case 'BKT_HWKF':
        BKT_HWKF = value;
        break;
      default:
        console.error(`${key} is not exists`);
        helpMethod();
        break;
    }
  } else {
    helpMethod(key);
  }
};
// #endregion

// #region helpMethod
const helpMethodAssist = {
  BPM_HOST: { changeConfig: ['BPM_HOST'], depBy: [] },
  WFP_EDITOR: { changeConfig: ['WFP_EDITOR'], depBy: [] },
  WEBSOCKET_URL: { changeConfig: ['WEBSOCKET_URL'], depBy: [] },
  IM_ENABLE: { changeConfig: ['IM_ENABLE'], depBy: [] },
  TRACE_LOG_ENABLE: { changeConfig: ['TRACE_LOG_ENABLE'], depBy: [] },
  MULTIPLE_SKIN_ENABLE: { changeConfig: ['MULTIPLE_SKIN_ENABLE'], depBy: [] },
  TOP_MENU_LABELS: { changeConfig: ['TOP_MENU_LABELS'], depBy: [] },
  TOP_MENU_UNION_LABEL: { changeConfig: ['TOP_MENU_UNION_LABEL'], depBy: [] },
  INVALID_TIME: { changeConfig: ['INVALID_TIME'], depBy: [] },
  CUSTOMIZE_ICON_NAME: { changeConfig: ['CUSTOMIZE_ICON_NAME'], depBy: [] },
  MULTIPLE_LANGUAGE_ENABLE: { changeConfig: ['MULTIPLE_LANGUAGE_ENABLE'], depBy: [] },
  BAIDU_MAP_AK: { changeConfig: ['BAIDU_MAP_AK'], depBy: [] },
  HZERO_PLATFORM: { changeConfig: ['HZERO_PLATFORM'], depBy: [] },
  HZERO_IAM: { changeConfig: ['HZERO_IAM', 'AUTH_SELF_URL'], depBy: [] },
  HZERO_DTT: { changeConfig: ['HZERO_DTT'], depBy: [] },
  HZERO_MSG: { changeConfig: ['HZERO_MSG'], depBy: [] },
  HZERO_PTL: { changeConfig: ['HZERO_PTL'], depBy: [] },
  HZERO_WFL: { changeConfig: ['HZERO_WFL'], depBy: [] },
  HZERO_DTW: { changeConfig: ['HZERO_DTW'], depBy: [] },
  HZERO_HDTW: { changeConfig: ['HZERO_HDTW'], depBy: [] },
  HZERO_SDR: { changeConfig: ['HZERO_SDR'], depBy: [] },
  HZERO_HSGP: { changeConfig: ['HZERO_HSGP'], depBy: [] },
  HZERO_HITF: { changeConfig: ['HZERO_HITF'], depBy: [] },
  HZERO_HFLE: { changeConfig: ['HZERO_HFLE', 'HZERO_FILE'], depBy: [] },
  HZERO_OAUTH: { changeConfig: ['HZERO_OAUTH', 'AUTH_HOST'], depBy: [] },
  HZERO_ASGARD: { changeConfig: ['HZERO_ASGARD'], depBy: [] },
  HZERO_IMP: { changeConfig: ['HZERO_IMP'], depBy: [] },
  HZERO_RPT: { changeConfig: ['HZERO_RPT'], depBy: [] },
  HZERO_HCNF: { changeConfig: ['HZERO_HCNF'], depBy: [] },
  HZERO_HWFP: { changeConfig: ['HZERO_HWFP'], depBy: [] },
  HZERO_FILE: { changeConfig: ['HZERO_FILE'], depBy: ['HZERO_FILE', 'HZERO_FILE'] },
  API_HOST: { changeConfig: ['HZERO_FILE', 'AUTH_HOST', 'AUTH_SELF_URL'], depBy: [] },
  HZERO_NLP: { changeConfig: ['HZERO_NLP'], depBy: [] },
  HZERO_HPAY: { changeConfig: ['HZERO_HPAY'], depBy: [] },
  HZERO_HEXL: { changeConfig: ['HZERO_HEXL'], depBy: [] },
  HZERO_MNT: { changeConfig: ['HZERO_MNT'], depBy: [] },
  HZERO_INVOICE: { changeConfig: ['HZERO_INVOICE'], depBy: [] },
  HZERO_IM: { changeConfig: ['HZERO_IM'], depBy: [] },
  HZERO_OCR: { changeConfig: ['HZERO_OCR'], depBy: [] },
  HZERO_CHG: { changeConfig: ['HZERO_CHG'], depBy: [] },
  HZERO_ADM: { changeConfig: ['HZERO_ADM'], depBy: [] },
  HZERO_DPM: { changeConfig: ['HZERO_DPM'], depBy: [] },
  HZERO_HSRH: { changeConfig: ['HZERO_HSRH'], depBy: [] },
  HZERO_HMDE: { changeConfig: ['HZERO_HMDE'], depBy: [] },
  HZERO_HRES: { changeConfig: ['HZERO_HRES'], depBy: [] },
  HZERO_HLCD: { changeConfig: ['HZERO_HLCD'], depBy: [] },
  HZERO_HLOD: { changeConfig: ['HZERO_HLOD'], depBy: [] },
  HZERO_HIOT: { changeConfig: ['HZERO_HIOT'], depBy: [] },
  HZERO_HEBK: { changeConfig: ['HZERO_HEBK'], depBy: [] },
  HZERO_ALT: { changeConfig: ['HZERO_ALT'], depBy: [] },
  HZERO_EVENT: { changeConfig: ['HZERO_EVENT'], depBy: [] },
  HZERO_HORC: { changeConfig: ['HZERO_HORC'], depBy: [] },
  HZERO_HFNT: { changeConfig: ['HZERO_HFNT'], depBy: [] },
  HZERO_HWKF: { changeConfig: ['HZERO_HWKF'], depBy: [] },
  AUTH_HOST: { changeConfig: ['AUTH_HOST'], depBy: ['AUTH_HOST', 'AUTH_HOST'] },
  LOGIN_URL: { changeConfig: ['LOGIN_URL'], depBy: [] },
  LOGOUT_URL: { changeConfig: ['LOGOUT_URL'], depBy: [] },
  AUTH_SELF_URL: { changeConfig: ['AUTH_SELF_URL'], depBy: ['AUTH_SELF_URL', 'AUTH_SELF_URL'] },
  VERSION_IS_OP: { changeConfig: ['VERSION_IS_OP'], depBy: ['VERSION_IS_OP'] },
  PLATFORM_VERSION: { changeConfig: ['VERSION_IS_OP'], depBy: [] },
  BKT_PUBLIC: { changeConfig: ['BKT_PUBLIC'], depBy: [] },
  BKT_PLATFORM: { changeConfig: ['BKT_PLATFORM'], depBy: [] },
  BKT_MSG: { changeConfig: ['BKT_MSG'], depBy: [] },
  BKT_SDR: { changeConfig: ['BKT_SDR'], depBy: [] },
  BKT_RPT: { changeConfig: ['BKT_RPT'], depBy: [] },
  BKT_INVOICE: { changeConfig: ['BKT_INVOICE'], depBy: [] },
  BKT_OCR: { changeConfig: ['BKT_OCR'], depBy: [] },
  BKT_ADM: { changeConfig: ['BKT_ADM'], depBy: [] },
  BKT_HWFP: { changeConfig: ['BKT_HWFP'], depBy: [] },
  BKT_HIMS: { changeConfig: ['BKT_HIMS'], depBy: [] },
  BKT_HITF: { changeConfig: ['BKT_HITF'], depBy: [] },
  BKT_HIMP: { changeConfig: ['BKT_HIMP'], depBy: [] },
  BKT_HFILE: { changeConfig: ['BKT_HFILE'], depBy: [] },
  BKT_HIOT: { changeConfig: ['BKT_HIOT'], depBy: [] },
  BKT_HORC: { changeConfig: ['BKT_HORC'], depBy: [] },
  BKT_HFNT: { changeConfig: ['BKT_HFNT'], depBy: [] },
  BKT_HWKF: { changeConfig: ['BKT_HWKF'], depBy: [] },
};
function helpMethod(key) {
  if (key && helpMethodAssist[key]) {
    console.error(
      `${key} 会更改: [${helpMethodAssist[key].changeConfig.join(
        ', '
      )}], 被级连更改: [${helpMethodAssist[key].depBy.join(', ')}]`
    );
  } else {
    console.error('使用 changeRoute() 查看可以更改的参数');
    console.error('使用 changeRoute("参数") 查看具体改变');
    console.error('使用 changeRoute("参数", "参数值") 更改参数');
    console.error(`可以更改的配置: [${Object.keys(helpMethodAssist).join(', ')}]`);
  }
}
// #endregion

// #regioin exportsConfig
export {
  CLIENT_ID,
  API_HOST,
  BPM_HOST,
  WFP_EDITOR,
  WEBSOCKET_URL,
  IM_ENABLE,
  TRACE_LOG_ENABLE,
  MULTIPLE_SKIN_ENABLE,
  TOP_MENU_LABELS,
  TOP_MENU_UNION_LABEL,
  ENV_SIGN,
  INVALID_TIME,
  CUSTOMIZE_ICON_NAME,
  MULTIPLE_LANGUAGE_ENABLE,
  BAIDU_MAP_AK,
  HZERO_PLATFORM,
  HZERO_IAM,
  HZERO_DTT,
  HZERO_MSG,
  HZERO_PTL,
  HZERO_WFL,
  HZERO_DTW,
  HZERO_HDTW,
  HZERO_SDR,
  HZERO_HSGP,
  HZERO_HITF,
  HZERO_HFLE,
  HZERO_OAUTH,
  HZERO_ASGARD,
  HZERO_IMP,
  HZERO_RPT,
  HZERO_HCNF,
  HZERO_HWFP,
  HZERO_FILE,
  HZERO_NLP,
  HZERO_HPAY,
  HZERO_HEXL,
  HZERO_MNT,
  HZERO_INVOICE,
  HZERO_IM,
  HZERO_OCR,
  HZERO_CHG,
  HZERO_ADM,
  HZERO_DPM,
  HZERO_HSRH,
  HZERO_HMDE,
  HZERO_HRES,
  HZERO_HLCD,
  HZERO_HLOD,
  HZERO_HIOT,
  HZERO_HEBK,
  HZERO_ALT,
  HZERO_HEVT,
  HZERO_HDSC,
  HZERO_HORC,
  HZERO_HFNT,
  HZERO_HWKF,
  AUTH_HOST,
  LOGIN_URL,
  LOGOUT_URL,
  AUTH_SELF_URL,
  VERSION_IS_OP,
  BKT_PUBLIC,
  BKT_PLATFORM,
  BKT_MSG,
  BKT_SDR,
  BKT_RPT,
  BKT_INVOICE,
  BKT_OCR,
  BKT_ADM,
  BKT_HWFP,
  BKT_HIMS,
  BKT_HITF,
  BKT_HIMP,
  BKT_HFILE,
  BKT_HIOT,
  BKT_HORC,
  BKT_HFNT,
  BKT_HWKF,
};
// #endregion
