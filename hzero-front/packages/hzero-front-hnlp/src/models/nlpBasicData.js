/**
 * nlpBasicData
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';
import {
  basicDataQuery,
  basicDataRemoveBatch,
  basicDataUpdate,
} from '../services/nlpBasicDataService';

export default {
  namespace: 'nlpBasicData',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
    detail: {}, // 详情数据
  },
  effects: {
    * init(/* {payload}, {call} */) {
      // 不需要init
    },
    * removeBatch({ payload }, { call }) {
      const { records } = payload;
      const res = yield call(basicDataRemoveBatch, records);
      return getResponse(res);
    },
    * update({ payload }, { call }) {
      const { record, id } = payload;
      const res = yield call(basicDataUpdate, id, record);
      return getResponse(res);
    },
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(basicDataQuery, query);
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
