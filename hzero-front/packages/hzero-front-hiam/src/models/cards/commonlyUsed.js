/**
 * model - 工作台卡片
 * @date: 2019-02-23
 * @author: YKK <kaikai.yang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse } from 'utils/utils';

import { queryFunctions, addFunctions } from '@/services/commonlyUsedService';

export default {
  namespace: 'commonlyUsed',
  state: {
    // Hzero
    functions: [], // 固定的常用功能
    allFunction: [], // 全部的常用功能
    checkedKeys: [], // 选中的常用功能
    allCheckedKeys: [],
    commonlyUsedLoading: true, // 常用功能预加载loading
    isListLoad: null,
  },

  effects: {
    // 查询固定的常用功能
    *queryFunctions({ payload }, { call, put }) {
      const data = getResponse(yield call(queryFunctions));
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            checkedKeys: data.map(item => item.menuId),
            functions: data.splice(0, payload.maxNum),
          },
        });
      }
      return data;
    },

    // 添加需要展示的常用功能
    *addFunctions({ payload }, { call }) {
      const orderType = getResponse(yield call(addFunctions, payload));
      return getResponse(orderType);
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
