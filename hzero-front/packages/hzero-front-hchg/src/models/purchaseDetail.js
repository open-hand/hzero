/**
 * model - 购买详单
 * @date: 2019/8/30
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'services/api';
import { queryList, queryDetail } from '../services/purchaseDetailService';

export default {
  namespace: 'purchaseDetail', // model名称
  state: {
    list: {
      dataSource: [], // 列表数据
      pagination: {}, // 列表分页
      statusList: [], // 订单状态
      detailInfo: {}, // 详情
    },
  },
  effects: {
    // 查询购买详单列表
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
      const statusList = getResponse(yield call(queryIdpValue, 'HCHG.ORDER_STATUS'));
      yield put({
        type: 'updateState',
        payload: {
          statusList,
        },
      });
    },

    // 查询详情
    * queryDetail({ payload }, { call, put }) {
      let result = yield call(queryDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detailInfo: result,
          },
        });
      }
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
