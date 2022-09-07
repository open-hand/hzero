/**
 * nlpTenantWord
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';
import {
  tenantWordCreate,
  tenantWordQuery,
  tenantWordRemoveBatch,
  tenantWordUpdate,
} from '../services/nlpTenantWordService';

export default {
  namespace: 'nlpTenantWord',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
    detail: {}, // 详情数据
  },
  effects: {
    * create({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(tenantWordCreate, record);
      return getResponse(res);
    },
    * removeBatch({ payload }, { call }) {
      const { records } = payload;
      const res = yield call(tenantWordRemoveBatch, records);
      return getResponse(res);
    },
    * update({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(tenantWordUpdate, record);
      return getResponse(res);
    },
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(tenantWordQuery, query);
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
