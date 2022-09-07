/**
 * model 自动转交配置
 * @date: 2018-8-22
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';
import { queryDelegateSet, addDelegateSet } from '../services/delegateService';

export default {
  namespace: 'delegate',
  state: {
    delegateSetDetail: {}, // 当前转交
  },
  effects: {
    // 查询当前转交配置
    * queryDelegateSet({ payload }, { call, put }) {
      const res = yield call(queryDelegateSet, payload);
      const delegateSetDetail = getResponse(res);
      if (delegateSetDetail) {
        yield put({
          type: 'updateState',
          payload: { delegateSetDetail },
        });
      }
    },
    // 保存转交配置
    * addDelegateSet({ payload }, { call }) {
      const res = getResponse(yield call(addDelegateSet, payload));
      return res;
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
