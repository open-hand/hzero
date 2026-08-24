/**
 * model 租户级期间定义
 * @date: 2018-7-12
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { searchHeader, getDetail, create, edit } from '@/services/costCenterService';
import { getResponse, createPagination } from 'utils/utils';

function dealDataState(data) {
  // 处理行 处理字段为update
  let config = [];
  if (Array.isArray(data) && data.length > 0) {
    config = data.map((item) => {
      return {
        ...item,
        _status: 'update',
      };
    });
  }
  return config;
}

export default {
  namespace: 'costCenter',
  state: {
    periodHeader: {
      // TabPane: "期间定义"
      list: [], // 数据列表
      pagination: {}, // 分页参数
    },
    detail: {},
  },
  effects: {
    // 获取"期间定义(TabPane)"数据
    *searchPeriodHeader({ payload }, { call, put }) {
      const result = getResponse(yield call(searchHeader, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            periodHeader: {
              list: dealDataState(result.content),
              pagination: createPagination(result),
            },
          },
        });
      }
      return result;
    },

    *create({ payload }, { call }) {
      const result = yield call(create, payload);
      return getResponse(result);
    },
    *edit({ payload }, { call }) {
      const result = yield call(edit, payload);
      return getResponse(result);
    },
    *getDetail({ payload }, { put, call }) {
      const result = yield call(getDetail, payload);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detail: result,
          },
        });
      }
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
