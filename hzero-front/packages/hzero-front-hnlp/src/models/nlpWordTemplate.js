/**
 * nlpWordTemplate
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';
import {
  wordTemplateCreate,
  wordTemplateQuery,
  wordTemplateRemoveBatch,
  wordTemplateUpdate,
} from '../services/nlpWordTemplateService';

export default {
  namespace: 'nlpWordTemplate',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
    detail: {}, // 详情数据
  },
  effects: {
    * create({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(wordTemplateCreate, record);
      return getResponse(res);
    },
    * removeBatch({ payload }, { call }) {
      const { records } = payload;
      const res = yield call(wordTemplateRemoveBatch, records);
      return getResponse(res);
    },
    * update({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(wordTemplateUpdate, record);
      return getResponse(res);
    },
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(wordTemplateQuery, query);
      const retRes = getResponse(res);
      if (retRes) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: retRes.content,
            pagination: createPagination(retRes),
          },
        });
      }
      return retRes;
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
