/**
 * @date 2018-07-21
 * @author WY <yang.wang06@hand-china.com>
 */

import { forEach } from 'lodash';
import { queryIdpValue, queryMapIdpValue, getPublicKey } from 'hzero-front/lib/services/api';
import notification from 'utils/notification';
import { getResponse, getSession, setSession } from 'utils/utils';

import {
  userInfoQuerySelf,
  userInfoUpdateRealName,
  userInfoQueryRoleDataSource,
  userInfoUpdateDefaultRole,
  userInfoQueryCompanyDataSource,
  userInfoUpdateDefaultCompany,
  userInfoUpdatePassword,
  userInfoValidatePre,
  userInfoPostOldPhoneCaptcha,
  userInfoValidateUnCheckedPhone,
  userInfoPostOldEmailCaptcha,
  userInfoValidateUnCheckedEmail,
  userInfoPostNewEmailCaptcha,
  userInfoPostNewPhoneCaptcha,
  userInfoValidateNewEmail,
  userInfoValidatePrePassword,
  userInfoValidateNewPhone,
  // userInfoCompanyQuery,
  queryLanguageData,
  updateDefaultLanguage,
  updateDefaultMenu,
  updateDefaultDate,
  updateDefaultTimeZone,
  fetchEnabledFile,
  uploadAvatar,
  saveAvatar,
  fetchOpenAccountList,
  unBindOpenAccount,
  beforeBind,
} from '../services/userInfoService';
import { getPasswordRule } from '../services/commonService';

