/**
 * model - 用户账单
 * @date: 2020/2/14
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'hzero-front/lib/utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { queryList, queryDetail } from '../services/billService';
import CodeConstants from '../constants/CodeConstants';

export default {
  namespace: 'bill', // model名称
  state: {
    list: {
      dataSource: [],
      pagination: {},
      statusList: [], // 账单状态
    },
  },
  effects: {
    // 查询用户账单列表
    * queryList({ payload }, { call, put }) {
      let result = yield call(queryList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 查询用户账单明细
    * queryDetail({ payload }, { call, put }) {
      let result = yield call(queryDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 查询值集
    * queryIdpValue(params, { call, put }) {
      const statusList = getResponse(yield call(queryIdpValue, CodeConstants.BillStatus));
      yield put({
        type: 'updateState',
        payload: {
          statusList,
        },
      });
    },
  },
  reducers: {
    // 合并state状态数据,生成新的state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
