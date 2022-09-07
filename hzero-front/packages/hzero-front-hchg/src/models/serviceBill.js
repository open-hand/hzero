/**
 * model - 服务账单
 * @date: 2019/8/30
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'services/api';
import { queryList } from '../services/serviceBillService';

export default {
  namespace: 'serviceBill', // model名称
  state: {
    list: {
      dataSource: [],
      pagination: {},
      statusList: [], // 订单状态
    },
  },
  effects: {
    // 查询服务账单列表
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

    // 查询值集
    * queryIdpValue(params, { call, put }) {
      const statusList = getResponse(yield call(queryIdpValue, 'HCHG.CHARGE_STATUS'));
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