export default {
  namespace: 'userInfo',
  state: {
    // 存放用户的信息
    userInfo: {},
    // 上传头像相关
    imgFormData: {}, // 图片表单数据
    uploadImgName: '', // 图片名称
    uploadImgPreviewUrl: '', // 图片上传预览
    imgUploadStatus: 'waiting', // 图片上传状态
    // 公司的属性
    companyProps: {},
    // 角色的属性
    roleProps: {},
    // 模态框 邮箱 手机 密码 的属性
    modalProps: {},
    languageMap: {}, // 系统支持的语言类型
    menuMap: {}, // 系统支持的菜单布局
    roleMergeMap: {}, // 系统角色合并
    dateMap: {}, // 系统支持的日期格式
    timeMap: {}, // 系统支持的时间格式
    openAccountList: [], // 第三方绑定列表
    limitData: {}, // 头像上传文件的限制类型和大小
    passwordTipMsg: {}, // 密码输入提示
    publicKey: '', // 密码公钥
  },
  effects: {
    // 加载用户信息并重置所有信息到初始状态
    *init({ payload }, { call, put }) {
      let res = yield call(userInfoQuerySelf, payload);
      res = getResponse(res);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: res,
          },
        });
      }
      return res;
    },
    // 初始化当前用户角色
    *initRoleDataSource(_, { call, put }) {
      const res = yield call(userInfoQueryRoleDataSource);
      const roleDataSource = getResponse(res);
      if (roleDataSource) {
        const roleMap = {};
        forEach(res, (role) => {
          roleMap[role.id] = role;
        });
        yield put({
          type: 'updateState',
          payload: {
            roleDataSource,
            roleMap,
          },
        });
      }
    },

    *getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
      return res;
    },

    // 初始化当前用户公司
    *initCompanyDataSource({ payload }, { call, put }) {
      const { organizationId } = payload;
      const res = yield call(userInfoQueryCompanyDataSource, organizationId);
      const companyDataSource = getResponse(res);
      if (companyDataSource) {
        const companyMap = {};
        forEach(res, (company) => {
          companyMap[company.companyId] = company;
        });
        yield put({
          type: 'updateState',
          payload: {
            companyDataSource,
            companyMap,
          },
        });
      }
    },
    // 获取系统支持的语言
    *initLanguageDataSource(_, { call, put }) {
      let result = yield call(queryLanguageData);
      result = getResponse(result);
      if (result) {
        const languageMap = {};
        forEach(result, (language) => {
          languageMap[language.code] = language;
        });
        yield put({
          type: 'updateState',
          payload: {
            languageMap,
          },
        });
      }
    },

    *initLovCode({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = yield call(queryMapIdpValue, lovCodes);
      const response = getResponse(res);
      const dateMap = {};
      if (response.dateMap) {
        forEach(response.dateMap, (date) => {
          dateMap[date.value] = date;
        });
      }
      const timeMap = {};
      if (response.timeMap) {
        forEach(response.timeMap, (time) => {
          timeMap[time.value] = time;
        });
      }
      yield put({
        type: 'updateState',
        payload: { ...response, dateMap, timeMap },
      });
    },

    // 获取系统支持的菜单布局
    *initMenuDataSource(_, { call, put }) {
      const menuMap = getResponse(yield call(queryIdpValue, 'HPFM.MENU_LAYOUT'));
      if (menuMap) {
        yield put({
          type: 'updateState',
          payload: {
            menuMap,
          },
        });
      }
    },
    // 获取系统角色合并
    *initRoleMergeDataSource(_, { call, put }) {
      const roleMergeMap = getResponse(yield call(queryIdpValue, 'HPFM.ENABLED_FLAG'));
      if (roleMergeMap) {
        yield put({
          type: 'updateState',
          payload: {
            roleMergeMap,
          },
        });
      }
    },
    // 获取系统支持的日期格式
    *initDateFormat(_, { call, put }) {
      let dateFormat = yield call(queryIdpValue, 'HIAM.DATE_FORMAT');
      dateFormat = getResponse(dateFormat);
      if (dateFormat) {
        const dateMap = {};
        forEach(dateFormat, (date) => {
          dateMap[date.value] = date;
        });
        yield put({
          type: 'updateState',
          payload: {
            dateMap,
          },
        });
      }
    },
    // 获取系统支持的时间格式
    *initTimeFormat(_, { call, put }) {
      let timeFormat = yield call(queryIdpValue, 'HIAM.TIME_FORMAT');
      timeFormat = getResponse(timeFormat);
      if (timeFormat) {
        const timeMap = {};
        forEach(timeFormat, (time) => {
          timeMap[time.value] = time;
        });
        yield put({
          type: 'updateState',
          payload: {
            timeMap,
          },
        });
      }
    },
    // 获取系统首页弹窗提醒的设置
    *initReminderFlagData(_, { call, put }) {
      const reminderFlagMap = getResponse(yield call(queryIdpValue, 'HPFM.ENABLED_FLAG'));
      if (reminderFlagMap) {
        yield put({
          type: 'updateState',
          payload: {
            reminderFlagMap,
          },
        });
      }
    },
    // 更新默认语言
    *updateLanguage({ payload }, { call, put }) {
      const { language, languageName, userInfo } = payload;
      let result = yield call(updateDefaultLanguage, { language });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              language,
              languageName,
            },
          },
        });
        // // 更新 global 的语言
        // yield put({
        //   type: 'global/changeLanguage',
        //   payload: language,
        // });
        // // 和 global 调用的同一个接口
        // yield put({
        //   type: 'global/updateDefaultLanguage',
        //   payload: { language },
        // });
        notification.success();
        return result;
      }
    },
    // 更新菜单布局
    *updateMenuType({ payload }, { call, put }) {
      const { menuLayout = 'default', userInfo } = payload;
      let result = yield call(updateDefaultMenu, { menuLayout });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              menuLayout,
            },
          },
        });
        yield put({
          type: 'user/updateCurrentUser',
          payload: {
            menuLayout,
          },
        });
        notification.success();
        return result;
      }
    },

    // 更新角色合并
    *updateRoleMerge({ payload }, { call, put }) {
      const { roleMergeFlag = -1, userInfo } = payload;
      let result = yield call(updateDefaultMenu, { roleMergeFlag });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              roleMergeFlag,
            },
          },
        });
        notification.success();
        return result;
      }
    },
    // 更新默认时间格式
    *updateDateFormat({ payload }, { call, put }) {
      const { dateFormat, dateFormatMeaning, userInfo } = payload;
      let result = yield call(updateDefaultDate, { dateFormat, timeFormat: userInfo.timeFormat });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              dateFormat,
              dateFormatMeaning,
            },
          },
        });
        yield put({
          type: 'user/updateCurrentUser',
          payload: {
            dateFormat,
            dateTimeFormat: `${dateFormat} ${userInfo.timeFormat}`,
          },
        });
        notification.success();
        return result;
      }
    },
    // 更新默认日期格式
    *updateTimeFormat({ payload }, { call, put }) {
      const { timeFormat, timeFormatMeaning, userInfo } = payload;
      let result = yield call(updateDefaultDate, { timeFormat, dateFormat: userInfo.dateFormat });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              timeFormat,
              timeFormatMeaning,
            },
          },
        });
        yield put({
          type: 'user/updateCurrentUser',
          payload: {
            timeFormat,
            dataTimeFormat: `${userInfo.dateFormat} ${timeFormat}`,
          },
        });
        notification.success();
        return result;
      }
    },
    // 更新默认时区
    *updateTimeZone({ payload }, { call, put }) {
      const { timeZone, timeZoneMeaning, userInfo } = payload;
      let result = yield call(updateDefaultTimeZone, { timeZone });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              timeZone,
              timeZoneMeaning,
            },
          },
        });
        yield put({
          type: 'user/updateCurrentUser',
          payload: {
            timeZone,
          },
        });
        notification.success();
        return result;
      }
    },
    // 更新昵称
    *updateRealName({ payload }, { call, put }) {
      const { realName, userInfo } = payload;
      const res = getResponse(yield call(userInfoUpdateRealName, realName));
      if (res) {
        notification.success();
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              realName,
            },
          },
        });
      }
      return res;
    },
    // 更新默认角色
    *updateRole({ payload }, { call, put }) {
      const { defaultRoleId, defaultRoleName, organizationId, userInfo } = payload;
      const res = getResponse(yield call(userInfoUpdateDefaultRole, organizationId, defaultRoleId));
      if (res) {
        notification.success();
        yield put({
          type: 'updateState',
          payload: {
            roleProps: {},
            userInfo: {
              ...userInfo,
              defaultRoleId,
              defaultRoleName,
            },
          },
        });
      }
      return res;
    },
    // 更新默认公司
    *updateCompany({ payload }, { call, put }) {
      const { defaultCompanyId, defaultCompanyName, userInfo, organizationId } = payload;
      const res = getResponse(
        yield call(userInfoUpdateDefaultCompany, organizationId, defaultCompanyId)
      );
      if (res) {
        notification.success();
        yield put({
          type: 'updateState',
          payload: {
            companyProps: {},
            userInfo: {
              ...userInfo,
              defaultCompanyId,
              defaultCompanyName,
            },
          },
        });
      }
      return res;
    },
    // 更新首页消息弹窗提醒状态
    *updateReminderFlag({ payload }, { call, put }) {
      const { popoutReminderFlag = -1, userInfo } = payload;
      let result = yield call(updateDefaultMenu, { popoutReminderFlag });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              reminderFlag: popoutReminderFlag,
            },
          },
        });
        notification.success();
        return result;
      }
    },
    // 关闭模态框
    *closeForm({ payload }, { put }) {
      const { modalProps } = payload;
      // 保留 modal 的,title,formItems, 保证在动画的时候不会与编辑时不一样.
      const { title, formItems, validCache = {} } = modalProps;
      yield put({
        type: 'updateState',
        payload: {
          modalProps: {
            validCache,
            title,
            formItems,
            visible: false,
          },
        },
      });
    },
    // 打开模态框
    *openForm({ payload }, { put }) {
      const { modalProps, ...params } = payload;
      const { title, formItems, lastCheckKey, captchaKey, validCache = {} } = modalProps;
      const { step } = payload;
      yield put({
        type: 'updateState',
        payload: {
          modalProps: {
            title,
            formItems,
            lastCheckKey,
            captchaKey,
            validCache,
            step,
            ...validCache[step],
            ...params,
            visible: true,
          },
        },
      });
    },
    // 停止 倒计时
    *captchaLimitEnd({ payload }, { put }) {
      const { modalProps } = payload;
      yield put({
        type: 'updateState',
        payload: {
          modalProps: {
            ...modalProps,
            validCodeSendLimitFlag: false,
          },
        },
      });
    },
    // 发送验证码
    *postCaptcha({ payload }, { call, put }) {
      const { type, value, modalProps, ...params } = payload;
      const queryParams = { ...params };
      let service;
      let field;
      const captchaField = 'captchaKey';
      let needCacheValidTime = false; // 不只需要存储时间 还需要存储对应的 key
      switch (type) {
        case 'newPhone':
          service = userInfoPostNewPhoneCaptcha;
          field = 'phone';
          break;
        case 'newEmail':
          service = userInfoPostNewEmailCaptcha;
          field = 'email';
          break;
        case 'oldEmail':
          needCacheValidTime = true;
          service = userInfoPostOldEmailCaptcha;
          field = 'email';
          break;
        case 'oldPhone':
        default:
          needCacheValidTime = true;
          service = userInfoPostOldPhoneCaptcha;
          field = 'phone';
          break;
      }
      if (field) {
        queryParams[field] = value;
      }
      const res = getResponse(yield call(service, queryParams));
      const validCodeLimitTimeStart = new Date().getTime();
      // 60秒限制
      const validCodeLimitTimeEnd = validCodeLimitTimeStart + 60000;
      const nextModalProps = {
        ...modalProps,
      };
      if (res) {
        notification.success({ message: res.message });
        if (captchaField) {
          nextModalProps[captchaField] = res[captchaField]; // 存储对应的 验证码的 captchaKey
          if (needCacheValidTime) {
            setSession(`user-info-${type}`, res[captchaField] || 0);
          }
          nextModalProps.validCodeSendLimitFlag = true;
          nextModalProps.validCodeLimitTimeEnd = validCodeLimitTimeEnd;
          if (needCacheValidTime) {
            nextModalProps.validCache = {
              ...modalProps.validCache,
              [modalProps.step]: {
                validCodeSendLimitFlag: true,
                validCodeLimitTimeEnd,
              },
            };
          }
        }
      }
      yield put({
        type: 'updateState',
        payload: {
          modalProps: nextModalProps,
        },
      });
      return res;
    },
    *validatePrePassword({ payload }, { call, put }) {
      const { modalProps, ...params } = payload;
      const res = getResponse(yield call(userInfoValidatePrePassword, params));
      const nextModalProps = { ...modalProps };
      if (res) {
        notification.success();
        nextModalProps.lastCheckKey = res.lastCheckKey;
      }
      yield put({
        type: 'updateState',
        payload: {
          modalProps: nextModalProps,
        },
      });
      return res;
    },
    *validatePreValidate({ payload }, { call, put }) {
      const { captchaKey, type, captcha, modalProps } = payload;
      let res;
      const nextModalProps = { ...modalProps };
      if (captchaKey) {
        res = getResponse(
          yield call(userInfoValidatePre, { captchaKey, captcha, businessScope: 'self' })
        );
        // 清除 nextModalProps 里面的 captchaKey
        nextModalProps.captchaKey = undefined;
      } else {
        const sessionCaptchaKey = getSession(`user-info-${type}`);
        // 清除 sessionStorage 里面的 captchaKey
        setSession(`user-info-${type}`, 0);
        if (sessionCaptchaKey) {
          res = getResponse(
            yield call(userInfoValidatePre, {
              captchaKey: sessionCaptchaKey,
              captcha,
              businessScope: 'self',
            })
          );
        }
      }
      if (res) {
        notification.success();
        nextModalProps.lastCheckKey = res.lastCheckKey;
      }
      yield put({
        type: 'updateState',
        payload: {
          modalProps: nextModalProps,
        },
      });
      return res;
    },
    // 验证并更新邮箱
    *validateNewEmail({ payload }, { call, put }) {
      const { userInfo, ...params } = payload;
      const res = getResponse(yield call(userInfoValidateNewEmail, params));
      if (res) {
        notification.success({ message: res.message });
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              email: payload.email,
              emailCheckFlag: 1, // 绑定邮箱后 更新 emailCheckFlag
            },
          },
        });
      }
      return res;
    },
    // 验证并更新手机
    *validateNewPhone({ payload }, { call, put }) {
      const { userInfo, ...params } = payload;
      const res = getResponse(yield call(userInfoValidateNewPhone, params));
      if (res) {
        notification.success({ message: res.message });
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              phone: params.phone,
            },
          },
        });
      }
      return res;
    },
    // 更新密码
    *updatePassword({ payload }, { call, put }) {
      const { password, originalPassword, userInfo = {} } = payload;
      const res = yield call(userInfoUpdatePassword, { password, originalPassword });
      if (res && !res.failed) {
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              passwordResetFlag: 1,
            },
          },
        });
        notification.success({ message: res.message });
        // 需要刷新界面,因为密码安全等级 是由后端 计算的。
      }
      return res;
    },

    // 获取密码校验规则
    *getPasswordRule({ payload }, { call, put }) {
      const res = getResponse(yield call(getPasswordRule, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            passwordTipMsg: res,
          },
        });
      }
      return res;
    },

    // 验证并绑定手机号
    *validateUnCheckedPhone({ payload }, { call, put }) {
      const { captchaKey, captcha, userInfo } = payload;
      const res = getResponse(yield call(userInfoValidateUnCheckedPhone, { captchaKey, captcha }));
      if (res) {
        notification.success({ message: res.message });
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              phoneCheckFlag: 1,
            },
          },
        });
      }
      return res;
    },
    // 验证并绑定邮箱
    *validateUnCheckedEmail({ payload }, { call, put }) {
      const { captchaKey, captcha, userInfo } = payload;
      const res = getResponse(yield call(userInfoValidateUnCheckedEmail, { captchaKey, captcha }));
      if (res) {
        notification.success({ message: res.message });
        yield put({
          type: 'updateState',
          payload: {
            userInfo: {
              ...userInfo,
              emailCheckFlag: 1,
            },
          },
        });
      }
      return res;
    },
    // 获取头像可上传的文件类型和大小
    *fetchEnabledFile({ payload }, { call, put }) {
      const res = yield call(fetchEnabledFile, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            limitData: result,
          },
        });
      }
      return result;
    },
    // 上传裁剪好的图片
    *uploadAvatar({ payload }, { call, put }) {
      const res = yield call(uploadAvatar, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            imgUploadStatus: 'done',
          },
        });
      } else {
        yield put({
          type: 'updateState',
          payload: {
            imgUploadStatus: 'faild',
          },
        });
      }
      return result;
    },
    // 保存头像
    *saveAvatar({ payload }, { call }) {
      const res = yield call(saveAvatar, payload);
      return getResponse(res);
    },
    *fetchOpenAccountList({ payload }, { call, put }) {
      const res = yield getResponse(call(fetchOpenAccountList, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            openAccountList: res,
          },
        });
      }
      return res;
    },
    // 解除第三方绑定
    *unBindOpenAccount({ payload }, { call }) {
      const res = yield call(unBindOpenAccount, payload);
      return getResponse(res);
    },
    // 绑定第三方应用之前
    *beforeBind({ payload }, { call }) {
      const res = yield call(beforeBind, payload);
      return getResponse(res);
    },
  },
  reducers: {
    // 生成新的state
    updateState(state, { payload }) {
      return { ...state, ...payload };
    },
  },
};
