/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryList } from '../services/interfaceStatisticsService';

export default {
  namespace: 'interfaceStatistics',
  state: {
    code: {},
  },
  effects: {
    * queryList({ params }, { call }) {
      const res = yield call(queryList, params);
      const response = getResponse(res) || {};
      return {
        dataSource: response.content || [],
        pagination: createPagination(response),
      };
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
  },
};
