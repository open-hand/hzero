/**
 * model 消息管理/模板服务映射
 * @date: 2018-8-21
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { getResponse, createPagination } from 'utils/utils';

import {
  querySMSList,
  searchHeader,
  saveHeader,
  updateHeader,
  deleteLine,
  deleteHeader,
  fetchLangType,
  sendMessage,
  getParams,
  searchCopyHeader,
  fetchTemplateData,
  GetWebhook,
  deleteWebhook,
  createWebhook,
} from '../services/sendConfigService';

import { searchCategoryCodeTree } from '../services/messageTemplateService';

export default {
  namespace: 'sendConfig',
  state: {
    list: [], // 数据列表
    pagination: {}, // 分页器
    header: {}, // 审核规则头
    messageType: [], // 消息类型
    langType: [], // 语言类型
    paramsName: [], // 参数名称
    copyDetail: {},
    templateData: {}, // 模板数据
    categoryTree: [],
    webHook: {},
  },
  effects: {
    *fetchType(_, { call, put }) {
      const categoryTree = getResponse(
        yield call(searchCategoryCodeTree, {
          'HMSG.TEMP_SERVER.CATEGORY': 1,
          'HMSG.TEMP_SERVER.SUBCATEGORY': 2,
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          categoryTree,
        },
      });
    },
    // 获取表格数据
    *queryTableList({ payload }, { call, put }) {
      let result = yield call(querySMSList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
      return result;
    },
    // 获取消息类型
    *queryMessageType(_, { call, put }) {
      const messageType = getResponse(yield call(queryIdpValue, 'HMSG.MESSAGE_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          messageType,
        },
      });
    },
    // 获取语言类型
    *fetchLangType({ payload }, { call, put }) {
      const list = yield call(fetchLangType, payload);
      const result = getResponse(list);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            langType: result,
          },
        });
      }
    },
    // 改变语言获取参数
    *getParams({ payload }, { call, put }) {
      const list = yield call(getParams, payload);
      const result = getResponse(list);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            paramsName: result,
          },
        });
      }
    },
    // 获取头信息
    *fetchHeader({ payload }, { call, put }) {
      let result = yield call(searchHeader, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: result,
          },
        });
      }
    },
    // 获取头信息
    *fetchCopyHeader({ payload }, { call, put }) {
      let result = yield call(searchCopyHeader, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            copyDetail: result,
          },
        });
      }
      return result;
    },
    // 获取信息
    *getWebhook({ payload }, { call, put }) {
      let result = yield call(GetWebhook, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            webHook: {
              webHookData: result.content,
              pagination: createPagination(result),
            },
          },
        });
      }
    },
    // 发送消息
    *sendMessage({ payload }, { call }) {
      const result = yield call(sendMessage, { ...payload });
      return getResponse(result);
    },
    // 新增模板服务
    *saveHeader({ payload }, { call }) {
      const result = yield call(saveHeader, { ...payload });
      return getResponse(result);
    },
    // 修改模板服务
    *updateHeader({ payload }, { call }) {
      const result = yield call(updateHeader, { ...payload });
      return getResponse(result);
    },
    // 删除模板服务
    *deleteHeader({ payload }, { call }) {
      const result = yield call(deleteHeader, { ...payload });
      return getResponse(result);
    },
    // 删除行服务
    *deleteLine({ payload }, { call }) {
      const result = yield call(deleteLine, { ...payload });
      return getResponse(result);
    },
    // 删除
    *deleteWebhook({ payload }, { call }) {
      const result = yield call(deleteWebhook, { ...payload });
      return getResponse(result);
    },
    //
    *createWebhook({ payload }, { call }) {
      const result = yield call(createWebhook, { ...payload });
      return getResponse(result);
    },
    *fetchTemplateData({ payload }, { call, put }) {
      // 请求前清除缓存
      yield put({
        type: 'updateState',
        payload: {
          templateData: '',
        },
      });
      const res = yield call(fetchTemplateData, { ...payload });
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            templateData: result.templateContent,
          },
        });
      }
    },
  },
  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
