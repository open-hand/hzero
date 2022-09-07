/**
 * @date 2018-12-24
 * @author WJC <jiacheng.wang@hand-china.com>
 */

import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { fetchSagaList, querySagaDetail } from '../services/sagaService';

export default {
  namespace: 'saga',

  state: {
    sagaList: [], // 事务定义列表
    sagaDetail: {}, // 详情数据
    pagination: {}, // 事务定义分页对象
  },

  effects: {
    // 获取列表数据
    * fetchSagaList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchSagaList, parseParameters(payload)));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaList: res.content,
            pagination: createPagination(res),
          },
        });
      }
    },
    // 获取列表数据
    * querySagaDetail({ payload }, { call, put }) {
      const res = getResponse(yield call(querySagaDetail, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaDetail: res,
          },
        });
      }
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
