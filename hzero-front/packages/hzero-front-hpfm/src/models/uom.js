/**
 * model 平台级计量单位定义
 * @date: 2018-7-6
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';

import { searchUomData, saveUomData } from '../services/uomService';

export default {
  namespace: 'uom',
  state: {
    list: [],
    pagination: {},
  },
  effects: {
    *fetchUomData({ payload }, { call, put }) {
      let result = yield call(searchUomData, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 批量保存新增计量单位
    *saveUomData({ payload }, { call }) {
      const result = yield call(saveUomData, payload);
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
