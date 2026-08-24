/**
 * model 平台多语言
 * @date: 2018-6-22
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchPromptList,
  createPrompt,
  deletePrompt,
  fetchPromptDetail,
  updatePrompt,
  refresh,
  queryLanguage,
} from '../services/promptService';

export default {
  namespace: 'prompt',

  state: {
    promptList: [], // 多语言列表
    languageList: [], // 语言列表
    promptDetail: {}, // 多语言详情
    pagination: {}, // 分页对象
  },

  effects: {
    // 获取初始化数据
    // *init(_, { call, put }) {
    //   const res = yield call(queryUnifyIdpValue, 'HPFM.LANGUAGE');
    //   const list = getResponse(res);
    //   if (list) {
    //     yield put({
    //       type: 'updateState',
    //       payload: {
    //         languageList: list,
    //       },
    //     });
    //   }
    // },

    // 查询初始化语言数据
    *fetchLanguages(_, { call, put }) {
      const response = yield call(queryLanguage);
      const languageData = getResponse(response);
      if (languageData) {
        yield put({
          type: 'updateState',
          payload: {
            languageList: languageData.content,
          },
        });
      }
    },

    // 获取平台语言列表
    *fetchPromptList({ payload }, { call, put }) {
      const res = yield call(fetchPromptList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            promptList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 获取平台语言详情
    *fetchPromptDetail({ payload }, { call, put }) {
      const res = yield call(fetchPromptDetail, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            promptDetail: list,
          },
        });
      }
    },

    // 新增平台语言
    *createPrompt({ payload }, { call }) {
      const res = yield call(createPrompt, payload);
      return getResponse(res);
    },

    // 刷新多语言缓存
    *refresh({ payload }, { call }) {
      const res = yield call(refresh, payload);
      return getResponse(res);
    },

    // 更新平台语言
    *updatePrompt({ payload }, { call }) {
      const res = yield call(updatePrompt, payload);
      return getResponse(res);
    },

    // 删除平台语言
    *deletePrompt({ payload }, { call }) {
      const res = yield call(deletePrompt, payload);
      return getResponse(res);
    },
  },

  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
