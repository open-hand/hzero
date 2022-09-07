/**
 * model 平台级期间定义
 * @date: 2018-7-10
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';

import {
  searchHeader,
  searchLine,
  savePeriodHeader,
  searchPeriodRule,
  savePeriod,
} from '../services/periodService';

export default {
  namespace: 'period',
  state: {
    periodHeader: {
      // TabPane: "期间定义"
      list: [],
      pagination: {},
      periodData: [], // 期间维护
    },
    periodLine: {
      // TabPane: "期间查询"
      list: [],
      pagination: {},
    },
  },
  effects: {
    *searchPeriodHeader({ payload }, { call, put }) {
      let result = yield call(searchHeader, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            periodHeader: {
              list: result.content,
              pagination: createPagination(result),
              periodData: [],
              refDetial: {},
            },
          },
        });
      }
    },
    *searchPeriodLine({ payload }, { call, put }) {
      let result = yield call(searchLine, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            periodLine: {
              list: result.content,
              pagination: createPagination(result),
            },
          },
        });
      }
    },
    *savePeriodHeader({ payload }, { call }) {
      const { saveData } = payload;
      const result = yield call(savePeriodHeader, saveData);
      return getResponse(result);
    },
    *searchPeriodRule({ payload }, { call, put }) {
      const { periodSetId, periodHeader } = payload;
      let result = yield call(searchPeriodRule, { periodSetId });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            periodHeader: {
              ...periodHeader,
              periodData: [...result],
            },
          },
        });
      }
    },
    *savePeriod({ payload }, { call }) {
      const result = yield call(savePeriod, payload);
      return getResponse(result);
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
