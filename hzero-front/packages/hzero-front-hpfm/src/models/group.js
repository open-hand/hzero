/**
 * model 集团
 * @date: 2018-7-16
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';

import { fetchGroup, updateGroup } from '../services/groupService';

export default {
  namespace: 'group',

  state: {
    groupData: [],
  },

  effects: {
    // 获取集团信息
    *fetchGroup({ payload }, { call, put }) {
      const res = yield call(fetchGroup, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            groupData: list,
          },
        });
      }
      return list;
    },
    // 更新集团信息
    *updateGroup({ payload }, { call, put }) {
      const res = yield call(updateGroup, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            groupData: list,
          },
        });
      }
      return list;
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
