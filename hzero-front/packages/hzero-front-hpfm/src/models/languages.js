/**
 * model - 语言维护
 * @date: 2018-8-9
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import { queryLanguage, editLanguage } from '../services/languagesService';

export default {
  namespace: 'languages',
  state: {
    query: {}, // 查询参数
    languageList: [], // 语言列表
    pagination: {}, // 分页信息
  },
  effects: {
    // 查询数据
    *fetchLanguages({ payload }, { call, put }) {
      const { page, ...query } = payload;
      const response = yield call(queryLanguage, payload);
      const languageData = getResponse(response);
      if (languageData) {
        yield put({
          type: 'updateState',
          payload: {
            query,
            pagination: createPagination(languageData),
            languageList: languageData.content,
          },
        });
      }
    },
    // 编辑数据
    *editLanguage({ payload }, { call }) {
      const response = yield call(editLanguage, payload);
      return getResponse(response);
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
