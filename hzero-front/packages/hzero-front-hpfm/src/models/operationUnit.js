/*
 * operationUnit.js - 业务实体 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryOperationUnit, saveOperationUnit } from '../services/operationUnitService';

export default {
  namespace: 'operationUnit',

  state: {
    list: {}, // 业务实体列表
  },

  effects: {
    // 查询业务实体
    * queryOperationUnit({ payload }, { call, put }) {
      const res = yield call(queryOperationUnit, payload);
      const list = getResponse(res);
      const pagination = createPagination(list);
      if (list) {
        yield put({
          type: 'updateState',
          payload: { list, pagination },
        });
      }
      return list;
    },

    // 新增或更新业务实体
    * saveOperationUnit({ payload }, { call }) {
      const res = yield call(saveOperationUnit, payload);
      return getResponse(res);
    },
  },

  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
