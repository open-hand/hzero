/**
 * model - CA证书管理
 * @date: 2019/9/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryList, deleteCa } from '../services/caManagementService';

export default {
  namespace: 'caManagement',
  state: {
    list: {
      dataSource: [], // 证书列表
      pagination: {}, // 证书分页
    },
  },
  effects: {
    // 查询证书列表
    *queryList({ payload }, { call, put }) {
      const res = yield call(queryList, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },

    // 删除证书
    *deleteCa({ payload }, { call }) {
      const res = yield call(deleteCa, payload);
      const response = getResponse(res);
      return response;
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
